package com.businessapp.fxgui.fxml;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.List;

import com.businessapp.model.Article;
import com.businessapp.repositories.ArticleRepositoryIntf;


class TableViewable_Article extends TableViewable {
	// Article object properties.
	private final ArticleRepositoryIntf repository;
	private final Article entity;

	// ArticleRepository properties.
	private final ObservableList<TableViewable> tvItemsList;
	private final TableViewFXMLController tvFxmlController;

	/*
	 * TableView description information.
	 */
	private static final String tabLabel = "Artikel";
	private static final String cssPrefix = "tableview-article";

	// Enumerate Article columns in Tableview.
	private enum Col { id, name, price };

	private static final String[][] _colDescr = {
		// i_col=0,			i_label=1,	i_css=2,				i_visble=3, i_edble=4
		{ Col.id.name(),	"Art.-Nr.",	cssPrefix + "-column-id",		"1", "0" },
		{ Col.name.name(),	"Name",		cssPrefix + "-column-name",		"1", "1" },
		{ Col.price.name(),	"Preis",	cssPrefix + "-column-price",	"1", "1" },
	};

	@Override
	Object[][] getColDescr() {
		return _colDescr;
	}
	/*
	 * Defined CSS style classes:
	 *   .tableview-article-column-id
	 *   .tableview-article-column-price
	 *   .tableview-article-column-name
	 *   .tableview-hbox
	 */

	/*
	 * Public constructor for ArticleRepositoryDAO instance.
	 */
	public TableViewable_Article( TableViewFXMLController tvFxmlController, ArticleRepositoryIntf articleRepo ) {
		this.repository = articleRepo;
		this.entity = null;		// null indicates ArticleRepositoryDAO instance.
		this.tvItemsList = FXCollections.observableArrayList();
		this.tvFxmlController = tvFxmlController;
	}

	/*
	 * Public constructor for individual ArticleDAO instances.
	 */
	private TableViewable_Article( TableViewFXMLController tvFxmlController, ArticleRepositoryIntf articleRepo,
			ObservableList<TableViewable> tvItemsList, Article e )
	{
		this.repository = articleRepo;
		this.entity = e;
		this.tvItemsList = tvItemsList;
		this.tvFxmlController = tvFxmlController;
	}

	@Override
	public void start() {
		reload();
	}

	@Override
	public void stop() {
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	private void reload() {
		tvItemsList.clear();
		for( Article entity : repository.findAll() ) {
			TableViewable_Article tvc = new TableViewable_Article( tvFxmlController, repository, tvItemsList, entity );
			tvItemsList.add( tvc );
		}
		tvFxmlController.reloadData();
	}


	/*
	 * CRUD-methods used to manipulate underlying data (list).
	 */
	@Override
	public TableViewable createInstance() {
		Article entity = repository.create();
		TableViewable tvEntity = new TableViewable_Article( tvFxmlController, repository, tvItemsList, entity );
		return tvEntity;
	}

	@Override
	public String getId() {
		return entity.getId();
	}

	@Override
	public void importFormData( HashMap<String, String> formData ) {

		if( formData != null ) {

			for( String key : formData.keySet() ) {
				String val = formData.get( key );
				//System.out.println( "==> updated(" + cx.getId() + "): " + key + ", v: " + val );

				switch( Col.valueOf( key ) ) {
				//case id:	//id's can't be updated.
				case name:
					entity.setName( val );
					break;

				case price:
					entity.setPrice( Long.parseLong( val ) );
					break;

				default:
					break;
				}
			}
			repository.update( entity, true );
			reload();
		}
	}

	@Override
	public void delete( List<String> ids ) {
		repository.delete( ids );
		reload();
	}

	@Override
	public ObservableList<TableViewable> getData() {
		return tvItemsList;
	}


	/*
	 * Table description methods used to construct TableView.
	 */
	@Override
	public String getTabLabel() {
		return tabLabel;
	}

	@Override
	public String getCellValueAsString( int col ) {
		switch( Col.valueOf( getColName( col ) ) ) {
		case id:	return entity.getId();
		case name:	return entity.getName();
		case price:
			double doubleprice = entity.getPrice() / 100;
			return String.format( "%.2f EUR", doubleprice );
		default:	return "-";
		}
	}

	@Override
	public String getCellValue( int col ) {
		return getCellValueAsString( col );
	}

	@Override
	public Callback<TableColumn<TableViewable, String>, TableCell<TableViewable, String>> getCellFactory( int col ) {
		return null;
	}

}
