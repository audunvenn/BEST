import java.io.File;
import java.io.FileInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;



/**
 * @author audunvennesland
 * 5. sep. 2017 
 */
public class XSDParser {
	
	public static void parse(String id) {
	    try {
	        // Setup classes to parse XSD file for complex types
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        //File schemaFile = new File("./files/xsd/Item.xsd");
	        Document doc = db.parse(new FileInputStream("./files/xsd/Test.xsd"));

	        // Given the id, go to correct place in XSD to get all the parameters
	        XPath xpath = XPathFactory.newInstance().newXPath();
	        NodeList result = (NodeList) xpath.compile(getExpression(id)).evaluate(doc, XPathConstants.NODESET);

	        for(int i = 0; i < result.getLength(); i++) 
	        {
	            Element e = (Element) result.item(i);
	            System.out.println(e.getAttribute("name") + " = " + e.getNodeValue());
	        }

	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	}

	// Get XSD Expression
	private static String getExpression(String id) {
	    String expression = "";

	    switch(id)
	    {
	    case "99":
	        expression = "//complexType[@name='SomethingOne']//element";
	        ////complexType[@name='SomethingOne']//element
	        break;

	    default:
	        System.out.println("\n Invalid id");
	        break;
	    }

	    return expression;
	}
	
	public static void main(String[] args) {
		
		parse("99");
	}

}
