package ufpa.facomp.gercom.iipdn;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertsDialog extends Alert {
	
	private String headerText;

	
	//Construtor para caixas de diálgos simples
	public AlertsDialog(AlertType alertType) {
		super(alertType);
		
		this.show();
	}

	
	//Contrutor para caixas de diálogos com conteúdo em texto editável 
	public AlertsDialog(AlertType alertType, String contentText, ButtonType... buttons) {
		super(alertType, contentText, buttons);
		
		this.show();
	}
	
	
	//Contrutor para caixas de diálogos com texto editável na header e no conteúdo 
	public AlertsDialog(AlertType alertType,String headerText,String contentText,ButtonType... buttons) {
		super(alertType, contentText, buttons);
		this.headerText=headerText;
		
		this.show();
	}
}
