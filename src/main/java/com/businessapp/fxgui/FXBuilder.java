package com.businessapp.fxgui;

import com.businessapp.repositories.ReservationRepositoryIntf;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import com.sun.javafx.application.PlatformImpl;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import com.businessapp.fxgui.fxml.AppFXMLController;
import com.businessapp.fxgui.fxml.CalculatorFXMLController;
import com.businessapp.fxgui.fxml.FXMLControllerIntf;
import com.businessapp.fxgui.fxml.TableViewable;
import com.businessapp.fxgui.fxml.TableViewFXMLController;
import com.businessapp.logic.CalculatorIntf;
import com.businessapp.logic.ManagedComponentIntf;
import com.businessapp.logic.LoggerProvider;
import com.businessapp.repositories.ArticleRepositoryIntf;
import com.businessapp.repositories.CustomerRepositoryIntf;
import com.businessapp.repositories.RepositoryBuilder;


/**
 * Public singleton builder for JavaFX Gui.
 *
 * @author Sven Graupner
 *
 */

public class FXBuilder extends Application implements ManagedComponentIntf {
	private static final LoggerProvider log = LoggerProvider.getLogger( FXBuilder.class );
	private static FXBuilder _singletonInstance = null;
	private final static String appTitle = "SE-BusinessApp";

	private enum Idx { i_name /*0*/, i_label /*1*/, i_fxml /*2*/, i_rszTble /*3*/ };
	private final static String _TabsDescr[][] = {
		// i_name=0,					i_label=1,		i_fxml=2,			i_reszble=3
		{ "App",						"App",			"App.fxml",			"0" },
		{ CalculatorIntf.Calculator,	"Calculator",	"Calculator.fxml",	"0" },
		{ RepositoryBuilder.Customer,	"Customers",	"TableView.fxml",	"1" },
		{ RepositoryBuilder.Article,		"Artikel",		"TableView.fxml",	"0" },
		{ RepositoryBuilder.Reservation,		"Reservation",		"TableView.fxml",	"0" },
	};

	private final Stage stage;
	private TabPane tabsPane;
	private int lifecycle;			// 0 initial, 1 started, 2 stopped


	/**
	 * Private constructor.
	 */
	private FXBuilder() {
		this.stage = new Stage();
		this.tabsPane = null;
		this.lifecycle = 0;
	}


	/**
	 * Public access method to singleton instance according to the singleton pattern.
	 * @param args String[] args passed to main( String[] args );
	 * @param exitFXGui join barrier to signal Application about Gui-exit event and Gui thread to leave.
	 * @return reference to FXBuilder singleton instance.
	 */
	public static FXBuilder getInstance( String[] args, CountDownLatch... exitFXGui ) {
		//launch( args );
		// https://stackoverflow.com/questions/11273773/javafx-2-1-toolkit-not-initialized
		PlatformImpl.startup( () -> {} );

		if( _singletonInstance == null ) {
			CountDownLatch waitForFXGuiCreated = new CountDownLatch( 1 );
			Platform.runLater( () -> {
				try {
					// run with new FXGui-Thread
					//( _singletonInstance = new AppBuilder() ).start( new Stage() );
					_singletonInstance = new FXBuilder();

					PlatformImpl.addListener( new PlatformImpl.FinishListener() {

						@Override	// called when window is closed;
						public void idle(boolean implicitExit) {
							shutdown();
						}

						@Override	// called on PlatformImpl.exit();
						public void exitCalled() {
							shutdown();
						}

						private void shutdown() {
							if( exitFXGui.length > 0 ) {
								exitFXGui[ 0 ].countDown();
						    }
							PlatformImpl.removeListener( this );
							_singletonInstance = null;
						}
					});

				} catch( Exception e ) {
					_singletonInstance = null;
					e.printStackTrace();

				}
				// FXGui-instance now created, unblock invoking thread.
				waitForFXGuiCreated.countDown();
			});
			try {
				// Make sure invoking thread does not leave before FXGui-instance has been created.
				waitForFXGuiCreated.await();

			} catch( InterruptedException e ) {
				e.printStackTrace();
			}
		}
		return _singletonInstance;
	}

	@Override
	public void start() {
		if( lifecycle == 0 ) {
			try {
				start( stage );

			} catch( Exception e ) {
				e.printStackTrace();
			}
			log.info( getName() + " started." );
		}
	}

