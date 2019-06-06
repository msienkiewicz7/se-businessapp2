package com.businessapp.fxgui.fxml;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.HashMap;


/*
 * Local helper class for a Popup that allows editing generic String property sets.
 * It is used when logic data, e.g. Customer data is updated or new objects are
 * created and property values need to be entered.
 */
class TableViewPopupUpdateForm extends Stage {

	TableViewPopupUpdateForm( String label, TableViewable c, boolean newItem ) {
		this.setTitle( label );
		this.initModality( Modality.WINDOW_MODAL );
		this.initStyle( StageStyle.UTILITY );
		this.setWidth( 360 );
		Node focus = null;

		int len = c.getColumnNumber();
		TextField[] textFields = new TextField[ len ];
		GridPane gp = new GridPane();

		for( int i = 0; i < len; i++ ) {
			if( c.fieldVisibleOnUpdate( i ) ) {
				String dp = c.getColLabel( i );
				textFields[ i ] = new TextField();
				gp.addRow( i, new Label( dp ), textFields[ i ] );
				gp.setHgap( 10 );
				gp.setVgap( 10 );
				GridPane.setHgrow( textFields[ i ], Priority.ALWAYS );
	
				textFields[ i ].setText( c.getCellValueAsString( i ) );
				if( ! c.fieldEditable( i ) ) {
					textFields[ i ].setEditable( false );
					textFields[ i ].setStyle(
						"-fx-border-radius: 4px;" +
						"-fx-border-color: lightgrey;" +
						"-fx-background-color: #fafafa;"
					);
				} else {
					if( focus==null ) {
						focus = textFields[ i ];
					}
				}
			}
		}

		Button OK = new Button( "OK" );
		OK.setDefaultButton( true );

		Button Cancel = new Button( "Cancel" );
		Cancel.setCancelButton( true );

		OK.setOnAction( e -> {
			// Update only altered values.
			HashMap<String,String> updatedValues = null;
			for( int i = 0; i < len; i++ ) {
				if( c.fieldVisibleOnUpdate( i ) && c.fieldEditable( i ) ) {
					String oldValue = c.getCellValueAsString( i );
					String updated = textFields[ i ].getText();
					if( oldValue != null && updated != null && oldValue != updated ) {
						if( ! oldValue.equals( updated ) ) {
							if( updatedValues == null ) {
								updatedValues = new HashMap<String,String>();
							}
							String field = c.getColName( i );
							updatedValues.put( field,  updated );
						}
					}
				}
			}
			c.importFormData( updatedValues );
			this.close();	// close() fires ACTION event.
		});

		Cancel.setOnAction( e -> {
			this.close();
		});

		HBox buttons = new HBox();
		buttons.setSpacing( 10 );
		buttons.setAlignment( Pos.CENTER_RIGHT );
		buttons.getChildren().addAll( OK, Cancel );

		VBox layout = new VBox( 20 );
		VBox vspacer = new VBox( 20 );	// add more vertical space to fully show buttons
		layout.getChildren().addAll( gp, buttons, vspacer );
		layout.setPadding( new Insets( 5 ) );

		Scene scene = new Scene( layout );
		this.setScene( scene );

		if( focus != null ) {
			focus.requestFocus();
		}
	}
}
