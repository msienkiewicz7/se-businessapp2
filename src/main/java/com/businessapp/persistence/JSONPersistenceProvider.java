package com.businessapp.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.businessapp.logic.LoggerProvider;
import com.businessapp.model.EntityIntf;
import com.businessapp.persistence.PersistenceProviderIntf.CollectorIntf;


/**
 * Local class that implements a JSON-serialization persistence provider.
 *
 * @author Sven Graupner
 *
 */
class JSONPersistenceProvider implements SerializationProviderIntf {
    private static final LoggerProvider log = LoggerProvider.getLogger( JSONPersistenceProvider.class );

    private final Class<? extends EntityIntf> clazz;
    private final String dirPath;
    private final String fileName;

    private final ObjectMapper mapper;


    /**
     * Constructor.
     *
     * @param path path to JSON file.
     * @param clazz entity class needed for de-serialization.
     */
    JSONPersistenceProvider( String path, Class<? extends EntityIntf> clazz ) {
        this.clazz = clazz;
        path = path.replace( '\\', '/' );
        int i2 = path.lastIndexOf( "/" ) + 1;
        this.dirPath = path.substring( 0, i2 );
        this.fileName = path.substring( i2, path.length() ) + ".json";
        this.mapper = new ObjectMapper();
        // "dd.MM.yyyy HH:mm", "dd.MM.yyyy", "HH:mm:ss", "yyyy-MM-dd HH:mm a z"
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm a z" );
        mapper.setDateFormat( dateFormat );
        //
        // https://www.baeldung.com/jackson-custom-serialization
        // https://www.baeldung.com/jackson-deserialization
        // - SimpleModule module = new SimpleModule();
        // - module.addSerializer( Customer.class, new CustomerJSONSerializer());
        // - mapper.registerModule(module);
    }


    /**
     * Serialize list of entities into serial JSON stream. The full object
     * tree is serialized.
     *
     * @param list list of entities to serialize.
     * @exception throws IOException.
     */
    @Override
    public void writeSerialStream( List<? extends EntityIntf> list ) throws IOException {
        File dir = new File( dirPath );
        if( ! dir.exists() ) {
            dir.mkdirs();	// create dataPath, if not present
            log.info( dirPath + " created." );
        }
        final String fullFilename = dirPath + fileName;
        File file = new File( fullFilename );
        log.info( "saved (" + fullFilename + ")." );
        mapper.writerWithDefaultPrettyPrinter().writeValue( file, list );
    }


    /**
     * Deserialize JSON-stream into entities. The collector interface is called
     * when an entity was deserialized passing the entity to the invoking method.
     *
     * @param collector functional interface to invoke caller passing a deserialized entity.
     * @exception throws IOException.
     */
    @Override
    public void readSerialStream( CollectorIntf collector ) throws IOException {
        final String fullFilename = dirPath + fileName;
        try {
            File file = new File( fullFilename );

            JsonNode n = mapper.readTree( file );

            for( JsonNode en : n ) {
                EntityIntf e3 = mapper.treeToValue( en , clazz );
                collector.collect(e3 );
            }
            log.info( "loaded (" + fullFilename + ")." );

        } catch( JsonParseException e ) {
            log.error( "JsonParseException: " + e.getMessage(), e );
            throw new IOException( e.getMessage() );

        } catch( FileNotFoundException e ) {
            // ignore, will create empty repository
            log.info( "FileNotFoundException: " + fullFilename );

        } catch( Exception e ) {
            log.error( "JSON deserialization: ClassNotFoundException: " + e.getMessage() , e );
            throw new IOException( e.getMessage() );
        }
    }

}