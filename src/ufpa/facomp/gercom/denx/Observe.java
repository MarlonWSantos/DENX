/*******************************************************************************
 * Copyright (c) 2015 Institute for Pervasive Computing, ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors:
 *    Matthias Kovatsch - creator and main architect
 *    Achim Kraus (Bosch Software Innovations GmbH) - add saving payload
 *    Marlon W. Santos (Federal University of Pará) - add observe
 *    Marlon W. Santos (Federal University of Pará) - add showInfoObs
 *    Marlon W. Santos (Federal University of Pará) - add setSavePath
 *    Marlon W. Santos (Federal University of Pará) - add getSavePath
 *    Marlon W. Santos (Federal University of Pará) - add saveFileObs
 *    Marlon W. Santos (Federal University of Pará) - add observeGroup
 *                                                      
 ******************************************************************************/
package ufpa.facomp.gercom.denx;

import java.io.File;
import java.io.PrintStream;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;

/**
 * Classe responsável por executar as observações dos motes.
 */
public class Observe{

	/** Objeto da classe CoapObserveRelation. (Californium) */
	private CoapObserveRelation relation;

	/** Objeto da classe CoapClient. (Californium) */
	private CoapClient client = new CoapClient();  //CoapClient para observação dos recursos

	/** Armazena a informação durante a observação. */
	private static StringBuilder infoObs;

	/** Objeto da classe Controller. */
	protected static Controller control;

	/** Objeto da classe PrintStream. */
	private static PrintStream file=null;

	/** Arquivo com as informações da observação. */
	private static String savePath = "/tmp/obsResult.txt"; //Caminho default para salvar dados da observação


	/**
	 * Cria buffer para armazenar informações durante observação.
	 */
	public void createBufferObs() {
		infoObs=null;
		Observe.infoObs= new StringBuilder("\nSaving Obs to "+ getSavePath()+"\n\nObserving ...\n");
	}

	/**
	 * Executa a observação de um mote.
	 * 
	 * @param url URL do mote
	 * @param control objeto da classe Controller
	 */
	public void observe(String url,Controller control) {

		infoObs = new StringBuilder("\nObserving ...\n\n");

		//Inseri a URL no coapClient
		client.setURI(url);

		//Inicia processo de observação e armazena os dados
		relation = client.observe(new CoapHandler() {
			@Override
			public void onLoad(CoapResponse response) {
				infoObs.append(response.getResponseText());//Armazena os dados da observação
				infoObs.append("\n");
				//Manda exibir os dados no terminal 
				showInfoObs(control);
			}
			@Override
			public void onError() {
				infoObs.append("Failed connection");//Em caso de falha durante observação
				infoObs.append("\n");
				//Manda exibir os dados no terminal 
				showInfoObs(control);

			}
		});


		try {

			synchronized (this) {
				//Trava o thread, enquanto faz a observação,esperanda a liberação de outro thread
				wait();
			}

		} catch (InterruptedException e) {
			new AlertsDialog(AlertType.WARNING,"Observation stopped incorrectly\nEnd the observation by pressing Obs", ButtonType.OK);
		} catch (Exception e) {
			new AlertsDialog(e);
		}

		//Finaliza a conexão com recurso
		relation.proactiveCancel();

		//Finaliza o thread clientCoap
		client.shutdown();

		//Armazena o último dado após fim da observação
		infoObs.append("\nObserve stopped!\n\n");
		showInfoObs(control);
	}	


	/**
	 * Exibe no terminal na GUI os dados da observação.
	 *  
	 * @param control objeto da classe Controller
	 */
	private void showInfoObs(Controller control) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				control.showOnGUI(infoObs.toString());
			}
		});
	}


	/**
	 * Salva o caminho para criar o arquivo que armazena dados da observação. 
	 * 
	 * @param savePath caminho para salvar o arquivo
	 */
	public void setSavePath(String savePath) {
		this.savePath=savePath;
	}


	/**
	 * Retorna o caminho onde será salvo o arquivo que guarda dados da obsrvação.
	 *  
	 * @return savePath caminho onde o arquivo será salvo
	 */
	public String getSavePath() {
		return savePath;
	}


	/**
	 * Cria o arquivo que armazenará os dados da observação.
	 *  
	 * @throws Exception em caso erro de escrita do arquivo
	 */
	public void saveFileObs() throws Exception {
		file = new PrintStream(new File(this.getSavePath()));
	}

	/**
	 * Executa a observação de grupo de motes.
	 * 
	 * @param url URL do mote
	 * @param control objeto da classe Controller
	 */
	public void observeGroup(String url,Controller control) {

		//Exibe no terminal o início da observação, local e arquivo usado para salvar os dados da Obs
		infoObs = new StringBuilder("\nSaving Obs to "+ getSavePath()+"\n\nObserving ...\n");

		//Inseri a URL no coapClient
		CoapClient client = new CoapClient(url);		

		//Inicia processo de observação e armazena os dados
		CoapObserveRelation relation = client.observe(new CoapHandler() {
			@Override
			public void onLoad(CoapResponse response) {
				System.setOut(file);							//Envia para arquivo dados da observação
				System.out.println(response.getResponseText());
				infoObs.append(response.getResponseText());//Armazena os dados da observação
				infoObs.append("\n");
				//Manda exibir os dados no terminal 
				showInfoObs(control);
			}
			@Override
			public void onError() {
				System.out.println("Failed connection");
				infoObs.append("Failed connection");//Em caso de falha durante observação
				infoObs.append("\n");
				//Manda exibir os dados no terminal 
				showInfoObs(control);
			}
		});


		try {

			synchronized (this) {
				//Trava o(s) thread(s), enquanto faz a observação,esperanda a liberação de outro thread
				wait();
			}
		} catch (InterruptedException e) {
			new AlertsDialog(AlertType.WARNING,"Observation stopped incorrectly\nEnd the observation by pressing Obs Group", ButtonType.OK);
		} catch (Exception e) {
			new AlertsDialog(e);
		}

		//Finaliza a conexão com recurso
		relation.proactiveCancel();

		//Finaliza o thread clientCoap
		client.shutdown();

		System.out.println("Observe stopped!");
		//Armazena o último dado após fim da observação
		infoObs.append("\nObserve stopped!\n\n");
		showInfoObs(control);
	}	
}
