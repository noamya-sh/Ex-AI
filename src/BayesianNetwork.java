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
        LinkedList<String> evidences = new LinkedList<>();
        if (arr1.length>1) {
            String[] arr3 = arr1[1].split("=|,");
            for (String str : arr3) {
                if (net.containsKey(str))
                    evidences.add(str);
            }
        }
        LinkedList<String> visited = BaseBall(arr2[0],evidences);
        if (visited.contains(arr2[1]))
            return "no";
        else
            return "yes";
    }

    private LinkedList<String> BaseBall(String var1,LinkedList<String> evidence){
        LinkedList<String> visited = new LinkedList<>();
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
    public void addFactor(Factor f){
        this.factors.put(f.variables,f);
    }
//    public Boolean BaseBall(String var1, String var2){
//
//        return false;
//    }
}
