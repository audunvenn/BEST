
/**
 * @author audunvennesland
 * 17. okt. 2017 
 */
public class Test {
	
	public static void main(String[] args) {
		
		String s1 = "CodeAirportHeliportType";
		
		String s2 = (s1.substring(0, s1.indexOf("Type"))) + "BaseType";
		
		System.out.println(s2);
	}

}
