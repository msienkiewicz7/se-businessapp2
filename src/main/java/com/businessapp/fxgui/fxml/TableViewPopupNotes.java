package com.businessapp.fxgui.fxml;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.ArrayList;

import com.businessapp.model.Note;


/**
 * Functional interface to report updated length of Note list.
 * @author Sven Graupner
 *
 */
@FunctionalInterface
interface UpdateListLengthFI {
	void update( int len );
}

/**
 * Popup to edit Notes-list.
 * @author Sven Graupner
 *
 */
class TableViewPopupNotes extends Stage {
	private final Scene scene;
	private final ObservableList<Note> cellDataObservable;
	private boolean altered = false;


	public TableViewPopupNotes( String label, List<Note> notes, UpdateListLengthFI updateListLength_CB ) {
		this.setTitle( label );
		this.setWidth( 600 );
		this.setHeight( 500 );

		TableView<Note> tableView = new TableView<Note>();

		tableView.getSelectionModel().setCellSelectionEnabled( true );
		tableView.setEditable( true );

		cellDataObservable = FXCollections.observableArrayList( notes );
		tableView.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );

		/**
		 * Define column: Time.
		 */
		TableColumn<Note,String> timeCol = new TableColumn<Note,String>( "Zeit" );
		timeCol.setMinWidth( 120 );

		timeCol.setCellValueFactory(
			cellData2 -> {
				String cellVal = cellData2.getValue().getSimpleTimestamp();
				return new SimpleStringProperty( cellVal );
			}
		);

		/**
		 * Define column: Notes.
		 */
		TableColumn<Note,String> noteCol = new TableColumn<Note,String>( "Notizen" );
		noteCol.setMinWidth( 440 );
		noteCol.setEditable( true );

		noteCol.setCellFactory( TextFieldTableCell.forTableColumn() );

		noteCol.setCellValueFactory(
			cellData2 -> {
				String cellVal = cellData2.getValue().getNoteText();
				return new SimpleStringProperty( cellVal );
			}
		);

		noteCol.setOnEditCommit( edit -> {
			Note cellValue = edit.getRowValue();
			String newVal = edit.getNewValue().trim();
			cellValue.setNoteText( newVal );	// update cell value
			if( cellValue != null && newVal != null && ! cellValue.equals( newVal ) ) {
				updateNoteList( notes );		// update List<Note>
			}
			// _this.fireEvent( new ActionEvent() );
		});

		/*
		 * Double-click on row: open update dialog.
		 */
		tableView.setRowFactory( tv -> {
			TableRow<Note> row = new TableRow<>();

			row.setOnMouseClicked( evm -> {
				// Double-click on empty row triggers adding a new Note.
				if( evm.getClickCount() == 2 && row.isEmpty() ) {
						row.setEditable( true );
						Note cdn = new Note();
						cellDataObservable.add( cdn );
						row.requestFocus();
						// report updated list length.
						updateListLength_CB.update( cellDataObservable.size() );
				}
				evm.consume();
			});
			return row;
		});

		tableView.getColumns().add( timeCol );
		tableView.getColumns().add( noteCol );

		tableView.setItems( cellDataObservable );

		/*
		 * A selected set of rows can be deleted with the DEL-/DELETE-key.
		 */
		tableView.setOnKeyPressed( evk -> {
			switch( evk.getCode() ) {
			case  DELETE:
				// Need COPY of list of items to be removed to avoid iterator clash during removal.
				List<Note> selection = new ArrayList<Note>( tableView.getSelectionModel().getSelectedItems() );
				for( Note n : selection ) {
					if( cellDataObservable.indexOf( n ) > 0 ) {	// do not remove first entry
						cellDataObservable.remove( n );
					}
				}
				selection.clear();
				updateNoteList( notes );
				updateListLength_CB.update( cellDataObservable.size() );
				tableView.getSelectionModel().clearSelection();
				break;

			case ENTER: evk.consume();
				break;
			default:
				break;
			}
		});

		VBox layout = new VBox( 20 );
		layout.getChildren().addAll( tableView );

		this.scene = new Scene( layout );
		this.setScene( this.scene );
	}

	public boolean isAltered() {
		return altered;
	}


	/*
	 * Private methods.
	 */

	private void updateNoteList( List<Note> notes ) {
		notes.clear();		// clear + rebuild List<Note>
		for( Note cn : cellDataObservable ) {
			if( ! Note.DefaultEntry.equals( cn.getNoteText() ) ) {
				notes.add( cn );
			}
		}
		altered = true;
	}

}
