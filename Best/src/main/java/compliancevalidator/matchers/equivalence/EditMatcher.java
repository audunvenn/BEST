package compliancevalidator.matchers.equivalence;

import org.semanticweb.owl.align.AlignmentProcess;

/**
 * @author audunvennesland
 * 4. sep. 2017 
 */
public class EditMatcher extends StringDistAlignment implements AlignmentProcess {

	    /** Creation **/
	    public EditMatcher() {
		methodName = "levenshteinDistance";
	    };
	}


