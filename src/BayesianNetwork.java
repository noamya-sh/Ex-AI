import java.util.*;

public class BayesianNetwork {
    HashMap<String, Variable> net;
    List<Factor> cpt;

    public BayesianNetwork(){
        this.net=new HashMap<>();
        this.cpt=new LinkedList<>();
    }

    public void addFactor(Factor f){
        this.cpt.add(f);
    }
    public List<Factor> copyFactors(){
        List<Factor> copy = new LinkedList<>();
        for (Factor f:cpt)
            copy.add(new Factor(f));
        return copy;
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

}
