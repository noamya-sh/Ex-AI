import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.ArrayList;
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

    public static void main(String[] args) {
        Test t = new Test();
        BayesianNetwork bn =t.init("src/alarm_net.xml");
        for (Factor f:bn.factors.values())
            System.out.println(f);
        System.out.println("***************************************************************");
        List<String> e= new LinkedList<>();
        e.add("J=T");e.add("M=T");
        bn.updateFactors(e);
        for (Factor f:bn.factors.values())
            System.out.println(f);
//        List<String> l = new LinkedList<>();
//        l.add("C1");l.add("C2");
//        System.out.println(bn.isAncestor(bn.net.get("A2"),l));
//        System.out.println(bn.getFactorsContain(bn.net.get("P")).size());
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
        //System.out.println(bn.checkIndeapendes("B-A|J=T,M=F"));
//        String prob ="I am noam ya shani";
//        String[] arrp = prob.split("\\s+");
//        for (int i = 0; i<arrp.length;i++){
//            System.out.println(arrp[i]);
//        }

    }
}
