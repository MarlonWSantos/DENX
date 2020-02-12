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
 *    Marlon W. Santos (Federal University of Pará) - add Observer                                                  
 ******************************************************************************/
package application;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;

public class Observe{
		
	
	private CoapObserveRelation relation;
	private CoapClient client;		
	private StringBuilder infoObs;	
	protected static Controller control;
	


	public void observe(String url,Controller control) {
		
		 infoObs = new StringBuilder();


		client = new CoapClient(url);

		relation = client.observe(new CoapHandler() {
			@Override
			public void onLoad(CoapResponse response) {
				System.out.println("resposta: "+response.getResponseText());
				infoObs.append(response.getResponseText());
				infoObs.append("\n");
				showInfoObs(control);
				
				
				
			}
			@Override
			public void onError() {
				System.err.println("Failed");
			}
		});


		try {
			
			synchronized (this) {

				wait();
			}	
			
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	
		client.shutdown();	

	}	
	
	private void showInfoObs(Controller control) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				control.showOnGUI(infoObs.toString());
				
			}
		});
	}
	
}
