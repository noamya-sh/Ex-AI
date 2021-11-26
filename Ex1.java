import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;



public class Ex1 {
    /**This function init new Bayesian network from xml input file.**/
    public BayesianNetwork init(String path) {
        try {
            File fXmlFile = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("VARIABLE");
            BayesianNetwork bn = new BayesianNetwork();
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    Variable n = new Variable(eElement.getElementsByTagName("NAME").item(0).getTextContent());

                    for (int i=0;i<eElement.getElementsByTagName("OUTCOME").getLength();i++)
                        n.addOutCome(eElement.getElementsByTagName("OUTCOME").item(i).getTextContent());
                    bn.addVariable(n);
                }
            }
            NodeList nList2 = doc.getElementsByTagName("DEFINITION");
            for (int temp = 0; temp < nList2.getLength(); temp++) {
                Node nNode = nList2.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String var =eElement.getElementsByTagName("FOR").item(0).getTextContent();
                    Variable n = bn.net.get(var);
                    List<Variable> list = new ArrayList<>();

                    if (eElement.getElementsByTagName("GIVEN").getLength()>0){
                        LinkedList<String> p =new LinkedList<>();
                        for (int i=0;i<eElement.getElementsByTagName("GIVEN").getLength();i++){
                            String t = eElement.getElementsByTagName("GIVEN").item(i).getTextContent();
                            p.add(t);
                            list.add(bn.net.get(t));
                        }
                        bn.updateParent(n,p);//insert parents of this variable
                    }

                    String t =eElement.getElementsByTagName("TABLE").item(0).getTextContent();
                    list.add(n);
                    bn.addFactor(new Factor(list,t));
                }
            }
            return bn;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**This function read input file, sends queries to algorithms Baseball and Variable Eliminate,
     * and print the answers to output file.**/
    public void readWrite(){
        try{
            List<String> all = Files.readAllLines(Paths.get("input.txt"));
            String xml=  all.remove(0);
            BayesianNetwork bn = init(xml);
            List<String> out= new LinkedList<>();
            for(String q:all){
                if (q.startsWith("P")){
                    VariableEliminate v = new VariableEliminate(bn,q);
                    out.add(v.getAnswer());
                }
                else {
                    Baseball b = new Baseball(bn,q);
                    out.add(b.getAnswer());
                }
            }
            FileWriter writer = new FileWriter("output.txt");
            String last = out.remove(out.size()-1);
            for(String ans: out) {
                writer.write(ans + System.lineSeparator());
            }
            writer.write(last);
            writer.close();
        }
        catch (Exception e){ System.out.print(e);}

    }
    public static void main(String[] args) throws IOException {
        Ex1 e = new Ex1();
        e.readWrite();
    }
}
