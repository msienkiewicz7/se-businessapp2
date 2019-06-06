package com.businessapp.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * Class Note represents a short text line to store hints or comments
 * entered into the system attached to an Entity class such as Customer
 * or Article.
 * 
 * A Note consists of a timeStamp, a separator (comma) and noteText.
 * Example: "2018-04-02 10:16:24:868, This is a short note."
 * 
 * @author Sven Graupner
 */
public class Note implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String DefaultEntry = "New.";

	public static final SimpleDateFormat df
		// "2018-04-02 10:16:24:868"
		= new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS" );

	public static final SimpleDateFormat df_simple
		// "02-Apr-2018, 10:16"
		= new SimpleDateFormat( "dd-MMM-yyyy, HH:mm" );

	private static final String FieldSeparator = ";; ";
	private static long lastTimeStamp = 0L;

	static {
		// static initialization code to initialize time with proper time zone.
		df.setTimeZone( TimeZone.getTimeZone( "GMT+01" ) );
		df_simple.setTimeZone( df.getTimeZone() );
	}

	/*
	 * Properties.
	 */
	private Date timeStamp = null;			// timeStamp part of Note.

	private String noteText = null;			// text part of Note.


	/**
	 * Public default constructor (required by JSON deserialization).
	 */
	public Note() {
		this( DefaultEntry );
	}

	/**
	 * Public constructor creating Note from externalized String.
	 * Example noteStr: "2018-04-02 10:16:24:868, This is a text entry."
	 * @param noteStr
	 */
	public Note( String noteStr ) {
		Object[] parts = parselogStr( noteStr );
		this.timeStamp = parts[0]==null? null : (Date)parts[0];
		this.noteText = (String)parts[1];
		if( this.timeStamp == null ) {
			this.timeStamp = nextUniqueTimeStamp();
		}
	}


	/*
	 * Public getter/setter methods.
	 */

	/**
	 * Get timeStamp part of Note rendered as String.
	 * @return timeStamp part of Note rendered as String.
	 */
	public String getSimpleTimestamp() {
		return df_simple.format( timeStamp );
	}

	/**
	 * Get text part of Note.
	 * @return text part of Note.
	 */
	public String getNoteText() {
		return noteText;
	}

	/**
	 * Set text part of Note.
	 * @param noteText text part of Note.
	 */
	public void setNoteText( String noteText ) {
		this.noteText = noteText;
	}

	/**
	 * Externalize Note as String. Example: "2018-03-15 20:10:27.730, Customer 1234 created".
	 * @return Note as externalized String.
	 */
	public String externalize() {
		return df.format( timeStamp ) + FieldSeparator + noteText;
	}


	/*
	 * Private methods.
	 */

	/**
	 * Helper function to split an externalized "timestamp;noteStr"-string into
	 * its parts that are separated by Separator.
	 * @param noteStr input Note as externalized String.
	 * @return Array of parts with types of Note properties.
	 */
	private Object[] parselogStr( String noteStr ) {
		Object[] res = new Object[] { null, null };
		String[] spl = noteStr.split( FieldSeparator, 2 );	// return max 2 splits, allows ',' to be used in logLine
		if( spl.length > 1 ) {
			// two parts, try to parse date
			try {
				res[0] = df.parse( spl[ 0 ] );
				res[1] = spl[ 1 ];

			} catch( ParseException e ) {
			}
		}
		if( res[1]==null ) {
			res[1] = noteStr;
		}
		return res;
	}

	/**
	 * Helper function to generate a unique timeStamp that differs at least by 1 msec
	 * from a prior call to nextUniqueTimeStamp().
	 * @return unique timeStamp as Date object.
	 */
	private Date nextUniqueTimeStamp() {
		Date now = new Date();
		long nowL = now.getTime();
		if( nowL <= lastTimeStamp ) {
			now = new Date( ++lastTimeStamp );
		} else {
			lastTimeStamp = nowL;
		}
		return now;
	}

}
