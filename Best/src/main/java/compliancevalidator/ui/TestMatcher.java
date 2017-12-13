package compliancevalidator.ui;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.Properties;

//Alignment API classes
import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentProcess;
import org.semanticweb.owl.align.AlignmentVisitor;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import compliancevalidator.matchers.equivalence.EditMatcher;
import compliancevalidator.matchers.equivalence.ISubMatcher;
import compliancevalidator.matchers.equivalence.InstanceMatcher;
import compliancevalidator.matchers.equivalence.WordNetMatcher;
import compliancevalidator.matchers.properties.PropEq_String_Matcher;
import compliancevalidator.matchers.properties.PropEq_WordNet_Matcher;
import compliancevalidator.matchers.subsumption.CompoundMatcher;
import compliancevalidator.matchers.subsumption.Subsumption_WordNet_Matcher;
import compliancevalidator.misc.StringUtils;
import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.impl.renderer.RDFRendererVisitor;


public class TestMatcher {
	
	//static Logger logger = LoggerFactory.getLogger(AncestorMatcher.class);

	public static void main(String[] args) throws AlignmentException, IOException, URISyntaxException, OWLOntologyCreationException {
		
		//logger.info("Hello from TestMatcher");

		/*** 1. SELECT THE MATCHER TO BE RUN ***/
		final String MATCHER = "STRING";

		/*** 2. SELECT THE TWO ONTOLOGIES TO BE MATCHED ***/
		//File ontoFile1 = new File("./files/wndomainexperiment/SchemaOrg/schema-org.owl");
		//File ontoFile2 = new File("./files/wndomainexperiment/efrbroo.owl");
		
		File ontoFile1 = new File("./files/BEST/airm/airm_mono.owl");
		File ontoFile2 = new File("./files/BEST/aixm/ontologies/aixm_airportheliport.owl");
		//File ontoFile1 = new File("./files/Path/schema-org.owl");
		//File ontoFile2 = new File("./files/Path/schema-org.owl");
		
		
		
		/*** 3. SELECT THE NEO4J DATABASE FILE (FOR THE STRUCTURAL MATCHERS ONLY) ***/
		final File dbFile = new File("/Users/audunvennesland/Documents/PhD/Development/Neo4J/biblio-bibo2");
		

		/*** INITIAL VALUES, NO NEED TO TOUCH THESE ***/
		double threshold;
		String alignmentFileName = null;
		File outputAlignment = null;
		String ontologyParameter1 = null;
		String ontologyParameter2 = null;
		PrintWriter writer = null;
		AlignmentVisitor renderer = null;
		Properties params = new Properties();
		AlignmentProcess a = null;
		
		/*** USED FOR INCLUDING THE ONTOLOGY FILE NAMES IN THE COMPUTED ALIGNMENT FILE ***/
		String onto1 = StringUtils.stripPath(ontoFile1.toString());
		String onto2 = StringUtils.stripPath(ontoFile2.toString());

		switch(MATCHER) {

		case "STRING":
			a = new ISubMatcher();
			//a = new EditMatcher();
			threshold = 0.8;

			a.init(ontoFile1.toURI(), ontoFile2.toURI());
			params = new Properties();
			params.setProperty("", "");
			a.align((Alignment)null, params);	
			
			System.err.println("The a alignment contains " + a.nbCells() + " correspondences");

			alignmentFileName = "./files/BEST/alignments/" + onto1 + "-" + onto2 + "-String.rdf";

			outputAlignment = new File(alignmentFileName);

			writer = new PrintWriter(
					new BufferedWriter(
							new FileWriter(outputAlignment)), true); 
			renderer = new RDFRendererVisitor(writer);

			BasicAlignment StringAlignment = (BasicAlignment)(a.clone());
			
			

			StringAlignment.cut(threshold);

			StringAlignment.render(renderer);
			
			System.err.println("The StringAlignment contains " + StringAlignment.nbCells() + " correspondences");
			writer.flush();
			writer.close();

			System.out.println("Matching completed!");
			break;
			
			
		case "INSTANCE":
			
			a = new InstanceMatcher();
			threshold = 0.1;

			a.init(ontoFile1.toURI(), ontoFile2.toURI());
			params = new Properties();
			params.setProperty("", "");
			a.align((Alignment)null, params);	

			//alignmentFileName = "./files/UoA/test/" + onto1 + "-" + onto2 + "/Instances.rdf";

			outputAlignment = new File(alignmentFileName);

			writer = new PrintWriter(
					new BufferedWriter(
							new FileWriter(outputAlignment)), true); 
			renderer = new RDFRendererVisitor(writer);

			BasicAlignment InstanceAlignment = (BasicAlignment)(a.clone());

			InstanceAlignment.cut(threshold);

			InstanceAlignment.render(renderer);
			writer.flush();
			writer.close();

			System.out.println("Matching completed!");
			break;
			
			

		case "WORDNET":
			
			a = new WordNetMatcher();
			threshold = 0.6;

			a.init(ontoFile1.toURI(), ontoFile2.toURI());
			params = new Properties();
			params.setProperty("", "");
			a.align((Alignment)null, params);	

			alignmentFileName = "./files/COMPOSE-ClassEq_WordNet.rdf";

			outputAlignment = new File(alignmentFileName);

			writer = new PrintWriter(
					new BufferedWriter(
							new FileWriter(outputAlignment)), true); 
			renderer = new RDFRendererVisitor(writer);

			BasicAlignment wordNetAlignment = (BasicAlignment)(a.clone());

			wordNetAlignment.cut(threshold);

			wordNetAlignment.render(renderer);
			writer.flush();
			writer.close();

			System.out.println("Matching completed!");
			break;

		

		case "SUBSUMPTION_COMPOUND":
			
			a = new CompoundMatcher();
			threshold = 0.6;

			a.init(ontoFile1.toURI(), ontoFile2.toURI());
			params = new Properties();
			params.setProperty("", "");
			a.align((Alignment)null, params);	

			//alignmentFileName = "./files/OAEI-16-conference/alignments/" + onto1 + "-" + onto2 + "/COMPOSE-Subsumption_String.rdf";		

			outputAlignment = new File(alignmentFileName);

			writer = new PrintWriter(
					new BufferedWriter(
							new FileWriter(outputAlignment)), true); 
			renderer = new RDFRendererVisitor(writer);

			BasicAlignment subsumptionCompoundAlignment = (BasicAlignment)(a.clone());

			subsumptionCompoundAlignment.cut(threshold);

			subsumptionCompoundAlignment.render(renderer);
			writer.flush();
			writer.close();

			System.out.println("Matching completed!");
			break;

		

		case "SUBSUMPTION_WORDNET":
			
			a = new Subsumption_WordNet_Matcher();
			threshold = 0.6;

			a.init(ontoFile1.toURI(), ontoFile2.toURI());
			params = new Properties();
			params.setProperty("", "");
			a.align((Alignment)null, params);	

			//alignmentFileName = "./files/OAEI-16-conference/alignments/" + onto1 + "-" + onto2 + "/Subsumption_WordNet.rdf";	

			outputAlignment = new File(alignmentFileName);

			writer = new PrintWriter(
					new BufferedWriter(
							new FileWriter(outputAlignment)), true); 
			renderer = new RDFRendererVisitor(writer);

			BasicAlignment subsumptionWordNetAlignment = (BasicAlignment)(a.clone());

			subsumptionWordNetAlignment.cut(threshold);

			subsumptionWordNetAlignment.render(renderer);
			writer.flush();
			writer.close();

			System.out.println("Matching completed!");
			break;
			

		case "PROPERTY_WORDNET":
			
			a = new PropEq_WordNet_Matcher();
			threshold = 0.6;

			a.init(ontoFile1.toURI(), ontoFile2.toURI());
			params = new Properties();
			params.setProperty("", "");
			a.align((Alignment)null, params);	

			//alignmentFileName = "./files/ntnu-lyon-paper/alignments/km4c-otn/" + onto1 + "-" + onto2 + "/PropEq_WordNet2.rdf";

			outputAlignment = new File(alignmentFileName);

			writer = new PrintWriter(
					new BufferedWriter(
							new FileWriter(outputAlignment)), true); 
			renderer = new RDFRendererVisitor(writer);

			BasicAlignment PropertyWordNet2Alignment = (BasicAlignment)(a.clone());

			PropertyWordNet2Alignment.cut(threshold);

			PropertyWordNet2Alignment.render(renderer);
			writer.flush();
			writer.close();

			System.out.println("Matching completed!");
			break;
			
		case "PROPERTY_STRING":
			
			a = new PropEq_String_Matcher();
			threshold = 0.8;

			a.init(ontoFile1.toURI(), ontoFile2.toURI());
			params = new Properties();
			params.setProperty("", "");
			a.align((Alignment)null, params);	

			//alignmentFileName = "./files/ntnu-lyon-paper/alignments/km4c-otn/" + onto1 + "-" + onto2 + "/PropEq_String.rdf";

			outputAlignment = new File(alignmentFileName);

			writer = new PrintWriter(
					new BufferedWriter(
							new FileWriter(outputAlignment)), true); 
			renderer = new RDFRendererVisitor(writer);

			BasicAlignment StringPropertyAlignment = (BasicAlignment)(a.clone());

			StringPropertyAlignment.cut(threshold);

			StringPropertyAlignment.render(renderer);
			writer.flush();
			writer.close();

			System.out.println("Matching completed!");
			break;


		}

	}


}