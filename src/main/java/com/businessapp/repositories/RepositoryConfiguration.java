package com.businessapp.repositories;

import java.util.List;

import com.businessapp.persistence.PersistenceProviderFactory.PersistenceSelector;


/**
 * Local class of repository configuration information. This includes
 * the name of the repository, a selector of a PersistenceProvider and
 * a static method to initialize an empty repository with mock data.
 * 
 * @author Sven Graupner
 *
 */
@FunctionalInterface
interface BuildFixtureStaticMethodIntf {
	void build( @SuppressWarnings("rawtypes") List list );
}

class RepositoryConfiguration {
	private final String name;
	private final PersistenceSelector selector;
	private final BuildFixtureStaticMethodIntf buildFixture;

	/**
	 * Repository configuration constructor.
	 * @param name name of the repository configuration
	 * @param selector selector for persistence provider
	 * @param buildFixture static method that initializes a repository with generated values
	 */
	RepositoryConfiguration( String name, PersistenceSelector selector, BuildFixtureStaticMethodIntf buildFixture ) {
		this.name = name;
		this.selector = selector;
		this.buildFixture = buildFixture;
	}

	/**
	 * Getter for name.
	 * @return name of the repository configuration
	 */
	String getName() {
		return name;
	}

	/**
	 * Getter for persistence selector.
	 * @return persistence selector
	 */
	PersistenceSelector getSelector() {
		return selector;
	}

	/**
	 * Getter for static method that initializes a repository with generated values.
	 * @param repository repository instance into which generated values are inserted
	 */
	void buildFixture( RepositoryIntf<?> repository ) {
		buildFixture.build( repository.findAll() );
	}

}
