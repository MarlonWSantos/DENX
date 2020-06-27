package ufpa.facomp.gercom.denx;

import javafx.application.Platform;
import javafx.scene.chart.XYChart;

/**
 * Classe responsável por criar threads para calculo da métrica de cada cluster.
 */
public class ThreadMetrics implements Runnable {
	
	/** Objeto da classe Controller. */
	protected Controller control;
	
	/** Thread para cálculo da métrica da rede. */
	private static Thread NetworkMetric;
	
	/** Armazena threads para cálculo da métrica no(s) cluster(s). */
	private static Thread[] ClusterMetric;
		
	/** Armazena conjunto de objetos Metrics. */
	private static Metrics[] metric;
	
	/** Número de clusters criados. */
	static int numberClusters;
	
	/** Posição no array de objetos Metrics. */
	static int indexMetric;
	
	/** Flag informa se já há dados sobre métricas salvas. */
	static boolean hasNoMetricSaved = true;
	
	/** Armazena séries de coordenadas. */
	XYChart.Series<Number, Number> series;
	
	/** Número de motes dentro do cluster. */
	int motesOnCluster;	
	
	/** Coordenada X. */
	double coordX;
	
	/** Coordenada Y. */
	double coordY;
	
	/** Armazena as informações das métricas. */	
	static StringBuilder infoMetrics = new StringBuilder();


	/**
	 * Construtor para criar thread para calcular as métricas.
	 * 
	 * @param control objeto da classe Controller
	 * 
	 * @throws InterruptedException em caso de interrupção do thread
	 */
	public ThreadMetrics(Controller control) throws InterruptedException {
		this.control=control;

		//Apaga se houve alguma informação
		infoMetrics.delete(0, infoMetrics.length());
		
		numberClusters = Cluster.numberClusters;
		
		createThreadToCalculateNetworkMetric();
		
		createMetricsObjects();

		createThreadsToCalculateMetrics(numberClusters);

		defineThreadStarts(numberClusters);	

	}

	/**
	 * Cria Thread para calcular a métrica da rede inteira.
	 *  
	 * @throws InterruptedException em caso de interrupção do thread
	 */
	public void createThreadToCalculateNetworkMetric() throws InterruptedException {

		NetworkMetric = new Thread(this,"Thread NetworkMetric");

		if(hasNoMetricSaved) {
			int tam = numberClusters+1;
			metric = new Metrics[tam];
			metric[0] = new Metrics();
		}

		NetworkMetric.start();
		NetworkMetric.join();

	}

	/**
	 * Cria os Threads que farão o cálculo da métrica de acordo com número de clusters.
	 * 
	 * @param numberClusters número de clusters que foram criados
	 */
	public void createThreadsToCalculateMetrics(int numberClusters) {

		int count=1;
		for(int i=0;i<numberClusters;i++) {
			ClusterMetric[i] = new Thread(this,"Thread ClusterMetric "+count);
			count++;
		}
	}

	/**
	 * Define quantos e quais threads serão executados.
	 * 
	 * @param numberClusters número de clusters que foram criados
	 *  
	 * @throws InterruptedException em caso de interrupção do thread
	 */
	public void defineThreadStarts(int numberClusters ) throws InterruptedException{

		for(int i=0;i<numberClusters;i++) {
			ClusterMetric[i].start();
			ClusterMetric[i].join();
		}		
	}

	/**
	 * Carrega as coordenadas e index de cada cluster de acordo com Thread em execução. 
	 * 
	 * @return dataSeries todas as coordenadas armazenadas na série
	 */
	public XYChart.Series<Number, Number> loadDataSeries() {

		XYChart.Series<Number, Number> dataSeries = null;

		switch(Thread.currentThread().getName()) {
		case "Thread NetworkMetric":
			dataSeries =  Cluster.graphic.getCoordinateSeriesNetwork();
			indexMetric = 0;
			break;
		case "Thread ClusterMetric 1":
			dataSeries =  Cluster.graphic.getCoordinateSeries(1);
			indexMetric = 1;
			break;
		case "Thread ClusterMetric 2":
			dataSeries =  Cluster.graphic.getCoordinateSeries(2);
			indexMetric = 2;			
			break;
		case "Thread ClusterMetric 3":
			dataSeries =  Cluster.graphic.getCoordinateSeries(3);
			indexMetric = 3;			
			break;
		case "Thread ClusterMetric 4":
			dataSeries =  Cluster.graphic.getCoordinateSeries(4);
			indexMetric = 4;			
			break;
		case "Thread ClusterMetric 5":
			dataSeries =  Cluster.graphic.getCoordinateSeries(5);
			indexMetric = 5;			
			break;
		case "Thread ClusterMetric 6":
			dataSeries =  Cluster.graphic.getCoordinateSeries(6);
			indexMetric = 6;			
			break;
		case "Thread ClusterMetric 7":
			dataSeries =  Cluster.graphic.getCoordinateSeries(7);
			indexMetric = 7;			
			break;
		case "Thread ClusterMetric 8":
			dataSeries =  Cluster.graphic.getCoordinateSeries(8);
			indexMetric = 8;			
			break;
		case "Thread ClusterMetric 9":
			dataSeries =  Cluster.graphic.getCoordinateSeries(9);
			indexMetric = 9;			
			break;
		case "Thread ClusterMetric 10":
			dataSeries =  Cluster.graphic.getCoordinateSeries(10);
			indexMetric = 10;			
			break;
		}
		return dataSeries;
	}
	
