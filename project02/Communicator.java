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
		speakCon = new Condition2(lock);
		listenCon = new Condition2(lock);
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
		lock.acquire();
				
		speakBuffer.add(KThread.currentThread());
		wordList.add(new Integer(word));

		if(listenBuffer.isEmpty()){
			speakCon.sleep();

			listenCon.wake();
		}
		else{
			speakBuffer.removeFirst();
			listenBuffer.removeFirst();

			listenCon.wake();
		}

		lock.release();

	}

	/**
	 * Wait for a thread to speak through this communicator, and then return the
	 * <i>word</i> that thread passed to <tt>speak()</tt>.
	 * 
	 * @return the integer transferred.
	 */
	public int listen() {
		lock.acquire();
		
		listenBuffer.add(KThread.currentThread());

		this.word = 0;

		if(speakBuffer.isEmpty()){
			listenCon.sleep();
		}
		else{
			speakCon.wake();
			speakBuffer.removeFirst();
			listenBuffer.removeFirst();
			listenCon.sleep();
		}
		this.word = wordList.removeFirst().intValue();

		lock.release();

		return this.word;
	}
	
	// custom vars
	private LinkedList<KThread> speakBuffer;
	private LinkedList<KThread> listenBuffer;
	private LinkedList<Integer> wordList;
	private int word;
	private boolean speaking = true;
	
	private static Lock lock = null;
	private static Condition2 speakCon = null;
	private static Condition2 listenCon = null;
}
