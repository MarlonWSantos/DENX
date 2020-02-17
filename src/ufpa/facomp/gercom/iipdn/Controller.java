package ufpa.facomp.gercom.iipdn;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Controller implements Initializable{

	private String urlBorderRouter;
	private String resource;
	private boolean loading=false;	
	private ObservableList<String> listGroup;



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
	@FXML private ListView<String> listViewGroup;
	@FXML private Label labelRoutes;
	@FXML private Label labelTerminal;
	@FXML private ScrollPane scrollTerminal;
	
	@FXML private CheckBox checkObsGroup;
	@FXML private Text textGroups;
    @FXML private Text textSaveto;
    @FXML private TextField texFieldSaveTo;
    @FXML private Button buttonAddItem;
    @FXML private Button buttonRemoveItem;
    @FXML private Button buttonClearGroup;
    @FXML private ToggleButton buttonObsGroup;
    

	
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

			 
			disableNodes(true);

			ResourcesMotes res = new ResourcesMotes();
			StringBuilder urlResource = new StringBuilder();


			//Captura o ip selecionado na listView
			String ipMote = listViewNeighbors.getSelectionModel().getSelectedItem();

			//Captura o recurso selecionado na listView e armazena sua URL
			urlResource = res.getURLResource(ipMote,resource);

			//TODO Threads criados aqui não são finalizados após o fim da execução
			
							
			//Cria uma thread para fazer requisição ao mote(servidor) solicitando observação do recurso
			new ThreadsObserve(this,urlResource.toString(),"Thread Observe");
			

		}else {
			
			  //Cria uma thread para finalizar a observação 
			new ThreadsObserve();
			
			
			disableNodes(false);
		}

	}

	
	//Desabilita botões e listViews
	public void disableNodes(boolean option ) {
		buttonDiscover.setDisable(option);
		buttonGet.setDisable(option);
		listViewNeighbors.setDisable(option);
		listViewInfoMote.setDisable(option);
		checkObsGroup.setDisable(option);
		
		if(checkObsGroup.isSelected()) {
			disableObsGroup(0.5,true);

		}
	}

	
	
	public void showOnGUI(String info) {
				
		//Insere a informação do recurso do mote na label
		labelTerminal.setText(info);

		//Habilita a quebra de linha em textos longos
		labelTerminal.setWrapText(true);

		//Define a largua máxima da label
		labelTerminal.setMaxWidth(670);// (mesma largura da ScrollPane)

		//Exibe a informação do recurso na GUI	
		scrollTerminal.setContent(labelTerminal);		
	}
	
	
	  //Desabilita o campo Observe Group
	public void disableObsGroup(double opacity,boolean option) {
		textGroups.setOpacity(opacity);
		listViewGroup.setDisable(option);
		buttonAddItem.setDisable(option);
		buttonRemoveItem.setDisable(option);
		buttonClearGroup.setDisable(option);
		buttonObsGroup.setDisable(option);
		textSaveto.setOpacity(opacity);
		texFieldSaveTo.setDisable(option);
	}
	
	
	 @FXML
	 private void visibleObsGroup(ActionEvent event) {
		 
		   //Se o checkbox estiver selecionado habilita o campo Observe Group
		 if(checkObsGroup.isSelected()) {
			 disableObsGroup(1, false);			 
			 
			listGroup = FXCollections.observableArrayList();
			 
			 
			 //Do contrário desabilita o campo Observe Group
		 }else {
			 disableObsGroup(0.5,true);
			 
			 clearGroup(event);
		 }			
	 }
	 
	 
	 @FXML
	 private void addGroupItem(ActionEvent event) {
		 
		  //Se a listView com IPs e a lista com recursos, ambas não estiverem vazias 
		if(listViewIsNotEmpty(listViewNeighbors) && (listViewIsNotEmpty(listViewInfoMote))) {
			
			  //Captura IP e recurso selecionado
			String ipMote = listViewNeighbors.getSelectionModel().getSelectedItem();
			String resource = listViewInfoMote.getSelectionModel().getSelectedItem();			
			
			  //Insere a URL no grupo pra exibição
			listGroup.add(ipMote+resource);			
			
			  //Exibe o grupo de IPs
			listViewGroup.setItems(listGroup);
		}
	 }

	 
	 @FXML
	 private void clearGroup(ActionEvent event) {
		 
		 //Se a lista de grupos de IPs não estiver vazia,
		if(listGroup.size() != 0) {
			
			  //Limpa a listView do grupo e apaga os IPs da memória
			listViewGroup.getItems().clear();
		//	URLGroup.delete(0, URLGroup.length());
		//	URLGroup.setLength(0);
		}
	 }
	    
	 
	 @FXML
	 private void removeGroupItem(ActionEvent event) {
		
	 }

	 
	 @FXML
	 private void obsGroup(ActionEvent event) {

	 }
	 
	 


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		  //Inicia com o campo Observe Group desabilitado
		disableObsGroup(0.5,true);
		
		
		
	}
	
  	
}