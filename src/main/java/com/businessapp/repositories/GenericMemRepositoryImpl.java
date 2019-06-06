package com.businessapp.repositories;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.businessapp.logic.LoggerProvider;
import com.businessapp.model.EntityIntf;
import com.businessapp.persistence.PersistenceProviderIntf;


/**
 * Local abstract class that implements the RepositoryIntf<E> interface based
 * on a simple transient (in-memory) List<E> implementation.
 * 
 * @author Sven Graupner
 *
 * @param <E> gneric entity type defined as sub-type of EntityIntf.
 */
abstract class GenericMemRepositoryImpl<E extends EntityIntf> implements RepositoryIntf<E> {
	private static final LoggerProvider log = LoggerProvider.getLogger( GenericMemRepositoryImpl.class );

	private PersistenceProviderIntf persistenceProvider;
	private final List<E> list;


	/**
	 * Constructor.
	 * @param list list<E> that is associated with the repository.
	 */
	GenericMemRepositoryImpl( List<E> list ) {
		this.persistenceProvider = null;
		this.list = list;
	}

	/**
	 * Injection of a persistence provider enables the repository to save its content
	 * to a persistence media and restore from there.
	 * @param persistenceProvider provider of PersistenceProviderIntf
	 */
	@Override
	public void inject( PersistenceProviderIntf persistenceProvider ) {
		this.persistenceProvider = persistenceProvider;
	}

	/**
	 * Basic lifecycle operations inherited from ManagedComponentIntf.
	 */
	@Override
	public void start() {
		log.info( getName() + " started." );
	}

	@Override
	public void stop() {
		list.clear();
		log.info( getName() + " stopped." );
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}


	/**
	 * Abstract method to create a new entity E that is *not* yet managed in the repository.
	 * A new entity is added to the repository using the update() method.
	 * Method is abstract because new instance cannot be created with generic type.
	 * Subclass must implement this method.
	 * 
	 * @return new entity instance that is not yet managed in the repository.
	 */
	@Override
	public abstract E create();


	/**
	 * Read method that returns all entities of the repository.
	 * 
	 * @return all entities of the repository.
	 */
	@Override
	public List<E> findAll() {
		return list;
	}


	/**
	 * Read method that returns the repository entity with matching id or null if
	 * entity is not found.
	 * 
	 * @return entity with matching id.
	 */
	@Override
	public E findById( String id ) {
		Collection<E> collection = list;
		return findById( collection, id );
	}

	private E findById( Collection<E> collection, String id ) {
		for( E e : collection ) {
			if( e.getId().equals( id ) ) {
				return e;
			}
		}
		return null;
	}


	/**
	 * Update method that sets values of entity passed as argument to an entity
	 * found in the repository with same id. If no entity with matching id is found,
	 * the entity passed as argument is inserted into the repository if the insert
	 * flag is set to true. If set to false, no update is performed.
	 * 
	 * @e entity to update values of repository entity with matching id.
	 * @insert if true, entity is inserted if no entity with matching id exists.
	 * @return reference to updated entity.
	 */
	@Override
	public E update( E entity, boolean insert ) {
		E e1 = findById( list, entity.getId() );
		if( e1 != null ) {
			if( e1 != entity ) {
				log.error( "==> duplicate instance update(" + entity.getId() + ").", null );
				entity = e1;
			} else {
				log.info( "==> updated(" + entity.getId() + ")" );
				final E entity2 = entity;
				persist( ta -> {
					ta.prepare( list );
					ta.update( entity2 );
					ta.commit();
				});
			}

		} else {
			if( insert ) {
				log.info( "==> inserted(" + entity.getId() + ")" );
				list.add( entity );
				final E entity2 = entity;
				persist( ta -> {
					ta.prepare( list );
					ta.create( entity2 );
					ta.commit();
				});
			}
		}
		return entity;
	}


	/**
	 * Delete entity with matching id from repository.
	 * 
	 * @id id of entity to be deleted from repository.
	 */
	@Override
	public void delete( String id ) {
		delete( Arrays.asList( id ) );
	}


	/**
	 * Delete all entities passed as argument from repository as one atomic transaction.
	 * 
	 * @ids list of entities to be deleted from repository.
	 */
	@Override
	public void delete( List<String> ids ) {
		persist( ta -> {
			ta.prepare( this.list );
		});
		for( String id : ids ) {
			E entity = findById( list, id );
			if( entity != null ) {
				list.remove( entity );
				persist( ta -> {
					ta.delete( entity );
				});
				log.info( "==> deleted(" + entity.getId() + ")" );
			}
		}
		persist( ta -> {
			ta.commit();
		});
	}


	/**
	 * Delete all entities from repository as one atomic transaction. The result
	 * is an empty repository.
	 */
	
	
	@Override
	public void deleteAll() {
		persist( ta -> {
			ta.prepare( this.list );
			ta.deleteAll();
			list.clear();
			ta.commit();

		}, () -> {
			list.clear();
		});
		log.info( "==> cleared(" + this.getClass().getSimpleName() + ")" );
	}


	/*
	 * Private methods.
	 */

	@FunctionalInterface
	interface TransactionIntf {
		void execute( PersistenceProviderIntf persistenceProvider );
	}

	@FunctionalInterface
	interface SimpleIntf {
		void execute();
	}

	private void persist( TransactionIntf tai ) {
		persist( tai, null );
	}
	private void persist( TransactionIntf tai, SimpleIntf si ) {
		if( persistenceProvider != null ) {
			tai.execute( persistenceProvider );
		} else {
			if( si != null ) {
				si.execute();
			}
		}
	}

}
