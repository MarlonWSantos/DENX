package ufpa.facomp.gercom.iipdn;

import java.io.IOException;
import java.util.Optional;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


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

			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent we) {

					verifyIsObserving(we);					
				}
			});

		} catch(Exception e) {

			new AlertsDialog(e);
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
			Scene loadingScene = new Scene(loading);
			screenLoading = new Stage();
			screenLoading.setScene(loadingScene);
			screenLoading.show();

		} catch (IOException e) {
			new AlertsDialog(e);
		}

	}

	//Fecha o PopUp Loading da tela do usuário
	public void closeScreenLoading() {
		screenLoading.close();
	}

	//Verifica se não está ocorrendo observação
	public void verifyIsObserving(WindowEvent we) {

		Controller ctrl = new Controller();

		//Se houver uma observação em andamento aviso usuário
		if(ctrl.isObserving()) {

			AlertsDialog alertInterrupt = new AlertsDialog(AlertType.CONFIRMATION);
			alertInterrupt.setContentText("There is an observation in progress\nReally want to close the application?");

			Optional<ButtonType> result = alertInterrupt.showAndWait();

			//Se usuário escolher sim, para o(s) thread(s) e finaliza aplicação
			if(result.get()== ButtonType.OK) {
				new ThreadsObserve();

				//do contrário, aborta finalização da aplicação
			}else {
				we.consume();
			}												
		} 
	}

}
