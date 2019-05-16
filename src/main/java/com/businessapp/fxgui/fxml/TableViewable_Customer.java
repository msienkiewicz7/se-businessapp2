package com.businessapp.fxgui.fxml;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.List;

import com.businessapp.model.Note;
import com.businessapp.model.Customer;
import com.businessapp.model.Customer.CustomerStatus;
import com.businessapp.repositories.CustomerRepositoryIntf;


class TableViewable_Customer extends TableViewable {
	public final static String ContactSeparator = "; ";

	// Customer object properties.
	private final CustomerRepositoryIntf repository;
	private final Customer entity;

	// CustomerRepository properties.
	private final ObservableList<TableViewable> tvItemsList;
	private final TableViewFXMLController tvFxmlController;

	/*
	 * TableView description information.
	 */
	private static final String tabLabel = "Kunden";
	private static final String cssPrefix = "tableview-customer";

	// Enumerate Customer columns in Tableview.
	private enum Col { id, firstname ,name, status, notes, contact };

	private static final String[][] _colDescr = {
		// i_col=0,			i_label=1,	i_css=2,				i_visble=3, i_edble=4
		{ Col.id.name(),	"Kund.-Nr.",cssPrefix + "-column-id",		"1", "0" },
		{ Col.firstname.name(),"Vorname",	cssPrefix + "-column-firstname",	"1", "1" },
		{ Col.name.name(),	"Name",		cssPrefix + "-column-name",		"1", "1" },
		{ Col.status.name(),"Status",	cssPrefix + "-column-status",	"1", "1" },
		{ Col.notes.name(),	"Anmerk.",	cssPrefix + "-column-notes",	"0", "0" },
		{ Col.contact.name(),"Kontakt",	cssPrefix + "-column-contacts",	"1", "1" },
	};

	@Override
	Object[][] getColDescr() {
		return _colDescr;
	}

	/*
	 * Defined CSS style classes:
	 *   .tableview-customer-column-id
	 *   .tableview-customer-column-name
	 *   .tableview-customer-column-status
	 *   .tableview-customer-column-notes
	 *   .tableview-customer-column-notes-button
	 *   .tableview-customer-column-contacts
	 *   .tableview-hbox
	 */

	/*
	 * Public constructor for CustomerRepositoryDAO instance.
	 */
	public TableViewable_Customer( TableViewFXMLController tvFxmlController, CustomerRepositoryIntf custRepo ) {
		this.repository = custRepo;
		this.entity = null;		// null indicates CustomerRepositoryDAO instance.
		this.tvItemsList = FXCollections.observableArrayList();
		this.tvFxmlController = tvFxmlController;
	}

	/*
	 * Public constructor for individual CustomerDAO instances.
	 */
	private TableViewable_Customer( TableViewFXMLController tvFxmlController, CustomerRepositoryIntf custRepo,
			ObservableList<TableViewable> tvItemsList, Customer e )
	{
		this.repository = custRepo;
		this.entity = e;
		this.tvItemsList = tvItemsList;
		this.tvFxmlController = tvFxmlController;
	}


