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
	//Pega a lista de IPs ativos já armazenados na plataforma
	public void getIPsActivesMotes(RoutesMotes routes) {		
		List<String> lista = new ArrayList<String>();
		lista = routes.getListIPs();
		IPs = new String[lista.size()];
		lista.toArray(IPs);	
	}
	/******************************************************************************/
	//Converte de CSV para um array na memória a lista de IPs gerados pelo cooja 
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
	//Lê o IP ativo da lista e busca no arquivo gerado pelo cooja suas coordenadas
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
	//Salva em CSV a lista com os IPs ativos e respectivas coordenadas
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
	//Carrega o arquivo CSV com os IPs ativos e suas coordenadas e converte para ARFF
	public void loadCSV() throws IOException {

		CSVLoader loader = new CSVLoader();
		loader.setSource(new File(PATH_CSV_FILE_ACTIVES));
		data = loader.getDataSet();
	}
	/******************************************************************************/
	//Carrega os dados com os IPs ativos e salva em formato ARFF
	public void saveARFF() throws IOException {

		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		saver.setFile(new File(PATH_ARFF_FILE));
		saver.setDestination(new File(PATH_ARFF_FILE));
		saver.writeBatch();
	}
	/********************************************************************************/
	//Carrega o arquivo ARFF e cria os clusters com base nos dados dos IPs e coordenadas 
	public void createClusters(Controller control) throws Exception {

		SimpleKMeans kmeans = new SimpleKMeans();
		
		BufferedReader datafile = readDataFile(PATH_ARFF_FILE);
		Instances dataForCluster = new Instances(datafile);

		numberClusters = elbow(dataForCluster);

		kmeans.setSeed(10);
		kmeans.setPreserveInstancesOrder(true);
		kmeans.setNumClusters(numberClusters);

		defineSeriesClusterToCreate(numberClusters);

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
	//Cria um buffer para ler os dados de um arquivo
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
	//Define quantos clusters serão criados de acordo com o elbow method
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
				//System.out.println(meanClusterErro[i-1]);
				
			} catch (Exception e) {
				System.out.println("Erro no elbow\n");
				e.printStackTrace();
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
		//System.out.println("The best cluster num according to Elbow is "+ bestClusterNum);
		return bestClusterNum;
	}
	
	
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
	      
	      //System.out.println("The computed distance is "+distance);
	      
	      return distance;
	    }

	/******************************************************************************/
	//Define quais séries que armazenarão os dados dos clusters serão criadas
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
