package com.businessapp.model.customserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

import com.businessapp.model.Customer;
import com.businessapp.model.Note;
import com.businessapp.model.customserializer.CustomerJSONSerializer.Attr;


/**
 * Custom-Deserialization for Customer class. Class is attached to Customer class
 * by @JsonDeserialize(using = CustomerJSONDeserializer.class).
 * \\
 * Source: https://www.baeldung.com/jackson-custom-serialization
 * https://www.baeldung.com/jackson-deserialization
 *
 * @author Sven Graupner
 *
 */
public class CustomerJSONDeserializer extends StdDeserializer<Customer> {
    private static final long serialVersionUID = 1L;

    /**
     * Public constructor.
     */
    public CustomerJSONDeserializer() {
        this( null );
    }

    /**
     * Public constructor.
     *
     * @param clazz class used to deserialize object from Jackson parser.
     */
    public CustomerJSONDeserializer( Class<?> clazz ) {
        super( clazz );
    }


    /**
     * Public method to deserialize an object from a Jackson parser.
     *
     * @param jp Jackson parser instance.
     * @param ctxt Jackson deserialization context.
     * @exception JsonProcessingException exception thrown for json parse and object construction errors
     * @exception IOException exception thrown for IO errors
     */
    @Override
    public Customer deserialize( JsonParser jp, DeserializationContext ctxt ) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree( jp );
        //int id = (Integer) ((IntNode) node.get("id")).numberValue();
        //String id = node.get( Attr.id.name() ).asText();
        String id = hasStr( node, Attr.id.name(), "-" );
        String firstname = hasStr( node, Attr.firstname.name(), "-" );
        String name = hasStr( node, Attr.name.name(), "-" );

        Customer c = new Customer( id, firstname, name );

        //deserialize contacts.
        String[] contactsSplitted = hasStr( node, Attr.contacts.name(), "" ).split( CustomerJSONSerializer.ContactSeperator.trim() );
        for( String contact : contactsSplitted ) {
            c.getContacts().add( contact.trim() );
        }

        //deserialize notes.
        JsonNode n1 = node.get( Attr.notes.name() );
        if( n1 != null && n1.isArray() ) {	// n1 is JSON ArrayNode
            if( n1.size() > 0 ) {
                c.getNotes().clear();
            }
            for( final JsonNode objNode : n1 ) {
                Note note = new Note( objNode.asText() );
                c.getNotes().add( note );
            }
        }

        //Customer.CustomerStatus status = node.has( Attr.status.name() )? ( Customer.CustomerStatus.valueOf( node.get( Attr.status.name() ).asText() ) ) : Customer.CustomerStatus.ACTIVE;
        Customer.CustomerStatus status = Customer.CustomerStatus.valueOf(
                hasStr( node, Attr.status.name(), Customer.CustomerStatus.ACTIVE.name() ) );
        c.setStatus( status );
        return c;
    }


    /*
     * Private methods.
     */

    private String hasStr( JsonNode node, String attrName, String defaultValue ) {
        // Test whether attrName is found in JSON to avoid null being returned.
        return node.has( attrName )? node.get( attrName ).asText() : defaultValue;
    }

}