	@Override
	public void stop() {
		if( lifecycle == 1 ) {
			PlatformImpl.runLater( ()-> {
				try {
					// Must execute in FXGui-Thread (IllegaStateException, if not).
					stage.close();
					Platform.exit();
					this.stop();

				} catch( Exception e ) {}
			});
			lifecycle = 2;
			log.info( getName() + " stopped." );
		}
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void start( Stage stage ) throws Exception {
		if( lifecycle == 0 && stage != null ) {

			// Must execute in FXGui-Thread (IllegaStateException).
			PlatformImpl.runLater( ()-> {
				buildFXApp();
				lifecycle = 1;
			});
		}
	}


	/*
	 * Private Methods.
	 */

	private void buildFXApp() {
		stage.setTitle( appTitle );
		//Pane pane = null;

		for( String[] descr : _TabsDescr ) {
			String label = descr[ Idx.i_label.ordinal() ];
			String fxmlRes = descr[ Idx.i_fxml.ordinal() ];
			Scene scene = stage.getScene();
			Parent root = scene==null? null : scene.getRoot();
			Pane pane = null;
			try {
				URL url = AppFXMLController.class.getResource( fxmlRes );
				if( url != null ) {
					FXMLLoader loader = new FXMLLoader( url );
					pane = loader.load();
					final FXMLControllerIntf fxmlController = loader.<FXMLControllerIntf>getController();

					if( tabsPane == null ) {
						root = pane;
						scene = new Scene( pane );
						stage.setScene( scene );

						if( fxmlController instanceof AppFXMLController ) {
							tabsPane = ((AppFXMLController)fxmlController).getTabPane();
							fxmlController.inject( this );
						}

						stage.setMaxWidth( 1200.0 );
						stage.setMinWidth(  520.0 );
						stage.setMinHeight( 420.0 );

					} else {
						Tab tab = new Tab( label );
						String tabId = label;	// Make tab id unique.
						for( Tab t : tabsPane.getTabs() ) {
							if( t.getId().equals( tabId ) ) {
								tabId += "A";
							}
						}
						tab.setId( tabId );
						tabsPane.getTabs().add( tab );

						TableViewFXMLController tvFxmlController = null;
						TableViewable tv = null;
						fxmlController.inject( this, tab );

						switch( descr[ Idx.i_name.ordinal() ] ) {
						//
						// Create and configure TableView for Customer entities.
						case RepositoryBuilder.Customer:
							// 1. Cast fxmlController to TableViewFXMLController.
							tvFxmlController = (TableViewFXMLController)fxmlController;
							// 2. Fetch Customer data repository.
							CustomerRepositoryIntf custRepo = (CustomerRepositoryIntf)RepositoryBuilder.getInstance().getCustomerRepository();
							// 3. Configure FXMLController with "isResizable" feature (resizable column widths).
							tvFxmlController.inject( "1".equals( descr[ Idx.i_rszTble.ordinal() ] ) );
							// 4. Create TableView for Customer and inject FXMLController and Customer repository into TableView.
							tv = TableViewable.createTableView_Customer( tvFxmlController, custRepo );
							// 5. Inject created TableView back into FXMLController.
							tvFxmlController.inject( tv );
							tvFxmlController.start();	// Start FXMLController.
							tv.start();	// Start TableView.
							break;

						case RepositoryBuilder.Article:
							tvFxmlController = (TableViewFXMLController)fxmlController;
							ArticleRepositoryIntf articleRepo = (ArticleRepositoryIntf)RepositoryBuilder.getInstance().getArticleRepository();
							tvFxmlController.inject( "1".equals( descr[ Idx.i_rszTble.ordinal() ] ) ); 	// isResizable feature
							tv = TableViewable.createTableView_Article( tvFxmlController, articleRepo );
							tvFxmlController.inject( tv );
							tvFxmlController.start();
							tv.start();
							break;

						case RepositoryBuilder.Reservation:
							// 1. Cast fxmlController to TableViewFXMLController.
							tvFxmlController = (TableViewFXMLController)fxmlController;

							// 2. Fetch Customer data repository.
							ReservationRepositoryIntf reservationRepo = RepositoryBuilder.getInstance().getReservationRepository();

							// 3. Configure FXMLController with "isResizable" feature (resizable column widths).
							tvFxmlController.inject( "1".equals( descr[ Idx.i_rszTble.ordinal() ] ) );

							// 4. Create TableView for Customer and inject FXMLController and Customer repository into TableView.
							tv = TableViewable.createTableView_Reservation( tvFxmlController, reservationRepo );

							// 5. Inject created TableView back into FXMLController.
							tvFxmlController.inject( tv );
							tvFxmlController.start();	// Start FXMLController.
							tv.start();	// Start TableView.
							break;



						case CalculatorIntf.Calculator:
							CalculatorFXMLController calcFxmlController = (CalculatorFXMLController)fxmlController;
							CalculatorIntf calcLogic = CalculatorIntf.createInstance();
							calcLogic.inject( calcFxmlController );
							calcFxmlController.inject( calcLogic );
							calcLogic.start();
							calcFxmlController.start();
							break;

						}
						// bind size of loaded FXMLRoot node to size of top-level rootPane.
						pane.prefWidthProperty().bind( ((BorderPane) root).widthProperty());
						pane.prefHeightProperty().bind( ((BorderPane) root).heightProperty());

						tab.setContent( pane );
					}
				}

				/*
				 * If fxmlRes.css exists, add .css stylesheet to root node.
				 */
				String cssPath = fxmlRes.replace( ".fxml", ".css" );
				if( ( url = AppFXMLController.class.getResource( cssPath ) ) != null ) {
					root.getStylesheets().add( url.toExternalForm() );
				}

				tabsPane.getSelectionModel().select( 2 );
				stage.show();

			} catch( IOException e ) {
				log.error( "IOException loading resource. " + e.getMessage() , e );
			}
		}
	}

}
