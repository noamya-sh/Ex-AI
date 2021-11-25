import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;



public class Ex1 {

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
                        bn.updateParent(n,p);
                    }

                    String p =eElement.getElementsByTagName("TABLE").item(0).getTextContent();
                    list.add(n);
                    bn.addFactor(new Factor(list,p));
                }
            }
            return bn;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public void readWrite() throws IOException {

        List<String> all = Files.readAllLines(Paths.get("src/input.txt"));
        String xml=  all.remove(0);
        BayesianNetwork bn = init(xml);

    }
    public static void main(String[] args) throws IOException {
//        Ex1 e = new Ex1();
//        e.readWrite();

        Ex1 t = new Ex1();
//
        BayesianNetwork bn =t.init("src/alarm_net.xml");
        VariableEliminate v =new VariableEliminate(bn,"P(B=T|J=T,M=T)");
        System.out.println(v.answer);
        VariableEliminate v1 =new VariableEliminate(bn,"P(B=T|J=T,M=T) E-A");
        System.out.println(v1.answer);
//        List<String> st =new ArrayList<>();
//        st.add("C3=T");st.add("B2=F");st.add("C2=v3");
//        List<Variable> va = new ArrayList<>();
//        //A2-D1-B3-C1-A1-B1-A3
////        P(B0=v3|C3=T,B2=F,C2=v3) A2-D1-B3-C1-A1-B1-A3 //check
////        P(A2=T|C3=T,B2=F,C2=v3) D1-B3-C1-B0-A1-B1-A3
////        P(A2=T|C2=v1) D1-C1-B0-A1-B1-A3-C3-B2-B3
////        P(D1=T|C2=v1,C3=F) A2-C1-B0-A1-B1-A3-B2-B3
////        A2-D1|C3=T,B2=F,C2=v3
////        A2-B3|C3=T,B2=F,C2=v3
////        A2-C1|C3=T,B2=F,C2=v3
////        A2-B0|C3=T,B2=F,C2=v3
////        A2-A1|C3=T,B2=F,C2=v3
////        C2-A3|B3=T,C1=T
////        B0-C2|A2=T,A3=T
////        A1-D1|C3=T,B2=F,B3=F
//        va.add(bn.net.get("A2"));va.add(bn.net.get("D1"));va.add(bn.net.get("B3"));va.add(bn.net.get("C1"));va.add(bn.net.get("A1"));
//        va.add(bn.net.get("B1"));va.add(bn.net.get("A3"));
//        double d = v.variableEliminate(bn.net.get("B0"),"B0=v3",st,va);
//        String result = String.format("%.5f", d);
//        System.out.println(result+" add:"+v.add+" mul:"+v.multi);


    }
}
