package ufpa.facomp.gercom.iipdn;

import javafx.collections.ObservableList;

/**
 * Classe responsável por criar threads para observar grupos numa rede,
 * onde cada thread irá observar um mote único, porém todos serão executados
 * de forma simultânea, dessa forma observando todos os membros do grupo.
 */
public class ThreadsObserve implements Runnable {

	/** Objeto da classe Observe. */
	protected static Observe obs = new Observe();
	
	/** Objeto da classe Controller. */
	protected Controller control;
	
	/** Armazena a URL que será observada. */
	private String url;
	
	/** Armazena o nome do thread a ser executado. */
	private String nomeThread;


	/**
	 * Contrutor para criação de thread que termina a observação 
	 */
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


	/**
	 * Contrutor para criação de thread que fará observação de recurso
	 *  
	 * @param control Objeto da classe Controller
	 * @param url URL do recurso a ser observado
	 * @param nomeThread nome do thread criado
	 */
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
