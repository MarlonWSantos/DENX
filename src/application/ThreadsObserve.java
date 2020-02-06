package application;


public class ThreadsObserve implements Runnable {

	protected static Observe obs = new Observe();
	private String url;


	public ThreadsObserve() {

		Thread t2 = new Thread(new Runnable() {

			@Override
			public void run() {

				synchronized (obs) {

					obs.notifyAll();
				}	

			}
		});

		t2.start();
	}


	public ThreadsObserve(String url) {
		this.url = url;
		Thread t1 = new Thread(this);
		t1.start();
	}


	@Override
	public void run() {

		obs.observe(url);

	}

}
