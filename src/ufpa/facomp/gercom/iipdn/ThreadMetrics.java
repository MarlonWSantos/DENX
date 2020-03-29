package ufpa.facomp.gercom.iipdn;

import javafx.application.Platform;
import javafx.scene.chart.XYChart;

public class ThreadMetrics implements Runnable {
	private static Controller control;
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
	static StringBuilder infoMetrics;
	

	public ThreadMetrics(Controller control) throws InterruptedException {
		this.control=control;

		createThreadsToCalculateMetrics();
		
		defineThreadStarts();
	}

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

	public XYChart.Series<Number, Number> loadDataSeries() {

		XYChart.Series<Number, Number> dataSeries = null;

		if(Thread.currentThread().getName()=="Thread ClusterMetric 1") {

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
		
		series = loadDataSeries();

		motesOnCluster = series.getData().size();

		//Cria os pontos do convex
		ConvexHull cluster_points[] = new ConvexHull[motesOnCluster]; 

		//Fórmula: N * wireless / área		

		for(int i=0;i<motesOnCluster;i++) {
			coordX = (double) series.getData().get(i).getXValue();
			coordY = (double) series.getData().get(i).getYValue();
			cluster_points[i] = new ConvexHull(coordX, coordY);
		}
		
		int n = cluster_points.length;

		//Calcula o poligono
		ConvexHull.convexHull(cluster_points, n); 

		ConvexHull.showPolygon();

		ConvexHull.calculateCenterPolygon();

		ConvexHull.calculateAnglePoints();

		ConvexHull.showAngles();

		double areaCluster;
		
		areaCluster = AreaConvex.computeArea();
		
		Metrics metric = new Metrics();
		
		metric.setMotesOnCluster(motesOnCluster);
		metric.setArea(areaCluster);
		double resultMetric = metric.calculateMetrics();
		
		infoMetrics.append(series.getName()+"\n");
		infoMetrics.append("Motes on Cluster: "+motesOnCluster+"\n");
		infoMetrics.append("Range Wireless:  "+metric.getRangeWireless());
		infoMetrics.append("Cluster's area: "+areaCluster);
		infoMetrics.append("Result Metric: "+resultMetric+"\n");
		
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				control.showInformationMetrics(infoMetrics);				
			}
		});		
	}
}
