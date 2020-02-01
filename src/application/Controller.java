package application;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.sun.org.glassfish.external.statistics.annotations.Reset;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import sun.net.www.protocol.file.Handler;

public class Controller implements Initializable{
	
	private String urlBorderRouter;
	private String urlmote;
	private String resource;
	private boolean loading=false;
    
	@FXML private Button buttonDiscover;
	@FXML private TextField textFieldURL;
    @FXML private Text textNeighbors;
    @FXML private Text textInfoMote;
    @FXML private Button buttonGet;
    @FXML private Button buttonObs;
    @FXML private Text textTerminal;
    @FXML private ListView<String> listViewNeighbors;
    @FXML private ListView<String> listViewInfoMote;
    @FXML private Label labelTerminal;
    @FXML private Text textRoutes;
    @FXML private Label labelRoutes;
    @FXML private Label labelRes;
    @FXML private Pane AlertMessage;
    @FXML private Text TextAlert;
    
    
    @FXML
    private void mainController(ActionEvent event) throws IOException {
    	WgetJava obj = new WgetJava();
    	RoutesMotes routes = new RoutesMotes();
    	ResourcesMotes res = new ResourcesMotes();
    	GETClient client = new GETClient();
    	
    	//Captura a URL do Border Router digitada
      urlBorderRouter=textFieldURL.getText();
	 
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
    	System.out.println(resource);
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
    		StringBuilder urlResource = new StringBuilder();
    		
    		  //Captura o ip selecionado na listView
    		String ipMote = listViewNeighbors.getSelectionModel().getSelectedItem();
    		
    		  //Captura o recurso selecionado na listView
    		urlResource = res.getURLResource(ipMote,resource);
    		
    		System.out.println(urlResource.toString());
    			
    		
    	}
    }
    
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		StringBuilder list = new StringBuilder();
		
		list.append("linha1\n");
		list.append("linha2\n");
		list.append("linha3\n");
		list.append("linha4\n");
		list.append("linha5\n");
		list.append("linha6\n");
		list.append("linha7\n");
		list.append("linha8\n");
		list.append("linha9\n");
		list.append("linha10\n");
		list.append("linha11\n");
		list.append("linha12\n");
		list.append("linha13\n");
		list.append("linha14\n");
		list.append("linha15\n");
		
		labelRoutes.setText(list.toString());
		
		ObservableList<String> listObs = FXCollections.observableArrayList();
		
		listObs.add(list.toString());
		
		listViewNeighbors.setItems(listObs);
		listViewInfoMote.setItems(listObs);
		
	}
    

}
