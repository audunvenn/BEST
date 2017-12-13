package compliancevalidator.matchers.equivalence;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import compliancevalidator.misc.ISub;
import compliancevalidator.misc.StringUtils;

/**
 * @author audunvennesland
 * 16. nov. 2017 
 */
public class AnnotationMatcher {
	
public static void main(String[] args) throws OWLOntologyCreationException {
		
		//import the two ontology files
		File ontoFile1 = new File("./files/BEST/airm/airm_mono.owl");
		File ontoFile2 = new File("./files/BEST/aixm/ontologies/aixm_airportheliport.owl");
		
//		//parse the ontology files into ontology objects
//		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
//		OWLOntology onto1 = manager.loadOntologyFromOntologyDocument(ontoFile1);
//		OWLOntology onto2 = manager.loadOntologyFromOntologyDocument(ontoFile2);
		
		//get a set of all classes for each of the OWL ontologies
		Set<OWLClass> onto1ClsSet = getOWLClasses(ontoFile1);
		Set<OWLClass> onto2ClsSet = getOWLClasses(ontoFile2);
		
		System.err.println("Created class sets");
		
		System.err.println("Ontology AIRM has " + onto1ClsSet.size() + " entities ");
		System.err.println("Ontology AIXM has " + onto2ClsSet.size() + " entities ");
		

		Map<OWLClass, String> onto1ClsAndDefinitionsMap = getDefinitions(ontoFile1);
		Map<OWLClass, String> onto2ClsAndDefinitionsMap = getDefinitions(ontoFile2);
		
		
		Map<String, Double> resultsMap = new HashMap<String, Double>();
		
		System.err.println("Starting matching process");
		final long startTime = System.nanoTime();



		double sim  = 0;
		String entityPair = null;
		for (Entry<OWLClass, String> e1 : onto1ClsAndDefinitionsMap.entrySet()) {
			for (Entry<OWLClass, String> e2 : onto2ClsAndDefinitionsMap.entrySet()) {
				sim = computeSimpleSim(e1.getValue(), e2.getValue());
				//System.out.println("Matching " + e1.getValue() + " and " + e2.getValue() + " with sim: " + sim);
				if ( sim > 0.9) {
					entityPair = e1.getKey().getIRI().toString() + " - " + e2.getKey().getIRI().toString();

					resultsMap.put(entityPair,sim);
				}
			}
		}
		
		System.out.println("Number of correspondences identified: " + resultsMap.size());
		final long duration = System.nanoTime() - startTime;

		System.out.println("The matching took " + (duration / 1000000000) + " seconds ");
		
		for (Entry<String, Double> e : resultsMap.entrySet()) {
			System.out.println(e);
		}
	}

/**
 * Creates a map holding all classes and corresponding definitions (RDFS:Comments) in an ontology
 * @param ontoFile
 * @return
 * @throws OWLOntologyCreationException
 */
private static Map<OWLClass, String> getDefinitions(File ontoFile) throws OWLOntologyCreationException {
	
	Map<OWLClass, String> classAndDefinition = new HashMap<OWLClass, String> ();
	String definition = null;
	

	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	OWLOntology onto = manager.loadOntologyFromOntologyDocument(ontoFile);
	Iterator<OWLClass> itr = onto.getClassesInSignature().iterator();

	OWLClass thisEntity;

	while (itr.hasNext()) {
		thisEntity = itr.next();

		for (OWLAnnotationAssertionAxiom a : onto.getAnnotationAssertionAxioms(thisEntity.getIRI())) {
			if (a.getProperty().isComment()) {
				definition = a.getAnnotation().getValue().toString();
			}
		}
		
		classAndDefinition.put(thisEntity, definition);

	}

	manager.removeOntology(onto);

	return classAndDefinition;
}
	
	
	
	private static String getEntityDefinition(OWLEntity ent, OWLOntology onto) {
		String definition = null;

		Set<OWLAnnotationAssertionAxiom> oaa = onto.getAnnotationAssertionAxioms(ent.getIRI());
		
		for (OWLAnnotationAssertionAxiom a : oaa) {
			if (a.getProperty().isComment()) {
				definition = a.getAnnotation().getValue().toString();
			}
		}
		
		return definition;

		
	}
	
	private static String getClassDefinition(OWLClass cls, OWLOntology onto) {
		String definition = null;

		Set<OWLAnnotationAssertionAxiom> oaa = onto.getAnnotationAssertionAxioms(cls.getIRI());
		
		for (OWLAnnotationAssertionAxiom a : oaa) {
			if (a.getProperty().isComment()) {
				definition = a.getAnnotation().getValue().toString();
			}
		}
		
		return definition;

		
	}
	
	public static double computeEntityDefinitionsSim (File ontoFile1, File ontoFile2, OWLEntity e1, OWLEntity e2) throws OWLOntologyCreationException {
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology onto1 = manager.loadOntologyFromOntologyDocument(ontoFile1);
		OWLOntology onto2 = manager.loadOntologyFromOntologyDocument(ontoFile2);
		
		double simDef = 0;
		
		String a1Def = getEntityDefinition(e1, onto1);
		String a2Def = getEntityDefinition(e2, onto2);
		
		//compute similarity between definitions
		computeSimpleSim(a1Def, a2Def);

		
		return simDef;
		
	}
	
	
	public static double computeClassDefinitionsSim (File ontoFile1, File ontoFile2, OWLClass c1, OWLClass c2) throws OWLOntologyCreationException {
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology onto1 = manager.loadOntologyFromOntologyDocument(ontoFile1);
		OWLOntology onto2 = manager.loadOntologyFromOntologyDocument(ontoFile2);
		
		double simDef = 0;

		
		String a1Def = getClassDefinition(c1, onto1);
		String a2Def = getClassDefinition(c2, onto2);
		
		//compute similarity between definitions
		computeSimpleSim(a1Def, a2Def);

		
		return simDef;
		
	}
	
	/**
	 * A very simple string matcher based on the ISub algorithm
	 * @param s1
	 * @param s2
	 * @return
	 */
	private static double computeSimpleSim(String s1, String s2) {
		
		double sim = 0; 
		
		ISub isub = new ISub();
		
		sim = isub.score(s1, s2);
		
		return sim;
	}
	
	private static Set<OWLEntity> getOWLEntities (File ontoFile) throws OWLOntologyCreationException {
		
		Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology onto = manager.loadOntologyFromOntologyDocument(ontoFile);
		
		entitySet = onto.getSignature();
		
		return entitySet;
		
	}
	
	private static Set<OWLClass> getOWLClasses (File ontoFile) throws OWLOntologyCreationException {
		
		Set<OWLClass> clsSet = new HashSet<OWLClass>();
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology onto = manager.loadOntologyFromOntologyDocument(ontoFile);
		
		clsSet = onto.getClassesInSignature();
		
		return clsSet;
		
	}
	
	
	
	

}
