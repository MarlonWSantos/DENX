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

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;

public class Observe{

    @FXML
    private Label lbl;

	public void observe(String url) {

		CoapClient client = new CoapClient(url);
		Controller ctr = new Controller();		

		CoapObserveRelation relation = client.observe(new CoapHandler() {
			@Override
			public void onLoad(CoapResponse response) {
				System.out.println(response.getResponseText());
				//ctr.showInfoObs(response.getResponseText().toString());
				lbl.setText(response.getResponseText().toString());
				lbl.setText("hello");


			}
			@Override
			public void onError() {
				System.err.println("Failed");
			}
		});

		System.out.println("Observando...");

		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		}		
		
		relation.proactiveCancel();	
		
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		System.out.println("Fim da observação");
	}	
}
