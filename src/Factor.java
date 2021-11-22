import java.util.*;

public class Factor {
    List<Variable> variables;
    HashMap<List<String>,Double> rows;
    int size;

    public Factor(List<Variable> variables,String prob){
        this.variables=variables;
        this.rows=new HashMap<>();
        add(variables,prob);
    }
    //copy constructor
    public Factor(Factor other){
        List<Variable> v =new ArrayList<>();
        for (Variable var: other.variables) {
            v.add(var);
        }
        this.size=other.size;
        this.variables=v;
        HashMap<List<String>,Double> r = new HashMap<>();
        for (var entry : rows.entrySet()) {
            List<String> list = new ArrayList<>();
            for(String s:entry.getKey())
                list.add(s);
            double d= entry.getValue();
            r.put(list,d);
        }
    }

    public void add(List<Variable> variables,String prob) {
        ArrayList<List<String>> arr = new ArrayList<>(); //contain all rows
        int options = getNumOpt(variables , 0),k=0;
        this.size=options;
        while (k<options){ //init all lists
            List<String> list = new ArrayList<>();
            arr.add(list);
            k++;
        }
        int sv = variables.size(), j = 0;
        while (j < sv) {
            k=0;
            Variable v = variables.get(j);
            int so = v.getOutcome().size(), i = 0,l;
            int nextOptions = getNumOpt(variables, j+1);
            int prevOptions = getNumOptF(variables, j);
            while (i < prevOptions) {
                l=0;
                while (l<so){
                    int m=0;
                    while (m<nextOptions){
                        String s = v.getK()+"="+v.getOutcome().get(l);
                        arr.get(k++).add(s);
                        m++;
//                        condition c = new condition(v.getK(), v.getOutcome().get(i));
                    }
                    l++;
                }
                i++;
            }
            j++;
        }
        int z=0;
        String[] arrp = prob.split("\\s+");
        for(List<String> list:arr){
            rows.put(list, Double.valueOf(arrp[z]));
            System.out.println(Arrays.toString(list.toArray())+" ["+arrp[z++]+"]");
        }
    }
    private int getNumOpt(List<Variable> variables , int ind){
        int i =1;
        while (ind < variables.size())
            i*=variables.get(ind++).getOutcome().size();
        return i;
    }
    private int getNumOptF(List<Variable> variables , int ind){
        int i =1,j=0;
        while (j<ind)
            i*=variables.get(j++).getOutcome().size();
        return i;
    }

    public void eliminate(Variable var) {

    }
}
