package ufpa.facomp.gercom.denx;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por calcular as métricas da rede e dos clusters. 
 */
public class Metrics{
	
	/** Número de motes dentro do cluster. */
	private int motesOnCluster;
	
	/** Cobertura wireless do mote. */
	final static int RANGE_WIRELESS=100;
	
	/** Área do cluster. */
	private double areaCluster;
	
	/** Contador da últimos cálculos das métricas. */
	private int countMetrics;
	
	/** Armazena a média móvel. */
	private double movingAverage;
	
	/** Armazena os últimos minutos de informação das métricas. */
	private List<Double> metricList;
	
	/** Quanto minutos serão armazenados na lista. */
	final static int MOVING_AVG_LAST_MINUTES = 5; 
	
	/**
	 * Construtor para calcular as métricas.
	 */
	public Metrics() {
		metricList = new ArrayList<Double>();
		countMetrics=0;
		movingAverage=0.0;
	}

	
	/**
	 * Armazena o número de motes no cluster.
	 *  
	 * @param motesOnCluster número de motes no cluster
	 */
	public void setMotesOnCluster(int motesOnCluster){
		this.motesOnCluster=motesOnCluster;
	}


	/**
	 * Retorna o número de motes no cluster.
	 *  
	 * @return motesOnCluster número de motes no cluster
	 */
	public int getMotesOnCluster(){
		return motesOnCluster; 
	}

	
	/**
	 * Retorna a cobertura wireless dos motes.
	 * 
	 * @return RANGE_WIRELESS cobertura wireless do mote
	 */
	public int getRangeWireless(){
		return RANGE_WIRELESS;
	}

	
	/**
	 * Armazena a área do cluster.
	 *  
	 * @param areaCluster área dentro cluster
	 */
	public void setArea(double areaCluster){
		this.areaCluster=areaCluster;
	}

	
	/**
	 * Retorna a área do cluster.
	 *  
	 * @return areaCluster área dentro cluster
	 */
	public double getAreaCluster(){
		return areaCluster;
	}

	
	/**
	 * Calcula a métrica do cluster.
	 * 
	 * @return result resultado do cálculo da métrica
	 */
	public double calculateMetrics() {

		double result = 0;

		result = (getMotesOnCluster() * getRangeWireless())/getAreaCluster();

		return result;
	}
	
	/**
	 * Chama os processos que executaram o cálculo da média móvel.
	 * 
	 * @param metric resultado da métrica
	 * @return movingAverage resultado da média móvel
	 */
	public double movingAverage(double metric) {				
		
		if(countMetrics <= MOVING_AVG_LAST_MINUTES-1 ) {
			metricList.add(metric);
			++countMetrics;
		}else {
			updateListMetric(metric);
		}
		
		boolean isFull = metricListIsFull();
		
		if(isFull) {
			this.movingAverage = calcMovingAverage();
		}
		
		return this.movingAverage;
	}

	/**
	 * Verifica se a lista com as métricas está cheia.
	 * 
	 * @return true se estiver cheia,
	 * 		   false se não estiver cheia
	 */
	public boolean metricListIsFull() {		
		if(metricList.size() == MOVING_AVG_LAST_MINUTES) {
			return true;
		}else {
			return false;	
		}
	}
	
	/**
	 * Atualiza a lista com os últimos resultados das métricas.
	 * 
	 * @param metric resultado da métrica
	 */
	public void updateListMetric(double metric) {
		metricList.remove(0);
		metricList.add(metric);
	}
	
	/**
	 * Calcula a média móvel.
	 * 
	 * @return movingAverage  resultado da média móvel
	 */
	public double calcMovingAverage() {		
		double sum=0.0;	
		
		for (double arr : metricList) {
			sum += arr;
		}
		movingAverage=sum/metricList.size();
		
		return movingAverage;
	}
}

