package com.businessapp.fxgui.fxml;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import com.businessapp.fxgui.FXBuilder;


public class AppFXMLController implements FXMLControllerIntf {
	private FXBuilder parent = null;

	@FXML
	private TabPane fxApp_TabsPane;


	@FXML
	private void exitButton() {
		if( parent != null ) {
			parent.stop();
		}
	}

	@Override
	public void inject( FXBuilder parent, Tab... tab ) {
		this.parent = parent;
	}

	public TabPane getTabPane() {
		return fxApp_TabsPane;
	}

}
