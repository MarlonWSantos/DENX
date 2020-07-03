package ufpa.facomp.gercom.denx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Optional;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;

/**
 * Classe responsável pela interação entre a GUI e as demais classes.
 */
public class Controller implements Initializable{

	/** URL do border router. */
	private String urlBorderRouter;

	/** Recursos dos motes. */
	private String resource;

	/** Flag informa se está se conectando com os motes. */
	private boolean loading=false;

	/** Lista de IPs que serão observados em simultâneo. */
	private ObservableList<String> listGroup;

	/** Flag informa se há motes sendo observados. */
	private static boolean isObserving;

	/** Thread que executa o Discover na rede. */
	private static Thread getData;

	@FXML private TextField textFieldURL;
	@FXML private Text textNeighbors;
	@FXML private Text textInfoMote;
	@FXML private Text textRoutes;
	@FXML private Text TextAlert;
	@FXML private Text textTerminal;
	@FXML private ToggleButton toggleObs;    
	@FXML private ToggleButton toggleDiscover;
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
	@FXML private Label labelOpenFile;
	@FXML private Label labelSaveFile;
	@FXML private Button buttonOpenFile;
	@FXML private Button buttonSaveFile;


	/**
	 * Controla e chama as demais funções da aplicação.
	 * 
	 * @param event um clique no botão discover na GUI
	 */
	@FXML
	private void mainController(ActionEvent event)   {

		//Se o campo da URL estiver vazio com botão selecionado, exibe alerta ao usuário pedindo URL
		if(textFieldURL.getText().isEmpty() && toggleDiscover.isSelected()) {

			toggleDiscover.setSelected(false);

			new AlertsDialog(Alert.AlertType.INFORMATION,"Insert a valid URL",ButtonType.CLOSE);

			//se URL digitada e o botão estiver selecionado
		}else if(!textFieldURL.getText().isEmpty() && toggleDiscover.isSelected()){

			toggleDiscover.setSelected(true);

			//Captura a URL do Border Router digitada
			urlBorderRouter=textFieldURL.getText();

			//Busca  informações dos motes e rotas via GET
			getInformation(urlBorderRouter);

			//Cria um thread para gerar clusters no gráfico
			//new ThreadCluster(this,routes);

			//se botão não estive selecionado (De ligado para desligado)
		}else if(!toggleDiscover.isSelected()) {

			toggleDiscover.setSelected(true);

			//Pergunta ao usuário se encerra a conexão com a rede
			AlertsDialog alertInterrupt = new AlertsDialog(AlertType.CONFIRMATION);
			alertInterrupt.setContentText("Do you want to stop the connection with network?");

			Optional<ButtonType> result = alertInterrupt.showAndWait();

			//Se usuário escolher sim, finaliza a conexão com a rede e limpa os dados
			if(result.get()== ButtonType.OK) {
				toggleDiscover.setSelected(false);
				getData.interrupt();
				clearGUI();

			}else {
				toggleDiscover.setSelected(true);
			}
		}
	}

