package com.businessapp.repositories;

import java.util.List;

import com.businessapp.model.Customer;


/**
 * Local in-memory repository implementation using GenericMemRepositoryImpl<E>.
 * @author Sven Graupner
 *
 */
class CustomerRepositoryImpl extends GenericMemRepositoryImpl<Customer> implements CustomerRepositoryIntf {

	CustomerRepositoryImpl( List<Customer> list ) {
		super( list );
	}


	/**
	 * Create a new entity that is *not* yet managed in the repository.
	 * A new entity is added to the repository using the update() method.
	 *
	 * @return new entity instance that is not yet managed in the repository.
	 */
	@Override
	public Customer create() {
		return new Customer( "vorname", "name" );
	}

}
