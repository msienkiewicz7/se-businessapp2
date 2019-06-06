package com.businessapp;

import java.util.concurrent.CountDownLatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import com.businessapp.fxgui.FXBuilder;
import com.businessapp.logic.LoggerProvider;
import com.businessapp.logic.ManagedComponentIntf;
import com.businessapp.repositories.RepositoryBuilder;


/**
 * Main Application class that is started as Spring Boot Application.
 * Class implements a structured lifecycle for building, starting up,
 * running and shutting down application components.
 * 
 * @author Sven Graupner
 *
 */

@SpringBootApplication
public class Application implements ManagedComponentIntf {
	private static Application _singleton = null;
	private final String[] args;
	private final LoggerProvider log;
	private final CountDownLatch joinBarrier_GuiThread;

	enum Lifecycle { zombie, building, ready, starting, running, shuttingdown };
	private Lifecycle lifecycle;

	private FXBuilder fxBuilder;
	private RepositoryBuilder repositoryBuilder;

	/**
	 * Protected constructor (protected to allow Spring Boot instance creation).
	 * @param args args[] passed from main()
	 */
	Application( String[] args ) {
		_singleton = this;		// needed when Spring Boot creates singleton instance
		this.args = args;
		this.log = LoggerProvider.getLogger( Application.class );
		this.joinBarrier_GuiThread = new CountDownLatch( 1 );
		this.lifecycle = Lifecycle.zombie;
		this.fxBuilder = null;
		this.repositoryBuilder = null;
	}


	public static void main( String[] args ) {
		/*
		 * Starting Spring Boot, which also creates the singleton Application instance.
		 * If started through Spring Boot, singleton instance will receive an
		 * ApplicationReadyEvent caught by the Application lifecycle() method.
		 */
		SpringApplication.run( Application.class, args );

		/*
		 * If Spring Boot has not been initialized, Application singleton instance
		 * needs to be created and Application lifecycle() method invoked.
		 */
		if( _singleton == null ) {
			_singleton = new Application( args );
			_singleton.lifecycle();
		}

	}

	/**
	 * Private method that performs the application lifecycle. Method is called either
	 * by Spring Boot initialization indirectly by receiving the ApplicationReadyEvent
	 * or it is called explicitly in main();
	 */
	@EventListener( ApplicationReadyEvent.class )
	private void lifecycle() {

		build();

		start();

		if( lifecycle == Lifecycle.running ) {
			log.info( getName() + " is now RUNNING." );
			log.info( "------------------------" );
			/*
			 * JavaFX spawns own threads for keeping the Gui responsive. This means
			 * that the Application thread performing the Application lifecycle
			 * operations immediately continues leaving JavaFX.
			 * It needs to be blocked from continuing until the Gui is finished
			 * (e.g. by pressing the EXIT-button). If the Application thread is not
			 * blocked, it continues with invoking stop() immediately shutting down
			 * and exiting the Application.
			 */
			try {
				/*
				 * Block the invoking thread until another thread (from FXGui in this case)
				 * has finished.
				 */
				joinBarrier_GuiThread.await();

			} catch (InterruptedException e) {}
		}

		stop();
	}


	public void build() {
		if( lifecycle == Lifecycle.zombie ) {
			lifecycle = Lifecycle.building;
			log.info( "------------------------" );
			log.info( getName() + " is building." );
			/*
			 * build Application components.
			 */
			repositoryBuilder = RepositoryBuilder.getInstance();
			fxBuilder = FXBuilder.getInstance( args, joinBarrier_GuiThread );

			lifecycle = Lifecycle.ready;
			log.info( getName() + " BUILT." );
		}
	}

	@Override
	public void start() {
		switch( lifecycle ) {
		case ready:
			lifecycle = Lifecycle.starting;
			log.info( getName() + "Starting." );

			/*
			 * start Application components.
			 */
			repositoryBuilder.start();

			fxBuilder.start();

			lifecycle = Lifecycle.running;
			break;

		case zombie:
			build();
			start();
			break;

		default:
		}
	}


	@Override
	public void stop() {
		if( lifecycle == Lifecycle.running ) {
			lifecycle = Lifecycle.shuttingdown;
			log.info( "------------------------" );
			log.info( getName() + " is shutting down." );
			/*
			 * stop Application components.
			 */

			if( fxBuilder != null ) {
				fxBuilder.stop();
			}

			if( repositoryBuilder != null ) {
				repositoryBuilder.stop();
			}
			//SpringApplication.exit( context, exitCodeGenerators );

			lifecycle = Lifecycle.zombie;
			log.info( getName() + " is now shut down." );
			log.info( "------------------------" );
		}
	}


	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

}
