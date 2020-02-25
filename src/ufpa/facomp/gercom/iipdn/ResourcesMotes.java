package ufpa.facomp.gercom.iipdn;


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

import java.util.ArrayList;
import java.util.List;


public class ResourcesMotes{

	private List<String> listResources;
	private List<String> listCoapIPs;


	//Armazena os IPs coap para busca de seus recursos  
	public void setIPs(List<String> listIPs){
		listCoapIPs = new ArrayList<>();


		for (String arr : listIPs) {
			//Muda o prefixo fe80 do IP para aaaa e armazena
			listCoapIPs.add(arr.replace("fe80", "[aaaa").replace("\n", "]"));
		}      
	}

	//Retorna a lista de IPs Coap
	public List<String> getCoapIPs(){
		return listCoapIPs;
	}


	//Retorna a URL Well-kwown/core dos motes
	public String getURLWellKnownCore(String ip) {

		return ip.replace("[aaaa", "coap://[aaaa").replace("]", "]:5683/.well-known/core");
	}


	//Armazena e retorna todas os IPs Coaps e seus respectivos recursos
	public List<String> setResources(String infoRes){


		listResources = new ArrayList<String>();

		//Define o padrão das tags dos recursos na mensagem
		Pattern res = Pattern.compile("</(.*?)>;");

		//Busca dentro da mensagem o padrão dos recursos definido
		Matcher matcherRes = res.matcher(infoRes);

		//Enquanto encontrar padrões do recursos,adiciona na lista temporária
		while (matcherRes.find()) {
			listResources.add(matcherRes.group().replace("<","").replace(">;",""));
		}

		return listResources;
	}


	//Retorna URL do recurso para busca
	public StringBuilder getURLResource(String ipMote, String resource) {

		StringBuilder URLResMote = new StringBuilder();

		URLResMote.append("coap://");
		URLResMote.append(ipMote);
		URLResMote.append(":5683");
		URLResMote.append(resource);

		return URLResMote;
	}


}
