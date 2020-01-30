package application;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class Controller implements Initializable{
	
	private String url;
	private String urlmote;

    @FXML
    private Button buttonDiscover;

    @FXML
    private TextField textFieldURL;

    @FXML
    private Text textNeighbors;

    @FXML
    private Text textInfoMote;

    @FXML
    private Button buttonGet;

    @FXML
    private Button buttonObs;

    @FXML
    private Text textTerminal;

    @FXML
    private ListView<String> listViewNeighbors;

    @FXML
    private ListView<String> listViewInfoMote;

    @FXML
    private Label labelTerminal;

    @FXML
    private Text textRoutes;


    @FXML
    private Label labelRoutes;
    
    @FXML
    private Text textResources;

    @FXML
    private Label labelRes;

    
    @FXML
    private void mainController(ActionEvent event) throws IOException {
    	WgetJava obj = new WgetJava();
    	RoutesMotes routes = new RoutesMotes();
    	ResourcesMotes res = new ResourcesMotes();
    	
      url=textFieldURL.getText();
	 
        //Armazena  URL do Border Router
      obj.setUrl(url);
    
        //Faz o pedido ao Border Router da informação e armazena
      obj.sendGET();
            
      treatmentOfInformation(obj,routes);
      showIPs(routes,res);
      showRoutes(routes);
      showResources();
    }
    
    private void treatmentOfInformation(WgetJava obj,RoutesMotes routes) {
    	
        //Armazena as informações das Rotas e IP
      routes.setResponse(obj.getResponse());
    
        //Filtra e separa os IPs das Rotas
      routes.filterResponse();
    }
    
    private void showIPs(RoutesMotes routes,ResourcesMotes res) {
        ObservableList<String> ips = FXCollections.observableArrayList ();
        
        res.setIPs(routes.getListIPs());
        
        ips.addAll(res.getCoapIPs());
        
        listViewNeighbors.setItems(ips);
    }
    
    private void showRoutes(RoutesMotes routes) {
    	            
      StringBuilder rotas = new StringBuilder();
            
      for (String arr : routes.getListRoutes()) {
    	    rotas.append(arr);
    	}      
    
      labelRoutes.setText(rotas.toString().replace(",", "")); 
    }
    
    private void showResources() {
		ResourcesMotes res = new ResourcesMotes();
		GETClient client = new GETClient();


    	listViewNeighbors.setOnMouseClicked(new EventHandler() {
			@Override
			public void handle(Event event) {
				showAlert();
				ObservableList<String> resources = FXCollections.observableArrayList();

				String url;
								
				url = res.URLWellKnownCore(listViewNeighbors.getSelectionModel().getSelectedItem());
				
				resources.addAll(res.setResources(client.discover(url)));
				System.out.println(resources.toString());
    			listViewInfoMote.setItems(resources);
			}			 
	 });
    }
    
    private void showAlert() {
        Alert alert = new Alert(null);
        //alert.setTitle("Test Connection");
 
        // Header Text: null
        alert.setHeaderText(null);
        alert.setWidth(50);
        alert.setContentText("Aguarde...");
 
        alert.showAndWait();
    }   
    
    
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
				
		
		
		
	}

    
    
       

}
