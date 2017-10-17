package modularisation;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasoner;


import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;

public class CreateModule {

	static OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();

	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException {

		//import AIRM ontology
		//File ontoFile = new File("./files/OAEI2011/301-302/301.rdf");
		File ontoFile = new File("./files/modules/v2Sept2017/airm_mono.owl");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();		
		OWLOntology AIRM_onto = manager.loadOntologyFromOntologyDocument(ontoFile);
		OWLDataFactory df = OWLManager.getOWLDataFactory();

		//create seed signature
		String seed = "#_BaseInfrastructure_";
		OWLClass cls = df.getOWLClass(IRI.create(AIRM_onto.getOntologyID().getOntologyIRI() + seed));

		System.out.println(".....the class name is " + cls.getIRI());

		Set<OWLEntity> sig = new HashSet<OWLEntity>();
		sig.add(cls);	
		System.out.println(".....added " + cls.getIRI() + " to seed");
		
		// We now add all subclasses (direct and indirect) of the chosen classes. 
		Set<OWLEntity> seedSig = new HashSet<OWLEntity>();

		//using the Hermit reasoner
		Reasoner reasoner=new Reasoner(AIRM_onto);

		for (OWLEntity ent : sig) {
			seedSig.add(ent);

			if (OWLClass.class.isAssignableFrom(ent.getClass())) {
				System.out.println(".....is assignable");

				NodeSet<OWLClass> subClasses = reasoner.getSubClasses((OWLClass) ent, false);
				seedSig.addAll(subClasses.getFlattened());
				System.out.println(".....adding subclasses");

			}
		}
		
	      

		//extract module       
		SyntacticLocalityModuleExtractor sme = new SyntacticLocalityModuleExtractor(manager, AIRM_onto, ModuleType.STAR);
		System.out.println(".....created extractor");
		File owl_file = new File("extracted_module.owl");
		IRI moduleIRI = IRI.create(owl_file.toURI());
		System.out.println(".....about to extract ontology");
		
		//this is where the problem is for AIXM
		OWLOntology mod = sme.extractAsOntology(seedSig, moduleIRI);
		System.out.println("Extracted ontology");
		
		
		//save intermediate module
		manager.saveOntology(mod, moduleIRI);
		System.out.println("Ontology created!");

		Set<OWLClass> classes = mod.getClassesInSignature();
		Set<OWLEntity> opSet = new HashSet<OWLEntity>();

		System.out.println("The extracted module contains " + classes.size() + " classes");
		
		//create a new ontology that also contains object properties, data properties and individuals associated with the classes
		//from the module
		File complete_owl_file = new File(".files/modules/v2Sept2017/airm_baseinfrastructure.owl");
		IRI complete_moduleIRI = IRI.create(complete_owl_file.toURI());
		
		OWLOntology complete_ontology = manager.createOntology(complete_moduleIRI);
		
		//"clone" the intermediate ontology to the complete ontology
		manager.addAxioms(complete_ontology, mod.getAxioms());
		
		
		System.out.println("Printing classes in module: ");
		
		for (OWLClass c : classes) {

			System.out.println("trying class " + c.getIRI().getFragment());
			
			//get object properties for which class c is the domain class
			Set<OWLEntity> objectPropSet = getObjectProperties(AIRM_onto, c);
			if (objectPropSet != null) {
				

				for (OWLEntity o : objectPropSet) {

					Set<OWLClassExpression> temp = o.asOWLObjectProperty().getDomains(AIRM_onto);
					for (OWLClassExpression oce : temp) {
						manager.applyChange(new AddAxiom(complete_ontology,df.getOWLObjectPropertyDomainAxiom(o.asOWLObjectProperty(), oce)));
					}
					
					Set<OWLClassExpression> temp2 = o.asOWLObjectProperty().getRanges(AIRM_onto);
					
					for (OWLClassExpression oce2 : temp2) {
						manager.applyChange(new AddAxiom(complete_ontology,df.getOWLObjectPropertyRangeAxiom(o.asOWLObjectProperty(), oce2)));
					}
					
				}
			}
			
			//get data properties for which class c is the domain class
			Set<OWLEntity> dataPropSet = getDataProperties(AIRM_onto, c);
			System.out.println("Size of dataprop set is " + dataPropSet.size());
			if (dataPropSet != null) {

				for (OWLEntity d : dataPropSet) {

					
					Set<OWLClassExpression> temp = d.asOWLDataProperty().getDomains(AIRM_onto);
					for (OWLClassExpression oce : temp) {
						System.out.println("Adding " + d.asOWLDataProperty().getIRI().getFragment() + " to ontology");
						manager.applyChange(new AddAxiom(complete_ontology,df.getOWLDataPropertyDomainAxiom(d.asOWLDataProperty(), oce)));
					}
					
					Set<OWLDataRange> temp2 = d.asOWLDataProperty().getRanges(AIRM_onto);
					
					for (OWLDataRange oce2 : temp2) {
						manager.applyChange(new AddAxiom(complete_ontology,df.getOWLDataPropertyRangeAxiom(d.asOWLDataProperty(), oce2)));
					}
				}
			}
			
		}

		//save completed module
		manager.saveOntology(complete_ontology, complete_moduleIRI);
		System.out.println("Ontology created!");
		System.out.println("Number of classes: " + getNumClasses(complete_ontology));
		System.out.println("Number of object properties: " + getNumObjectProperties(complete_ontology));
		System.out.println("Number of data properties: " + getNumDataProperties(complete_ontology));
		System.out.println("Number of individuals: " + getNumIndividuals(complete_ontology));
		System.out.println("Number of axioms: " + getNumAxioms(complete_ontology));

	}
	