	/**
	 * Cria os objetos da classe Metrics de acordo com o número de clusters.
	 */
	public void createMetricsObjects() {
		int tam = numberClusters;

		if(hasNoMetricSaved) {
			for(int i=1;i<=tam;i++) {
				metric[i] = new Metrics();
				hasNoMetricSaved = false;
			}
		}
	}
	

	@Override
	public void run() {

		//Busca as séries que armazenam as coordenadas
		series = loadDataSeries();

		//Armazena o número de motes em cada cluster
		motesOnCluster = series.getData().size();

		//Se houver menos de 3 motes no cluster
		if(motesOnCluster < 3 ) {

			infoMetrics.append(series.getName()+"\n");
			infoMetrics.append("Number of Motes: "+motesOnCluster+"\n");
			infoMetrics.append("Insuficients to metric!\n\n");

			//Se houver 3 motes ou mais no cluster
		}else {

			//Cria array que armazena os pontos do convex
			ConvexHull cluster_points[] = new ConvexHull[motesOnCluster]; 

			//Transfere as coordenadas armazenadas na série para o array do convexHull
			for(int i=0;i<motesOnCluster;i++) {
				coordX = (double) series.getData().get(i).getXValue();
				coordY = (double) series.getData().get(i).getYValue();
				cluster_points[i] = new ConvexHull(coordX, coordY);
			}

			//Tamanho do array convexHull
			int n = cluster_points.length;

			//Calcula o polígono
			ConvexHull.convexHull(cluster_points, n); 

			//Exibe as coordenadas que forma o polígono
			//ConvexHull.showPolygon();

			//Calcula o centro do polígono
			ConvexHull.calculateCenterPolygon();

			//Calcula os ângulos do polígono
			ConvexHull.calculateAnglePoints();

			//Exibe os ângulos do polígono
			//ConvexHull.showAngles();

			double areaCluster;

			//Armazena o resultado do cálculo da área do cluster
			areaCluster = AreaConvex.computeArea();

			//Limpa o array que armazena as coordenadas durante o cálculo do convexHull
			ConvexHull.clearHull();
			

			//Envia o número de motes para usar no cálculo da métrica
			metric[indexMetric].setMotesOnCluster(motesOnCluster);

			//Envia a área do cluster para usar no cálculo da métrica
			metric[indexMetric].setArea(areaCluster);

			//Recebe e armazena o resultado do cálculo da métrica
			double resultMetric = metric[indexMetric].calculateMetrics();
			double resultMovAVG = metric[indexMetric].movingAverage(resultMetric);

			//Armazena as informações da métrica
			saveInformationMetric(metric[indexMetric],areaCluster,resultMetric,resultMovAVG);
			
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					//Envia as informações da métrica para a GUI
					control.showInformationMetrics(infoMetrics);				
				}
			});		
		}
	}

	/**
	 * Armazena a informação da métrica que será exibida na GUI.
	 * 
	 * @param metric objeto da classe Metrics
	 * @param areaCluster área do cluster
	 * @param resultMetric resultado do cálculo da métrica 
	 * @param movingAverage resultado da média móvel 
	 */
	public void saveInformationMetric(Metrics metric, double areaCluster, double resultMetric, double movingAverage) {

		if(Thread.currentThread().getName()=="Thread NetworkMetric") {
			infoMetrics.append(series.getName()+"\n");
			infoMetrics.append("Motes on Network: "+motesOnCluster+"\n");
			infoMetrics.append("Range Wireless:  "+metric.getRangeWireless()+"\n");
			infoMetrics.append("Network's area: "+areaCluster+"\n");
			infoMetrics.append("Result Metric for Network: "+resultMetric+"\n");
			infoMetrics.append("Moving Average(Last 5 min.): "+movingAverage+"\n\n");
		}else {
			infoMetrics.append(series.getName()+"\n");
			infoMetrics.append("Motes on Cluster: "+motesOnCluster+"\n");
			infoMetrics.append("Range Wireless:  "+metric.getRangeWireless()+"\n");
			infoMetrics.append("Cluster's area: "+areaCluster+"\n");
			infoMetrics.append("Result Metric for Cluster: "+resultMetric+"\n");
			infoMetrics.append("Moving Average(Last 5 min.): "+movingAverage+"\n\n");
		}
	}
}
