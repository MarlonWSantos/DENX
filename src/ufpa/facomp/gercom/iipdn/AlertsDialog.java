package ufpa.facomp.gercom.iipdn;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class AlertsDialog extends Alert {

	private String headerText;
	private Exception ex;
	private String exceptionText;
	private Label label;
	private TextArea textArea;
	private GridPane expContent;


	//Construtor para caixas de diálgos simples
	public AlertsDialog(AlertType alertType) {
		super(alertType);		
	}


	//Contrutor para caixas de diálogos com conteúdo em texto editável 
	public AlertsDialog(AlertType alertType, String contentText, ButtonType... buttons) {
		super(alertType, contentText, buttons);

		//Exibe alerta
		this.show();
	}


	//Contrutor para caixas de diálogos com texto editável na header e no conteúdo 
	public AlertsDialog(AlertType alertType,String headerText,String contentText,ButtonType... buttons) {
		super(alertType, contentText, buttons);

		//Inseri o conteúdo da Header
		this.headerText=headerText;

		//Inseri o conteúdo do alerta
		this.setHeaderText(headerText);

		//Exibe o alerta
		this.show();
	}


	//construtor para caixas de diálogos com Exceptions como conteúdo
	public AlertsDialog(Exception e) {
		super(AlertType.ERROR);

		//Recebe a exception
		this.ex=e;

		//Inseri o conteúdo do alerta
		this.setHeaderText("Exception");

		//Torna o alerta redimensionável
		this.setResizable(true);

		//Coleta o conteúdo da stacktrace
		createStackTrace();

		//Constroi área que vai receber a stacktrace
		createContent();

		//Constroi grid para exibir a stacktrace
		createGridContent();

		//Inseri no alerta o grid com o conteúdo da exception
		this.getDialogPane().setContent(expContent);

		//Defino a altura e largura do alerta para no mínimo igual a altura  e largura do conteúdo 
		this.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);		
		this.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);

		//Exibe o alerta
		this.showAndWait();
	}


	//Coleta o conteúdo da stacktrace
	public void createStackTrace( ) {

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		//Captura conteúdo do stacktrace para buffer
		ex.printStackTrace(pw);

		//Armazena o conteúdo da exception que estava no buffer
		exceptionText = sw.toString();
	}


	//Constroi área que vai receber a stacktrace
	public void createContent() {

		label = new Label("The stacktrace:");
		textArea = new TextArea(exceptionText);

		//Torna não editavel a área que exibirá o conteúdo do stacktrace
		textArea.setEditable(false);

		//Torna o conteúdo ajustavél ao tamanho da tela
		textArea.setWrapText(true);

		//Define largura e altura ajustável até o máximo permitido 
		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
	}


	//Constroi grid para exibir a stacktrace
	public void createGridContent() {

		//Define prioridade de exibição na vertical e horizontal do componente
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		expContent = new GridPane();

		//Define tamanho máximo do Grid
		expContent.setMaxWidth(Double.MAX_VALUE);

		//Inseri os componentes dentro do grid
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);
	}	
}
