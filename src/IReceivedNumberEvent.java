/**
 * Interface used to signal that a number has been received.
 * 
 * @author Michele Gozzi
 *
 */
public interface IReceivedNumberEvent {
	/**
	 * Method to invoke for the number elaboration.
	 * 
	 * @param number	Number to elaborate.
	 */
	public void process(Integer number);
}
