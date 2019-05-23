package com.businessapp.fxgui.fxml;

import com.businessapp.repositories.ReservationRepositoryIntf;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.List;

import com.businessapp.logic.ManagedComponentIntf;
import com.businessapp.repositories.ArticleRepositoryIntf;
import com.businessapp.repositories.CustomerRepositoryIntf;


public abstract class TableViewable implements ManagedComponentIntf {
	// Generic column names in TableView.
	enum Fields { name, label, css, isVisible, isEdible };

	/*
	 * Public factory methods.
	 */
	public static TableViewable createTableView_Customer(
		TableViewFXMLController tvFxmlController, CustomerRepositoryIntf repository )
	{
		return new TableViewable_Customer( tvFxmlController, repository );
	}

	public static TableViewable createTableView_Article(
			TableViewFXMLController tvFxmlController, ArticleRepositoryIntf repository )
	{
		return new TableViewable_Article( tvFxmlController, repository );
	}

	public static TableViewable createTableView_Reservation(
			TableViewFXMLController tvFxmlController, ReservationRepositoryIntf repository )
	{
		return new TableViewable_Reservation( tvFxmlController, repository );
	}

	abstract Object[][] getColDescr();

	Object getColDescr( Fields field, int col ) {
		switch( field ) {
		case name:		return getColDescr()[ col ][ Fields.name.ordinal() ];
		case label:		return getColDescr()[ col ][ Fields.label.ordinal() ];
		case css:		return getColDescr()[ col ][ Fields.css.ordinal() ];
		case isVisible:	return getColDescr()[ col ][ Fields.isVisible.ordinal() ].equals( "1" );
		case isEdible:	return getColDescr()[ col ][ Fields.isEdible.ordinal() ].equals( "1" );
		}
		return null;
	}

	/*
	 * CRUD-methods used to manipulate underlying data (list).
	 */
	public abstract TableViewable createInstance();
	public abstract String getId();
	public abstract void importFormData( HashMap<String,String> formValues );
	public abstract void delete( List<String> ids );

	public abstract ObservableList<TableViewable> getData();


	/*
	 * Table description methods used to construct TableView.
	 */
	public abstract String getTabLabel();
	public abstract String getCellValueAsString( int col );
	public abstract String getCellValue( int col );
	public abstract Callback<TableColumn<TableViewable,String>,
		TableCell<TableViewable,String>> getCellFactory( int col );

	public int getColumnNumber() {
		return getColDescr().length;
	}

	public String getColName( int col ) {
		return (String)getColDescr( Fields.name, col );
	}

	public String getColLabel( int col ) {
		return (String)getColDescr( Fields.label, col );
	}

	public String getColStyleClass( int col ) {
		return (String)getColDescr( Fields.css, col );
	}

	public boolean fieldVisibleOnUpdate( int col ) {
		return (boolean)getColDescr( Fields.isVisible, col );
	}

	public boolean fieldEditable( int col ) {
		return (boolean)getColDescr( Fields.isEdible, col );
	}

}
