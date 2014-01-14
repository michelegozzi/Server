import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * InputWorker transforms the input string.
 * <p>
 * NO BENEFIT
 * 
 * @author Michele Gozzi
 * 
 */

public class InputWorker implements Runnable {
	private final String inputLine;
	private ITerminateEvent ite;
	private ITerminateEvent iteSession;
	private IReceivedNumberEvent irne;

	InputWorker(ITerminateEvent te, ITerminateEvent teSession, IReceivedNumberEvent rne, String inputLine) {
		this.ite = te;
		this.iteSession = teSession;
		this.irne = rne;
		this.inputLine = inputLine;
	}

	@Override
	public void run() {
		boolean found = false;
	  	
	    try {
	    	Pattern pattern = 
    		Pattern.compile("^\\d{9}$");
	
	    	Matcher matcher = 
			pattern.matcher(inputLine);
	
	    	if (matcher.find()) {
	    		//System.out.println(String.format("I found the number \"%s\"", matcher.group()));
	  	
	    		Integer res = Integer.parseInt(matcher.group());
	    		irne.process(res);
	    		found = true;
	    	}
		    else {
		    	System.out.println(String.format("Imput discharged: \"%s\"", inputLine));
		    }
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	      
	    if (!found) {
    		//setActive(false);
            if (inputLine.equals("terminate")) {
            	System.out.println("Signal terminate event");
            	ite.terminate();
            }
            //break;
            iteSession.terminate();
        }
	}
} 