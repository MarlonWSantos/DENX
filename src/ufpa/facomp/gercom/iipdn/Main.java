package ufpa.facomp.gercom.iipdn;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;


public class Main extends Application {


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

}
