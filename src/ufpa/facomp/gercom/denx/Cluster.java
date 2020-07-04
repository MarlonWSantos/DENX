package ufpa.facomp.gercom.denx;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.converters.ArffSaver;
import java.io.File;

/**
 * Classe responsável pela leitura das coordenadas vindas do cooja,
 * leitura delas pelo weka e passando as informações dos clusters
 * para dentro da plataforma. 
 */

public class Cluster {

	/** Informação dos motes. */
	static Instances data;

	/** Armazena os IPs dos motes ativos. */
	static ArrayList<ArrayList<String>> motesActives;

	/** Cria as séries e armazena as coordenadas dos motes de cada cluster nelas. */
	protected static Graphic graphic = new Graphic();

	/** Número de clusters. */
	protected static int numberClusters;


	/********************************************************************************/

	/**
	 * Carrega o arquivo ARFF e cria os clusters com base nos dados dos IPs e coordenadas.
	 * 
	 * @param control permite o envio da informação para GUI
	 * @throws Exception em caso de erro na leitura do arquivo
	 */
	public void createClusters(BufferedReader datafile) throws Exception {

		SimpleKMeans kmeans = new SimpleKMeans();

		Instances dataForCluster = new Instances(datafile);

		numberClusters = elbow(dataForCluster);

		kmeans.setSeed(10);
		kmeans.setPreserveInstancesOrder(true);
		kmeans.setNumClusters(numberClusters);

		//graphic.createSerieCluster(numberClusters);

		kmeans.buildClusterer(dataForCluster);

		StringBuilder infoKmeans = new StringBuilder();
		String infoCluster;
		String infoMote;

		int[] assignments = kmeans.getAssignments();
		int mote=0;
		double coordX;
		double coordY;
		int i=1;
		for(int clusterNum : assignments) {

			infoCluster=String.format("Instance %d -> Cluster %d \n", i, clusterNum+1);
			//infoMote=String.format("%s",motesActives.get(mote));

			infoKmeans.append(infoCluster);
			//infoKmeans.append(infoMote);
			//infoKmeans.append("\n\n");

			//coordX=Double.parseDouble(motesActives.get(mote).get(1));
			//coordY=Double.parseDouble(motesActives.get(mote).get(2));

			//graphic.setCoordinatesSeries(coordX, coordY,clusterNum);

			i++;
			mote++;
		}
		System.out.println(infoKmeans.toString());


//				Platform.runLater(new Runnable() {
//					Controller control = new Controller();
//
//					@Override
//					public void run() {
//						control.showInformationCluster(infoKmeans);	
//						System.out.println(infoKmeans);
//					}
//				});
	}
	/******************************************************************************/	

	/**
	 * Define quantos clusters serão criados de acordo com o elbow method. 
	 * 
	 * @param data dados do motes
	 * @return melhor número de cluster
	 */
	public static int elbow(Instances data) {
		int maxClusterNum=10;

		double[] meanClusterErro = new double [maxClusterNum];
		double p, start, end;
		double distanceToLine;
		int bestClusterNum=0;

		SimpleKMeans kmeans = new SimpleKMeans();
		kmeans.setSeed(10);
		kmeans.setPreserveInstancesOrder(true);


		for(int i=1;i<=maxClusterNum;i++) {
			try {
				kmeans.setNumClusters(i);
				kmeans.buildClusterer(data);
				meanClusterErro[i-1]=kmeans.getSquaredError()/i;

			} catch (Exception e) {
				new AlertsDialog(e);
			}
		}


		start=meanClusterErro[0];//first point
		end=meanClusterErro[maxClusterNum-1];//last point
		distanceToLine=0;

		for(int i=0;i<maxClusterNum;i++) {//get the max distance
			p=meanClusterErro[i];
			if(distanceToLine<pDistance(i,p,0,start,maxClusterNum-1,end)) {
				distanceToLine=pDistance(i,p,0,start,maxClusterNum-1,end);
				bestClusterNum=i+1;
			}
		}
		return bestClusterNum;
	}

	/**
	 * Calcula a distância entre um ponto e a linha do elbow method.
	 * 
	 * @param x coordenada do número de clusters da elbow
	 * @param y coordenada da média de erros da elbow 
	 * @param x1 coordenada x no início do elbow
	 * @param y1 coordenada y no início do elbow
	 * @param x2 coordenada x no fim do elbow
	 * @param y2 coordenada y no fim do elbow  
	 * @return distance distância entre coordenada e linha 
	 */
	public static double pDistance(double x, double y, double x1, double y1, double x2, double y2) {
		//x,y is the point
		//x1,y1 is the beginning of the line
		//x2,y2 is the end of the line
		double distance;

		double A = x - x1; // position of point rel one end of line
		double B = y - y1;
		double C = x2 - x1; // vector along line
		double D = y2 - y1;
		double E = -D; // orthogonal vector
		double F = C;

		double dot = A * E + B * F;
		double len_sq = E * E + F * F;

		distance =  Math.abs(dot) / Math.sqrt(len_sq);

		return distance;
	}

	/******************************************************************************/

	/**
	 * Calcula a métrica da rede inteira. 
	 */
	public void calculateMetricNetwork(){
		double coordX=0;
		double coordY=0;

		graphic.createSerieNetwork();

		for(int i=0;i<motesActives.size();i++) {
			coordX=Double.parseDouble(motesActives.get(i).get(1));
			coordY=Double.parseDouble(motesActives.get(i).get(2));
			graphic.setCoordinatesSeriesNetwork(coordX,coordY);
		}
	}
}
