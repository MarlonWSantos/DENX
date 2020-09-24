package ufpa.facomp.gercom.denx;

import java.util.ArrayList;

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

	/** Armazena um array de URL que serão observados. */
	private String[] urls;

	/** Armazena a lista de URL que serão observadas. */
	private ArrayList<String> listURL;

	/** Armazena o nome do thread a ser executado. */
	private String nomeThread;

	/** Index para o array de URL em observação de grupo. */
	static int index=0;

	/** Array para threads na observação em grupo. */
	private Thread[] beginObserve;


	/**
	 * Construtor para criação de thread que termina a observação 
	 */
	public ThreadsObserve( ) {

		//Cria o thread
		Thread stopObserve = new Thread(new Runnable() {

			@Override
			public void run() {

				synchronized (obs) {

					//Notifica os threads anteriores para parar a observação
					obs.notifyAll();

					//Zera a posição para array de URL dos threads 
					index=0;
				}				
			}
		});

		//Thread inicia execução
		stopObserve.start();
	}


	/**
	 * Construtor para criação de thread que fará observação de único recurso
	 *  
	 * @param control Objeto da classe Controller
	 * @param url URL do recurso a ser observado
	 * @param nomeThread nome do thread criado
	 */
	public ThreadsObserve(Controller control,String url,String nomeThread){
		this.nomeThread=nomeThread;
		this.url = url;
		this.control=control;

		//Cria o thread
		Thread beginObserve = new Thread(this,nomeThread);		

		//Thread inicia execução
		beginObserve.start();			
	}

	/**
	 * Construtor para criação de thread que fará observação de grupos
	 *  
	 * @param control Objeto da classe Controller
	 * @param listaURL Todas as URL de recursos na lista
	 * @param nomeThread nome do thread criado
	 */
	public ThreadsObserve(Controller control,ArrayList<String> listURL,String nomeThread){
		this.nomeThread=nomeThread;
		this.control=control;
		this.listURL=listURL;

		int sizeListURL = listURL.size();

		beginObserve = new Thread[sizeListURL];

		urls = new String[sizeListURL];

		for(int i=0;i<sizeListURL;i++) {
			//Cria o(s) thread(s)
			beginObserve[i] = new Thread(this,nomeThread);
			//Separa e armazena a(s) URL do(s) recurso(s)
			urls[i]=listURL.get(i);
		}

		//Inicia o(s) thread(s)
		for(int i=0;i<sizeListURL;i++) {
			beginObserve[i].start();
		}						
	}


	@Override
	public void run() {

		//Se o thread for individal, chama a função pra observar uma única URL 
		if(Thread.currentThread().getName().equalsIgnoreCase("Thread Observe")) {
			obs.observe(url,control);
		}

		//Se o thread fizer parte de um grupo, chama a função para observar um grupo de URLs
		if(Thread.currentThread().getName().equalsIgnoreCase("Thread Observe Group")) {
			obs.observeGroup(urls[index++],control);
		}				
	}
}
