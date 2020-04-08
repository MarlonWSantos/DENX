package ufpa.facomp.gercom.iipdn;

public class Metrics{
	private int motesOnCluster;
	final static int RANGE_WIRELESS=100;
	private double areaCluster;

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
		//System.out.println("Result Metrics: "+result);

		return result;
	}

}

