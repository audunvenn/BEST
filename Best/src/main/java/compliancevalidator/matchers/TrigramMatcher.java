package compliancevalidator.matchers;

/**
 * @author audunvennesland
 * 4. sep. 2017 
 */

import org.semanticweb.owl.align.AlignmentProcess;

public class TrigramMatcher extends StringDistAlignment implements AlignmentProcess {

    /** Creation **/
    public TrigramMatcher(){
	methodName = "ngramDistance";
    };
}
