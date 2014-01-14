import java.net.Socket;
//import java.io.InputStream;
//import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Runnable object used to handle the client session.
 * 
 * @author Michele Gozzi
 *
 */
public class Session extends Thread implements ITerminateEvent {

	/**
	 * client socket
	 */
	private Socket socket = null;
	private boolean active;
	
	/**
	 * event to fire when a number has been received
	 */
    private IReceivedNumberEvent irne;
    
    /**
     * event to fire when the server must be terminated
     */
    private ITerminateEvent ite;

    /**
     * Constructor
     * @param te	Event used to signal that the server must be terminated.
     * @param rne	Event used to signal that a number has been received.
     * @param clientSocket	Client socket connection.
     * @throws Exception
     */
    Session(ITerminateEvent te, IReceivedNumberEvent rne, Socket clientSocket) throws Exception {
    	ite = te;
    	irne = rne;
    	socket = clientSocket;
    }

    public void run() {
    	
    	//ExecutorService executor = Executors.newFixedThreadPool(10);
    	
        setActive(true);
        
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
       
            String inputLine;
            out.println("Welcome!");	// welcome message
 
            // reads an input line from the stream when it's available
            while (isActive() && (inputLine = in.readLine()) != null) {
                /*
            	Runnable worker = new InputWorker(ite, this, irne, inputLine);
                executor.execute(worker);
                */
            	
            	if (!processInput(inputLine)) {
            		setActive(false);
            		
            		// intercepts the terminate command 
	                if (inputLine.equals("terminate")) {
	                	System.out.println("Signal terminate event");
	                	ite.terminate();	// signal the termination event
	                }
                    break;
                }
                
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
        	
        	/*
        	executor.shutdown();
            // Wait until all threads are finish
            try {
				executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            System.out.println("Finished all threads");
            */
        	
        	setActive(false);
        }
    }

    /**
     * Processes the input line. Signals an {@link IReceivedNumberEvent} when a 9 digit number has been found.
     * This method uses a RegEx pattern to match the 9 digit number.
     *
     * @param  inputLine The input line. 
     * @return      Returns true if inputLine is 9 digit number, otherwise returns false.
     */
    private boolean processInput(String inputLine) {
    	
    	boolean found = false;
    	
        try {
        	
        	Pattern pattern = Pattern.compile("^\\d{9}$");
    		Matcher matcher = pattern.matcher(inputLine);

            if (matcher.find()) {
            	Integer res = Integer.parseInt(matcher.group());
            	irne.process(res);
            	found = true;
            	//System.out.println(String.format("I found the number \"%d\"", res));
            }
            else {
            	System.out.println(String.format("Input discharged: \"%s\"", inputLine));
            }
        } catch ( Exception e ) {
        	e.printStackTrace();
        }
        
        return found;
    }

	boolean isActive() {
		return active;
	}

	void setActive(boolean active) {
		this.active = active;
	}
	
	/**
     * Terminates the client session.
     */
	@Override
	public void terminate() {
		setActive(false);
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}