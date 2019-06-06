package com.businessapp.fxgui.fxml;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.fxml.FXML;

import java.util.ArrayList;
import java.util.List;

import com.businessapp.fxgui.FXBuilder;


/**
 * FXML Controller class for Customer.fxml
 * 
 */
public class TableViewFXMLController implements FXMLControllerIntf {//, RepositoryIntf<EntityIntf> {
	private FXBuilder parent = null;
	private Tab tab = null;
	private TableViewable tvd = null;
	private boolean isResizableTable = false;

	/**
	 * FXML skeleton defined as:
	 * AnchorPane > GridPane > TableView	- GridPane as resizable container for TableView
	 * AnchorPane > HBox > Button			- buttons in footer area
	 * 
	 * Defined CSS styles in derived <?>-classes:
	 *   .tableview-<?>-column-id
	 *   .tableview-<?>-column-name
	 *   .tableview-<?>-column-status
	 *   .tableview-<?>-column-contacts
	 *   .tableview-<?>-column-notes
	 *   .tableview-<?>-column-notes-button
	 *   .tableview-hbox
	 */

	@FXML
	private AnchorPane fxTableView_AnchorPane;

	@FXML
	private GridPane fxTableView_GridPane;

	@FXML
	private TableView<TableViewable> fxTableView_TableView;

	@FXML
	private TableColumn<TableViewable,String> fxTableView_TableCol_ID;

	@FXML
	private HBox fxTableView_HBox;	// Bottom area container for buttons, search box, etc.


	@Override
	public void inject( FXBuilder parent, Tab... tab ) {
		this.parent = parent;
		if( tab.length > 0 ) {
			this.tab = tab[ 0 ];
		}
	}

	public void inject( TableViewable tvd ) {
		this.tvd = tvd;
	}

	public void inject( boolean isResizable ) {
		this.isResizableTable = isResizable;
	}

	//@Override
	public void start() {
		tab.setText( tvd.getTabLabel() );
		// Width adjustment assumes layoutX="12.0", layoutY="8.0" offset.
		fxTableView_HBox.prefWidthProperty().bind( ((AnchorPane) fxTableView_AnchorPane).widthProperty().subtract( 12 ) );
		fxTableView_HBox.prefHeightProperty().bind( ((AnchorPane) fxTableView_AnchorPane).heightProperty() );

		fxTableView_GridPane.prefWidthProperty().bind( ((AnchorPane) fxTableView_AnchorPane).widthProperty().subtract( 16 ) );
		fxTableView_GridPane.prefHeightProperty().bind( ((AnchorPane) fxTableView_AnchorPane).heightProperty().subtract( 70 ) );
		fxTableView_HBox.setPickOnBounds( false );
		fxTableView_HBox.getStyleClass().add( "tableview-hbox" );	//tvd.getCssPrefix() + "-hbox" );

		for( int i = 0; i < tvd.getColumnNumber(); i++ ) {
			TableColumn<TableViewable,String> tableCol = null;
			if( i == 0 ) {
				tableCol = fxTableView_TableCol_ID;
			} else {
				tableCol = new TableColumn<>();
				fxTableView_TableView.getColumns().add( tableCol );
			}
			tableCol.setText( tvd.getColLabel( i ) );
			tableCol.getStyleClass().add( tvd.getColStyleClass( i ) );

			final Callback<TableColumn<TableViewable,String>, TableCell<TableViewable,String>> cellFactory = tvd.getCellFactory( i );
			if( cellFactory != null ) {
				tableCol.setCellFactory( cellFactory );

			} else {
				final int i2 = i;	// must use final variable inside lambda-{}.
				tableCol.setCellValueFactory( cellData -> {
					StringProperty observable = new SimpleStringProperty();
					TableViewable tvv = cellData.getValue();
					observable.set( tvv.getCellValue( i2 ) );
					return observable;
				});
			}
		}

		/*
		 * Define selection model that allows to select multiple rows.
		 */
		fxTableView_TableView.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );

		/*
		 * Allow horizontal column squeeze of TableView columns. Column width can be fixed
		 * with -fx-pref-width: 80px;
		 */
		if( this.isResizableTable ) {
			fxTableView_TableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
		}

		/*
		 * Double-click on row: open update dialog.
		 */
		fxTableView_TableView.setRowFactory( tv -> {
			TableRow<TableViewable> row = new TableRow<>();
			row.setOnMouseClicked( event -> {
				if( event.getClickCount() == 2 && ( ! row.isEmpty() ) ) {
					fxTableView_Update();
				}
			});
			return row;
		});
	}


	public void reloadData() {
		ObservableList<TableViewable> list = tvd.getData();
		fxTableView_TableView.setItems( list );
	}


	@FXML
	void fxTableView_Delete() {
		ObservableList<TableViewable> selection = fxTableView_TableView.getSelectionModel().getSelectedItems();
		if( selection.size() > 0 ) {
			List<String> ids = new ArrayList<String>();
			for( TableViewable tvd : selection ) {
				ids.add( tvd.getId() );
			}
			fxTableView_TableView.getSelectionModel().clearSelection();
			tvd.delete( ids );
		} else {
			//TODO: log
			System.err.println( "nothing selected." );
		}
	}

	@FXML
	void fxTableView_New() {
		TableViewable tv = tvd.createInstance();
		openUpdateDialog( tv, true );
	}

	@FXML
	void fxTableView_Update() {
		TableViewable tv = fxTableView_TableView.getSelectionModel().getSelectedItem();
		if( tv != null ) {
			openUpdateDialog( tv, false );

		} else {
			//TODO: log
			System.err.println( "nothing selected." );
		}
	}

	@FXML
	void fxTableView_Exit() {
		if( parent != null ) {
			parent.stop();
		}
	}


	/*
	 * Private methods.
	 */
	private void openUpdateDialog( TableViewable tv, boolean newItem ) {
		TableViewPopupUpdateForm dialog = new TableViewPopupUpdateForm( tv.getTabLabel(), tv, newItem );
		dialog.show();
	}

}
