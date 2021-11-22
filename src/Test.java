import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class Test {


    public BayesianNetwork init(String path) {
        try {
            File fXmlFile = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

//            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getElementsByTagName("VARIABLE");
//            System.out.println("----------------------------");
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
//            for (int temp = 0; temp < nList.getLength(); temp++) {
//                Node nNode = nList.item(temp);
////                System.out.println("\nCurrent Element :" + nNode.getNodeName());
//                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
//                    Element eElement = (Element) nNode;
//                    System.out.println("name : "
//                            + eElement.getElementsByTagName("NAME")
//                            .item(0).getTextContent());
//                    for (int i=0;i<eElement.getElementsByTagName("OUTCOME").getLength();i++)
//                        System.out.println("outcome "+(i+1)+": " + eElement.getElementsByTagName("OUTCOME")
//                                .item(i).getTextContent());
//                }
//            }
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
                    list.add(n);
                    String p =eElement.getElementsByTagName("TABLE").item(0).getTextContent();
                    bn.addFactor(new Factor(list,p));
                }
            }return bn;
//            NodeList nList2 = doc.getElementsByTagName("DEFINITION");
//            System.out.println("----------------------------");
//            for (int temp = 0; temp < nList2.getLength(); temp++) {
//                Node nNode = nList2.item(temp);
////                System.out.println("\nCurrent Element :" + nNode.getNodeName());
//                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
//                    Element eElement = (Element) nNode;
//                    System.out.println("for : "
//                            + eElement.getElementsByTagName("FOR")
//                            .item(0).getTextContent());
//                    if (eElement.getElementsByTagName("GIVEN").getLength()>0)
//                        for (int i=0;i<eElement.getElementsByTagName("GIVEN").getLength();i++)
//                            System.out.println("given "+(i+1)+": " + eElement.getElementsByTagName("GIVEN")
//                                    .item(i).getTextContent());
//                    System.out.println("table: " + eElement.getElementsByTagName("TABLE")
//                            .item(0).getTextContent()+"\n");
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        Test t = new Test();
        BayesianNetwork bn =t.init("src/alarm_net.xml");
        System.out.println(bn.getFactorsContain(bn.net.get("P")).size());
//        for(int i = 0; i<bn.factors.size();i++)
//            System.out.println(bn.factors.size());
//        Variable A = new Variable("A");
//        A.addOutCome("T");A.addOutCome("F");
//        Variable B = new Variable("B");
//        B.addOutCome("T");B.addOutCome("F");
//        Variable E = new Variable("E");
//        E.addOutCome("T");E.addOutCome("F");
//
//        List<Variable> list = new ArrayList<>();
//        list.add(E);list.add(B);list.add(A);
//        Factor f = new Factor(list,"0.95 0.05 0.29 0.71 0.94 0.06 0.001 0.999");
//        List<String> list2 = new ArrayList<>();
//        list2.add("B=T");list2.add("E=T");list2.add("A=T");
//        System.out.println(f.rows.get(list2));
        System.out.println(bn.checkIndeapendes("B-A|J=T,M=F"));
//        String prob ="I am noam ya shani";
//        String[] arrp = prob.split("\\s+");
//        for (int i = 0; i<arrp.length;i++){
//            System.out.println(arrp[i]);
//        }

    }
}
