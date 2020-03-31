package ufpa.facomp.gercom.iipdn;

import javafx.application.Platform;
import javafx.scene.chart.XYChart;

public class ThreadMetrics implements Runnable {
	protected Controller control;
	private static Thread NetworkMetric;
	private static Thread ClusterMetric1;		
	private static Thread ClusterMetric2;
	private static Thread ClusterMetric3;
	private static Thread ClusterMetric4;
	private static Thread ClusterMetric5;
	private static Thread ClusterMetric6;
	static int numberClusters;
	XYChart.Series<Number, Number> series;
	int motesOnCluster;	
	double coordX;
	double coordY;
	static StringBuilder infoMetrics = new StringBuilder();;

	//Construtor
	public ThreadMetrics(Controller control) throws InterruptedException {
		this.control=control;

		createThreadToCalculateNetworkMetric();

		createThreadsToCalculateMetrics();

		defineThreadStarts();
	}

	//Cria Thread para calcular a métrica da rede inteira
	public void createThreadToCalculateNetworkMetric() throws InterruptedException {

		NetworkMetric = new Thread(this,"Thread NetworkMetric");

		NetworkMetric.start();
		NetworkMetric.join();

	}

	//Cria os Threads que farão o cálculo da métrica de acordo com número de clusters
	public void createThreadsToCalculateMetrics() {
		numberClusters = Cluster.numberClusters;

		switch (numberClusters) {
		case 1:
			ClusterMetric1 = new Thread(this,"Thread ClusterMetric 1");
			break;
		case 2:
			ClusterMetric1 = new Thread(this,"Thread ClusterMetric 1");
			ClusterMetric2 = new Thread(this,"Thread ClusterMetric 2");
			break;
		case 3:
			ClusterMetric1 = new Thread(this,"Thread ClusterMetric 1");		
			ClusterMetric2 = new Thread(this,"Thread ClusterMetric 2");
			ClusterMetric3 = new Thread(this,"Thread ClusterMetric 3");
			break;
		case 4:
			ClusterMetric1 = new Thread(this,"Thread ClusterMetric 1");		
			ClusterMetric2 = new Thread(this,"Thread ClusterMetric 2");
			ClusterMetric3 = new Thread(this,"Thread ClusterMetric 3");
			ClusterMetric4 = new Thread(this,"Thread ClusterMetric 4");
			break;
		case 5:
			ClusterMetric1 = new Thread(this,"Thread ClusterMetric 1");		
			ClusterMetric2 = new Thread(this,"Thread ClusterMetric 2");
			ClusterMetric3 = new Thread(this,"Thread ClusterMetric 3");
			ClusterMetric4 = new Thread(this,"Thread ClusterMetric 4");
			ClusterMetric5 = new Thread(this,"Thread ClusterMetric 5");
			break;
		case 6:
			ClusterMetric1 = new Thread(this,"Thread ClusterMetric 1");		
			ClusterMetric2 = new Thread(this,"Thread ClusterMetric 2");
			ClusterMetric3 = new Thread(this,"Thread ClusterMetric 3");
			ClusterMetric4 = new Thread(this,"Thread ClusterMetric 4");
			ClusterMetric5 = new Thread(this,"Thread ClusterMetric 5");
			ClusterMetric6 = new Thread(this,"Thread ClusterMetric 6");
			break;			
		}
	}

