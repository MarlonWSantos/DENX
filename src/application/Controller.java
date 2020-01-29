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
    private void discoverMotes(ActionEvent event) throws IOException {
    	WgetJava obj = new WgetJava();
    	
      url=textFieldURL.getText();
	 
        //Armazena  URL do Border Router
      obj.setUrl(url);
    
        //Faz o pedido ao Border Router da informação e armazena
      obj.sendGET();
      treatmentOfInformation(obj);
    }
    
    private void treatmentOfInformation(WgetJava obj) {
    	
      RoutesMotes routes = new RoutesMotes();
    
        //Armazena as informações das Rotas e IP
      routes.setResponse(obj.getResponse());
    
        //Filtra e separa os IPs das Rotas
      routes.filterResponse();
      showRoutesAndIPs(routes);
    }
    
    
    private void showRoutesAndIPs(RoutesMotes routes) {
    	
      ObservableList<String> ips = FXCollections.observableArrayList ();
 
      
      ips.addAll(routes.getListIPs());
      
      listViewNeighbors.setItems(ips);
      
            
      StringBuilder rotas = new StringBuilder();
      
      
      for (String arr : routes.getListRoutes()) {
    	    rotas.append(arr);
    	}      
    
      labelRoutes.setText(rotas.toString().replace(",", "")); 
    }
    
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		listViewNeighbors.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		 listViewNeighbors.setOnMouseClicked(new EventHandler() {
				@Override
				public void handle(Event event) {
		            System.out.print(listViewNeighbors.getSelectionModel().getSelectedItem());					
				}			 
		 });
		
		
		
		
		
	}

    
    
       

}
