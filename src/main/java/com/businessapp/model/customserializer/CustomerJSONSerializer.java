package com.businessapp.model.customserializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.businessapp.model.Customer;

import com.businessapp.model.Note;


/**
 * Custom-Serialization for Customer class. Class is attached to Customer class
 * by @JsonSerialize(using = CustomerJSONSerializer.class)
 * \\
 * Source: https://www.baeldung.com/jackson-custom-serialization
 * https://www.baeldung.com/jackson-deserialization
 *
 * @author Sven Graupner
 *
 */
public class CustomerJSONSerializer extends StdSerializer<Customer> {
    private static final long serialVersionUID = 1L;

    final static String ContactSeperator = "; ";

    enum Attr { id, firstname, name, contacts, notes, status };

    /**
     * Public constructor.
     */
    public CustomerJSONSerializer() {
        this( null );
    }

    /**
     * Public constructor.
     *
     * @param e entity object to serialize.
     */
    public CustomerJSONSerializer( Class<Customer> e ) {
        super( e );
    }


    /**
     * Public method to deserialize an object from a Jackson parser.
     *
     * @param entity object to serialize.
     * @param jgen Jackson JSON generator.
     * @param provider Jackson serializer provider.
     * @exception JsonProcessingException exception thrown for object serialization errors
     * @exception IOException exception thrown for IO errors
     */
    @Override
    public void serialize( Customer entity, JsonGenerator jgen, SerializerProvider provider ) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeStringField( Attr.id.name(), entity.getId() );
        jgen.writeStringField( Attr.firstname.name(), entity.getFirstName() );
        jgen.writeStringField( Attr.name.name(), entity.getName() );

        //serialize contacts.
        StringBuffer sb = new StringBuffer();
        for( String contact : entity.getContacts() ) {
            sb.append( sb.length() > 0? ContactSeperator : "" ).append( contact );
        }
        jgen.writeStringField( Attr.contacts.name(), sb.toString() );
        sb.setLength(0);

        //serialize notes.
        if( entity.getNotes().size() > 0 ) {
            jgen.writeArrayFieldStart( Attr.notes.name() );
            // jgen.writeRaw( "\n" );
            for( final Note note : entity.getNotes() ) {
                jgen.writeRaw( "\n\t" );
                jgen.writeString( note.externalize() );
            }
            jgen.writeEndArray();
        }

        jgen.writeStringField( Attr.status.name(), entity.getStatus().name() );
        //jgen.writeNumberField( "id", entity.id );
        jgen.writeEndObject();
    }
}