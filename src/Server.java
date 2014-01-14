import java.io.IOException;
import java.net.*;

/**
 * Server accepts multiple client connections.
 * For every connection it creates a Session object (thread).
 * It is capable to handle up to 1000 concurrent sessions.
 * 
 * @author Michele
 *
 */
public class Server implements ITerminateEvent {
	
	private static Server instance = null;
	 
    public static synchronized Server getInstance(IReceivedNumberEvent event) throws Exception {
        if (instance == null) 
        {
        	instance = new Server(event);
        }
        return instance;
    }

    //List<Session> sessions;
    private final int MAX = 1000;
    private Session[] sessions;
    private int size;
    private ServerSocket serverSocket;
    private boolean active;
    private IReceivedNumberEvent ie;
    
    /**
     * Constructor
     * 
     * @param event	Event used to signal that a number has been received.
     * 
     * @throws Exception
     */
    Server(IReceivedNumberEvent event) throws Exception {
        this.ie = event;
        this.size = 0;
        this.sessions = new Session[MAX];
    	active = true;
    	serverSocket = new ServerSocket(4000);
        listen();
    }

    /**
     * Listener method used for accept new client connections.
     */
    public void listen() {
        while ( active ) {
            Socket clientSocket;
			try {
				clientSocket = serverSocket.accept();	// accept a client connection
				
				do {
	            	this.size++;
	            	if (size > MAX) size = 1;
	            
	            }	while (sessions[size-1] != null && sessions[size-1].isActive());
	            
	            try {
	            	// creates a new session
					this.sessions[size-1] = new Session( this, ie, clientSocket );
					this.sessions[size-1].start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        System.out.println ("Terminate clients.");
        // terminate clients
        size = 0;
        while (size < MAX && sessions[size] != null && sessions[size].isActive()) {
        	sessions[size].setActive(false);
        	size++;
        }
        
        System.out.println ("Wait for clients termination.");
        // wait for clients termination
        size = 0;
        while (size < MAX && sessions[size] != null && sessions[size].isActive()) {
        	try {
				sessions[size].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	size++;
        }
        
        System.out.println ("Listener terminated.");
    }
    
    /**
     * Terminates the server.
     */
    @Override
	public void terminate() {
    	this.active = false;
    	try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
}

