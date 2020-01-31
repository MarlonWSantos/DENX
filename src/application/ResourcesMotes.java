package application;


/*     Resources Motes - It store the resources of each mote
*      Copyright (c) 2020 Marlon W. Santos <marlon.santos.santos@icen.ufpa.br>
*
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*
*/

import java.util.regex.*;

import javax.management.StringValueExp;
import javax.swing.text.StringContent;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.io.*;
import java.lang.reflect.Array;


public class ResourcesMotes{

  private String ip;
  private List<String> listResources;
  private List<String> listCoapIPs;
  private String[][] moteResource;

  
    //Armazena os IPs coap para busca de seus recursos  
  public void setIPs(List<String> listIPs){
	  listCoapIPs = new ArrayList<>();
	  
	  
      for (String arr : listIPs) {
    	    //Muda o prefixo fe80 do IP para aaaa e armazena
    	  listCoapIPs.add(arr.replace("fe80", "[aaaa").replace("\n", "]"));
     }      
  }
  
  public List<String> getCoapIPs(){
	  return listCoapIPs;
  }
    
  
    //Retorna a URL Well-kwown/core dos motes
  public String getURLWellKnownCore(String ip) {
		
	  return ip.replace("[aaaa", "coap://[aaaa").replace("]", "]:5683/.well-known/core");
  }
  //TODO refactoring
//*************************************************************************************************
    //Armazena todas os IPs Coaps e seus respectivos recursos
  public List<String> setResources(String infoRes){
	  
	  
	listResources = new ArrayList<String>();
	
	//ArrayList<String> resources = new ArrayList<String>();
	
	//moteResource = new String[infoRes.length][];
	
		
	  //Repete de acordo com o número de IPs Coaps existentes      
	//for(int i=0;i<listCoapIPs.size();i++){
		
		  //Adiciona o IP Coap 
		//listResources.add("\n"+getCoapIP(i));
		//listResources.add("\n");
						  	
	    //Define o padrão das tags dos recursos na mensagem
      Pattern res = Pattern.compile("</(.*?)>;");
	  
        //Busca dentro da mensagem o padrão dos recursos definido
	  Matcher matcherRes = res.matcher(infoRes);

	    //Enquanto encontrar padrões do recursos,adiciona na lista temporária
	  while (matcherRes.find()) {
	    listResources.add(matcherRes.group().replace("<","").replace(">;",""));
	  }
	  
	    //Converte para array String a lista com os padrões de recursos e os armazena-os
	 // moteResource[i] = resources.toArray(new String[resources.size()]);
	  
	  
	    //Adiciona os recursos da lista temporária para a lista definitava
	  //listResources.addAll(resources);
	    //Limpa a lista temporária
	 // resources.clear();
	
     return listResources;
  }
  
  public List<String> getListResources(){
	  return listResources;
  }
  
  
    //Mostra os recursos por mote
  public void showMoteResource(int mote) {
	  
	  System.out.println("\nRecursos do Mote\n");
	  
	    //Exibe o IP do mote
	  System.out.println(getCoapIP(mote));
	  
	  //Exibe os recursos do mote especificado
	for(int j=0;j<moteResource[mote].length;j++) {
	  System.out.println(moteResource[mote][j]);	
	}  
  }
  
  
    //Exibe todos os recursos dos motes
  public void showAllResources(){

    System.out.println("\nRecursos disponíveis\n");
    
      //Exibe os recursos de acordo com o tamanho da lista
    for(int i=0;i<listResources.size();i++) {
    	System.out.println(listResources.get(i));
    }        
  }  


    //Retorna o Coap IP do mote
  public String getCoapIP(int i){
    return listCoapIPs.get(i);
  }

  public String getResources(int i){
	  return listResources.get(i);
  }
 
  public int getSizeListResources() {
	  return listResources.size();
  }
}
