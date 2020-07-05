package ufpa.facomp.gercom.denx;

import javafx.scene.chart.XYChart;

/**
 * Classe responsável por armazenar em séries as coordenadas de cada cluster
 * gerado e retornado pelo weka na classe Cluster.
 * @see Cluster.java
 */

public class Graphic {

	
	/** Armazena coordenadas de todos os motes ativos da rede. */
	private XYChart.Series<Number, Number> seriesNetwork;
	
	/** Array armazena coordenadas dos motes dos clusters. */
	private XYChart.Series<Number,Number>[] series;

	
	/**
	 * Cria série que armazena coordenadas de todos motes ativos da rede. 
	 */
	public void createSerieNetwork() {
		seriesNetwork = new XYChart.Series<>();
		seriesNetwork.setName("Network ");
	}

	/**
	 * Cria série(s) que armazena(m) coordenadas dos motes do(s) respectivo(s) cluster(s).
	 * 
	 *  @param numberClusters número de cluster
	 */
	public void createSerieCluster(int numberClusters) {
		int count=1;
		
		series = new XYChart.Series[numberClusters];
		
		for(int i=0;i<numberClusters;i++) {
			series[i] = new XYChart.Series<>();
			series[i].setName("Cluster "+count+" ");
			count++;
		}
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
	 * Insere coordenadas do(s) cluster(s) na(s) série(s) 
	 * 
	 * @param X coordenada x do mote
	 * @param Y coordenada y do mote
	 * @param clusterNum index do cluster criado pela classe Cluster
	 */
	public void setCoordinatesSeries(double X,double Y,int clusterNum) {
		series[clusterNum].getData().add(new XYChart.Data<>(X, Y));
		
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
	 * Retorna as coordenadas do cluster definido.
	 * 
	 * @param clusterNum número do respectivo cluster
	 *  
	 * @return series1 coordenadas dentro do cluster 1
	 * @return series2 coordenadas dentro do cluster 2 
	 * @return series3 coordenadas dentro do cluster 3 
	 * @return series4 coordenadas dentro do cluster 4 
	 * @return series5 coordenadas dentro do cluster 5 
	 * @return series6 coordenadas dentro do cluster 6 
	 * @return series7 coordenadas dentro do cluster 7 
	 * @return series8 coordenadas dentro do cluster 8 
	 * @return series9 coordenadas dentro do cluster 9
 	 * @return series10 coordenadas dentro do cluster 10 
	 */
	public XYChart.Series<Number, Number> getCoordinateSeries(int clusterNum) {
		
		XYChart.Series<Number, Number> dataSeries = null;
		
		switch(clusterNum) {
		case 1:
			dataSeries = series[0];
			break;
		case 2:
			dataSeries =  series[1];
			break;
		case 3:
			dataSeries =  series[2];
			break;
		case 4:
			dataSeries =  series[3];
			break;
		case 5:
			dataSeries =  series[4];
			break;
		case 6:
			dataSeries =  series[5];
			break;
		case 7:
			dataSeries =  series[6];
			break;
		case 8:
			dataSeries =  series[7];
			break;
		case 9:
			dataSeries =  series[8];
			break;
		case 10:
			dataSeries =  series[9];
			break;
		}		
		return dataSeries;
	}
}
