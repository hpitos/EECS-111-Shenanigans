package nachos.threads;

import java.util.LinkedList;

import nachos.machine.*;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
	/**
	 * Allocate a new Alarm. Set the machine's timer interrupt handler to this
	 * alarm's callback.
	 * 
	 * <p>
	 * <b>Note</b>: Nachos will not function correctly with more than one alarm.
	 */
	public Alarm() {
		Machine.timer().setInterruptHandler(new Runnable() {
			public void run() {
				timerInterrupt();
			}
		});
	}

	/**
	 * The timer interrupt handler. This is called by the machine's timer
	 * periodically (approximately every 500 clock ticks). Causes the current
	 * thread to yield, forcing a context switch if there is another thread that
	 * should be run.
	 */
	public void timerInterrupt() {
		
		if (wakeTime.size() != 0 && waitQueue.size() != 0) {
		
			for (int i = 0; i < wakeTime.size(); i++) {
				if (wakeTime.get(i) <= Machine.timer().getTime()) {
				
					boolean intStatus = Machine.interrupt().disable();
					
					waitQueue.remove(i).ready();
					wakeTime.remove(i);
					
					Machine.interrupt().restore(intStatus);
				}
			}
		}
	}

	/**
	 * Put the current thread to sleep for at least <i>x</i> ticks, waking it up
	 * in the timer interrupt handler. The thread must be woken up (placed in
	 * the scheduler ready set) during the first timer interrupt where
	 * 
	 * <p>
	 * <blockquote> (current time) >= (WaitUntil called time)+(x) </blockquote>
	 * 
	 * @param x the minimum number of clock ticks to wait.
	 * 
	 * @see nachos.machine.Timer#getTime()
	 */
	public void waitUntil(long x) {
		
		wakeTime.add(new Long(Machine.timer().getTime() + x));
		
		waitQueue.add(KThread.currentThread());
		
		boolean intStatus = Machine.interrupt().disable();
		
		KThread.sleep();
		
		Machine.interrupt().restore(intStatus);
		
	}
	
	private LinkedList<KThread> waitQueue = new LinkedList<KThread>();
	private LinkedList<Long> wakeTime = new LinkedList<Long>();
	
}
