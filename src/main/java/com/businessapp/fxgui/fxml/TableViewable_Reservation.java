package com.businessapp.fxgui.fxml;

import com.businessapp.logic.LoggerProvider;
import com.businessapp.model.Reservation;
import com.businessapp.repositories.ReservationRepositoryIntf;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TableViewable_Reservation extends TableViewable {
    private static final LoggerProvider log = LoggerProvider.getLogger( TableViewable_Reservation.class );

    public final static String ReservationSeparator = "; ";

    // Reservation object properties.
    private final ReservationRepositoryIntf repository;
    private final Reservation entity;

    // CustomerRepository properties.
    private final ObservableList<TableViewable> tvItemsList;
    private final TableViewFXMLController tvFxmlController;

    /*
     * TableView description information.
     */
    private static final String tabLabel = "Reservierungen";
    private static final String cssPrefix = "tableview-reservation";


    // Enumerate Customer columns in Tableview.
    private enum Col { id, cid ,status, date, aids};

    private static final String[][] _colDescr = {
            // i_col=0,			i_label=1,	i_css=2,				i_visble=3, i_edble=4
            { Col.id.name(),	"Res.-Nr.",cssPrefix + "-column-id",		"1", "0" },
            { Col.cid.name(),"Kund.-Nr.",	cssPrefix + "-column-cid",	"1", "1" },
            { Col.status.name(),"Status",	cssPrefix + "-column-status",	"1", "1" },
            { Col.date.name(),"Ausleihdatum",	cssPrefix + "-column-date",	"1", "1" },
            { Col.aids.name(),"Artikeln",	cssPrefix + "-column-aids",	"1", "1" },
    };



    public TableViewable_Reservation(TableViewFXMLController tvFxmlController, ReservationRepositoryIntf repository) {
        this.repository = repository;
        this.entity = null;		// null indicates ReservationRepositoryDAO instance.
        this.tvItemsList = FXCollections.observableArrayList();
        this.tvFxmlController = tvFxmlController;
    }

    public TableViewable_Reservation(TableViewFXMLController tvFxmlController, ReservationRepositoryIntf repository, ObservableList<TableViewable> tvItemsList, Reservation entity) {
        this.repository = repository;
        this.entity = entity;
        this.tvItemsList = tvItemsList;
        this.tvFxmlController = tvFxmlController;
    }

    @Override
    Object[][] getColDescr() {
        return _colDescr;
    }

    @Override
    public TableViewable createInstance() {
        Reservation entity = repository.create();
        TableViewable tvEntity = new TableViewable_Reservation( tvFxmlController, repository, tvItemsList, entity);

        return tvEntity;
    }

    @Override
    public String getId() {
        return entity.getId();
    }

    @Override
    public void importFormData(HashMap<String, String> formData) {
        if( formData != null ) {

            for( String key : formData.keySet() ) {
                String val = formData.get( key );
                //System.out.println( "==> updated(" + cx.getId() + "): " + key + ", v: " + val );

                switch( Col.valueOf( key ) ) {
                    //case id:	//id's can't be updated.
                    case cid:
                        entity.setCustomerId( val );
                        break;

                    case status:
                        String st = val.toLowerCase();
                        entity.setStatus( st.startsWith( "act" )? Reservation.ReservationStatus.ACTIVE :
                                st.startsWith( "sus" )? Reservation.ReservationStatus.SUSPENDED :
                                        st.startsWith("term")? Reservation.ReservationStatus.TERMINATED : Reservation.ReservationStatus.NOT_CONFIRMED
                        );
                        break;

                    case date:
                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.GERMANY);
                        try {
                            entity.setDate(format.parse(val));
                        } catch (ParseException e) {
                            log.error(e.getMessage(), e);
//                            entity.setDate(new Date());
                        }
                        break;


                    case aids:
                        entity.getArticleIds().clear();
                        // comma-separated list of contacts
                        String[] csv = val.split( ReservationSeparator.trim() );
                        for( String aid : csv ) {
                            entity.getArticleIds().add( aid.trim() );
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
    public void delete(List<String> ids) {
        repository.delete( ids );
        reload();
    }

    @Override
    public ObservableList<TableViewable> getData() {
        return tvItemsList;
    }

    @Override
    public String getTabLabel() {
        return tabLabel;
    }

    @Override
    public String getCellValueAsString(int col) {
        String ret = "-";
        switch( Col.valueOf( getColName( col ) ) ) {
            case id:
                return entity.getId();
            case cid:
//                log.info("getCellValue: " + entity.getCustomerId());
                return entity.getCustomerId();
            case status:
                Reservation.ReservationStatus st = entity.getStatus();
                switch( st ) {
                    case SUSPENDED: ret = "SUSP"; break;
                    case TERMINATED: ret = "TERM"; break;
                    case NOT_CONFIRMED: ret = "NOT_COMF"; break;
                    default: ret = st.name();
                }
                return ret;
            case date:
                SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                return sdformat.format(entity.getDate());

            case aids:
                StringBuffer sb = new StringBuffer();
                for( String article : entity.getArticleIds() ) {
                    sb.append( sb.length() > 0? ReservationSeparator : "" ).append( article );
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
    public String getCellValue(int col) {
        return getCellValueAsString(col);
    }

    @Override
    public Callback<TableColumn<TableViewable, String>, TableCell<TableViewable, String>> getCellFactory(int col) {
        return null;
    }

    /**
     * Method to initiate the start of the component.
     */
    @Override
    public void start() {
        reload();
    }

    private void reload() {
        tvItemsList.clear();
        for( Reservation entity : repository.findAll() ) {
            TableViewable_Reservation tvc = new TableViewable_Reservation( tvFxmlController, repository, tvItemsList, entity );
            tvItemsList.add( tvc );
        }
        tvFxmlController.reloadData();
    }

    /**
     * Method to initiate shutdown of the component.
     */
    @Override
    public void stop() {

    }

    /**
     * Method that returns component name.
     *
     * @return component name.
     */
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
