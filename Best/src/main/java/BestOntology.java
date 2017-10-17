
import java.io.*;
import java.util.*;


import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset.Entry;




public class BestOntology  {

	public static final IRI best_iri = IRI.create("http://www.best.com/best.owl");
/*	PrefixManager pm = new DefaultPrefixManager(
			"http://www.semanticweb.org/owlapi/ontologies/ontology#");*/
	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	
	//OWLOntology ontology = manager.createOntology(best_iri);
	

	OWLDataFactory df = OWLManager.getOWLDataFactory();
	OWLDeclarationAxiom declarationAxiom = null;
	static SAXBuilder sax = new SAXBuilder();


	public BestOntology(){}

	/*JDOM methods*/

	/**
	 * Returns a List of Elements from an XMI input file
	 * @param inputFile an XMI-file containing nodes named "elements"
	 * @return a List of element names
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static List<Element> getElements(File inputFile) throws JDOMException, IOException {
		Document doc = sax.build(inputFile);

		Element root = doc.getRootElement();
		Element elementsNode = root.getChild("elements");
		List<Element> elementList = elementsNode.getChildren();

		return elementList;
	}

	/**
	 * Returns a List of connector nodes (source and target) and their relation as a 3-dimensional array (String, String, String)
	 * @param inputFile an XMI file generated from UML
	 * @return a 3-dimensional array containing source, target, and relationship type
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static List<Element> getConnectors(File xmi) throws JDOMException, IOException {
		Document doc = sax.build(xmi);
		Element root = doc.getRootElement();
		Element connectorsNode = root.getChild("connectors");
		List<Element> connectorsList = connectorsNode.getChildren();

		return connectorsList;

	}

	/*OWL API Methods*/

/*	public void createOntology() throws OWLOntologyCreationException {
		OWLOntology ontology = manager.createOntology(best_iri);
	}*/


/*	private String getSubjectField(String input) {

		String subjectField = null;

		return subjectField;

	}*/
	
	public static OWLOntology createAIRMOntology(File xmi) throws OWLOntologyCreationException, JDOMException, IOException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory df = OWLManager.getOWLDataFactory();
		OWLOntology ontology = manager.createOntology(best_iri);
		
		//get the elements
		List<Element> airmAllElements = getElements(xmi);
		//remove unwanted elements
		ArrayList<String> airmFilteredElements = removeElements(airmAllElements);
		//get the connectors
		List<Element> airmAllConnectors = getConnectors(xmi);
		
		
		
