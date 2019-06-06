package com.businessapp.logic;


/**
 * Public management interface that allows basic lifecycle operations
 * for system components.
 * 
 * @author Sven Graupner
 *
 */
public interface ManagedComponentIntf {

	/**
	 * Method to initiate the start of the component.
	 */
	public void start();

	/**
	 * Method to initiate shutdown of the component.
	 */
	public void stop();

	/**
	 * Method that returns component name.
	 * @return component name.
	 */
	public String getName();

}
