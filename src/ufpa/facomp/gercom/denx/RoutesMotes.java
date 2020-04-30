package ufpa.facomp.gercom.denx;


/*     Routes Motes - It filter and store the routes and IPS of the motes
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

/**
 * Classe responsável por armazenar as rotas e IPs da rede.
 */
public class RoutesMotes{

	/** Armazena a informação da rede. */
	private String response;
	
	/** Armazena a lista de rotas. */
	private List<String> listRoutes;
	
	/** Armazena a lista de IPs da rede. */
	private List<String> listIPs;



	/**
	 * Armazena as informações dos IPs e das rotas dos motes. 
	 * 
	 * @param args armazena a informação da rede
	 */
	public void setResponse(String args){
		response = args;
	}


	/**
	 * Filtra e elimina os ruídos das informações dos IPs e das Rotas 
	 */
	public void filterResponse(){

		//Define o padrão de rotas
		Pattern route = Pattern.compile("aaaa:.*s");

		//Define o padrão de IPs
		Pattern ip = Pattern.compile("fe80:.*:.*:.*:[1-9|a-f]?[0-9|a-f]\n");

		//Busca dentra da mensagem o padrão da rota definido
		Matcher matcherRoute = route.matcher(response);

		//Busca dentro da mensagem o padrão de IPs definido
		Matcher matcherIP = ip.matcher(response);

		//Cria lista para armazenar as rotas
		listRoutes = new ArrayList<String>();

		//Cria lista para armazenar os IPs
		listIPs = new ArrayList<>();

		//Enquanto encontrar padrões da rota,adiciona na lista
		while (matcherRoute.find()) {
			listRoutes.add(matcherRoute.group());
			listRoutes.add(",\n");
		}

		//Enquanto encontrar padrões de IP,adiciona na lista
		while(matcherIP.find()){
			listIPs.add(matcherIP.group());
		}
	}


	/**
	 * Retorna o IP do mote.
	 *  
	 * @param i posição na lista de IPs
	 * @return o IP na respectiva posição na lista
	 */
	public String getIP(int i){
		return listIPs.get(i);
	}


	/**
	 * Retorna a lista de IPs.
	 *  
	 * @return lista com os IPs
	 */
	public List<String> getListIPs(){
		return listIPs;
	}

	
	/**
	 * Retorna a lista de rotas.
	 * 
	 * @return listRoutes lista com as rotas
	 */
	public List<String> getListRoutes(){
		return listRoutes;	  
	}

}
