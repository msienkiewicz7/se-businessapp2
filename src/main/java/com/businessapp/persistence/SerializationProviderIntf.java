package com.businessapp.persistence;

import java.io.IOException;
import java.util.List;

import com.businessapp.model.EntityIntf;
import com.businessapp.persistence.PersistenceProviderIntf.CollectorIntf;


/**
 * Local interface of a SerializationProvider with read/write-methods for
 * Entity collections.
 * 
 * @author Sven Graupner
 *
 */
interface SerializationProviderIntf {

	/**
	 * Serialize a collection of entities into a serial stream.
	 * 
	 * @param list list of entities to be serialized.
	 * @throws IOException IOException thrown in case of IO failure.
	 */
	public void writeSerialStream( List<? extends EntityIntf> list ) throws IOException;


	/**
	 * Deserialize a stream of serialized entities into entity objects.
	 * 
	 * @param collector functional interface called from the persistence provider
	 * 			for each deserialized entity.
	 * @throws IOException IOException thrown in case of IO failure.
	 */
	public void readSerialStream( CollectorIntf collector ) throws IOException;

}
