import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * @author audunvennesland
 * 5. sep. 2017 
 */
public class XSDImport2 {
	
	static ISub iSubMatcher = new ISub();
	

    public static void main(String args[]) { 
        try { 
            // parse the document
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document xsd1 = docBuilder.parse (new File("./files/xsd/ubl/UBL-CommonBasicComponents-2.1.xsd")); 
            NodeList xsd1_list = xsd1.getElementsByTagName("xsd:element"); 
            Document xsd2 = docBuilder.parse (new File("./files/xsd/shared/SharedCommon.xsd")); 
            NodeList xsd2_list = xsd2.getElementsByTagName("xsd:element"); 
            
            //map to hold relations above 0.5
            Map<String, String> mappingMap = new HashMap<String, String>();
            
            // match the names from the xsds using iSub
            
            for (int i = 0; i < xsd1_list.getLength(); i++) {
            	for (int j = 0; j < xsd2_list.getLength(); j++) {
            		
            		Element e1 = (Element)xsd1_list.item(i);
            		Element e2 = (Element)xsd2_list.item(j);
            		
            		if (e1.hasAttributes() && e2.hasAttributes()) {
            			String s1 = e1.getAttribute("name");
            			String s2 = e2.getAttribute("name");
            			
            			double score = iSubMatcher.score(s1, s2);

            			System.out.println("The score between " + s1 + " and " + s2 + " is " + score);
            			
            			if (score > 0.9) {
            				
            				mappingMap.put(s1, s2);
            			}
            			
            		}
            		
            	}
            }
            
            System.out.println("The mapping map contains " + mappingMap.size() + " mappings");
            
            for (Entry<String, String> e : mappingMap.entrySet()) {
            	
            	System.out.println(e.getKey() + " - " + e.getValue());
            	
            }


        } 
        catch (ParserConfigurationException e) 
        {
            e.printStackTrace();
        }
        catch (SAXException e) 
        { 
            e.printStackTrace();
        }
        catch (IOException ed) 
        {
            ed.printStackTrace();
        }
    
}

}
