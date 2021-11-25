import java.util.*;

public class VariableEliminate {
    private BayesianNetwork bn;
    private List<Factor> factors;
    private int add;
    private int multi;
    private double d;
    private String answer;

    public VariableEliminate(BayesianNetwork bn,String s){
        this.bn=bn;
        this.factors= bn.copyFactors();
        this.add=0;
        this.multi=0;
        int indc = s.indexOf(")");
        String question = s.substring(2,indc);
        String req = question.substring(0,question.indexOf("|"));
        Variable query =bn.net.get(req.substring(0,req.indexOf("=")));
        String[] arr1 = question.split("\\|");
        List<String> evidence = new LinkedList<>();
        if(arr1.length>1){
            String[] arr2 = arr1[1].split(",");
            for(String e:arr2)
                evidence.add(e);
        }
        List<Variable> hiddens = new LinkedList<>();
        if (s.length()>(indc+2)){
            String[] arr3 = s.substring(indc+2).split("-");
            for(String var : arr3)
                hiddens.add(bn.net.get(var));
        }
        this.d = getVE(query,req,evidence,hiddens);
        String[] split = Double.toString(d).split("\\.");
        String result;
        if (split[1].length()>5)
            result = String.format("%.5f", d);
        else
            result = Double.toString(d);
        this.answer = result+","+add+","+multi;
    }

    public String getAnswer() {
        return answer;
    }

    private void removeFactorsContain(Variable v){
        Iterator i = factors.iterator();
        while (i.hasNext()){
            Factor f = (Factor) i.next();
            if (f.variables.contains(v))
               i.remove();
        }
    }
    private List<Variable> updateHiddens(Variable query, List<String> evidences, List<Variable> hiddens){
        //check independence
        Baseball b =new Baseball(this.bn);
        List<String> visited =b.getPath(query.getK(),evidences);
        Iterator i = hiddens.iterator();
        while (i.hasNext()){
            Variable v= (Variable)i.next();
            if (!visited.contains(v.getK())) {
                removeFactorsContain(v);
                i.remove();
            }
        }
        //check for each hidden variable if is ancestor
        List<String> list = new ArrayList<>(evidences);
        list.add(query.getK());
        Iterator it = hiddens.iterator();
        while (it.hasNext()){
            Variable v= (Variable)it.next();
            if(!bn.isAncestor(v,list)) {
                removeFactorsContain(v);
                it.remove();
            }
        }
        return hiddens;
    }
    private List<String> getVarListFromString(List<String> evidences){
        List<String> evidVar= new LinkedList<>();
        for (String s:evidences){
            int x = s.indexOf("=");
            evidVar.add(s.substring(0,x));
        }
        return evidVar;
    }
    private void updateFactors(List<String> evidences){
        List<String> evidVar= getVarListFromString(evidences);
        for (Factor g:factors) {
            for (String s : evidVar) {
                if (g.variables.contains(bn.net.get(s))) {
                    String r = firstStr(evidences, s);
                    g.rows.entrySet().removeIf(row -> !row.getKey().contains(r));
                    //remove this 'given' from each row contain it.
                    for(var key:g.rows.entrySet())
                        key.getKey().remove(r);
                    g.variables.remove(bn.net.get(s));
                }
            }
        }
    }
    private double getVE(Variable query,String req, List<String> evidences, List<Variable> hiddens){
        List<String> evidVar= getVarListFromString(evidences);
        //check if exist cpt contain the answer.
        List<Variable> l = new LinkedList<>();
        evidVar.stream().iterator().forEachRemaining(f-> l.add(bn.net.get(f)));
        l.add(query);
        for(Factor f:factors)
            if (f.variables.equals(l)){
                List<String> cor = new LinkedList<>(evidences);
                cor.add(req);
                for (var entry:f.rows.entrySet())
                    if (entry.getKey().containsAll(cor))
                        return entry.getValue();
            }
        hiddens = updateHiddens(query,evidVar,hiddens);//delete hidden variables not relevant
        updateFactors(evidences);//clean factors by given
        for (Variable v:hiddens){
            List<Factor> j = join(v);
            Factor f = eliminate(v,j);
            factors.add(f);
        }
        List<Factor> j = join(query);
//        System.out.println(j.get(0));
        return normal(j.get(0),req);
    }
    private List<Factor> getFactorsContain(Variable v){
        List<Factor> list = new ArrayList<>();
        for (Factor f:factors){
            if(f.variables.contains(v)){
                list.add(f);
            }
        }
        return list;
    }
    private List<Factor> join(Variable v){
        List<Factor> list_factors =getFactorsContain(v);
        if (list_factors.size()<=1)
            return list_factors;
        for(Factor f:list_factors)
            if (f.size()==1)
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
            System.out.println(jf);
            list_factors.add(jf);
            list_factors.remove(f1);
            factors.remove(f1);
            list_factors.remove(f2);
            factors.remove(f2);
        }
        return list_factors;
    }
    private Factor eliminate(Variable v, List<Factor> list){
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
        System.out.println(ef);
        factors.remove(f);
        return ef;
    }
    private double normal(Factor f, String req){
        List<String> l =new ArrayList<>();
        l.add(req);
        double sum=0;
        for(double n:f.rows.values())
            sum+=n;
        add+=f.size()-1;
        double d=0;
        for (var entry:f.rows.entrySet())
            if (entry.getKey().equals(l))
                d = entry.getValue();
        return d/sum;
    }
    private String firstStr(List<String> list, String prefix){
        for (String s:list){
            if (s.startsWith(prefix))
                return s;
        }
        return "";
    }
}