	/**
	 * Cria um thread e faz requisições sobre os motes e rotas a cada minuto.
	 * 
	 * @param urlBorderRouter URL digitada na GUI
	 */
	private void getInformation(String urlBorderRouter) {

		getData = new Thread(new Runnable() {

			@Override
			public void run() {

				WgetJava obj = new WgetJava();
				RoutesMotes routes = new RoutesMotes();
				ResourcesMotes res = new ResourcesMotes();

				//Armazena  URL do Border Router
				obj.setUrl(urlBorderRouter);

				try {
					while(true) {

						//Faz o pedido ao Border Router da informação e armazena
						obj.sendGET();
						treatmentOfInformation(obj,routes);

						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								//Exibe na GUI
								showIPs(routes,res);
								showRoutes(routes);								
							}
						});	

						//Thread pausa por 1 minuto e volta ao começo
						Thread.sleep(60000);
					}

				}catch(ProtocolException e) {

					DefineAlertsDialog("Protocol failure",e);

				}catch (MalformedURLException e) {

					DefineAlertsDialog("Invalid CoAP URL",e);

				}catch(UnknownHostException e) {

					DefineAlertsDialog("404 Not Found",e);

				}catch(IOException e) {

					DefineAlertsDialog("Communication failure",e);

				} catch (InterruptedException e) {

					return;

				}catch(Exception e) {

					DefineAlertsDialog("Exception",e);
				}

			}
		},"Thread Discover") {

		}; 

		//Inicia o thread
		getData.start();
	}

	/**
	 * Limpa as informações de todos os campos da GUI.
	 */
	private void clearGUI() {
		listViewInfoMote.getItems().clear();
		listViewNeighbors.getItems().clear();
		listViewGroup.getItems().clear();
		textAreaRoutes.clear();
		textAreaClusters.clear();
		textAreaMetrics.clear();
		scatterChartGraphic.getData().remove(0,scatterChartGraphic.getData().size());
		labelTerminal.setText("");
	}

	/**
	 * Define o tipo de alerta que será exibido ao usuário.
	 *  
	 * @param error tipo da exception ocorrida
	 * @param e 		conteúdo da exception
	 */
	private void DefineAlertsDialog(String error,Exception e) {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				toggleDiscover.setSelected(false);				

				if(error.equalsIgnoreCase("Exception")) {
					new AlertsDialog(e);
				}else {
					new AlertsDialog(AlertType.ERROR, error,ButtonType.CLOSE);
				}
			}
		});
	}

	/**
	 * Transforma as informações recebidas do border router, do formato HTML
	 * para um formato legível.
	 * 
	 * @param obj objeto da classe WgetJava
	 * @param routes objeto da classe RoutesMotes
	 */
	private void treatmentOfInformation(WgetJava obj,RoutesMotes routes) {

		//Armazena as informações das Rotas e IP
		routes.setResponse(obj.getResponse());

		//Filtra e separa os IPs das Rotas
		routes.filterResponse();
	}

	/**
	 * Exibe os IPs dos motes na GUI.
	 * 
	 * @param routes
	 * @param res
	 */
	private void showIPs(RoutesMotes routes,ResourcesMotes res) {
		ObservableList<String> ips = FXCollections.observableArrayList ();

		//Converta os IPs do formato fe80 para formato coap://[]
		res.setIPs(routes.getListIPs());

		//Busca e armazena os Ips no formato COAP na lista
		ips.addAll(res.getCoapIPs());

		//Exibe na GUI a lista de IPs COAPs
		listViewNeighbors.setItems(ips);
	}


	/**
	 * Exibe as rotas dos motes na GUI.
	 * 
	 * @param routes objeto da classe RoutesMotes
	 */
	private void showRoutes(RoutesMotes routes) {

		StringBuilder rotas = new StringBuilder();

		//Busca as rotas dos motes e insere na lista
		for (String arr : routes.getListRoutes()) {
			rotas.append(arr);
		}      

		//Exibe na GUI a lista com as rotas
		textAreaRoutes.setText(rotas.toString().replace(",", ""));
	}


	/**
	 * Exibe os recursos dos motes na GUI.
	 * 
	 * @param event clique do mouse na GUI
	 */
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


	/**
	 * Captura o nome do recurso clicado na GUI.
	 * 
	 * @param event clique do mouse na GUI.
	 */
	@FXML
	private void moteResource(MouseEvent event) {

		//Se a listView com IPs e a lista com recursos, ambas não estiverem vazias 
		if(listViewIsNotEmpty(listViewNeighbors) && (listViewIsNotEmpty(listViewInfoMote))) { 

			//Captura o recurso selecionado da listView e armazena
			resource=listViewInfoMote.getSelectionModel().getSelectedItem();
		}
	}

	/**
	 * Verifica se a lista na GUI não está vazia, se não exibe informação.
	 * 
	 * @param listView lista dos IPs ou dos recursos 
	 * @return true se estiver com informação,
	 * 		   false se estiver sem informação
	 */
	private boolean listViewIsNotEmpty(ListView<String> listView) {

		//Retorna TRUE se a listView não estiver vazia
		return !listView.getSelectionModel().getSelectedItems().isEmpty();	
	}

	/**
	 * Faz requisição ao mote buscando informação sobre o recurso.
	 * 
	 * @param event clique do mouse no botão GET na GUI.
	 */
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

	/**
	 * Faz a observação do recurso do mote.
	 * 
	 * @param event clique do mouse no botão OBS na GUI.
	 */
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


	/**
	 * Desabilita botões e listViews 
	 * 
	 * @param option true para ocultar parte da GUI
	 * @param option false para exibir parte da GUI
	 */
	public void disableNodes(boolean option ) {
		toggleDiscover.setDisable(option);
		buttonGet.setDisable(option);
		listViewNeighbors.setDisable(option);
		listViewInfoMote.setDisable(option);
		checkObsGroup.setDisable(option);
	}


	/**
	 * Exibe as informações no saída da GUI.
	 * 
	 * @param info informação a ser exibido no terminal da GUI
	 */
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



	/**
	 * Desabilita o campo Observe Group
	 *  
	 * @param opacity 1  para exibir parta da GUI
	 * @param opacity 0.5 para ocultar parta da GUI
	 * @param option true para ocultar parte da GUI
	 * @param option false para exibir parte da GUI
	 */
	public void disableObsGroup(double opacity,boolean option) {
		textGroups.setOpacity(opacity);
		listViewGroup.setDisable(option);
		buttonAddItem.setDisable(option);
		buttonRemoveItem.setDisable(option);
		buttonClearGroup.setDisable(option);
		textSaveto.setOpacity(opacity);
		texFieldSaveTo.setDisable(option);

	}

	/**
	 * Exibe ou oculta o campo ObsGroup na GUI.
	 * 
	 * @param event clique no checkbox Observe Group
	 */
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

	/**
	 * Adiciona um IP da GUI para a lista de observação de grupos.
	 * 
	 * @param event clique no botão Add
	 */
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

	/**
	 * Limpa a lista Group.
	 * 
	 * @param event clique no botão Clear na GUI
	 */
	@FXML
	private void clearGroup(ActionEvent event) {

		//Se a lista de grupos de IPs não estiver vazia,
		if(listGroup.size() != 0) {

			//Limpa a listView do grupo e apaga os IPs da memória
			listViewGroup.getItems().clear();
		}
	}

	/**
	 * Remove um item da lista Group.
	 * 
	 * @param event clique no botão Del na GUI
	 */
	@FXML
	private void removeGroupItem(ActionEvent event) {

		//Se a lista não estiver vazia, deleta o item selecionado
		if(listViewIsNotEmpty(listViewGroup)) {
			listGroup.remove(listViewGroup.getSelectionModel().getSelectedIndex());
		}		
	}

	/**
	 *  Faz a observação de um grupo de motes.
	 *   
	 * @param event clique do mouse no botão Obs Group na GUI
	 */
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

				ArrayList<String> listURL = new ArrayList<String>();

				//Lê as URL da lista grupo da interface e converte para formato coap://URI 
				for (String uri : listGroup) {					
					String url = uri.replace("[", "coap://[").replace("]/", "]:5683/");
					listURL.add(url);
				}

				//Envia pra classe a lista com URL onde será criada os threads de observação
				new ThreadsObserve(this,listURL,"Thread Observe Group");


			}catch(InterruptedException e) {

				new AlertsDialog(AlertType.ERROR,"Thread Interrupted",ButtonType.CLOSE);

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


	/**
	 * Retorna se está ocorrendo uma observação. 
	 * 
	 * @return true se houver observação em andamento,
	 *         false se não houver observação em andamento
	 */
	public boolean isObserving() {
		return Controller.isObserving;
	}

	/**
	 * Carrega os dados dos clusters e insere no gráfico na GUI. 
	 */
	public void LoadGraphic() {

		//Se o gráfico não estiver vazio, remove todos os dados dele
		if (!scatterChartGraphic.getData().isEmpty()) {
			scatterChartGraphic.getData().remove(0,scatterChartGraphic.getData().size());
		}

		//Carrega o número de clusters criado
		int numberClusters = Cluster.numberClusters;

		//De acordo com o número de clusters, carrega os dados de cada um
		LoadSeries(numberClusters);
	}

	/**
	 * Carrega os dados de cada cluster.
	 * 
	 * @param numberCluster número de clusters que terão as informações carregadas
	 */
	public void LoadSeries(int numberClusters) {
		for(int i=1;i<=numberClusters;i++) {
			//Insere os dados dentro do gráfico
			scatterChartGraphic.getData().add(Cluster.graphic.getCoordinateSeries(i));
		}
	}


	/**
	 * Exibe na GUI as informações dos IPs(coordenadas) alocados em seus respectivos clusters. 
	 * 
	 * @param infoKmeans informações sobre os clusters 
	 */
	public void showInformationCluster(StringBuilder infoKmeans) {

		textAreaClusters.setText(infoKmeans.toString());
	}


	/**
	 * Exibe na GUI as informações do cálculo da métrica de cada cluster. 
	 * 
	 * @param infoMetrics resultado das métricas
	 */
	public void showInformationMetrics(StringBuilder infoMetrics) {

		textAreaMetrics.setText(infoMetrics.toString());
	}
	
    @FXML
    private void openFile(ActionEvent event) {
    	
    	FileChooser fc = new FileChooser();
		fc.getExtensionFilters().addAll(new ExtensionFilter("ARFF File","*.arff"));
		File selectedFile = fc.showOpenDialog(null);
		
		if(selectedFile != null) {
			labelOpenFile.setText(selectedFile.getName().toString());
			Cluster cluster = new Cluster();
			System.out.println(selectedFile.getAbsolutePath());
			try {
				cluster.createClusters(selectedFile.getAbsolutePath());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
    
    @FXML
    private void saveFile(ActionEvent event) {
    	
    	FileChooser fc = new FileChooser();
		fc.getExtensionFilters().addAll(new ExtensionFilter("TXT File","*.txt"));
		File selectedFile = fc.showSaveDialog(null);		
		
		if(selectedFile != null) {
			labelSaveFile.setText(selectedFile.getName().toString());
			Cluster cluster = new Cluster();
			System.out.println(selectedFile.getAbsolutePath());			
		}
    }

	/**
	 * Inicializa a plataforma com o campo Observe Group desabilitado.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		//Inicia com o campo Observe Group desabilitado
		disableObsGroup(0.5,true);
		toggleObsGroup.setDisable(true);		
	}
}
