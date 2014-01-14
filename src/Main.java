/**
 * Server application.
 * 
 * @author Michele
 *
 */
public class Main {

	public static void main(String[] args) {
		
		try {
			System.out.println("Start NumberCollector");
			NumberCollector.getInstance().start();
			System.out.println("Start Server");
			Server.getInstance(NumberCollector.getInstance()).listen();
			
			System.out.println("Stop NumberCollector");
			NumberCollector.getInstance().setActive(false);
			NumberCollector.getInstance().join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			System.out.println("Server terminated");
		}
	}
}
