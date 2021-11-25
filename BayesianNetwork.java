import java.util.*;

public class BayesianNetwork {
    HashMap<String, Variable> net;
    private List<Factor> cpt;

    public BayesianNetwork(){
        this.net=new HashMap<>();
        this.cpt=new LinkedList<>();
    }

    public void addFactor(Factor f){
        this.cpt.add(f);
    }

    /**@return deep-copy of cpt list**/
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
    /**update parents for each varisble**/
    public void updateParent(Variable n, LinkedList<String> list){
        n.setParents(list);
        for(String s:list){
            Variable a = net.get(s);
            a.addChild(n.getK());
        }
    }
    /**This function check if variable is ancestor of other variables.
     * @param hidden  variable to check.
     * @param parents  list of variable to check if contain h variable
     * @return true if parents list contain specific variable**/
    public boolean isAncestor(Variable hidden, List<String> parents) {
       if (parents.contains(hidden.getK()))
           return true;
       if (parents.size()==0)
            return false;
       boolean b = false;
       for (String s:parents){
           b = b || isAncestor(hidden,net.get(s).getParents());
       }
       return b;
    }

}
