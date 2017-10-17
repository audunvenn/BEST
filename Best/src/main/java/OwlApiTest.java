import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

/**
 * @author audunvennesland
 * 10. mai 2017 
 */
public class OwlApiTest {
	
	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException {
	
	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	
	//get a reference to a data factory
	OWLDataFactory factory = manager.getOWLDataFactory();
	
	//we can also use a prefix manager to avoid having to specify the full prefix every time
	PrefixManager pm = new DefaultPrefixManager("http://www.semanticweb.org/owlapi/ontologies/ontology#");
	
	OWLClass clsMethodA = factory.getOWLClass(":A", pm);

	//create an ontology and add a declaration axiom to the ontology that declares the above class
	OWLOntology ontology = manager.createOntology(IRI.create("http://www.semanticweb.org/owlapi/ontologies/ontology"));
	
	//we can add a declaration axiom to the ontology, that essentially adds the class to the signature of our ontology
	OWLDeclarationAxiom declarationAxiom = factory.getOWLDeclarationAxiom(clsMethodA);
	manager.addAxiom(ontology, declarationAxiom);
	
	//specify that A is a subclass of B. Add a subclass axiom
	OWLClass clsA = factory.getOWLClass(":A", pm);
	OWLClass clsB = factory.getOWLClass(":B", pm);
	
	//create the axiom
	OWLAxiom axiom = factory.getOWLSubClassOfAxiom(clsA, clsB);
	
	//specify some individuals of classes A and B
	OWLNamedIndividual audun = factory.getOWLNamedIndividual(":Audun", pm);
	
	//create a ClassAssertion to specify that :audun is an instance of A
	OWLClassAssertionAxiom classAssertion = factory.getOWLClassAssertionAxiom(clsA, audun);
	
	//Add the class assertion to the ontology
	manager.addAxiom(ontology, classAssertion);
	
	//specify an object property between A and B
	OWLIndividual tone = factory.getOWLNamedIndividual(":Tone", pm);
	OWLIndividual gabriel = factory.getOWLNamedIndividual(":Gabriel", pm);
	OWLIndividual jesper = factory.getOWLNamedIndividual(":Jesper", pm);
	
	//link the subject and object with the hasFather object property
	OWLObjectProperty hasFather = factory.getOWLObjectProperty(":hasFather", pm);
	OWLObjectPropertyAssertionAxiom assertion = factory.getOWLObjectPropertyAssertionAxiom(hasFather, gabriel, audun);
	
	//add the axiom to the ontology and save
	AddAxiom addAxiomChange = new AddAxiom(ontology, assertion);
	manager.applyChange(addAxiomChange);
	manager.saveOntology(ontology, IRI.create("file:/Users/audunvennesland/Documents/OWLAPITest.owl"));
	
	}

}
