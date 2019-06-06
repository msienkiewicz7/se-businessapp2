package com.businessapp.persistence;

import java.io.IOException;
import java.util.List;

import com.businessapp.model.EntityIntf;


/**
 * Local class of a serialization persistence provider that implements the
 * entity-based persistence provider CRUD interface.
 * 
 * @author Sven Graupner
 *
 */
class SerializationPersistenceProvider implements PersistenceProviderIntf {

	private final SerializationProviderIntf provider;
	private List<? extends EntityIntf> entityList;


	SerializationPersistenceProvider( SerializationProviderIntf provider ) {
		this.provider = provider;
	}


	/**
	 * Begin a transaction.
	 * @param entityList list of entities affected by the transaction.
	 */
	@Override
	public void prepare( List<? extends EntityIntf> entityList ) {
		this.entityList = entityList;
	}


	/**
	 * Commit transaction.
	 */
	@Override
	public void commit() {
		this.entityList = null;
	}


	/**
	 * C - Create new entity in underlying persistence provider.
	 * @param entity entity to be created.
	 */
	@Override
	public void create( EntityIntf entity ) {
		save();
	}


	/**
	 * R - Read operations from a persistence provider are initiated by
	 * invoking read methods. Entities collected from the persistence
	 * provider are delivered by invoking the collect( entity );
	 * callback.
	 * 
	 * @param collector functional interface that is called from the
	 * persistence provider for each collected entity.
	 *
	 */
	@Override
	public void read( String selector, CollectorIntf collector ) {
		load( collector );
	}

	@Override
	public void readAll( CollectorIntf collector ) {
		load( collector );
	}


	/**
	 * U - Update operations are used to update entities by the persistence
	 * provider.
	 * 
	 * @param entity entity to be updated by persistence provider,
	 */
	@Override
	public void update( EntityIntf entity ) {
		save();
	}

	@Override
	public void updateAll( List<? extends EntityIntf> entityList ) {
		save( entityList );
	}


	/**
	 * D - Delete operations are used to delete entities by the persistence
	 * provider.
	 * 
	 * @param entity entity to be updated by persistence provider,
	 */
	@Override
	public void delete( EntityIntf entity ) {
		save();
	}

	@Override
	public void deleteAll() {
		save();
	}


	/*
	 * Private methods.
	 */

	private void save() {
		save( entityList );
	}
	private void save( List<? extends EntityIntf> entityList ) {
		if( provider != null ) {
			try {

				provider.writeSerialStream( entityList );

			} catch( IOException e ) {
			}
		}
	}

	private void load( CollectorIntf collector ) {
		if( provider != null ) {
			try {
				provider.readSerialStream( collector );
	
			} catch( IOException e ) {
			}
		}
	}

}
