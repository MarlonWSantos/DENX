package ufpa.facomp.gercom.denx;

import javafx.scene.chart.XYChart;

/**
 * Classe responsável por armazenar em séries as coordenadas de cada cluster
 * gerado e retornado pelo weka na classe Cluster.
 * @see Cluster
 */
public class Graphic {

	/** Armazena coordenadas de todos os motes ativos da rede. */
	private XYChart.Series<Number, Number> seriesNetwork;
	
	/** Armazena coordenadas dos motes do cluster 1. */
	private XYChart.Series<Number, Number> series1;
	
	/** Armazena coordenadas dos motes do cluster 2. */
	private XYChart.Series<Number, Number> series2;
	
	/** Armazena coordenadas dos motes do cluster 3. */
	private XYChart.Series<Number, Number> series3;
	
	/** Armazena coordenadas dos motes do cluster 4. */
	private XYChart.Series<Number, Number> series4;
	
	/** Armazena coordenadas dos motes do cluster 5. */
	private XYChart.Series<Number, Number> series5;
	
	/** Armazena coordenadas dos motes do cluster 6. */
	private XYChart.Series<Number, Number> series6;


	/**
	 * Cria série que armazena coordenadas de todos motes ativos da rede. 
	 */
	public void createSerieNetwork() {
		seriesNetwork = new XYChart.Series<>();
		seriesNetwork.setName("Network ");
	}

	/**
	 * Cria série que armazena coordenadas dos motes do cluster 1. 
	 */
	public void createSerieCluster1() {
		series1 = new XYChart.Series<>();
		series1.setName("Cluster 1 ");
	}

	/**
	 * Cria série que armazena coordenadas dos motes do cluster 2. 
	 */
	public void createSerieCluster2() {
		series2 = new XYChart.Series<>();
		series2.setName("Cluster 2 ");
	}

	/**
	 * Cria série que armazena coordenadas dos motes do cluster 3. 
	 */
	public void createSerieCluster3() {
		series3 = new XYChart.Series<>();
		series3.setName("Cluster 3 ");
	}

	/**
	 * Cria série que armazena coordenadas dos motes do cluster 4. 
	 */
	public void createSerieCluster4() {
		series4 = new XYChart.Series<>();
		series4.setName("Cluster 4 ");
	}

	/**
	 * Cria série que armazena coordenadas dos motes do cluster 5. 
	 */
	public void createSerieCluster5() {
		series5 = new XYChart.Series<>();
		series5.setName("Cluster 5 ");
	}

	/**
	 * Cria série que armazena coordenadas dos motes do cluster 6. 
	 */
	public void createSerieCluster6() {
		series6 = new XYChart.Series<>();
		series6.setName("Cluster 6 ");
	}


	/**
	 * Insere coordenadas dos motes ativos da rede.
	 *  
	 * @param X coordenada x do mote
	 * @param Y coordenada y do mote
	 */
	public void setCoordinatesSeriesNetwork(double X,double Y) {
		seriesNetwork.getData().add(new XYChart.Data<>(X, Y));
	}


	/**
	 * Insere coordenadas do cluster 1 na série 1 
	 * 
	 * @param X coordenada x do mote
	 * @param Y coordenada y do mote
	 */
	public void setCoordinatesSeries1(double X,double Y) {
		series1.getData().add(new XYChart.Data<>(X, Y));
	}

	/**
	 * Insere coordenadas do cluster 2 na série 2 
	 * 
	 * @param X coordenada x do mote
	 * @param Y coordenada y do mote
	 */
	public void setCoordinatesSeries2(double X,double Y) {
		series2.getData().add(new XYChart.Data<>(X, Y));
	}


	/**
	 * Insere coordenadas do cluster 3 na série 3 
	 * 
	 * @param X coordenada x do mote
	 * @param Y coordenada y do mote
	 */
	public void setCoordinatesSeries3(double X,double Y) {
		series3.getData().add(new XYChart.Data<>(X, Y));
	}

	
	/**
	 * Insere coordenadas do cluster 4 na série 4 
	 * 
	 * @param X coordenada x do mote
	 * @param Y coordenada y do mote
	 */
	public void setCoordinatesSeries4(double X,double Y) {
		series4.getData().add(new XYChart.Data<>(X, Y));
	}


	/**
	 * Insere coordenadas do cluster 5 na série 5 
	 * 
	 * @param X coordenada x do mote
	 * @param Y coordenada y do mote
	 */
	public void setCoordinatesSeries5(double X,double Y) {
		series5.getData().add(new XYChart.Data<>(X, Y));
	}


	/**
	 * Insere coordenadas do cluster 6 na série 6 
	 * 
	 * @param X coordenada x do mote
	 * @param Y coordenada y do mote
	 */
	public void setCoordinatesSeries6(double X,double Y) {
		series6.getData().add(new XYChart.Data<>(X, Y));
	}


	/**
	 * Retorna as coordenadas dos motes ativos da rede.
	 *  
	 * @return seriesNetwork coordenadas de motes ativos
	 */
	public XYChart.Series<Number, Number> getCoordinateSeriesNetwork() {
		return seriesNetwork;
	}

	/**
	 * Retorna as coordenadas do cluster 1.
	 *  
	 * @return series1 coordenadas dentro do cluster 1 
	 */
	public XYChart.Series<Number, Number> getCoordinateSeries1() {
		return series1;
	}

	/**
	 * Retorna as coordenadas do cluster 2.
	 *  
	 * @return series2 coordenadas dentro do cluster 2 
	 */
	public XYChart.Series<Number, Number> getCoordinateSeries2() {
		return series2;
	}

	/**
	 * Retorna as coordenadas do cluster 3.
	 *  
	 * @return series3 coordenadas dentro do cluster 3
	 */
	public XYChart.Series<Number, Number> getCoordinateSeries3() {
		return series3;
	}

	/**
	 * Retorna as coordenadas do cluster 4.
	 *  
	 * @return series4 coordenadas dentro do cluster 4 
	 */
	public XYChart.Series<Number, Number> getCoordinateSeries4() {
		return series4;
	}

	/**
	 * Retorna as coordenadas do cluster 5.
	 *  
	 * @return series5 coordenadas dentro do cluster 5 
	 */
	public XYChart.Series<Number, Number> getCoordinateSeries5() {
		return series5;
	}

	/**
	 * Retorna as coordenadas do cluster 6.
	 *  
	 * @return series6 coordenadas dentro do cluster 6 
	 */
	public XYChart.Series<Number, Number> getCoordinateSeries6() {
		return series6;
	}
}
