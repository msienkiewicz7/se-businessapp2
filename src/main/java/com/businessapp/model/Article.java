package com.businessapp.model;

import com.businessapp.logic.IDGenerator;
import com.businessapp.model.customserializer.ArticleJSONDeserializer;
import com.businessapp.model.customserializer.ArticleJSONSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


/**
 * Article is an Entity-class that represents an item for rent or for sale.
 *
 * @author Sven Graupner
 *
 */

@JsonSerialize(using = ArticleJSONSerializer.class)
@JsonDeserialize(using = ArticleJSONDeserializer.class)

public class Article implements EntityIntf {
	private static final long serialVersionUID = 1L;

	/*
	 * Properties.
	 */
	private final String id;	// Unique, non-null Article id.

	private String name;		// Article full name.

	private String short_name;		// Article short name.

	private double price;		// Article price (in cent).

	/**
	 * Private default constructor (required by JSON deserialization).
	 */
	@SuppressWarnings("unused")
	private Article() { this( null, null, null, 0 ); }

	/**
	 * Public constructor.
	 * @param name Article name.
	 */
	public Article(  String name, double price ) {
		this( null, name, name, price );
	}

	public Article(String name, String short_name, double price ) {
		this( null, name, short_name, price );
	}

	/**
	 * Public constructor.
	 * @param id Article id. If null, a new id is generated.
	 * @param name Article name.
	 */
	private static final IDGenerator IDG = new IDGenerator( null, IDGenerator.IDTYPE.NUM, 8 );
	//
	public Article(String id, String name, String short_name, double price ) {
		this.id = id==null? IDG.nextId() : id;
		this.name = name;
		this.short_name = short_name;
		setPrice( price );
	}


	/*
	 * Public getter/setter methods.
	 */

	/**
	 * Return Article id.
	 * @return Article id.
	 */
	public String getId() {		// No setId(). Id's cannot be altered.
		return id;
	}

	/**
	 * Return Article name.
	 * @return Article name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set Article name.
	 * @param name Article name.
	 */
	public void setName( String name ) {
		this.name = name;
	}

	/**
	 * Return Article .
	 * @return Article name.
	 */
	public String getShortName() {
		return short_name;
	}

	/**
	 * Set Article name.
	 * @param short_name Article name.
	 */
	public void setShortName( String short_name ) {
		this.short_name = short_name;
	}

	/**
	 * Return Article price.
	 * @return Article price.
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * Return Article price.
	 * @return Article price as an String.
	 */
	public String getPriceAsString() {
		return String.format( "%.2f EUR", price );
	}

	/**
	 * Set Article price.
	 * @param price Article price.
	 */
	public void setPrice( double price ) {
		this.price = price;
	}

}
