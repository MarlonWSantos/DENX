package ufpa.facomp.gercom.denx;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

/** 
 * Classe responsável por comparar os IPs ativos e os dados dos motes do arquivo,
 * depois de comparar, envia os dados para a criação dos clusters na classe Cluster.
 */
public class BridgeCoapCluster {

	/** Armazena a lista dos motes ativos. */
	static ArrayList<String> listIP  = new ArrayList<String>();
	
	/** Armazena o conteúdo do arquivo ARFF. */
	static BufferedReader contentFile;

	/** Flag informa se o arquivo já foi carregado pra mémória. */
	static Boolean dataSetLoaded=false;

	/** Armazena o caminho do arquivo ARFF com as coordenadas dos motes. */
	static String path;
	
	
	/**
	 *  Busca os motes ativos e manda carregar os dados do arquivo ARFF.
	 *  
	 *  @param routes objeto da classe RoutesMotes
	 *  @param res objeto da classe ResourcesMotes
	 */
	public void FindActivesMotes(RoutesMotes routes,ResourcesMotes res) {

		if (!listIP.isEmpty()) {
			listIP.clear();
		}

		//Converta os IPs do formato fe80 para formato coap://[]
		res.setIPs(routes.getListIPs());

		//Busca e armazena os Ips no formato COAP na lista
		listIP.addAll(res.getCoapIPs());
		
		if(listIP.size()>0 && dataSetLoaded) {
			loadCoordinatesFile();
			motesActivesOnDataSet();
		}
	}
	
	
	/**
	 * Salva o caminho para o arquivo ARFF com as coordenadas dos motes.
	 * 
	 * @param pathFile caminho para o arquivo
	 */
	public void savePathFile(String pathFile) {
		path=pathFile;
		dataSetLoaded = true;
	}

	
	/**
	 * Cria um buffer, lê o arquivo ARFF e salva seu conteúdo na memória. 
	 */
	public void loadCoordinatesFile(){

		try {
			contentFile = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) {
			
			new AlertsDialog(AlertType.ERROR,"Failed to access the file Coordinates",ButtonType.CLOSE);
		}
		

	}

	
	/**
	 * Compara os IPs do arquivo ARFF que estão na memória com os IPs dos motes ativos
	 * e envia os dados dos motes ativos para gerar os clusters. 
	 */
	public void motesActivesOnDataSet() {

		StringBuilder activeMotes = new StringBuilder();
		String line = null;
		
		try {
			while ((line=contentFile.readLine())!=null){
				if(line.startsWith("@")){
					activeMotes.append(line).append("\n");
				}else {
					for (String ip : listIP) {
						if(line.startsWith(ip)) {
							activeMotes.append(line).append("\n");
						}
					}
				}
			}

		BufferedReader dataSet = new BufferedReader(new StringReader(activeMotes.toString()));
	
		Cluster cluster = new Cluster();
		cluster.createClusters(dataSet);
		
		} catch (IOException e) {
			
			new AlertsDialog(AlertType.ERROR,"Failed to access the file Coordinates",ButtonType.CLOSE);
			e.printStackTrace();
			
		}catch(Exception e) {
			
			new AlertsDialog(e);
			
		}
	}
}
