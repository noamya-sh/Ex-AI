import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Baseball {
    private BayesianNetwork bn;
    private String answer;
    private List<String> visited;

    public Baseball(BayesianNetwork bn){
        this.bn=bn;
    }
    public Baseball(BayesianNetwork bn, String s){
        this.bn =bn;
        String[] arr1 = s.split("\\|");
        String[] arr2 = arr1[0].split("-");
        List<String> evidences = new LinkedList<>();
        if (arr1.length>1) {
            String[] arr3 = arr1[1].split("=|,");
            for (String str : arr3) {
                if (bn.net.containsKey(str))
                    evidences.add(str);
            }
        }
        List<String> visited = getPath(arr2[0],evidences);
        if (visited.contains(arr2[1]))
            this.answer = "no";
        else
            this.answer = "yes";
    }

    public List<String> getPath(String var1, List<String> evidence){
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
                    addList(toVisit,from,bn.net.get(s).getParents(),"from_child");
                }
                if (!Bottom.contains(s)){
                    Bottom.add(s);
                    addList(toVisit,from,bn.net.get(s).getChilds(),"from_parent");
                }
            }
            if (f== "from_parent"){
                if (evidence.contains(s) && !Top.contains(s)){
                    Top.add(s);
                    addList(toVisit,from,bn.net.get(s).getParents(),"from_child");
                }
                if (!evidence.contains(s) && !Bottom.contains(s)){
                    Bottom.add(s);
                    addList(toVisit,from,bn.net.get(s).getChilds(),"from_parent");
                }
            }
        }
        return visited;
    }
    private void addList(Queue<String> ex, Queue<String> ex2,LinkedList<String> ad, String from){
        for (String s:ad){
            ex.add(s);
            ex2.add(from);
        }
    }
}
