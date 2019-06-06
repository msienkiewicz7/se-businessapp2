package com.businessapp.fxgui.fxml;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import com.businessapp.fxgui.FXBuilder;
import com.businessapp.logic.CalculatorIntf;


public class CalculatorFXMLController implements FXMLControllerIntf, CalculatorIntf {
	private CalculatorIntf logic = null;
	private Tab tab = null;
	

	private final static Object[][] ShortCutKeys = new Object[][] {
		{ "\r",	Token.K_EQ		},
		{ "\b",	Token.K_BACK	},
		{ "c",	Token.K_C		},
		{ "e",	Token.K_CE		},
		{ ".",	Token.K_DOT		},
		{ "m",	Token.K_VAT		},
		{ "t",	Token.K_1000	},
	};

	@FXML
	private AnchorPane anchorPane;

	@FXML
	private TextField displayTextField;

	@FXML
	private TextArea sideTextArea;

	@FXML
	private GridPane keypadGridPane;


	@Override
	public void inject( FXBuilder parent, Tab... tab ) {
		if( tab.length > 0 ) {
			this.tab = tab[ 0 ];
		}
	}

	@Override
	public void inject( CalculatorIntf component ) {
		this.logic = component;
	}

	@Override
	public void nextToken( Token tok ) {
		// empty on GUI-side of CalculatorIntf
	}

	@Override
	public void write( int selector, String text ) {
		switch( selector ) {
		case 0:
			displayTextField.setText( text );
			break;
		case 1:
			sideTextArea.setText( text );
			break;
		}
	}


	@Override
	public void start() {
		if( tab != null ) {
			TabPane tabsPane = tab.getTabPane();
	
			// add KEY_TYPED event handler when Calculator tab is selected, and remove if unselected
			tabsPane.getSelectionModel().selectedItemProperty().addListener( ( ov, oldTab, newTab ) -> {
				if( tab.getId().equals( newTab.getId() ) ) {
					//pane.setOnKeyTyped( e -> {
					//});
					tabsPane.addEventHandler( KeyEvent.KEY_TYPED, KEY_TYPED_EventHandler );
	
				} else {
					if( tab.getId().equals( oldTab.getId() ) ) {
						tabsPane.removeEventHandler( KeyEvent.KEY_TYPED, KEY_TYPED_EventHandler );
					}
				}
			});
	
			int keyPadCols = 4;	// -- Java 9: keypadGridPane.getColumnCount();
			int i = 0;
			for( Node n : keypadGridPane.getChildren() ) {
				Button btn = (Button)n;

				btn.setOnMousePressed( ( e ) -> {
					Button bt = (Button)e.getSource();
					int row = GridPane.getRowIndex( bt );
					int col = GridPane.getColumnIndex( bt );
					int idx = row * keyPadCols + col;	// flatten grid coordinates to idx[0..n]
					if( this.logic != null ) {
						this.logic.nextToken( Token.values()[ idx ] );
					}
				});
	
				// button has focus after valid KeyPress-event, button then also receives KeyRelease
				// to release focus (and remove border highlighting from button)
				btn.setOnKeyReleased( ( e ) -> {
					anchorPane.requestFocus();
				});
	
				btn.setText( KeyLabels[ i++ ] );
			}
	
			// regain focus of anchorPane element to receive key events.
			anchorPane.addEventFilter( MouseEvent.ANY, (e) -> anchorPane.requestFocus() );
	
			displayTextField.setEditable( false );
			sideTextArea.setEditable( false );
		}
	}

	@Override
	public void stop() { }

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	/*
	 * Private methods.
	 */

	private EventHandler<KeyEvent> KEY_TYPED_EventHandler = new EventHandler<KeyEvent>() {
		public void handle( KeyEvent e ) {
			String s = e.getCharacter();	// e.getCode() -> case UP, DOWN, SHIFT...
			int idx = -1;
			for( int j=0; idx < 0 && j < KeyLabels.length; j++ ) {
				if( KeyLabels[ j ].equals( s ) ) {
					idx = j;
				}
			}
			if( idx < 0 ) {
				for( Object[] sc : ShortCutKeys ) {
					if( s.equals( sc[0] ) ) {
						idx = ((Token)sc[1]).ordinal();
						break;
					}
				}
			}
			if( idx >= 0 ) {
				Button btn = (Button)keypadGridPane.getChildren().get( idx );
				btn.requestFocus();	// mimic mouse pressed highlighting
				btn.fireEvent(
					new MouseEvent(MouseEvent.MOUSE_PRESSED,
						btn.getLayoutX(), btn.getLayoutY(),
						btn.getLayoutX(), btn.getLayoutY(), MouseButton.PRIMARY, 1,
						true, true, true, true, true, true, true, true, true, true, null
				));
			}
		}
	};

}
