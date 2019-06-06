package com.businessapp.model;

import java.io.Serializable;


/**
 * Public entity interface. The interface is used for anonymous references to
 * objects that represent entities in the system. Entity classes typically
 * represent classes of business objects such as Customer or Article and will
 * later be flagged with the @Entity annotation.
 * 
 * @author Sven Graupner
 *
 */
public interface EntityIntf extends Serializable {

	/**
	 * Return unique identifier of entity instance.
	 * @return entity unique identifier.
	 */
	public String getId();

}
