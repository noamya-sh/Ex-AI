import java.util.LinkedList;

public class Variable {
    private String k;
    private LinkedList<String> parents;
    private LinkedList<String> childs;
    private LinkedList<String> outcome;

    public Variable(String k, LinkedList<String> parents, LinkedList<String> childs, LinkedList<String> outcome) {
        this.k = k;
        this.parents = parents;
        this.childs = childs;
        this.outcome = outcome;
    }

    public Variable(String s){
        this.outcome =new LinkedList<>();
        this.parents=new LinkedList<>();
        this.childs=new LinkedList<>();
        this.k =s;
    }
    //Copy constructor
    public Variable(Variable other){
        this.k = other.getK();
        LinkedList<String> c= new LinkedList<>();
        for(String child: other.childs){
            c.add(child);
        }
        this.childs=c;
        LinkedList<String> p= new LinkedList<>();
        for(String parent: other.parents){
            p.add(parent);
        }
        this.parents=p;
        LinkedList<String> o= new LinkedList<>();
        for(String out: other.outcome){
            o.add(out);
        }
        this.outcome=o;

    }

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public LinkedList<String> getParents() {
        return parents;
    }

    public void setParents(LinkedList<String> parents) {
        this.parents = parents;
    }

    public LinkedList<String> getChilds() {
        return childs;
    }

    public void setChilds(LinkedList<String> childs) {
        this.childs = childs;
    }

    public LinkedList<String> getOutcome() {
        return outcome;
    }

    public void setOutcome(LinkedList<String> outcome) {
        this.outcome = outcome;
    }

    public void addOutCome(String o){
        this.outcome.add(o);
    }

    public void addChild(String var) {
        if (!childs.contains(var))
            childs.add(var);
    }
}
