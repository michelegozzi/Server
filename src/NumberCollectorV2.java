import java.util.Hashtable;
//import java.util.concurrent.atomic.AtomicInteger;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 * @author Michele
 *
 */
public class NumberCollectorV2 extends Thread implements IReceivedNumberEvent {

	private static NumberCollectorV2 instance = null;
	
	/**
	 * Singleton instance.
	 * 
	 * @return	the singleton instance
	 */
    public static synchronized NumberCollectorV2 getInstance() {
        if (instance == null) 
        	instance = new NumberCollectorV2();
        return instance;
    }
    
    private Hashtable<Integer, Integer> hash;
    //private AtomicInteger priorityLock = new AtomicInteger();
    
    private volatile Integer received = 0;
    private volatile Integer duplicates = 0;
    private volatile Integer lastReceived = 0;
    private volatile Integer lastDuplicates = 0;
    private boolean active;
    
    private BufferedWriter bw;
	
	private NumberCollectorV2() {
		hash = new Hashtable<Integer, Integer>();
		
		try {
			 
			File file = new File("numbers.log");
			file.createNewFile();
			
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
        setActive(true);
        while ( isActive() ) {
            
        	
            try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            System.out.println(report());
        }
        
        try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	@Override
	public void process(Integer number) {
		// TODO Auto-generated method stub
		Boolean isDuplicated = false;
		
		synchronized (this) {
			if (!hash.containsKey(number))
			{
				hash.put(number, 0);
			}
			else
			{
				isDuplicated = true;
			}
			
			++received;
			if (isDuplicated) ++duplicates;
			
			if (!isDuplicated) {
				try {
					bw.write(String.format("%d%n", number));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		// wait for priority lock
		//while (!priorityLock.compareAndSet(0, 1)){
		    //}
		
		

		//if (!priorityLock.compareAndSet(1, 0)) priorityLock.decrementAndGet();
	}
	
	public String report() {
		
		//priorityLock.addAndGet(2);
		
		//while (!priorityLock.compareAndSet(2, 2)){
			
	    //}
		
		int r = received;
		int d = duplicates;
		
		int rD = r - lastReceived;
		int dD = d - lastDuplicates;
		
		lastReceived = r;
		lastDuplicates = d;
		
		//priorityLock.addAndGet(-2);
		
		try {
			bw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return String.format("received %d numbers, %d duplicates", rD, dD);
	}

	boolean isActive() {
		return active;
	}

	void setActive(boolean active) {
		this.active = active;
	}
	
}
