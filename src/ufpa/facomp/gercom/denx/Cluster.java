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
	
	/** Arquivo com as coordenadas dos motes. */
	final static String PATH_CSV_FILE = "/tmp/motes_coordinates.csv";
	
	/** Arquivo com as coordenads somente dos motes ativos. */
	final static String PATH_CSV_FILE_ACTIVES = "/tmp/actives_motes_coordinates.csv";
	
	/** Arquivo com as coordenadas dos motes ativos a ser lido no weka. */
	final static String PATH_ARFF_FILE = "/tmp/motes_coordinates.arff";
	
	/** Armazena as informações carregadas do arquivo. */
	static ArrayList<ArrayList<String>> dataFromFile;
	
	/** Armazena os IPs dos motes ativos. */
	static ArrayList<ArrayList<String>> motesActives;
	
	/** Cria as séries e armazena as coordenadas dos motes de cada cluster nelas. */
	protected static Graphic graphic = new Graphic();
	
	/** Lista de IPs dos motes. */
	static String[] IPs;
	
	/** Número de clusters. */
	protected static int numberClusters;

	/******************************************************************************/
		//TODO mover para fora da classe cluster, enviar somente a lista pra classe
	/**
	 * Pega a lista de IPs ativos já armazenados na plataforma.
	 * 
	 * @param routes rotas dos motes
	 */
	public void getIPsActivesMotes(RoutesMotes routes) {		
		List<String> lista = new ArrayList<String>();
		lista = routes.getListIPs();
		IPs = new String[lista.size()];
		lista.toArray(IPs);	
	}
	/******************************************************************************/
		//TODO remover esse método
	/** 
	 * Converte de CSV para um array na memória a lista de IPs gerados pelo cooja. 
	 *  
	 * @throws FileNotFoundException em caso de erro na leitura do arquivo
	 */
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
		//TODO modificar pra reutilizar
	/**
	 * Lê o IP ativo da lista e busca no arquivo gerado pelo cooja suas coordenadas
	 * 
	 * @throws FileNotFoundException em caso de erro na leitura do arquivo
	 */
	public void readEndAddressFindData( )  {
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
			//TODO remover esse métodoj
	/**
	 * Salva em CSV a lista com os IPs ativos e respectivas coordenadas.
	 * 	
	 * @throws IOException em caso de erro na escrita do arquivo
	 */
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
			//TODO remover esse método
	/**
	 * Carrega o arquivo CSV com os IPs ativos e suas coordenadas e converte para ARFF. 
	 * 
	 * @throws IOException em caso de erro na leitura do arquivo
	 */
	public void loadCSV() throws IOException {

		CSVLoader loader = new CSVLoader();
		loader.setSource(new File(PATH_CSV_FILE_ACTIVES));
		data = loader.getDataSet();
	}
	/******************************************************************************/
			//TODO remover esse método
	/**
	 * Carrega os dados com os IPs ativos e salva em formato ARFF. 
	 * 
	 * @throws IOException em caso de erro na escrita do arquivo
	 */
	public void saveARFF() throws IOException {

		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		saver.setFile(new File(PATH_ARFF_FILE));
		saver.setDestination(new File(PATH_ARFF_FILE));
		saver.writeBatch();
	}
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


//		Platform.runLater(new Runnable() {
//
//			@Override
//			public void run() {
//				//control.showInformationCluster(infoKmeans);	
//				System.out.println(infoKmeans);
//			}
//		});

	}
	/******************************************************************************/
	
	/**
	 * Cria um buffer para ler os dados de um arquivo. 
	 * 
	 * @param filename nome e caminho para o arquivo
	 * @return inputReader buffer com as informações do arquivo
	 */
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
