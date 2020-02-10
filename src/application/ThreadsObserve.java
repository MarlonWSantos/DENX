package application;

import com.sun.webkit.ContextMenu.ShowContext;

import javafx.application.Platform;

public class ThreadsObserve implements Runnable {
	
	
	protected static Observe obs = new Observe();
	private String url;
	private String nomeThread;
	
	//protected static Controller control = new Controller();
	protected static Controller control;

	




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
		Thread refresh = new Thread(this,"refresh terminal");
		
		beginObserve.start();
		

		
		refresh.start();
	}


	@Override
	public void run() {
		

		if(Thread.currentThread().getName().equalsIgnoreCase("Thread Obs #1")) {

			obs.observe(url);
				

		}

		
		
		if(Thread.currentThread().getName().equalsIgnoreCase("refresh terminal")) {

			Platform.runLater(new Runnable() {
				@Override
				public void run() {

							control.show();

					
				}
			});

		}
		
	}

}
