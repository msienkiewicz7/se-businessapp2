package com.businessapp.persistence;

import com.businessapp.model.EntityIntf;


/**
 * Factory class to create persistence provider instances that can be associated
 * with repositories for simple serialization-based persistence.
 * 
 * @author Sven Graupner
 *
 */
public class PersistenceProviderFactory {

	/**
	 * Selector providing choices for simple serialization-based persistence.
	 */
	public enum PersistenceSelector {
		Default,
		JavaSerialization,
		JSONSerialization
	};


	/**
	 * Public factory method that returns persistence provider.
	 * 
	 * @param providerSelector choice of persistence (Java-/JSON-serialization)
	 * @param path path to persisted data source (file)
	 * @return persistence provider.
	 */
	public static PersistenceProviderIntf getInstance( PersistenceSelector providerSelector, String path, Class<? extends EntityIntf> clazz  ) {
		PersistenceProviderIntf provider = null;
		SerializationProviderIntf serializationProvider = null;

		switch( providerSelector ) {

		case JavaSerialization:
			serializationProvider = new JavaSerializationPersistenceProvider( path );
			break;

		case JSONSerialization:
			//serializationProvider = new JSONPersistenceProvider( path, clazz );
			break;

		default:
		}

		provider = new SerializationPersistenceProvider( serializationProvider );

		return provider;
	}

}
