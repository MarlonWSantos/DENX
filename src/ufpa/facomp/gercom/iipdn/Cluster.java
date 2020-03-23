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
				String []dataLineInArray=line.split(",");
				ArrayList<String> rowDataFromFile=new ArrayList<String>(Arrays.asList(dataLineInArray));
				dataFromFile.add(rowDataFromFile);
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

		String pattern = "fe80::200:0:0:[1-9a-f]?[1-9a-f]";

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
	public void createClusters() throws Exception {

		SimpleKMeans kmeans = new SimpleKMeans();

		graphic.createSerieCluster1();
		graphic.createSerieCluster2();
		graphic.createSerieCluster3();
		graphic.createSerieCluster4();
		graphic.createSerieCluster5();
		graphic.createSerieCluster6();

		kmeans.setSeed(10);
		kmeans.setPreserveInstancesOrder(true);
		kmeans.setNumClusters(3);

		BufferedReader datafile = readDataFile(PATH_ARFF_FILE);
		Instances dataForCluster = new Instances(datafile);

		kmeans.buildClusterer(dataForCluster);
		
		int[] assignments = kmeans.getAssignments();
		int mote=0;
		double coordX;
		double coordY;
		int i=0;
		for(int clusterNum : assignments) {
			System.out.printf("Instance %d -> Cluster %d \n", i, clusterNum);
			System.out.println(motesActives.get(mote));

			coordX=Double.parseDouble(motesActives.get(mote).get(1));
			coordY=Double.parseDouble(motesActives.get(mote).get(2));

			System.out.println();

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
}
