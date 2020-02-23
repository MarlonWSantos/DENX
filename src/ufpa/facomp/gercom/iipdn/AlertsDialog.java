package ufpa.facomp.gercom.iipdn;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertsDialog extends Alert {
	
	private String headerText;

	public AlertsDialog(AlertType alertType) {
		super(alertType);
		
		this.show();
	}

	public AlertsDialog(AlertType alertType, String contentText, ButtonType... buttons) {
		super(alertType, contentText, buttons);
		
		this.show();
	}
	
	public AlertsDialog(AlertType alertType,String headerText,String contentText,ButtonType... buttons) {
		super(alertType, contentText, buttons);
		this.headerText=headerText;
		
		this.show();
	}
	

}
