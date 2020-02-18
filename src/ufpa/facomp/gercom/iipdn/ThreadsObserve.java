package ufpa.facomp.gercom.iipdn;

import javafx.collections.ObservableList;

public class ThreadsObserve implements Runnable {


	protected static Observe obs = new Observe();
	protected Controller control;
	private String url;
	private String nomeThread;




	public ThreadsObserve( ) {
		Thread stopObserve = new Thread(new Runnable() {

			@Override
			public void run() {

				synchronized (obs) {
					obs.notifyAll();					
				}				
			}
		});

		stopObserve.start();
	}


	public ThreadsObserve(Controller control,String url,String nomeThread) {
		this.nomeThread=nomeThread;
		this.url = url;
		this.control=control;

		Thread beginObserve = new Thread(this,nomeThread);		

		beginObserve.start();		

	}


	public ThreadsObserve(String url,String nomeThread) {
		this.nomeThread=nomeThread;
		this.url = url;


		Thread beginObserveGroup = new Thread(this,nomeThread);

		beginObserveGroup.start();		



	}


	@Override
	public void run() {

		if(Thread.currentThread().getName().equalsIgnoreCase("Thread Observe")) {
			obs.observe(url,control);
		}

		if(Thread.currentThread().getName().equalsIgnoreCase("Thread Observe Group")) {
			obs.observeGroup(url);
		}


	}

}
