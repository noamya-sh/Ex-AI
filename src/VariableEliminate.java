import java.util.*;

public class VariableEliminate {
    BayesianNetwork bn;
    List<Factor> factors;
    int add;
    int multi;
    double d;
    String answer;

//    public VariableEliminate(BayesianNetwork bn){
//        this.bn=bn;
//        this.factors= bn.copyFactors();
//        this.add=0;
//        this.multi=0;
//    }
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
        }//P(B=T|M=T) A-E
        List<Variable> hiddens = new LinkedList<>();
        if (s.length()>(indc+2)){
            String[] arr3 = s.substring(indc+2).split("-");
            for(String var : arr3)
                hiddens.add(bn.net.get(var));
        }
        this.d = getVE(query,req,evidence,hiddens);
        String result = String.format("%.5f", d);
        this.answer = result+","+add+","+multi;
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
    public void updateFactors(List<String> evidences){
        List<String> evidVar= new LinkedList<>();
        for (String s:evidences){
            int x = s.indexOf("=");
            evidVar.add(s.substring(0,x));
        }
        for (Factor g:factors) {
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
    public double getVE(Variable query,String req, List<String> evidences, List<Variable> hiddens){
//        List<Factor> copy = bn.copyFactors();//save deep copy
        List<String> evidVar= new LinkedList<>();
        for (String s:evidences){
            int x = s.indexOf("=");
            evidVar.add(s.substring(0,x));
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
    public List<Factor> getFactorsContain(Variable v){
        List<Factor> list = new ArrayList<>();
        for (Factor f:factors){
            if(f.variables.contains(v)){
                list.add(f);
            }
        }
        return list;
    }
    public List<Factor> join(Variable v){
        List<Factor> list_factors =getFactorsContain(v);
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
            factors.remove(f1);
            list_factors.remove(f2);
            factors.remove(f2);
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
        factors.remove(f);
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
