package ufpa.facomp.gercom.iipdn;

import java.util.ArrayList;
import java.util.List;

public class Metrics{
	private int motesOnCluster;
	final static int RANGE_WIRELESS=100;
	private double areaCluster;
	private int countMetrics;
	private double movingAverage;
	private List<Double> metricList;
	final static int MOVING_AVG_LAST_MINUTES = 3; 
	
	public Metrics() {
		metricList = new ArrayList<Double>();
		countMetrics=0;
		movingAverage=0.0;
	}

	//Armazena o número de motes no cluster
	public void setMotesOnCluster(int motesOnCluster){
		this.motesOnCluster=motesOnCluster;
	}

	//Retorna o número de motes no cluster
	public int getMotesOnCluster(){
		return motesOnCluster; 
	}

	//Retorna a cobertura wireless dos motes
	public int getRangeWireless(){
		return RANGE_WIRELESS;
	}

	//Armazena a área do cluster
	public void setArea(double areaCluster){
		this.areaCluster=areaCluster;
	}

	//retorna a área do cluster
	public double getAreaCluster(){
		return areaCluster;
	}

	//Calcula a métrica do cluster
	public double calculateMetrics() {

		double result = 0;

		result = (getMotesOnCluster() * getRangeWireless())/getAreaCluster();

		return result;
	}
	
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

	public boolean metricListIsFull() {		
		if(metricList.size() == MOVING_AVG_LAST_MINUTES) {
			return true;
		}else {
			return false;	
		}
	}
	
	public void updateListMetric(double metric) {
		metricList.remove(0);
		metricList.add(metric);
	}
	
	public double calcMovingAverage() {		
		double sum=0.0;	
		
		for (double arr : metricList) {
			sum += arr;
		}
		System.out.println(metricList.toString());
		System.out.println("Result moving Average: "+sum/metricList.size());
		movingAverage=sum/metricList.size();
		
		return movingAverage;
	}
}

