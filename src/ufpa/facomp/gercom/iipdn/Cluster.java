package ufpa.facomp.gercom.iipdn;

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

public class Cluster {

	static Instances data;
	final static String PATH_CSV_FILE = "/tmp/motes_coordinates.csv";
	final static String PATH_CSV_FILE_ACTIVES = "/tmp/actives_motes_coordinates.csv";
	final static String PATH_ARFF_FILE = "/tmp/motes_coordinates.arff";
	static ArrayList<ArrayList<String>> dataFromFile;
	static ArrayList<ArrayList<String>> motesActives;
	protected static Graphic graphic = new Graphic();
	static String[] IPs;
	protected static int numberClusters;

/******************************************************************************/	
	public void getIPsActivesMotes(RoutesMotes routes) {		
		List<String> lista = new ArrayList<String>();
		lista = routes.getListIPs();
		IPs = new String[lista.size()];
		lista.toArray(IPs);	
	}
/******************************************************************************/	
	public void convertCSV2Array() throws FileNotFoundException {

		dataFromFile=new ArrayList<ArrayList<String>>();

		try{
			Scanner scanner=new Scanner(new FileReader(PATH_CSV_FILE));
			scanner.useDelimiter(",");
			while(scanner.hasNext())
			{
				String line=scanner.nextLine();
				if(!line.equalsIgnoreCase("onServerStarted")) {
					String []dataLineInArray=line.split(",");
					ArrayList<String> rowDataFromFile=new ArrayList<String>(Arrays.asList(dataLineInArray));
					dataFromFile.add(rowDataFromFile);
				}
			}
			scanner.close();
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}
	}
/******************************************************************************/
	public void readEndAddressFindData( ) throws FileNotFoundException  {
		int posicao=0;
		
		motesActives=new ArrayList<ArrayList<String>>();

		String pattern = "fe80::200:0:0:[1-9a-f]?[0-9a-f]";

		Pattern EndIP = Pattern.compile(pattern);

		for(int i=0;i<IPs.length;i++) {
			Matcher matcher = EndIP.matcher(IPs[i]);
			if (matcher.find( )) {
				String end = matcher.group(0).replace("fe80::200:0:0:","");
				posicao = Integer.parseInt(end,16);
				motesActives.add(dataFromFile.get(posicao-1));
			}
		}
	}
/******************************************************************************/
	public void savingActivesMotesInCSV() throws IOException {

		BufferedWriter br = new BufferedWriter(new FileWriter(PATH_CSV_FILE_ACTIVES));
		StringBuilder sb = new StringBuilder();

		sb.append(dataFromFile.get(0).toString().replace("[","").replace("]","").replace(", ",","));
		sb.append("\n");

		for (ArrayList<String> element : motesActives) {
			sb.append(element.toString().replaceFirst("^.","").replaceFirst(".$",""));
			sb.append("\n");
		}
		br.write(sb.toString());
		br.close();
	}
/******************************************************************************/	
	public void loadCSV() throws IOException {

		CSVLoader loader = new CSVLoader();
		loader.setSource(new File(PATH_CSV_FILE_ACTIVES));
		data = loader.getDataSet();
	}
/******************************************************************************/
	public void saveARFF() throws IOException {

		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		saver.setFile(new File(PATH_ARFF_FILE));
		saver.setDestination(new File(PATH_ARFF_FILE));
		saver.writeBatch();
	}
/********************************************************************************/	
	public void createClusters(Controller control) throws Exception {

		SimpleKMeans kmeans = new SimpleKMeans();
		
		numberClusters = defineNumberClusters();

		kmeans.setSeed(10);
		kmeans.setPreserveInstancesOrder(true);
		kmeans.setNumClusters(numberClusters);

		defineSeriesClusterToCreate(numberClusters);

		BufferedReader datafile = readDataFile(PATH_ARFF_FILE);
		Instances dataForCluster = new Instances(datafile);

		kmeans.buildClusterer(dataForCluster);
		
		StringBuilder infoKmeans = new StringBuilder();
		String infoCluster;
		String infoMote;
		
		int[] assignments = kmeans.getAssignments();
		int mote=0;
		double coordX;
		double coordY;
		int i=0;
		for(int clusterNum : assignments) {

			infoCluster=String.format("Instance %d -> Cluster %d \n", i, clusterNum);
			infoMote=String.format("%s",motesActives.get(mote));
		
			infoKmeans.append(infoCluster);
			infoKmeans.append(infoMote);
			infoKmeans.append("\n\n");
						
			coordX=Double.parseDouble(motesActives.get(mote).get(1));
			coordY=Double.parseDouble(motesActives.get(mote).get(2));


			switch (clusterNum) {
			case 0:
				graphic.setCoordinatesSeries1(coordX, coordY);
				break;
			case 1:
				graphic.setCoordinatesSeries2(coordX, coordY);
				break;
			case 2:
				graphic.setCoordinatesSeries3(coordX, coordY);
				break;
			case 3:
				graphic.setCoordinatesSeries4(coordX, coordY);
				break;
			case 4:
				graphic.setCoordinatesSeries5(coordX, coordY);
				break;
			case 5:
				graphic.setCoordinatesSeries6(coordX, coordY);
				break;			
			}
			i++;
			mote++;
		}
		
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				control.showInformationCluster(infoKmeans);					
			}
		});

	}
/******************************************************************************/
	public BufferedReader readDataFile(String filename) {

		BufferedReader inputReader = null;
		try {
			inputReader = new BufferedReader(new FileReader(filename));

		} catch (FileNotFoundException ex) {
			System.err.println("File not found: " + filename);
		}
		return inputReader;
	}
/******************************************************************************/
	public int defineNumberClusters() {
		int totalClusters = 0;
		int totalMotesActives = motesActives.size();
		
		if(totalMotesActives <= 5) {
			totalClusters = 1;
		}else if(totalMotesActives >= 6 && totalMotesActives <= 8) {
			totalClusters = 2;
		}else if(totalMotesActives >= 9 && totalMotesActives <= 11) {
			totalClusters = 3;
		}else if(totalMotesActives >= 12 && totalMotesActives <= 14) {
			totalClusters = 4;
		}else if(totalMotesActives >= 15 && totalMotesActives <= 17) {
			totalClusters = 5;
		}else if(totalMotesActives >= 18) {
			totalClusters = 6;
		}		
		return totalClusters;
	}
/******************************************************************************/
	public void defineSeriesClusterToCreate(int numberClusters){
		
		switch (numberClusters) {
		case 1:
			graphic.createSerieCluster1();
			break;
		case 2:
			graphic.createSerieCluster1();
			graphic.createSerieCluster2();
			break;
		case 3:
			graphic.createSerieCluster1();
			graphic.createSerieCluster2();
			graphic.createSerieCluster3();
			break;
		case 4:
			graphic.createSerieCluster1();
			graphic.createSerieCluster2();
			graphic.createSerieCluster3();
			graphic.createSerieCluster4();
			break;
		case 5:
			graphic.createSerieCluster1();
			graphic.createSerieCluster2();
			graphic.createSerieCluster3();
			graphic.createSerieCluster4();
			graphic.createSerieCluster5();
			break;
		case 6:
			graphic.createSerieCluster1();
			graphic.createSerieCluster2();
			graphic.createSerieCluster3();
			graphic.createSerieCluster4();
			graphic.createSerieCluster5();
			graphic.createSerieCluster6();
			break;			
		}		
	}
}
