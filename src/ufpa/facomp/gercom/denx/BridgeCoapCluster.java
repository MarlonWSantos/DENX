package ufpa.facomp.gercom.denx;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;


public class BridgeCoapCluster {

	static ArrayList<String> listIP  = new ArrayList<String>();
	static BufferedReader contentFile;
	static Boolean dataSetLoaded=false;

	public void FindActivesMotes(RoutesMotes routes,ResourcesMotes res) {

		if (!listIP.isEmpty()) {
			listIP.clear();
		}

		//Converta os IPs do formato fe80 para formato coap://[]
		res.setIPs(routes.getListIPs());

		//Busca e armazena os Ips no formato COAP na lista
		listIP.addAll(res.getCoapIPs());
		
		if(listIP.size()>0 && dataSetLoaded) {
			motesActivesOnDataSet();
		}
	}
	


	public void loadCoordinatesFile(String path) throws FileNotFoundException {

		contentFile = new BufferedReader(new FileReader(path));
		
		dataSetLoaded = true;

	}

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
