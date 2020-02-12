package application;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Controller {

	private String urlBorderRouter;
	private String resource;
	private boolean loading=false;

	@FXML private TextField textFieldURL;
	@FXML private Text textNeighbors;
	@FXML private Text textInfoMote;
	@FXML private Text textRoutes;
	@FXML private Text TextAlert;
	@FXML private Text textTerminal;
	@FXML private ToggleButton toggleObs;    
	@FXML private Button buttonDiscover;
	@FXML private Button buttonGet;
	@FXML private Button buttonObs;
	@FXML private ListView<String> listViewNeighbors;
	@FXML private ListView<String> listViewInfoMote;
	@FXML private Label labelRoutes;
	@FXML private Label labelTerminal;
	@FXML private ScrollPane scrollTerminal;

	public static StringBuilder lista = new StringBuilder("Vazio\n");
	
	public static boolean stopObs;


	@FXML
	private void mainController(ActionEvent event) throws IOException {
		WgetJava obj = new WgetJava();
		RoutesMotes routes = new RoutesMotes();
		ResourcesMotes res = new ResourcesMotes();
		
		//Captura a URL do Border Router digitada
		urlBorderRouter=textFieldURL.getText();

		
		//TODO try..catch.. para tratamento da URL digitada pelo usuário

		
		//Armazena  URL do Border Router
		obj.setUrl(urlBorderRouter);

		//Faz o pedido ao Border Router da informação e armazena
		obj.sendGET();

		treatmentOfInformation(obj,routes);
		showIPs(routes,res);
		showRoutes(routes);
	}


	private void treatmentOfInformation(WgetJava obj,RoutesMotes routes) {

		//Armazena as informações das Rotas e IP
		routes.setResponse(obj.getResponse());

		//Filtra e separa os IPs das Rotas
		routes.filterResponse();
	}


	private void showIPs(RoutesMotes routes,ResourcesMotes res) {
		ObservableList<String> ips = FXCollections.observableArrayList ();

		//Converta os IPs do formato fe80 para formato coap://[]
		res.setIPs(routes.getListIPs());

		//Busca e armazena os Ips no formato COAP na lista
		ips.addAll(res.getCoapIPs());

		//Exibe na GUI a lista de IPs COAPs
		listViewNeighbors.setItems(ips);
	}


	private void showRoutes(RoutesMotes routes) {

		StringBuilder rotas = new StringBuilder();

		//Busca as rotas dos motes e insere na lista
		for (String arr : routes.getListRoutes()) {
			rotas.append(arr);
		}      

		//Exibe na GUI a lista com as rotas
		labelRoutes.setText(rotas.toString().replace(",", "")); 
	}


	@FXML
	private void showMoteResources(MouseEvent event) {

		//Se a listView com IPs não estiver vazia
		if(listViewIsNotEmpty(listViewNeighbors)) { 
			
			
			//TODO Exibir PopUp com texto "Aguarde..." para o usuário


			ResourcesMotes res = new ResourcesMotes();
			GETClient client = new GETClient();

			//Desabilita a listView após o primeiro clique
			listViewNeighbors.setDisable(true);

			//Se não estiver buscando recursos, muda flag informando que estará buscando
			if(loading==false) {
				loading=true;

				ObservableList<String> resources = FXCollections.observableArrayList();

				String urlWellKnownCore;

				//Captura o IP clicado na lista e busca sua URL do Well-known/core
				urlWellKnownCore = res.getURLWellKnownCore(listViewNeighbors.getSelectionModel().getSelectedItem());

				//Faz um busca dos recursos através da URL/well-known/core e armazena na lista					
				resources.addAll(res.setResources(client.discover(urlWellKnownCore)));

				//Exibe na GUI a lista com os recursos
				listViewInfoMote.setItems(resources);

				//Cria um thead aguardando 2 segundos até a próxima busca de recursos				
				Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						//muda flag informando que não está buscando recursos
						loading=false;
						//Reabilita a listView para novo clique
						listViewNeighbors.setDisable(false);
					}

				}));
				timeline.play();
			}
		}
	}

	@FXML
	private void moteResource(MouseEvent event) {

		//Se a listView com IPs e a lista com recursos, ambas não estiverem vazias 
		if(listViewIsNotEmpty(listViewNeighbors) && (listViewIsNotEmpty(listViewInfoMote))) { 

			//Captura o recurso selecionado da listView e armazena
			resource=listViewInfoMote.getSelectionModel().getSelectedItem();
		}
	}


	private boolean listViewIsNotEmpty(ListView<String> listView) {
		//Retorna TRUE se a listView não estiver vazia
		return !listView.getSelectionModel().getSelectedItems().isEmpty();	
	}



	@FXML
	private void getMoteResource(ActionEvent event) {

		//Se a listView com IPs e a lista com recursos, ambas não estiverem vazias 
		if(listViewIsNotEmpty(listViewNeighbors) && (listViewIsNotEmpty(listViewInfoMote))) { 

			ResourcesMotes res = new ResourcesMotes();
			GETClient client = new GETClient();
			StringBuilder urlResource = new StringBuilder();
			StringBuilder infoResource = new StringBuilder();


			//Captura o ip selecionado na listView
			String ipMote = listViewNeighbors.getSelectionModel().getSelectedItem();

			//Captura o recurso selecionado na listView e armazena sua URL
			urlResource = res.getURLResource(ipMote,resource);

			//Faz uma requisição ao mote(servidor) pela informação sobre o recurso e armazena
			infoResource = client.get(urlResource.toString());
			
			showOnGUI(infoResource.toString());
		}
	}


	@FXML
	private void obsMote(ActionEvent event) {
		


		//Se a listView com IPs e a lista com recursos, ambas não estiverem vazias e o botão selecionado
		if(listViewIsNotEmpty(listViewNeighbors) && listViewIsNotEmpty(listViewInfoMote) && toggleObs.isSelected()) {

			
			disableNodes();

			ResourcesMotes res = new ResourcesMotes();
			StringBuilder urlResource = new StringBuilder();
			Observe obs = new Observe();
			
			


			//Captura o ip selecionado na listView
			String ipMote = listViewNeighbors.getSelectionModel().getSelectedItem();

			//Captura o recurso selecionado na listView e armazena sua URL
			urlResource = res.getURLResource(ipMote,resource);

			//TODO Threads criados aqui não são finalizados após o fim da execução
			
							
			//Cria uma thread para fazer requisição ao mote(servidor) solicitando observação do recurso
			new ThreadsObserve(this,urlResource.toString(),"Thread Obs #1");

		}else {
			
			  //Cria uma thread para finalizar a observação 
			new ThreadsObserve();
			enableNodes();
		}

	}

	//Desabilita botões e listViews
	public void disableNodes() {
		buttonDiscover.setDisable(true);
		buttonGet.setDisable(true);
		listViewNeighbors.setDisable(true);
		listViewInfoMote.setDisable(true);
	}

	//Habilita botões e listViews
	private void enableNodes() {
		buttonDiscover.setDisable(false);
		buttonGet.setDisable(false);
		listViewNeighbors.setDisable(false);
		listViewInfoMote.setDisable(false);
	}
	
	public void showOnGUI(String info) {
				
		//Insere a informação do recurso do mote na label
		labelTerminal.setText(info);

		//Habilita a quebra de linha em textos longos
		labelTerminal.setWrapText(true);

		//Define a largua máxima da label
		labelTerminal.setMaxWidth(784);// (mesma largura da ScrollPane)

		//Exibe a informação do recurso na GUI	
		scrollTerminal.setContent(labelTerminal);		
	}
	
  	
}