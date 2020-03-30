package ufpa.facomp.gercom.iipdn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Controller implements Initializable{

	private String urlBorderRouter;
	private String resource;
	private boolean loading=false;	
	private ObservableList<String> listGroup;
	private static boolean isObserving;

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
	@FXML private Label labelTerminal;
	@FXML private ScrollPane scrollTerminal;

	@FXML private CheckBox checkObsGroup;
	@FXML private Text textGroups;
	@FXML private Text textSaveto;
	@FXML private TextField texFieldSaveTo;
	@FXML private Button buttonAddItem;
	@FXML private Button buttonRemoveItem;
	@FXML private Button buttonClearGroup;
	@FXML private ToggleButton toggleObsGroup;
	@FXML private ScatterChart<Number, Number> scatterChartGraphic;
	@FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private TextArea textAreaRoutes;
    @FXML private TextArea textAreaClusters;
    @FXML private Text textClusers;
    @FXML private TextArea textAreaMetrics;
    @FXML private Text textMetrics;
	
	@FXML
	private void mainController(ActionEvent event)   {

		//Se o campo da URL estiver vazio, exibe alerta ao usuário pedindo que insira uma URL
		if(textFieldURL.getText().isEmpty()) {

			new AlertsDialog(Alert.AlertType.INFORMATION,"Insira uma URL válida",ButtonType.CLOSE);

		}else {

			WgetJava obj = new WgetJava();
			RoutesMotes routes = new RoutesMotes();
			ResourcesMotes res = new ResourcesMotes();

			//Captura a URL do Border Router digitada
			urlBorderRouter=textFieldURL.getText();

			//Armazena  URL do Border Router
			obj.setUrl(urlBorderRouter);

			try {

				//Faz o pedido ao Border Router da informação e armazena
				obj.sendGET();

				treatmentOfInformation(obj,routes);
				showIPs(routes,res);
				showRoutes(routes);
				
				//Cria um thread para gerar clusters no gráfico
				new ThreadCluster(this,routes);

				//Mensagens de erro para usuário			
			}catch(ProtocolException e) {

				new AlertsDialog(AlertType.ERROR,"Protocol failure",ButtonType.CLOSE);	

			}catch (MalformedURLException e) {

				new AlertsDialog(AlertType.ERROR, "Invalid CoAP URL",ButtonType.CLOSE);

			}catch(UnknownHostException e) {

				new AlertsDialog(AlertType.ERROR, "404 Not Found",ButtonType.CLOSE);
			
			}catch(IOException e) {

				new AlertsDialog(AlertType.ERROR, "Communication failure", ButtonType.CLOSE);

			}catch(Exception e) {

				new AlertsDialog(e);
			}
		}
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
		textAreaRoutes.setText(rotas.toString().replace(",", ""));
	}


	@FXML
	private void showMoteResources(MouseEvent event) {

		//Se a listView com IPs não estiver vazia e se não estiver buscando recursos
		if(listViewIsNotEmpty(listViewNeighbors) && !loading) {

			//flag informa que está buscando recurso
			loading = true;

			//Desabilita a listView após o clique
			listViewNeighbors.setDisable(true);	

			Main main = new Main();

			//Exibi PopUp com texto "Loading..." para o usuário
			Platform.runLater(new Runnable() {				
				@Override
				public void run() {
					main.showScreenLoading();					
				}
			});

			//Um segundo após o PopUp ser exibido, inica a busca pelos recursos
			Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {					
					ResourcesMotes res = new ResourcesMotes();
					GETClient client = new GETClient();

					ObservableList<String> resources = FXCollections.observableArrayList();
					String urlWellKnownCore;

					//Captura o IP clicado na lista e busca sua URL do Well-known/core
					urlWellKnownCore = res.getURLWellKnownCore(listViewNeighbors.getSelectionModel().getSelectedItem());

					//Faz um busca dos recursos através da URL/well-known/core e armazena na lista					
					resources.addAll(res.setResources(client.discover(urlWellKnownCore)));

					//Exibe na GUI a lista com os recursos
					listViewInfoMote.setItems(resources);

					//Fecha o PopUp que exibe Loading
					main.closeScreenLoading();		

					//Reabilita a listView para novo clique
					listViewNeighbors.setDisable(false);

					//muda flag informando que não está buscando recursos
					loading=false;
				}
			}));
			timeline.play();						
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

			//Exibe no terminal
			showOnGUI(infoResource.toString());

		}else {

			new AlertsDialog(AlertType.WARNING, "Select an IP in Neighbors and\n a resource in Resources Mote", ButtonType.OK);
		}
	}


	@FXML
	private void obsMote(ActionEvent event) {
		//Se a listView com IPs e a lista com recursos, ambas não estiverem selecionadas, exibe mensagem
		if(!listViewIsNotEmpty(listViewNeighbors) || !listViewIsNotEmpty(listViewInfoMote)){
			toggleObs.setSelected(false);
			new AlertsDialog(AlertType.WARNING, "Select an IP in Neighbors and\n a resource to Observe", ButtonType.OK);
		}

		//Se a listView com IPs e a lista com recursos, ambas não estiverem vazias, o botão selecionado e não estiver observando
		if(listViewIsNotEmpty(listViewNeighbors) && listViewIsNotEmpty(listViewInfoMote) && toggleObs.isSelected() && !isObserving) {
			//Muda flag para observando
			isObserving=true;

			toggleObs.setSelected(true);

			//desabilita botões
			disableNodes(true);

			//Se campo obsGroup estiver selecionado, desabilita
			if(checkObsGroup.isSelected()) {
				disableObsGroup(0.5,true);
				toggleObsGroup.setDisable(true);
			}

			ResourcesMotes res = new ResourcesMotes();
			StringBuilder urlResource = new StringBuilder();

			//Captura o ip selecionado na listView
			String ipMote = listViewNeighbors.getSelectionModel().getSelectedItem();

			//Captura o recurso selecionado na listView e armazena sua URL
			urlResource = res.getURLResource(ipMote,resource);

			//Cria uma thread para fazer requisição ao mote(servidor) solicitando observação do recurso
			new ThreadsObserve(this,urlResource.toString(),"Thread Observe");
		} 

		//Se a listView com IPs e a lista com recursos, ambas não estiverem vazias, o botão não selecionado e observando
		if(listViewIsNotEmpty(listViewNeighbors) && listViewIsNotEmpty(listViewInfoMote) && !toggleObs.isSelected() && isObserving) {

			//Cria uma thread para finalizar a observação 
			new ThreadsObserve();

			//Habilita botões
			disableNodes(false);

			//Habilita campo obsGroup se tiver desabilitado
			if(checkObsGroup.isSelected()) {
				disableObsGroup(1,false);
				toggleObsGroup.setDisable(false);
			}			
			//Muda flag, não observando
			isObserving=false;
		}	
	}


	//Desabilita botões e listViews
	public void disableNodes(boolean option ) {
		buttonDiscover.setDisable(option);
		buttonGet.setDisable(option);
		listViewNeighbors.setDisable(option);
		listViewInfoMote.setDisable(option);
		checkObsGroup.setDisable(option);
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
		textSaveto.setOpacity(opacity);
		texFieldSaveTo.setDisable(option);

	}


	@FXML
	private void visibleObsGroup(ActionEvent event) {

		//Se o checkbox estiver selecionado habilita o campo Observe Group
		if(checkObsGroup.isSelected()) {
			disableObsGroup(1, false);
			toggleObsGroup.setDisable(false);

			listGroup = FXCollections.observableArrayList();


			//Do contrário desabilita o campo Observe Group
		}else {
			disableObsGroup(0.5,true);
			toggleObsGroup.setDisable(true);

			//Limpa a lista obsGroup
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
		}
	}


	@FXML
	private void removeGroupItem(ActionEvent event) {

		//Se a lista não estiver vazia, deleta o item selecionado
		if(listViewIsNotEmpty(listViewGroup)) {
			listGroup.remove(listViewGroup.getSelectionModel().getSelectedIndex());
		}		
	}


	@FXML
	private void obsGroup(ActionEvent event) {

		//Se a lista de grupo não estive vazia, o botão selecionado e não observando
		if(!listGroup.isEmpty() && toggleObsGroup.isSelected() && !isObserving){

			try {

				//Captura o caminho e nome do arquivo para salvar dados Obs
				String pathToSave = texFieldSaveTo.getText();
				Observe obs = new Observe();

				//Se um caminho tiver sido digitado
				if(!pathToSave.isEmpty()) {

					//salva o caminho e cria nele um arquivo para armazenar os dados
					obs.setSavePath(pathToSave);
					obs.saveFileObs();

					//do contrário, apenas cria um arquivo num caminho default
				}else {

					obs.saveFileObs();
				}

				//Muda flag para observando
				isObserving=true;

				//Desabilita botões
				disableNodes(true);
				disableObsGroup(0.5,true);
				toggleObs.setDisable(true);

				//TODO verificar a aceitação de URL sem prefixo coap

				//Lê as URL da lista grupo e cria um Thread para cada uma 
				for (String url : listGroup) {
					url = url.replace("[", "coap://[").replace("]/", "]:5683/");
					new ThreadsObserve(this,url,"Thread Observe Group");
				}

			}catch(NullPointerException e) {

				new AlertsDialog(AlertType.ERROR,"Invalid file path",ButtonType.CLOSE);

			}catch(SecurityException e) {

				new AlertsDialog(AlertType.ERROR,"Access denied to write to the file",ButtonType.CLOSE);

			}catch(FileNotFoundException e) {

				new AlertsDialog(AlertType.ERROR,"File path not found",ButtonType.CLOSE);

			}catch(IOException e) {

				new AlertsDialog(AlertType.ERROR,"Failed to access the file",ButtonType.CLOSE);

			}catch(Exception e) {

				new AlertsDialog(e);

			}finally {

				//Após uma exception, se não houver uma observação ocorrendo
				if(!isObserving) {
					//Não seleciona o botão Obs Group
					toggleObsGroup.setSelected(false);
				}
			}



		}

		//Se a lista grupo não estiver vazia, o botão não selecionado e estiver observando
		if(!listGroup.isEmpty() && !toggleObsGroup.isSelected() && isObserving){

			//Cria novo Thread para finalizar a observação
			new ThreadsObserve();

			//Exibe na tela o fim da observação
			showOnGUI("\nObserve stopped!\n");

			//Muda a flag para não observando
			isObserving=false;

			//Habilita os botões
			disableNodes(false);
			disableObsGroup(1,false);
			toggleObs.setDisable(false);
		}

		//Se a lista Group estiver vazia, não seleciona o botão
		if(listGroup.isEmpty()) {
			toggleObsGroup.setSelected(false);
		}
	}

	//Retorna se está ocorrendo uma observação
	public boolean isObserving() {
		return Controller.isObserving;
	}
	
	//Carrega os dados dos clusters e insere no gráfico na GUI
	public void LoadGraphic() {
		
		//Se o gráfico não estiver vazio, remove todos os dados dele
        if (!scatterChartGraphic.getData().isEmpty()) {
        	scatterChartGraphic.getData().remove(0,scatterChartGraphic.getData().size());
        }
        
        //Carrega o número de clusters criado
        int numberClusters = Cluster.numberClusters;
        
        //De acordo com o número de clusters, carrega os dados de cada um
		switch (numberClusters) {
		case 1:
	        scatterChartGraphic.getData().add(Cluster.graphic.getCoordinateSeries1());
			break;
		case 2:
	        scatterChartGraphic.getData().add(Cluster.graphic.getCoordinateSeries1());
	        scatterChartGraphic.getData().add(Cluster.graphic.getCoordinateSeries2());			break;
		case 3:
	        scatterChartGraphic.getData().add(Cluster.graphic.getCoordinateSeries1());
	        scatterChartGraphic.getData().add(Cluster.graphic.getCoordinateSeries2());
	        scatterChartGraphic.getData().add(Cluster.graphic.getCoordinateSeries3());			break;
		case 4:
	        scatterChartGraphic.getData().add(Cluster.graphic.getCoordinateSeries1());
	        scatterChartGraphic.getData().add(Cluster.graphic.getCoordinateSeries2());
	        scatterChartGraphic.getData().add(Cluster.graphic.getCoordinateSeries3());
	        scatterChartGraphic.getData().add(Cluster.graphic.getCoordinateSeries4());			break;
		case 5:
	        scatterChartGraphic.getData().add(Cluster.graphic.getCoordinateSeries1());
	        scatterChartGraphic.getData().add(Cluster.graphic.getCoordinateSeries2());
	        scatterChartGraphic.getData().add(Cluster.graphic.getCoordinateSeries3());
	        scatterChartGraphic.getData().add(Cluster.graphic.getCoordinateSeries4());
	        scatterChartGraphic.getData().add(Cluster.graphic.getCoordinateSeries5());			break;	
		case 6:
	        scatterChartGraphic.getData().add(Cluster.graphic.getCoordinateSeries1());
	        scatterChartGraphic.getData().add(Cluster.graphic.getCoordinateSeries2());
	        scatterChartGraphic.getData().add(Cluster.graphic.getCoordinateSeries3());
	        scatterChartGraphic.getData().add(Cluster.graphic.getCoordinateSeries4());
	        scatterChartGraphic.getData().add(Cluster.graphic.getCoordinateSeries5());
	        scatterChartGraphic.getData().add(Cluster.graphic.getCoordinateSeries6());				break;	
		}
	}
	
	//Mostra na GUI as informações dos IPs(coordenadas) alocados em seus respectivos clusters
	public void showInformationCluster(StringBuilder infoKmeans) {
		
		textAreaClusters.setText(infoKmeans.toString());
	}

	//Mostra na GUI as informações do cálculo da métrica de cada cluster
	public void showInformationMetrics(StringBuilder infoMetrics) {
		
		textAreaMetrics.setText(infoMetrics.toString());
	}
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		//Inicia com o campo Observe Group desabilitado
		disableObsGroup(0.5,true);
		toggleObsGroup.setDisable(true);
		
		}
}
