package ufpa.facomp.gercom.iipdn;

import javafx.collections.ObservableList;

public class ThreadsObserve implements Runnable {

	protected static Observe obs = new Observe();
	protected Controller control;
	private String url;
	private String nomeThread;

	//Contrutor para criação de thread que para a observação
	public ThreadsObserve( ) {

		//Cria o thread
		Thread stopObserve = new Thread(new Runnable() {

			@Override
			public void run() {

				synchronized (obs) {
					//Notifica os threads anteriores para parar a observação
					obs.notifyAll();					
				}				
			}
		});

		//Thread inicia execução
		stopObserve.start();
	}


	//Contrutor para criação de thread que fará observação de recurso 
	public ThreadsObserve(Controller control,String url,String nomeThread) {
		this.nomeThread=nomeThread;
		this.url = url;
		this.control=control;

		//Cria o thread
		Thread beginObserve = new Thread(this,nomeThread);		

		//Thread inicia execução
		beginObserve.start();
	}


	@Override
	public void run() {

		//Se o thread for individal, chama a função pra observar uma única URL 
		if(Thread.currentThread().getName().equalsIgnoreCase("Thread Observe")) {
			obs.observe(url,control);
		}

		//Se o thread fizer parte de um grupo, chama a função para observar um grupo de URLs
		if(Thread.currentThread().getName().equalsIgnoreCase("Thread Observe Group")) {
			obs.observeGroup(url,control);
		}
	}
}
