import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasoner;

import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;

public class Examples {
	
	
	 /** This example shows how to extract modules.
     * 
     * @throws OWLOntologyCreationException
     * @throws OWLOntologyStorageException */
    
    public static void shouldExtractModules() throws OWLOntologyCreationException,
            OWLOntologyStorageException {
        String DOCUMENT_IRI = "http://protege.stanford.edu/ontologies/pizza/pizza.owl";
        // Create our manager
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        // Load the pizza ontology
        OWLOntology ont = man.loadOntologyFromOntologyDocument(IRI.create(DOCUMENT_IRI));
        System.out.println("Loaded: " + ont.getOntologyID());
        // We want to extract a module for all toppings. We therefore have to
        // generate a seed signature that contains "PizzaTopping" and its
        // subclasses. We start by creating a signature that consists of
        // "PizzaTopping".
        OWLDataFactory df = man.getOWLDataFactory();
        OWLClass toppingCls = df.getOWLClass(IRI.create(ont.getOntologyID()
                .getOntologyIRI().toString()
                + "#Pizza"));
        Set<OWLEntity> sig = new HashSet<OWLEntity>();
        sig.add(toppingCls);
        // We now add all subclasses (direct and indirect) of the chosen
        // classes. Ideally, it should be done using a DL reasoner, in order to
        // take inferred subclass relations into account. We are using the
        // structural reasoner of the OWL API for simplicity.
        Set<OWLEntity> seedSig = new HashSet<OWLEntity>();
        OWLReasoner reasoner = new StructuralReasoner(ont, new SimpleConfiguration(),
                BufferingMode.NON_BUFFERING);
        for (OWLEntity ent : sig) {
            seedSig.add(ent);
            if (OWLClass.class.isAssignableFrom(ent.getClass())) {
                NodeSet<OWLClass> subClasses = reasoner.getSubClasses((OWLClass) ent,
                        false);
                seedSig.addAll(subClasses.getFlattened());
            }
        }
        // Output for debugging purposes
        System.out.println();
        System.out
                .println("Extracting the module for the seed signature consisting of the following entities:");
        for (OWLEntity ent : seedSig) {
            System.out.println("  " + ent);
        }
        System.out.println();
        System.out.println("Some statistics of the original ontology:");
        System.out.println("  " + ont.getSignature().size() + " entities");

        System.out.println("  " + ont.getLogicalAxiomCount() + " logical axioms");
        System.out.println("  " + (ont.getAxiomCount() - ont.getLogicalAxiomCount())
                + " other axioms");
        System.out.println();
        // We now extract a locality-based module. For most reuse purposes, the
        // module type should be STAR -- this yields the smallest possible
        // locality-based module. These modules guarantee that all entailments
        // of the original ontology that can be formulated using only terms from
        // the seed signature or the module will also be entailments of the
        // module. In easier words, the module preserves all knowledge of the
        // ontology about the terms in the seed signature or the module.
        SyntacticLocalityModuleExtractor sme = new SyntacticLocalityModuleExtractor(man,
                ont, ModuleType.STAR);
        
        File owl_file = new File("PizzaModule.owl");
        IRI moduleIRI = IRI.create(owl_file.toURI());
        
        OWLOntology mod = sme.extractAsOntology(seedSig, moduleIRI);
       
        // Output for debugging purposes
        System.out.println("Some statistics of the module:");
        System.out.println("  " + mod.getSignature().size() + " entities");
        System.out.println("  " + mod.getLogicalAxiomCount() + " logical axioms");
        System.out.println("  " + (mod.getAxiomCount() - mod.getLogicalAxiomCount())
                + " other axioms");
        Set<OWLObjectProperty> opSet = mod.getObjectPropertiesInSignature();
        
        System.out.println("Printing object properties:");
        for (OWLObjectProperty op : opSet) {
        	System.out.println(op.getIRI().getFragment());
        }
        
        
        System.out.println();
        // And we save the module.
        System.out
                .println("Saving the module as " + mod.getOntologyID().getOntologyIRI());
        man.saveOntology(mod);
    }
    
    public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException {
    	
    	shouldExtractModules();
    }
}