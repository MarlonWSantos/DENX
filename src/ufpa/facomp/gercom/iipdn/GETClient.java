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
 *    Marlon W. Santos (Federal University of Pará) - add discover                                                  
 *     
 ******************************************************************************/
package ufpa.facomp.gercom.iipdn;

import java.io.IOException;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.elements.exception.ConnectorException;

import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class GETClient {

	public StringBuilder get(String url) {

		//Declara o Coapclient de acordo com o número de URL recebidos
		CoapClient client = new CoapClient();

		StringBuilder resourceInfo = new StringBuilder();

		//Cria os Coapclient um para cada URL recebida
		client = new CoapClient(url);

		CoapResponse response = null;   

		try {
			//Cada Coapclient faz uma requisição para a URL e armazena a resposta
			response = client.get();

			//Se houver conteúdo na mensagem
			if (response!=null) {

				//Armazena a informação do recurso do mote
				resourceInfo.append(response.getCode()+"\n");
				resourceInfo.append(response.getOptions()+"\n");
				resourceInfo.append(response.getResponseText()+"\n");
				resourceInfo.append(System.lineSeparator() + "ADVANCED" + System.lineSeparator()+"\n");
				resourceInfo.append(Utils.prettyPrint(response)+"\n");

			}else {
				new AlertsDialog(AlertType.WARNING,"No response received",ButtonType.CLOSE);
			}


		}catch (ConnectorException e) {

			new AlertsDialog(AlertType.ERROR,"Connector error during connection",ButtonType.CLOSE);

		}catch (IOException e) {

			new AlertsDialog(AlertType.ERROR,"Connection error occurred",ButtonType.CLOSE);

		}catch (Exception e) {

			new AlertsDialog(e);
		}

		//Finaliza o Coapclient criado
		client.shutdown();
		//Retorna a informação do recurso
		return resourceInfo;
	}


	//Retorna uma lista com o recursos dos motes da rede
	public String discover(String url) {

		//Declara o Coapclient de acordo com o URL recebido
		CoapClient client = new CoapClient(url);

		CoapResponse response = null;

		//Cria lista para armazenar os recursos
		String listResourceInfo = new String();


		try {
			//Coapclient faz uma requisição para a URL e armazena a resposta
			response = client.get();

			//Se houver conteúdo na mensagem,adiciona na lista
			if (response!=null) {
				listResourceInfo=response.getResponseText();
			}else {
				new AlertsDialog(AlertType.WARNING,"No response received",ButtonType.CLOSE);
			}


		}catch (ConnectorException e) {

			new AlertsDialog(AlertType.ERROR,"Connector error during connection",ButtonType.CLOSE);

		}catch (IOException e) {

			new AlertsDialog(AlertType.ERROR,"Connection error occurred",ButtonType.CLOSE);

		}catch (Exception e) {

			new AlertsDialog(e);
		}

		//Finaliza o Coapclient criado
		client.shutdown();
		//Retorna a lista de recursos
		return listResourceInfo;
	}
}
