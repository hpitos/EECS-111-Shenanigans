package nachos.threads;

import java.util.LinkedList;

import nachos.machine.*;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>, and multiple
 * threads can be waiting to <i>listen</i>. But there should never be a time
 * when both a speaker and a listener are waiting, because the two threads can
 * be paired off at this point.
 */
public class Communicator {
	/**
	 * Allocate a new communicator.
	 */
	public Communicator() {
		speakBuffer = new LinkedList<KThread>();
		listenBuffer = new LinkedList<KThread>();
		wordList = new LinkedList<Integer>();
		
		lock = new Lock();
		conVariable = new Condition2(lock);
	}

	/**
	 * Wait for a thread to listen through this communicator, and then transfer
	 * <i>word</i> to the listener.
	 * 
	 * <p>
	 * Does not return until this thread is paired up with a listening thread.
	 * Exactly one listener should receive <i>word</i>.
	 * 
	 * @param word the integer to transfer.
	 */
	public void speak(int word) {
		
		speakBuffer.add(KThread.currentThread());
		wordList.add(new Integer(word));
		
		while (KThread.currentThread() != speakBuffer.getFirst()) {
			conVariable.sleep();
		}
		
	}

	/**
	 * Wait for a thread to speak through this communicator, and then return the
	 * <i>word</i> that thread passed to <tt>speak()</tt>.
	 * 
	 * @return the integer transferred.
	 */
	public int listen() {
		
		listenBuffer.add(KThread.currentThread());
		
		return 0;
	}
	
	// custom vars
	private LinkedList<KThread> speakBuffer;
	private LinkedList<KThread> listenBuffer;
	private LinkedList<Integer> wordList;
	
	private static Lock lock = null;
	private static Condition2 conVariable = null;
}
