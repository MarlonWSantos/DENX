package ufpa.facomp.gercom.iipdn;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class Main extends Application {

		
	private static Stage screenLoading = null;

	@Override
	public void start(Stage primaryStage) {
		try {
			AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("iipdn.fxml"));
			Scene scene = new Scene(root,924,537);
			scene.getStylesheets().add(getClass().getResource("iipdn.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("IIPDN - GUI CoAP Client");
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		launch(args);

	}
	
	//Cria o PopUp loading na tela do usuário
	public void showScreenLoading() {
		Pane loading = null;
		try {
			loading = (Pane)FXMLLoader.load(getClass().getResource("alert.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Scene loadingScene = new Scene(loading);
		screenLoading = new Stage();
		screenLoading.setScene(loadingScene);
		screenLoading.show();		
	}
	
	//Fecha o PopUp Loading da tela do usuário
	public void closeScreenLoading() {
		screenLoading.close();
	}

}
