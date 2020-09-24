package ufpa.facomp.gercom.denx;


/*     Wget Java is a non-interactive network downloader in Java
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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Classe responsável por fazer a comunicação entre cliente e border router.
 */
public class WgetJava{

	/** Armazena a URL do border router. */
	private String url;
	
	/** Armazena a resposta do border router. */
	private StringBuffer response;


	/**
	 * Armazena a URL do Border Router para buscar os IPs dos motes
	 * 
	 * @param args número IP do border router
	 */
	public void setUrl(String args) {
		url = "http://["+args+"]";		
	}


	/**
	 * Pede ao Border Router a lista de IPs da rede
	 * 
	 * @throws IOException erro na comunicação entre cliente e border router
	 */
	public void sendGET() throws IOException {

		//Cria um objeto com a URL do Border Router
		URL obj = new URL(url);

		//Abre uma conexão HTTP com a URL indicada
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//Gera uma Exception se não houver conexão ou resposta do servidor
		con.setConnectTimeout(2000); //2segundos

		//Envia o pedido ao servidor
		con.setRequestMethod("GET");

		//Recebe e armazena o código de resposta dada pelo servidor
		int responseCode = con.getResponseCode();

		//Se o código recebido for igual a HTTP_OK(200)
		if (responseCode == HttpURLConnection.HTTP_OK) {

			//Cria buffer para armazenar a mensagem enviada pelo servidor com os IPs dos motes
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;

			//Cria buffer de String que vai receber o conteúdo do buffer
			response = new StringBuffer();

			//Enquanto linha do buffer for diferente de null,guarda uma linha da mensagem
			while ((inputLine = in.readLine()) != null) {

				//Adiciona ao buffer de String o conteúdo da linha da mensagem
				response.append(inputLine);
				response.append("\n");
			}

			//Finaliza o buffer que recebeu a mensagem
			in.close();

			/* Se o código recebido for diferente de HTTP_OK(200) envia exception
			 * para exibir mensagem de erro
			 */
		}else{
			throw new UnknownHostException();
		}
	}


	/**
	 * Retorna as informações que o border router possui da rede.
	 * 
	 * @return response resposta do Border Router
	 */
	public String getResponse() {
		return response.toString();	  
	}

}
