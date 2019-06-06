package com.businessapp.fxgui.fxml;

import javafx.scene.control.Tab;

import com.businessapp.fxgui.FXBuilder;


public interface FXMLControllerIntf {

	public void inject( FXBuilder parent, Tab... tab );

}
