import java.util.*;

/**Factor contains a table that has probabilities for conditions. **/
public class Factor implements Comparable<Factor>{
    List<Variable> variables;
    HashMap<List<String>,Double> rows;


    public Factor(){
        this.rows=new HashMap<>();
        this.variables=new LinkedList<>();

    }
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
        this.variables=v;
        HashMap<List<String>,Double> r = new HashMap<>();
        for (Map.Entry<List<String>, Double> entry : other.rows.entrySet()) {
            List<String> list = new ArrayList<>();
            for(String s:entry.getKey())
                list.add(s);
            double d= entry.getValue();
            r.put(list,d);
        }
        this.rows=r;
    }
    public int size(){
        return rows.size();
    }

    /**This function init cpt for each variable in bayesian network.
     * @param variables all variables in bayesianNetwork.
     * @param prob string containing all the probabilitiesץ**/
    public void add(List<Variable> variables,String prob) {
        ArrayList<List<String>> arr = new ArrayList<>(); //contain all rows
        int options = getNumOptForward(variables , 0),k=0;
        while (k<options){ //init all lists
            List<String> list = new ArrayList<>();
            arr.add(list);
            k++;
        }
        /**Some loops that insert conditions according to the outcomes of each variable.
         * In each column - enter the outcome in sequence as the sum of the outcomes of the
         * variables in the following columns, and perform a new round as the sum of the outcomes
         * of the variables in the previous columns
         *For example, in the case where there are 3 variables in the factor, each of which has 2 outcomes (true and false):
         * A=T | B=T | C=T | 0.xxx |
         * A=T | B=T | C=F | 0.xxx |
         * A=T | B=F | C=T | 0.xxx |
         * A=T | B=F | C=F | 0.xxx |
         * A=F | B=T | C=T | 0.xxx |
         * A=F | B=T | C=F | 0.xxx |
         * A=F | B=F | C=T | 0.xxx |
         * A=F | B=F | C=F | 0.xxx |*/
        int sv = variables.size(), j = 0;
        while (j < sv) {
            k=0;
            Variable v = variables.get(j);
            int so = v.getOutcome().size(), i = 0,l;
            int nextOptions = getNumOptForward(variables, j+1);
            int prevOptions = getNumOptBackward(variables, j);
            while (i < prevOptions) {
                l=0;
                while (l<so){
                    int m=0;
                    while (m<nextOptions){
                        String s = v.getK()+"="+v.getOutcome().get(l);
                        arr.get(k++).add(s);
                        m++;
                    }
                    l++;
                }
                i++;
            }
            j++;
        }
        int z=0;
        //put in hash list of condition line as key, and probability as value.
        String[] arrp = prob.split("\\s+");
        for(List<String> list:arr){
            rows.put(list, Double.valueOf(arrp[z++]));
        }
    }
    /**@return options number of outcomes of variables further down the list.**/
    private int getNumOptForward(List<Variable> variables , int ind){
        int i =1;
        while (ind < variables.size())
            i*=variables.get(ind++).getOutcome().size();
        return i;
    }
    /**@return options number of outcomes of variables from beginning of the list.**/
    private int getNumOptBackward(List<Variable> variables , int ind){
        int i = 1,j = 0;
        while (j<ind)
            i*=variables.get(j++).getOutcome().size();
        return i;
    }
    /**@return sum ascii values of variables names**/
    private int ascii(List<Variable> list){
        String s="";
        int ascii=0;
        for(Variable v:list)
            s+=v.getK();
        for(char c:s.toCharArray())
            ascii+=(int)c;
        return ascii;
    }

    @Override
    /**comparing by size of rows in factor and than by ascii value of the variables in factor**/
    public int compareTo(Factor o) {
        return Comparator.comparing((Factor f)->f.size())
                .thenComparing(f->f.ascii(f.variables))
                .compare(this, o);
    }
    public String toString(){
        String s="Variable: ";
        for (Variable v:variables){
            s+=v.getK()+" ";
        }
        s+="\n";
        for(Map.Entry<List<String>, Double> en:rows.entrySet())
            s+=Arrays.toString(en.getKey().toArray())+" "+en.getValue()+"\n";
        return s;
    }

}
