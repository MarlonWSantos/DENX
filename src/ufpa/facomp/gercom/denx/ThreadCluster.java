package ufpa.facomp.gercom.denx;

import java.io.File;
import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

/**
 * Classe responsável por criar threads para cada cluster.
 */
public class ThreadCluster implements Runnable{
	
	/** Arquivo com as coordenadas dos motes. */
	final static String PATH_CSV_FILE = "/tmp/motes_coordinates.csv";
	
	/** Objeto da classe Controller. */
	protected Controller control;
	
	/** Objeto da classe Routes Motes. */
	protected RoutesMotes routes;

	/**
	 * Construtor cria um thread e o inícia em seguida.
	 *  
	 * @param control Objeto da classe Controller
	 * @param routes Objeto da classe Routes Motes
	 */
	public ThreadCluster(Controller control,RoutesMotes routes) {
		this.control=control;
		this.routes=routes;

		//Cria thread para gerar os clusters
		Thread createCluster = new Thread(this,"Thread Cluster");		

		//Inicia o thread
		createCluster.start();		
	}

	@Override
	public void run() {

		//Carrega o arquivo CSV gerado pelo cooja no path indicado
		File file = new File(PATH_CSV_FILE);

		Cluster cluster = new Cluster();

		try {

			//Deixa o thread dentro do loop
			while(true) {

				//Se existir o arquivo CSV no path indicado
				if (file.exists()){

					//Pega a lista de IPs ativos já armazenados na plataforma
					cluster.getIPsActivesMotes(routes);

					//Converte de CSV para um array na memória a lista de IPs gerados pelo cooja 
					cluster.convertCSV2Array();

					//Lê o IP ativo da lista e busca no arquivo gerado pelo cooja suas coordenadas
					cluster.readEndAddressFindData();

					//Busca e armazena IPs e coordenadas de toda a rede de motes ativos
					cluster.calculateMetricNetwork();

					//Salva em CSV a lista com os IPs ativos e respectivas coordenadas
					cluster.savingActivesMotesInCSV();

					//Carrega o arquivo CSV com os IPs ativos e suas coordenadas e converte para ARFF
					cluster.loadCSV();

					//Carrega os dados com os IPs ativos e salva em formato ARFF
					cluster.saveARFF();

					//Carrega o arquivo ARFF e cria os clusters com base nos dados dos IPs e coordenadas 
					cluster.createClusters(control);


					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							//Chama a função que carrega os dados e insere no gráfico na GUI
							control.LoadGraphic();							
						}
					});

					//Cria um thread para calcular as métricas
					///new ThreadMetrics(control);

				}

				//Thread pausa por 1 minuto e volta ao começo
				Thread.sleep(60000);			
			}

		} catch (InterruptedException e) {
			new AlertsDialog(AlertType.ERROR,"Thread Interrupted",ButtonType.CLOSE);	
		} catch (IOException e) {
			new AlertsDialog(AlertType.ERROR, "Communication failure", ButtonType.CLOSE);
		}catch (Exception e) {
			new AlertsDialog(e);
		}
	}
}
