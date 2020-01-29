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
 ******************************************************************************/
package application;

import java.io.IOException;
import java.util.List;

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

public class GETClient {
	
	private List<String> listResourceInfo;
	
		
  public void get(String[] url) {
	  
	  //Declara o Coapclient de acordo com o número de URL recebidos
	CoapClient[] client = new CoapClient[url.length];
	  
	  //Cria os Coapclient um para cada URL recebida
	for(int i=0;i<url.length;i++){		 
      client[i] = new CoapClient(url[i]);
	}
  
	CoapResponse response = null;   
				
	  try {
		  //Cada Coapclient faz uma requisição para a URL e armazena a resposta
		for(int i=0;i<url.length;i++) {  
	      response = client[i].get();
	    
	        //Se houver conteúdo na mensagem
	      if (response!=null) {

	          //Exibe as informações recebidas
		    System.out.println(response.getCode());
		    System.out.println(response.getOptions());
		    System.out.println(response.getResponseText());
		    System.out.println(System.lineSeparator() + "ADVANCED" + System.lineSeparator());
		    System.out.println(Utils.prettyPrint(response));
				
		  }else {
		    System.out.println("No response received.");
		  }
	    }
		
	  }catch (ConnectorException e) {
	  	  // TODO Auto-generated catch block
	    e.printStackTrace();
	  }catch (IOException e) {
	      // TODO Auto-generated catch block
	    e.printStackTrace();
	  }
			
	    //Finaliza os Coapclients criados
	  for(int i=0;i<url.length;i++) {
		client[i].shutdown();
	  }
  }
	
  
    //Retorna uma lista com o recursos dos motes da rede
  public String[] discover(String[] url) {
	  
      //Declara o Coapclient de acordo com o número de URL recebidos
    CoapClient[] client = new CoapClient[url.length];
	  
	  //Vincula os Coapclient a cada URL recebida
	for(int i=0;i<url.length;i++){		 
      client[i] = new CoapClient(url[i]);
	}
  
	CoapResponse response = null;
	
	  //Cria lista para armazenar os recursos
	String[] listResourceInfo = new String[url.length];

	
	  try {
		  //Cada Coapclient faz uma requisição para a URL e armazena a resposta
		for(int i=0;i<url.length;i++) {  
	      response = client[i].get();
	    
	        //Se houver conteúdo na mensagem,adiciona na lista
	      if (response!=null) {
	    	  listResourceInfo[i]=response.getResponseText();
		  }else {
		    System.out.println("No response received.");
		  }
	    }
		
	  }catch (ConnectorException e) {
	  	  // TODO Auto-generated catch block
	    e.printStackTrace();
	  }catch (IOException e) {
	      // TODO Auto-generated catch block
	    e.printStackTrace();
	  }
			
	    //Finaliza os Coapclients criados
	  for(int i=0;i<url.length;i++) {
		client[i].shutdown();
		
	  }
	  return listResourceInfo;
  }
}
