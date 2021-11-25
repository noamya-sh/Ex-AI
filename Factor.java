import java.util.*;

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
        for (var entry : other.rows.entrySet()) {
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
    public void add(List<Variable> variables,String prob) {
        ArrayList<List<String>> arr = new ArrayList<>(); //contain all rows
        int options = getNumOpt(variables , 0),k=0;
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
            rows.put(list, Double.valueOf(arrp[z++]));
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
        for(var en:rows.entrySet())
            s+=Arrays.toString(en.getKey().toArray())+" "+en.getValue()+"\n";
        return s;
    }

}
