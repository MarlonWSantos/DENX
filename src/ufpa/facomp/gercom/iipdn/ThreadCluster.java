package ufpa.facomp.gercom.iipdn;

import java.io.File;
import java.io.IOException;

import javafx.application.Platform;

public class ThreadCluster implements Runnable{
	final static String PATH_CSV_FILE = "/tmp/motes_coordinates.csv";
	private static Controller control;
	private static RoutesMotes routes;
	
	public ThreadCluster(Controller control,RoutesMotes routes) {
		this.control=control;
		this.routes=routes;
		Thread createCluster = new Thread(this,"Thread Cluster");		

		createCluster.start();		
	}

	@Override
	public void run() {

		File file = new File(PATH_CSV_FILE);

		Cluster obj = new Cluster();

		try {

			while(true) {

				if (file.exists()){
					obj.getIPsActivesMotes(routes);
					obj.convertCSV2Array();
					obj.readEndAddressFindData();
					obj.savingActivesMotesInCSV();
					obj.loadCSV();
					obj.saveARFF();
					obj.createClusters();

					Platform.runLater(new Runnable() {

						@Override
						public void run() {
				control.LoadGraphic();							
						}
					});

				}else {
					System.out.println("No");
				}

				Thread.sleep(60000);			
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