	//Define quantos e quais threads serão executados
	public void defineThreadStarts() throws InterruptedException{

		if(numberClusters==1) {
			ClusterMetric1.start();
			ClusterMetric1.join();
		}
		if(numberClusters==2) {
			ClusterMetric1.start();
			ClusterMetric1.join();
			ClusterMetric2.start();
			ClusterMetric2.join();
		}
		if(numberClusters==3) {
			ClusterMetric1.start();
			ClusterMetric1.join();
			ClusterMetric2.start();
			ClusterMetric2.join();
			ClusterMetric3.start();
			ClusterMetric3.join();
		}
		if(numberClusters==4) {
			ClusterMetric1.start();
			ClusterMetric1.join();
			ClusterMetric2.start();
			ClusterMetric2.join();
			ClusterMetric3.start();
			ClusterMetric3.join();
			ClusterMetric4.start();
			ClusterMetric4.join();
		}
		if(numberClusters==5) {
			ClusterMetric1.start();
			ClusterMetric1.join();
			ClusterMetric2.start();
			ClusterMetric2.join();
			ClusterMetric3.start();
			ClusterMetric3.join();
			ClusterMetric4.start();
			ClusterMetric4.join();
			ClusterMetric5.start();
			ClusterMetric5.join();
		}
		if(numberClusters==6) {
			ClusterMetric1.start();
			ClusterMetric1.join();
			ClusterMetric2.start();
			ClusterMetric2.join();
			ClusterMetric3.start();
			ClusterMetric3.join();
			ClusterMetric4.start();
			ClusterMetric4.join();
			ClusterMetric5.start();
			ClusterMetric5.join();
			ClusterMetric6.start();
			ClusterMetric6.join();
		}		
	}

	//Carrega as coordenadas de cada cluster de acordo com Thread em execução
	public XYChart.Series<Number, Number> loadDataSeries() {

		XYChart.Series<Number, Number> dataSeries = null;

		if(Thread.currentThread().getName()=="Thread NetworkMetric") {

			dataSeries =  Cluster.graphic.getCoordinateSeriesNetwork();

		}else if(Thread.currentThread().getName()=="Thread ClusterMetric 1") {

			dataSeries =  Cluster.graphic.getCoordinateSeries1();

		}else if(Thread.currentThread().getName()=="Thread ClusterMetric 2") {

			dataSeries =  Cluster.graphic.getCoordinateSeries2();

		}else if(Thread.currentThread().getName()=="Thread ClusterMetric 3") {

			dataSeries =  Cluster.graphic.getCoordinateSeries3();

		}else if(Thread.currentThread().getName()=="Thread ClusterMetric 4") {

			dataSeries =  Cluster.graphic.getCoordinateSeries4();

		}else if(Thread.currentThread().getName()=="Thread ClusterMetric 5") {

			dataSeries =  Cluster.graphic.getCoordinateSeries5();

		}else if(Thread.currentThread().getName()=="Thread ClusterMetric 6") {

			dataSeries =  Cluster.graphic.getCoordinateSeries6();
		}
		return dataSeries;
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
			infoMetrics.append("Motes on Cluster: "+motesOnCluster+"\n");
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
			ConvexHull.showPolygon();

			//Calcula o centro do polígono
			ConvexHull.calculateCenterPolygon();

			//Calcula os ângulos do polígono
			ConvexHull.calculateAnglePoints();

			//Exibe os ângulos do polígono
			ConvexHull.showAngles();

			double areaCluster;

			//Armazena o resultado do cálculo da área do cluster
			areaCluster = AreaConvex.computeArea();

			//Limpa o array que armazena as coordenadas durante o cálculo do convexHull
			ConvexHull.clearHull();

			Metrics metric = new Metrics();

			//Envia o número de motes para usar no cálculo da métrica
			metric.setMotesOnCluster(motesOnCluster);

			//Envia a área do cluster para usar no cálculo da métrica
			metric.setArea(areaCluster);

			//Recebe e armazena o resultado do cálculo da métrica
			double resultMetric = metric.calculateMetrics();

			//Armazena as informações da métrica
			infoMetrics.append(series.getName()+"\n");
			infoMetrics.append("Motes on Cluster: "+motesOnCluster+"\n");
			infoMetrics.append("Range Wireless:  "+metric.getRangeWireless()+"\n");
			infoMetrics.append("Cluster's area: "+areaCluster+"\n");
			infoMetrics.append("Result Metric: "+resultMetric+"\n\n");

			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					//Envia as informações da métrica para a GUI
					control.showInformationMetrics(infoMetrics);				
				}
			});		
		}
	}
}
