import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * NumberCollector manages the counters, it checks if a number is duplicated or not and it writes new number on a log file.
 * 
 * @author Michele Gozzi
 *
 */
public class NumberCollector extends Thread implements IReceivedNumberEvent {

	private static NumberCollector instance = null;
	
	/**
	 * Singleton instance.
	 * 
	 * @return	the singleton instance
	 */
    public static synchronized NumberCollector getInstance() {
        if (instance == null) 
        	instance = new NumberCollector();
        return instance;
    }
    
    /**
     * Keeps track of the received numbers.
     */
    private Hashtable<Integer, Integer> hash;
    
    /**
     * Used to provide a lock on the update operations on hash and counters with a priority for the report method
     */
    private AtomicInteger priorityLock = new AtomicInteger();
    
    /**
     * Counter for received numbers.
     */
    private volatile Integer received = 0;
    
    /**
     * Counter for duplicates.
     */
    private volatile Integer duplicates = 0;
    
    /**
     * State of the runnable object.
     */
    private boolean active;
    
    /**
     * Buffered writer used to write the log file
     */
    private BufferedWriter bw;
	
	private NumberCollector() {
		hash = new Hashtable<Integer, Integer>();
		
		try {
			// creates a new log file 
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
				e.printStackTrace();
			}
            System.out.println(report());
        }
        
        try {
			bw.close();	// closes the log file.
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	/**
	 * Performs the following operations:
	 * - checks if number is duplicated or not
	 * - updates the counters
	 * - writes the new number on the log file
	 * 
	 * @param number	Input number
	 */
	@Override
	public void process(Integer number) {
		// TODO Auto-generated method stub
		Boolean isDuplicated = false;
		
		/*
		synchronized (this) {
			if (!hash.containsKey(number))
			{
				hash.put(number, 0);
			}
			else
			{
				isDuplicated = true;
			}
		}
		*/
		
		// wait for priority lock, the report method has the priority
		while (!priorityLock.compareAndSet(0, 1)){
		    }
		
		if (!hash.containsKey(number))
		{
			hash.put(number, 0);	// new number
		}
		else
		{
			isDuplicated = true;	// duplicated
		}
		
		++received;
		if (isDuplicated) ++duplicates;
		
		// writes the new number on the log file
		if (!isDuplicated) {
			try {
				bw.write(String.format("%d%n", number));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// releases the lock.
		// in case it is reserved from the report method, just decrements by 1.
		if (!priorityLock.compareAndSet(1, 0)) priorityLock.decrementAndGet();
	}
	
	/**
	 * Returns a report string containing the counters of received numbers and duplicates since the last report.
	 * 
	 * @return Returns a string.
	 */
	public String report() {
		
		priorityLock.addAndGet(2); // priority reservation
		
		// wait until it is my turn!
		while (!priorityLock.compareAndSet(2, 2)){
			
	    }
		
		// creates a copy of the values
		int r = received;
		int d = duplicates;
		
		// resets the counters
		received = 0;
		duplicates = 0;
		
		// releases my reservation
		priorityLock.addAndGet(-2);
		
		// flushes the buffer
		try {
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return String.format("received %d numbers, %d duplicates", r, d);
	}

	/**
	 * Returns the state of the object.
	 * 
	 * @return	True if the object is active, otherwise false.
	 */
	boolean isActive() {
		return active;
	}

	/**
	 * Changes the object state.
	 * 
	 * @param active New object state.
	 */
	void setActive(boolean active) {
		this.active = active;
	}
}
