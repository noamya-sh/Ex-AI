import java.util.*;

public class VariableEliminate {
    BayesianNetwork bn;
    int add;
    int multi;

    public VariableEliminate(BayesianNetwork bn){
        this.bn=bn;
        this.add=0;
        this.multi=0;
    }
    private void removeFactorsContain(Variable v){
        for(Factor f:bn.factors.values())
            if (f.variables.contains(v))
                bn.factors.remove(f.variables,f);
    }
    private void updateHiddens(Variable query, List<String> evidences, List<Variable> hiddens){
        //check independence
        List<String> visited = bn.BaseBall(query.getK(),evidences);
        for(Variable v:hiddens){
            if (!visited.contains(v.getK())) {
                hiddens.remove(v);
                removeFactorsContain(v);
            }
        }
        //check for each hidden variable if is ancestor
        List<String> list = new ArrayList<>(evidences);
        list.add(query.getK());
        for(Variable v:hiddens){
            if(!bn.isAncestor(v,list)) {
                hiddens.remove(v);
                removeFactorsContain(v);
            }
        }
    }
    public void updateFactors(List<String> evidences){
        List<String> evidVar= new LinkedList<>();
        for (String s:evidences){
            int x = s.indexOf("=");
            evidVar.add(s.substring(0,x));
        }
        for (var entry: bn.factors.entrySet()) {
            Factor g = entry.getValue();
            for (String s : evidVar) {
                if (g.variables.contains(bn.net.get(s))) {
                    String r = firstStr(evidences, s);
                    g.rows.entrySet().removeIf(row -> !row.getKey().contains(r));
                    //remove this 'given' from each row contain it.
                    for(var key:g.rows.entrySet())
                        key.getKey().remove(r);
                    g.variables.remove(bn.net.get(s));
                    g.size=g.rows.size();
                }
            }
        }
    }
    public double variableEliminate(Variable query,String req, List<String> evidences, List<Variable> hiddens){
        List<String> evidVar= new LinkedList<>();
        for (String s:evidences){
            int x = s.indexOf("=");
            evidVar.add(s.substring(0,x));
        }
        updateHiddens(query,evidVar,hiddens);//delete hidden variables not relevant
        HashMap<List<Variable>,Factor> copy = bn.copyFactors();//save deep copy
        updateFactors(evidences);//clean factors by given
        for (Variable v:hiddens){
            List<Factor> j = join(v);
            Factor f = eliminate(v,j);
            bn.factors.put(f.variables,f);
        }
        List<Factor> j = join(query);
        Factor f = eliminate(query,j);
        double d =normal(f,req);
        return 0;
    }

    public List<Factor> join(Variable v){
        List<Factor> list_factors =bn.getFactorsContain(v);
        if (list_factors.size()<=1)
            return list_factors;
        for(Factor f:list_factors)
            if (f.size==1)
                list_factors.remove(f);
        while (list_factors.size()>=2){
            Collections.sort(list_factors);
            Factor f1 = list_factors.get(0);
            Factor f2 = list_factors.get(1);
            System.out.println(f1);System.out.println(f2);
            System.out.println("*****join*****");
            Factor jf = new Factor();
            //add variables to new factor
            List<Variable> all = new LinkedList<>(f1.variables);
            all.removeAll(f2.variables);
            all.addAll(f2.variables);
            jf.variables=all;
            List<Variable> common = new LinkedList<>(f1.variables);
            common.retainAll(f2.variables);
            for(var row:f1.rows.entrySet()){
                List<String> common_val = new LinkedList<>();
                for (Variable e :common)
                    common_val.add(firstStr(row.getKey(),e.getK()));
                for (var row2:f2.rows.entrySet()){
                    if (row2.getKey().containsAll(common_val)){
                        List<String> newR = new ArrayList<>(row.getKey());
                        newR.removeAll(row2.getKey());
                        newR.addAll(row2.getKey());
                        double p = row.getValue()*row2.getValue();
                        multi++;
                        jf.rows.put(newR,p);
                    }
                }
            }
            jf.size=jf.rows.size();
            System.out.println(jf);
            list_factors.add(jf);
            list_factors.remove(f1);
            list_factors.remove(f2);
        }
        return list_factors;
    }
    public Factor eliminate(Variable v, List<Factor> list){
        Factor f = list.get(0);
        System.out.println("*********Eliminate********");
        List<Variable> remain = f.variables;
        remain.remove(v);
        Factor ef=new Factor();
        ef.variables =remain;
        List<List<String>> l =new ArrayList<>();
        for(List<String> key:f.rows.keySet()){
            key.removeIf(s->s.startsWith(v.getK()));
            if (!l.contains(key))
                l.add(key);
        }
        for(List<String> m:l) {
            double d = 0;
            int count=0;
            for (var entry : f.rows.entrySet())
                if (entry.getKey().containsAll(m)) {
                    d += entry.getValue();
                    count++;
                }
            ef.rows.put(m,d);
            add+=count-1;
        }
        ef.size=ef.rows.size();
        System.out.println(ef);
        bn.factors.remove(f);
        return ef;
    }
    public double normal(Factor f, String req){
        List<String> l =new ArrayList<>();
        l.add(req);
        double sum=0;
        for(double n:f.rows.values())
            sum+=n;
        add+=f.size-1;
        return f.rows.get(l)/sum;
    }
    private String firstStr(List<String> list, String prefix){
        for (String s:list){
            if (s.startsWith(prefix))
                return s;
        }
        return "";
    }
}
