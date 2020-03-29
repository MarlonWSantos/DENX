package ufpa.facomp.gercom.iipdn;

public class Metrics{
	private int motesOnCluster;
	final static int RANGE_WIRELESS=100;
	private double areaCluster;

	public void setMotesOnCluster(int motesOnCluster){
		this.motesOnCluster=motesOnCluster;
	}

	public int getMotesOnCluster(){
		return motesOnCluster; 
	}

	public int getRangeWireless(){
		return RANGE_WIRELESS;
	}

	public void setArea(double areaCluster){
		this.areaCluster=areaCluster;
	}

	public double getAreaCluster(){
		return areaCluster;
	}

	public double calculateMetrics() {

		double result = 0;
		
		result = (getMotesOnCluster() * getRangeWireless())/getAreaCluster();
        System.out.println("Result Metrics: "+result);
        
		return result;
	}

}

