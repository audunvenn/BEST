package compliancevalidator.matchers;

/**
 * @author audunvennesland
 * 4. sep. 2017 
 */
import org.semanticweb.owl.align.AlignmentProcess;

public class SmoaMatcher extends StringDistAlignment implements AlignmentProcess {

    /** Creation **/
    public SmoaMatcher(){
	methodName = "smoaDistance";
    };
}
