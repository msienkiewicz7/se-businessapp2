package com.businessapp.persistence;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.businessapp.logic.LoggerProvider;
import com.businessapp.model.EntityIntf;
import com.businessapp.persistence.PersistenceProviderIntf.CollectorIntf;


/**
 * Local class that implements an entity serialization provider using Java-Serialization.
 * With Java-Serialization, the full object tree is serialized and fully be reconstructed.
 * 
 * @author Sven Graupner
 *
 */
class JavaSerializationPersistenceProvider implements SerializationProviderIntf {
	private static final LoggerProvider log = LoggerProvider.getLogger( JavaSerializationPersistenceProvider.class );

	private final String dirPath;		// directory path of serialized file
	private final String fileName;		// name of serialized file


	/**
	 * Constructor.
	 * @param path dir/file path to serialization file.
	 */
	JavaSerializationPersistenceProvider( String path ) {
		path = path.replace( '\\', '/' );
		int i2 = path.lastIndexOf( "/" ) + 1;
		this.dirPath = path.substring( 0, i2 );
		this.fileName = path.substring( i2, path.length() );
	}


	/**
	 * Serialize list of entities into serial stream using Java-Serialization. The full object
	 * tree is serialized.
	 * 
	 * @param list list of entities to serialize.
	 * @exception throws IOException.
	 */
	@Override
	public void writeSerialStream( List<? extends EntityIntf> list ) throws IOException {
		if( list != null ) {
			File dir = new File( dirPath );
			if( ! dir.exists() ) {
				dir.mkdirs();	// create dataPath, if not present
				log.info( dirPath + " created." );
			}
			final String fullFilename = dirPath + fileName;
	
			final Path destination = new File( fullFilename ).toPath();
			ObjectOutputStream out = null;
			try {
				out = new ObjectOutputStream( Files.newOutputStream( destination ) );
			    out.writeObject( list );
	
			} catch( IOException ex ) {
				ex.printStackTrace();
	
			} finally {
				if( out != null ) {
					try {
						out.close();
					} catch( IOException ex2 ) {
						ex2.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Deserialize stream into entities using Java-Serialization for object reincarnation.
	 * When an entity could have been deserialized, collector is called to pass entity to
	 * invoking method.
	 * 
	 * @param collector functional interface to invoke caller passing a deserialized entity.
	 * @exception throws IOException.
	 */
	@Override
	public void readSerialStream( CollectorIntf collector ) throws IOException {
		if( collector != null ) {
			final String fullFilename = dirPath + fileName;

			final Path source = new File( fullFilename ).toPath();
			final ObjectInputStream in = new ObjectInputStream( Files.newInputStream( source ) );
			try {
				Serializable deserialized = (Serializable)in.readObject();
				if( deserialized instanceof List<?> ) {
					List<?> objL = (List<?>)deserialized; 
					for( Object obj : objL ) {
						EntityIntf e = (EntityIntf)obj;
						collector.collect( e );
					}
				}

			} catch( ClassNotFoundException ex ) {
				ex.printStackTrace();

			} finally {
				in.close();
			}

			log.info( "loaded (" + dirPath + fileName + ")." );
		}
	}

}
