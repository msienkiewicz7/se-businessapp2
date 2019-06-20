package com.businessapp.logic;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Public provider class to wrap specific logging framework in code base.
 * Wrapper classes allow to change or switch the used framework at one
 * location rather than dispersed across the code base .
 *
 * @author Sven Graupner
 *
 */
public class LoggerProvider implements ManagedComponentIntf {
	private final Class<?> clazz;				// class to which logger instance is associated
	private final								// actual logger used internally
		// java.util.logging.Logger realLogger;		// Java's built-in logging framework
		//org.slf4j.Logger realLogger;				// slf4j logging framework
//		org.apache.log4j.Logger realLogger;		// widely used log4j logging framework
		org.apache.logging.log4j.Logger realLogger;


	/**
	 * Private constructor.
	 * @param clazz class to which logger instance is associated.
	 */
	private LoggerProvider( Class<?> clazz ) {
		this.clazz = clazz;
		this.realLogger =
			//java.util.logging.Logger.getLogger( clazz.getName() );
//			configureJavaUtilLogger( java.util.logging.Logger.getLogger( clazz.getName() ) );
			//org.slf4j.LoggerFactory.getLogger( clazz );
//			org.apache.log4j.Logger.getLogger( clazz );
			org.apache.logging.log4j.LogManager.getLogger();
	}


	/**
	 * Public static access method to obtain logger provider instance
	 * for a class (logger instances are associated with classes).
	 *
	 * @param clazz class to which logger instance is associated.
	 * @return logger provider instance.
	 */
	public static LoggerProvider getLogger( Class<?> clazz ) {
		LoggerProvider logger = new LoggerProvider( clazz );
		return logger;
	}

	/**
	 * Basic lifecycle operations inherited from ManagedComponentIntf.
	 */
	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName() + " for " + clazz.getSimpleName();
	}


	/**
	 * Public log methods for a variety of log levels:
	 * - debug()
	 * - info()
	 * - warn()
	 * - error()
	 * - fatal()
	 *
	 * @param message log message.
	 */
	public void info( String message ) {
		realLogger.info( message );
//		realLogger.log( java.util.logging.Level.INFO, message );
	}

	public void warn( String message ) {
		realLogger.warn( message );
//		realLogger.log( java.util.logging.Level.WARNING, message );
	}

	public void error( String message, Exception e ) {
		realLogger.error( message );
//		realLogger.log( java.util.logging.Level.SEVERE, message );
	}


	/*
	 * Private methods.
	 */

	private java.util.logging.Logger configureJavaUtilLogger( java.util.logging.Logger logger ) {
		logger.setUseParentHandlers( false );

		/*
		 * Source:
		 * https://stackoverflow.com/questions/27825682/flushing-streamhandlers-during-debugging-using-java-util-logging-autoflush
		 */
		final java.util.logging.Formatter formatter = new java.util.logging.SimpleFormatter() {
			@Override
			public String format(java.util.logging.LogRecord record) {
				StringBuffer log = new StringBuffer();
				// log.append("[${record.getLevel()}]");
				log.append( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss:SSS" ).format( new Date( record.getMillis() ) ) );
				//log.append( " [" ).append( record.getLoggerName()).append(( "]" ) );
				log.append( " [" + record.getLevel() + "]" );

				log.append(": ");
				log.append(record.getMessage());
				log.append(System.getProperty( "line.separator" ) );
				return log.toString();
			}
		};

		final java.util.logging.StreamHandler handler = new java.util.logging.StreamHandler( System.out, formatter ) {
			@Override
			public synchronized void publish( final java.util.logging.LogRecord record ) {
				super.publish( record );
				flush();
			}
	    };

		handler.setLevel(java.util.logging.Level.ALL);
		logger.addHandler(handler);
		return logger;
	}

}
