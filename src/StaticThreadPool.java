

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.locks.*;

public final class StaticThreadPool{
	private boolean debug = false;
	private WaitingRunnableQueue queue = null;
	private static StaticThreadPool instance=null;
	private Vector<ThreadPoolThread> availableThreads = null;
	private static ReentrantLock lock=new ReentrantLock();

	public StaticThreadPool(int maxThreadNum, boolean debug){
		this.debug = debug;
		queue = new WaitingRunnableQueue(this);
		availableThreads = new Vector<ThreadPoolThread>();
		for(int i = 0; i < maxThreadNum; i++){
			ThreadPoolThread th = new ThreadPoolThread(this, queue, i);
			availableThreads.add(th);
			th.start();
		}
	}

	public void shutdown(){
		System.out.println("Shutting Down");
		for(ThreadPoolThread t: availableThreads){
		
			t.setStopped(true);
			t.interrupt();
			
		}
	}
	
	public static StaticThreadPool getInstance(int max,Boolean b){ 
		lock.lock(); 
		try{
		if(instance==null) {
		//	System.out.println("Creating new instance");
			instance = new StaticThreadPool(max,b); 
		}else{
		//	System.out.println("Using same instance");
		}
		}finally{
		lock.unlock(); 
		}
		return instance; 
		} 
	
	
	
	public void execute(Runnable runnable){
		queue.put(runnable);
	}

	public int getWaitingRunnableQueueSize(){
		ReentrantLock lock=queue.getLock();
		lock.lock();
		try{
		return queue.size();
		}
		finally{
			lock.unlock();
		}
	}

	public int getThreadPoolSize(){
		return availableThreads.size();
	}


	private class WaitingRunnableQueue{
		private ArrayList<Runnable> runnables = new ArrayList<Runnable>();
		private StaticThreadPool pool;
		private ReentrantLock queueLock;
		private Condition runnablesAvailable;

		public WaitingRunnableQueue(StaticThreadPool pool){
			this.pool = pool;
			queueLock = new ReentrantLock();
			runnablesAvailable = queueLock.newCondition();
		}

		public int size(){
			queueLock.lock();
			try{
			return runnables.size();
			}
			finally{
				queueLock.unlock();
			}
		}
		
		
		public ReentrantLock getLock(){
			return queueLock;
		}
		
		
		public void put(Runnable obj){
			queueLock.lock();
			try{
				runnables.add(obj);
				if(pool.debug==true) System.out.println("A runnable queued.");
				runnablesAvailable.signalAll();
			}
			finally{
				queueLock.unlock();
			}
		}

		public Runnable get(){
			queueLock.lock();
			try{
				while(runnables.isEmpty()){
					if(pool.debug==true) System.out.println("Waiting for a runnable...");
					runnablesAvailable.await();
				}
				if(pool.debug==true) System.out.println("A runnable dequeued.");
				return runnables.remove(0);
			}
			catch(InterruptedException ex){
				return null;
			}
			finally{
				queueLock.unlock();
			}
		}
	}


	private class ThreadPoolThread extends Thread{
		private StaticThreadPool pool;
		private WaitingRunnableQueue queue;
		private int id;
		private Boolean stopped=false;
		private  ReentrantLock tlock=new ReentrantLock();

		public Boolean getStopped() {
			return stopped;
		}

		public void setStopped(Boolean stopped) {
			tlock.lock();
			try{
			this.stopped = stopped;
			}
			finally{
				tlock.unlock();
			}
		}

		public ThreadPoolThread(StaticThreadPool pool, WaitingRunnableQueue queue, int id){
			this.pool = pool;
			this.queue = queue;
			this.id = id;
		}

		public void run(){
			if(pool.debug==true) System.out.println("Thread " + id + " starts.");
			if(stopped){
				return;
			}
			while(true){
				Runnable runnable = queue.get();
				if(runnable==null){
					if(pool.debug==true)
						System.out.println("Thread " + this.id + " is being stopped due to an InterruptedException.");
					continue;
				}
				else{
					if(pool.debug==true) System.out.println("Thread " + id + " executes a runnable.");
					runnable.run();
					if(pool.debug == true)
						System.out.println("ThreadPoolThread " + id + " finishes executing a runnable.");
				}
			}
//			if(pool.debug==true) System.out.println("Thread " + id + " stops.");
		}
	}
	
/*	public static void main(String[] args){
		StaticThreadPool pool = StaticThreadPool.getInstance(2, true);
		pool.execute(new RunnableTest("a"));
		pool.execute(new RunnableTest("b"));
		pool.execute(new RunnableTest("c"));
		pool.execute(new RunnableTest("d"));
		
		pool.shutdown();
	}*/
}
