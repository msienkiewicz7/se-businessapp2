package com.businessapp.persistence;

import java.util.List;

import com.businessapp.model.EntityIntf;


/**
 * Public CRUD (create, read, update, delete) interface of an entity-based
 * persistence provider.
 * 
 * @author Sven Graupner
 *
 */
public interface PersistenceProviderIntf {

	/**
	 * Begin a transaction.
	 * @param entityList list of entities affected by the transaction.
	 */
	public void prepare( List<? extends EntityIntf> entityList );

	/**
	 * Commit transaction.
	 */
	public void commit();

	/**
	 * C - Create new entity in underlying persistence provider.
	 * @param entity entity to be created.
	 */
	public void create( EntityIntf entity );

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
	@FunctionalInterface
	interface CollectorIntf {
		void collect( EntityIntf entity );
	}
	public void read( String selector, CollectorIntf collector );
	public void readAll( CollectorIntf collector );


	/**
	 * U - Update operations are used to update entities by the persistence
	 * provider.
	 * 
	 * @param entity entity to be updated by persistence provider,
	 */
	public void update( EntityIntf entity );
	public void updateAll( List<? extends EntityIntf> entityList );


	/**
	 * D - Delete operations are used to delete entities by the persistence
	 * provider.
	 * 
	 * @param entity entity to be updated by persistence provider,
	 */
	public void delete( EntityIntf entity );
	public void deleteAll();

}
