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

import java.io.BufferedReader;
import java.util.Scanner;
import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.network.config.NetworkConfigDefaultHandler;
import org.eclipse.californium.core.network.config.NetworkConfig.Keys;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;

public class Observe {
	
	private CoapObserveRelation relation;
	private CoapClient client;;
	//private StringBuilder infoObs;

	public  void observe(String url) {
		//StringBuilder infoObs = new StringBuilder();
		client = new CoapClient(url);
      
		relation = client.observe(new CoapHandler() {
		  @Override
		  public void onLoad(CoapResponse response) {
		    System.out.println(response.getResponseText());
			 // infoObs.append(response.getResponseText()+"\n");
			
		  }
		  @Override
		  public void onError() {
		    System.err.println("Failed");
		  }
		});
		
		System.out.println("inicia espera");
		
				
		  //relation.proactiveCancel();			 
	}
		
}