	private static Set<OWLEntity> getObjectProperties(OWLOntology onto, OWLClass cls) throws OWLOntologyCreationException{

		Set<OWLObjectProperty> opSet = new HashSet<OWLObjectProperty>();
		Set<OWLEntity> eSet = new HashSet<OWLEntity>();
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		Set<OWLObjectProperty> prop = onto.getObjectPropertiesInSignature();      

		//System.out.println("Object Property Domain");
		for (OWLObjectPropertyDomainAxiom op : onto.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN)) {                        
			if (op.getDomain().equals(cls)) {   
				for(OWLObjectProperty oop : op.getObjectPropertiesInSignature()){
					opSet.add(oop);
					System.out.println(oop.getIRI());
				}
			}
		}
		
		eSet.addAll(opSet);
		
		return eSet;
		
	}

	private static Set<OWLEntity> getDataProperties(OWLOntology onto, OWLClass cls) throws OWLOntologyCreationException{

		Set<OWLDataProperty> dpSet = new HashSet<OWLDataProperty>();
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		Set<OWLDataProperty> dataProp = onto.getDataPropertiesInSignature();    
		Set<OWLEntity> eSet = new HashSet<OWLEntity>();

		//must designate the source class as domain in the XSLT transformation!
		//System.out.println("Data Property Domain");
		for (OWLDataPropertyDomainAxiom dp : onto.getAxioms(AxiomType.DATA_PROPERTY_DOMAIN)) {
			if (dp.getDomain().equals(cls)) {   
				for(OWLDataProperty odp : dp.getDataPropertiesInSignature()){
					dpSet.add(odp);
					//System.out.println(odp.getIRI().getShortForm());
				}
			}
		}
		
		eSet.addAll(dpSet);
		return eSet;
		
	}
	
	/**
	 * Get number of classes in an ontology
	 * @param ontoFile	the file path of the OWL ontology
	 * @return numClasses an integer stating how many OWL classes the OWL ontology has
	 * @throws OWLOntologyCreationException An exception which describes an error during the creation of an ontology. If an ontology cannot be created then subclasses of this class will describe the reasons.
	 */
	private static int getNumClasses(OWLOntology onto) throws OWLOntologyCreationException {
	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	int numClasses = onto.getClassesInSignature().size();

	manager.removeOntology(onto);

	return numClasses;
}
	
	/**
	 * Returns an integer stating how many object properties an OWL ontology has
	 * @param ontoFile	the file path of the input OWL ontology
	 * @return numObjectProperties an integer stating number of object properties in an OWL ontology
	 * @throws OWLOntologyCreationException An exception which describes an error during the creation of an ontology. If an ontology cannot be created then subclasses of this class will describe the reasons.
	 */
	private static int getNumObjectProperties(OWLOntology onto) throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		int numObjectProperties = onto.getObjectPropertiesInSignature().size();

		manager.removeOntology(onto);

		return numObjectProperties;
	}
	
	private static int getNumDataProperties(OWLOntology onto) throws OWLOntologyCreationException {

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		int numDataProperties = onto.getDataPropertiesInSignature().size();

		manager.removeOntology(onto);

		return numDataProperties;
	}

	/**
	 * Returns an integer stating how many individuals an OWL ontology has
	 * @param ontoFile the file path of the input OWL ontology
	 * @return numIndividuals an integer stating number of individuals in an OWL ontology
	 * @throws OWLOntologyCreationException An exception which describes an error during the creation of an ontology. If an ontology cannot be created then subclasses of this class will describe the reasons.
	 */
	private static int getNumIndividuals(OWLOntology onto) throws OWLOntologyCreationException {

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		int numIndividuals = onto.getIndividualsInSignature().size();

		manager.removeOntology(onto);

		return numIndividuals;
	}
	
	private static int getNumAxioms(OWLOntology onto) throws OWLOntologyCreationException {

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		int axioms = onto.getAxiomCount();

		manager.removeOntology(onto);

		return axioms;
	}
	
}