	/*
	 * Managebility methods.
	 */
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
		for( Customer entity : repository.findAll() ) {
			TableViewable_Customer tvc = new TableViewable_Customer( tvFxmlController, repository, tvItemsList, entity );
			tvItemsList.add( tvc );
		}
		tvFxmlController.reloadData();
	}

	/*
	 * CRUD-methods used to manipulate underlying data (list).
	 */
	@Override
	public TableViewable createInstance() {
		Customer entity = repository.create();
		TableViewable tvEntity = new TableViewable_Customer( tvFxmlController, repository, tvItemsList, entity );
		return tvEntity;
	}

	@Override
	public String getId() {
		return entity.getId();
	}

	@Override
	public void importFormData( HashMap<String,String> formData ) {

		if( formData != null ) {

			for( String key : formData.keySet() ) {

				String val = formData.get( key );
				//System.out.println( "==> updated(" + cx.getId() + "): " + key + ", v: " + val );
				switch( Col.valueOf( key ) ) {
				//case id:	//id's can't be updated.

				case firstname:
					entity.setFirstName( val );
					break;

				case name:
					entity.setName( val );
					break;

				case status:
					String st = val.toLowerCase();
					entity.setStatus( st.startsWith( "act" )? CustomerStatus.ACTIVE :
						st.startsWith( "sus" )? CustomerStatus.SUSPENDED : CustomerStatus.TERMINATED
					);
					break;

				case notes:
					entity.getNotes().add( new Note( val ) );
					break;

				case contact:
					entity.getContacts().clear();
					// comma-separated list of contacts
					String[] csv = val.split( ContactSeparator.trim() );
					for( String contact : csv ) {
						entity.getContacts().add( contact.trim() );
					}
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
		String ret = "-";
		switch( Col.valueOf( getColName( col ) ) ) {
		case id:	return entity.getId();
		case firstname:	return entity.getFirstName();
		case name:	return entity.getName();
		case status:
			CustomerStatus st = entity.getStatus();
			switch( st ) {
			case SUSPENDED: ret = "SUSP"; break;
			case TERMINATED: ret = "TERM"; break;
			default: ret = st.name();
			}
			return ret;
		case notes:
			List<Note> notes = entity.getNotes();
			ret = "" + notes.size();
			break;
		case contact:
			StringBuffer sb = new StringBuffer();
			for( String contact : entity.getContacts() ) {
				sb.append( sb.length() > 0? ContactSeparator : "" ).append( contact );
			}
			if( sb.length() == 0 ) {
				sb.append( "--" );
			}
			ret = sb.toString();
			break;
		}
		return ret;
	}

	@Override
	public String getCellValue( int col ) {
		String ret = "-";
		switch( Col.valueOf( getColName( col ) ) ) {

		case notes:
			List<Note> notes = entity.getNotes();
			ret = "" + notes.size();
			break;

		case contact:
			List<String> contacts = entity.getContacts();
			if( contacts.size() >= 1 ) {
				ret = contacts.get( 0 );
				if( contacts.size() > 1 ) {
					ret += ContactSeparator + contacts.get( 1 );
					if( contacts.size() > 2 ) {
						int len = contacts.size() - 2;
						ret += ContactSeparator + " +" + len + " more...";
					}
				}
			} else {
				ret = "--";
			}
			break;

		default:
			return getCellValueAsString( col );
		}
		return ret;
	}

	@Override
	public Callback<TableColumn<TableViewable, String>, TableCell<TableViewable, String>> getCellFactory( int col ) {
		switch( Col.values()[ col ] ) {
		case notes:
			// return special cell rendering for Notes column showing clickable label with Notes-count.
			return getNotesCellFactory();
		default:
			return null;
		}
	}


	/*
	 * Private methods.
	 */

	private Callback<TableColumn<TableViewable, String>, TableCell<TableViewable, String>> getNotesCellFactory() {
		return new Callback<TableColumn<TableViewable, String>, TableCell<TableViewable, String>>() {

			@Override
			public TableCell<TableViewable, String> call( TableColumn<TableViewable, String> col ) {
				col.setCellValueFactory( cellData -> {
					TableViewable c = cellData.getValue();
					StringProperty observable = new SimpleStringProperty();
					observable.set( c.getId() );
					return observable;
				});

				TableCell<TableViewable, String> tc = new TableCell<TableViewable, String>() {
					TableViewPopupNotes popupNotes = null;
					final Button btn = new Button();

					@Override public void updateItem( final String item, final boolean empty ) {
						super.updateItem( item, empty );
						int rowIdx = getIndex();
						ObservableList<TableViewable> cust = tvItemsList;

						if( rowIdx >= 0 && rowIdx < cust.size() ) {
							TableViewable_Customer customer = (TableViewable_Customer)cust.get( rowIdx );
							setGraphic( null );		// always clear, needed for refresh
							if( customer != null ) {
								btn.getStyleClass().add( cssPrefix + "-column-notes-button" );
								List<Note> nL = customer.entity.getNotes();
								btn.setText( "notes: " + nL.size() );
								setGraphic( btn );	// set button as rendering of cell value

								//Event updateEvent = new ActionEvent();
								btn.setOnMouseClicked( event -> {
									String n = customer.entity.getName();
									String label = ( n == null || n.length() == 0 )? customer.getId() : n;

									if( popupNotes == null ) {	// ensure that only one Popup is opened.

										popupNotes = new TableViewPopupNotes( label, nL,
											updatedListLen ->  {
												btn.setText( "notes: " + updatedListLen );
											}
										);
										popupNotes.show();

										popupNotes.setOnCloseRequest( evt3 -> {
											btn.setText( "notes: " + customer.entity.getNotes().size() );
											if( popupNotes.isAltered() ) {
												//List<Note> n2L = customer.entity.getNotes();
												//for( Note n2 : n2L ) {
												//	System.out.println( "--> " + n2.getNoteText() );
												//}
												repository.update( customer.entity, false );
											}
											popupNotes.close();
											popupNotes = null;
										});
									}
									if( popupNotes != null ) {
										popupNotes.requestFocus();
									}
								});
							}

						} else {

							setGraphic( null );		// reset button in other rows
						}
					}
				};
				return tc;
			}
		};
	}
}
