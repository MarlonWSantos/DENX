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

public class Observer {

	public static void observer(String[] url) {
		
		int i=0;
			
		CoapClient[] client = new CoapClient[url.length];
		
		for (i=0;i<url.length;i++) {
			client[i] = new CoapClient(url[i]);
		}
		
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

				System.out.println("OBSERVE (press enter to exit)");
				
				CoapObserveRelation[] relation = new CoapObserveRelation[url.length];
				
				for(i=0;i<url.length;i++) {
				
					relation[i] = client[i].observe(new CoapHandler() {
						@Override
						public void onLoad(CoapResponse response) {
							System.out.println(response.getResponseText());
						}
						@Override
						public void onError() {
							System.err.println("Failed");
						}
					});
				}
							
		// wait for user
				try { br.readLine(); } catch (IOException e) { }
				
				System.out.println("CANCELLATION");
				  
	};
}
