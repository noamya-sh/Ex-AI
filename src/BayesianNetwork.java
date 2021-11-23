import java.util.*;

public class BayesianNetwork {
    HashMap<String, Variable> net;
    HashMap<List<Variable>,Factor> factors;

    public BayesianNetwork(){
        this.net=new HashMap<>();
        this.factors=new HashMap<>();
    }

    public List<Factor> getFactorsContain(Variable v){
        List<Factor> list = new ArrayList<>();
        for (var entry : factors.entrySet()){
            if(entry.getKey().contains(v)){
                list.add(entry.getValue());
            }
        }
        return list;
    }
    public void addFactor(Factor f){
        this.factors.put(f.variables,f);
    }
    private HashMap<List<Variable>,Factor> copyFactors(){
        HashMap<List<Variable>,Factor> copy = new HashMap<>();
        for (var entry:factors.entrySet())
            copy.put(entry.getKey(), new Factor(entry.getValue()));
        return copy;
    }
    public void addVariable(String s){
        Variable n = new Variable(s);
        if (!net.containsKey(s))
            net.put(s,n);
    }
    public void addVariable(Variable n){
        if (!net.containsKey(n.getK()))
            net.put(n.getK(),n);
    }
    public void updateParent(Variable n, LinkedList<String> list){
        n.setParents(list);
        for(String s:list){
            Variable a = net.get(s);
            a.addChild(n.getK());
        }
    }
    public String checkIndeapendes(String s){
        String[] arr1 = s.split("\\|");
        String[] arr2 = arr1[0].split("-");
        List<String> evidences = new LinkedList<>();
        if (arr1.length>1) {
            String[] arr3 = arr1[1].split("=|,");
            for (String str : arr3) {
                if (net.containsKey(str))
                    evidences.add(str);
            }
        }
        List<String> visited = BaseBall(arr2[0],evidences);
        if (visited.contains(arr2[1]))
            return "no";
        else
            return "yes";
    }

    private List<String> BaseBall(String var1,List<String> evidence){
        List<String> visited = new LinkedList<>();
        LinkedList<String> Bottom = new LinkedList<>();
        LinkedList<String> Top = new LinkedList<>();
        Queue<String> toVisit = new LinkedList<>();
        Queue<String> from = new LinkedList<>();
        toVisit.add(var1);
        from.add("from_child");
        while (!toVisit.isEmpty()){
            String s = toVisit.poll();
            String f = from.poll();
            visited.add(s);
            if (!evidence.contains(s) && f =="from_child"){
                if (!Top.contains(s)){
                    Top.add(s);
                    addList(toVisit,from,this.net.get(s).getParents(),"from_child");
                }
                if (!Bottom.contains(s)){
                    Bottom.add(s);
                    addList(toVisit,from,this.net.get(s).getChilds(),"from_parent");
                }
            }
            if (f== "from_parent"){
                if (evidence.contains(s) && !Top.contains(s)){
                    Top.add(s);
                    addList(toVisit,from,this.net.get(s).getParents(),"from_child");
                }
                if (!evidence.contains(s) && !Bottom.contains(s)){
                    Bottom.add(s);
                    addList(toVisit,from,this.net.get(s).getChilds(),"from_parent");
                }
            }
        }
        return visited;
    }
    private void addList(Queue<String> ex, Queue<String> ex2,LinkedList<String> ad, String from){
        for (String s:ad){
//            if (!ex.contains(s))
            ex.add(s);
            ex2.add(from);
        }
    }
    private void updateHiddens(Variable query, List<String> evidences, List<Variable> hiddens){
        //check independence
        List<String> visited = BaseBall(query.getK(),evidences);
        for(Variable v:hiddens){
            if (!visited.contains(v.getK()))
                hiddens.remove(v);
        }
        //check for each hidden variable if is ancestor
        List<String> list = new ArrayList<>(evidences);
        list.add(query.getK());
        for(Variable v:hiddens){
            if(!isAncestor(v,list))
                hiddens.remove(v);
        }
    }

    public boolean isAncestor(Variable h, List<String> parents) {
       if (parents.contains(h.getK()))
           return true;
       if (parents.size()==0)
            return false;
       boolean b = false;
       for (String s:parents){
           b = b || isAncestor(h,net.get(s).getParents());
       }
       return b;
    }
    public void updateFactors(List<String> evidences){
        List<String> evidVar= new LinkedList<>();
        for (String s:evidences){
            int x = s.indexOf("=");
            evidVar.add(s.substring(0,x));
        }
        for (var entry: factors.entrySet()) {
            Factor g = entry.getValue();
            for (String s : evidVar) {
                if (g.variables.contains(net.get(s))) {
                    String r = firstStr(evidences, s);
                    g.rows.entrySet().removeIf(row -> !row.getKey().contains(r));
                    for(var key:g.rows.entrySet())
                        key.getKey().remove(r);
                    g.variables.remove(net.get(s));
                    g.size=g.rows.size();
                }
            }
        }
    }
    public double variableEliminate(Variable query, List<String> evidences, List<Variable> hiddens){
        List<String> evidVar= new LinkedList<>();
        for (String s:evidences){
            int x = s.indexOf("=");
            evidVar.add(s.substring(0,x));
        }
        updateHiddens(query,evidVar,hiddens);
        HashMap<List<Variable>,Factor> copy = copyFactors();
        updateFactors(evidences);

//            if (f.variables.contains())
//            for (String e : evidences) {
//                List<String>
//                if (entry.getKey().contains(e))
//            }

//        int j=0;
//        while (j<hiddens.length){
//            joinAndEliminate(hiddens[j]);
//        }

        return 0;
    }
    private String firstStr(List<String> list, String prefix){
        for (String s:list){
            if (s.startsWith(prefix))
                return s;
        }
        return "";
    }
}