		return ontology;
		
	}
	
	/**
	 * This method iterates through each "element" and creates an OWLClass. In the next step, it checks if the newly created
	 * OWLClass has the same name as connector.source@name and if the connector@property="Generalization". If this is the case
	 * then a subClassOf axiom is created where the connector.target@name is the superclass.
	 * @return
	 *//*
	private OWLClass createClassAndSubClasses(List<Element> e) {
	
	
	
	}
	*//**
	 * Start by checking if element.attributes.attribute.properties@type="Boolean". 

	 * @return
	 *//*
	private OWLClass createOWLClassFromBooleanAttribute() {
		
	}
	
	private OWLObjectProperty createOWLOPFromUMLAssociation() {
		
	}
	
	private OWLObjectProperty createOWLOPFromUMLAttribute() {
		
	}

	private OWLObjectProperty createOWLOPFromUMLAggregation() {
	
	}
	
	private OWLDataProperty createOWLDPFromUMLAttribute() {
		
	}
	
	private OWLAxiom createSubClassAxiomFromUMLGeneralization() {
		
	}
	
	private OWLNamedIndividual createIndividualFromUMLCodeList() {
		
	}
	
	private Boolean isAggregation(Element connector) {
	
	}
	
	private Boolean isBoolean(Element element) {
	
	}
	
	private Boolean isPackage (Element element) {
	
	}
	
	private Boolean isComplexDataType(Element element) {
	
	}
	
	private String getOPName (Element element) {
	
	}
	
	private String getRangeClass (Element connector) {
	
	}
	
	private String getDomainClass (Element connector) {
	
	}
	
	
	
	
	
	*/

	private static void getClasses(List<Element> elements) {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory df = OWLManager.getOWLDataFactory();

		String className = null;

		for (Element e : elements) {
			className = "#" + e.getName();
			OWLEntity entity = df.getOWLEntity(EntityType.CLASS, IRI.create(className));
		}

	}
	
	
	
	private OWLClass createClassFromElement (OWLOntology ontology, String inputElement) {
		
		OWLClass cls = df.getOWLClass(IRI.create(best_iri + "#" + inputElement));
		declarationAxiom = df.getOWLDeclarationAxiom(cls);
		manager.addAxiom(ontology, declarationAxiom);
		return cls;
	}

	private String getSuperClass(String input) {

		String superClass = null;

		return superClass;
	}

	private void saveOntology() {
		File owl_file = new File("best.owl");
		IRI documentIRI = IRI.create(owl_file.toURI());

	}

	/**
	 * This method removes elements that should not be transformed to the OWL ontology
	 * @param elements a list of elements retrieved from the JDOM processing of the AIRM XMI
	 * @return an ArrayList of elements to be transformed to an OWL ontology
	 */
	public static ArrayList<String> removeElements(List<Element> elements) {

		ArrayList<String> removedElementList = new ArrayList<String>();
				
		for (Element e : elements) {
			if (e.getAttribute("name") != null && !e.getAttributeValue("xmiType").toString().equals("umlPackage") &&
					!e.getAttributeValue("xmiType").toString().equals("umlBoundary")) {
				removedElementList.add(e.getAttribute("name").getValue());
			}
		}		
		return removedElementList;		
	}
	
	/**
	 * Returns a Map where the source node in the Connector node is map key and the target node is the map value
	 * @param xmi an input XMI file
	 * @return Map containing source nodes as key and target nodes as value
	 * @throws IOException 
	 * @throws JDOMException 
	 */

	public static Multimap<String, String> createConnectors(File xmi) throws JDOMException, IOException {

		Multimap<String, String> connectorsMap = ArrayListMultimap.create();
		
		Document doc = sax.build(xmi);
		Element root = doc.getRootElement();
		Element connectorsNode = root.getChild("connectors");
		List<Element> connectorsList = connectorsNode.getChildren();
		
		//get all source nodes and put them in a Set
		for (Element e : connectorsList) {
			List<Element> sn = e.getChildren("source");
			List<Element> tn = e.getChildren("target");
			
			String source = null;
			String target = null;
			
			for (Element e3 : sn) {
				List<Element> e4 = e3.getChildren("model");
				for (Element e5 : e4) {
					source = e5.getAttributeValue("name");
				}
			}
			
			for (Element e6 : tn) {
				List<Element> e7 = e6.getChildren("model");
				for (Element e8 : e7) {
					target = e8.getAttributeValue("name");
				}
			}
			
			System.out.println("Creating map of " + source + " and " + target);

			connectorsMap.put(source, target);

		}
		
		return connectorsMap;
	}

	public static void main(String[] args) throws JDOMException, IOException, OWLOntologyCreationException, OWLOntologyStorageException {

		File inputFile = new File("./files/ConsolidatedLogicalDataModel_v06_stripped.xml");
		
		System.out.println("Creating connectors map");
		Multimap<String, String> connectorsMap = createConnectors(inputFile);

		
		for(java.util.Map.Entry<String, String> e : connectorsMap.entries()) {
			  System.out.println(e.getKey()+": "+e.getValue());
			}

		//testing the getElements method
		List<Element> elementList = getElements(inputFile);
		
		System.out.println("PRINTING ELEMENTS");
		for (Element e : elementList) {
			//System.out.println(e.getAttribute("xmiType"));			
		}
		
		//testing the removeElements method (on the elements)
		ArrayList<String> elementArrayList = removeElements(elementList);
		
		System.out.println("Printing removedElements: " + elementArrayList.size());
		for (String s : elementArrayList) {
			//System.out.println(s);
		}
		
		//testing the getConnectors method
		List<Element> connectorList = getConnectors(inputFile);
		//System.out.println("Size of connectorlist: " + connectorList.size());
		
		for (Element e1 : connectorList) {

			List<Element> e2 = e1.getChildren("source");
			
			for (Element e3 : e2) {
				List<Element> e4 = e3.getChildren("model");
				for (Element e5 : e4) {
					//System.out.println(e5.getAttributeValue("name"));
				}
			}

		}


		
		
		
		//testing to create an owl class for each element
		ArrayList<String> e = removeElements(elementList);
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory df = OWLManager.getOWLDataFactory();
		OWLOntology ontology = manager.createOntology(best_iri);
		

		//create a few dummy-classes
		OWLClass clsA = df.getOWLClass(IRI.create(best_iri + "#A"));
		OWLClass clsB = df.getOWLClass(IRI.create(best_iri + "#B"));

		//create a few axioms
		OWLAxiom subClassAxiom = df.getOWLSubClassOfAxiom(clsA, clsB);

		//add the axiom to the ontology
		AddAxiom addAxiom = new AddAxiom(ontology, subClassAxiom);

		//use the manager to apply the change
		manager.applyChange(addAxiom);

		//test if the classes were added
		Set<OWLClass> classes = ontology.getClassesInSignature();
		for (OWLClass cls : classes) {
			System.out.println(cls.getIRI().getFragment());
		}

		//saving the ontology
		File owl_file = new File("best.owl");
		IRI documentIRI = IRI.create(owl_file.toURI());
		manager.saveOntology(ontology, documentIRI);



	}

}
