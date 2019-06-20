package com.businessapp.model.customserializer;

import com.businessapp.model.Reservation;
import com.businessapp.model.Note;
import com.businessapp.model.customserializer.ReservationJSONSerializer.Attr;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Date;


/**
 * Custom-Deserialization for Reservation class. Class is attached to Reservation class
 * by @JsonDeserialize(using = ReservationJSONDeserializer.class).
 * \\
 * Source: https://www.baeldung.com/jackson-custom-serialization
 * https://www.baeldung.com/jackson-deserialization
 *
 * @author Sven Graupner
 *
 */
public class ReservationJSONDeserializer extends StdDeserializer<Reservation> {
    private static final long serialVersionUID = 1L;

    /**
     * Public constructor.
     */
    public ReservationJSONDeserializer() {
        this( null );
    }

    /**
     * Public constructor.
     *
     * @param clazz class used to deserialize object from Jackson parser.
     */
    public ReservationJSONDeserializer(Class<?> clazz ) {
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
    public Reservation deserialize( JsonParser jp, DeserializationContext ctxt ) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree( jp );
        //int id = (Integer) ((IntNode) node.get("id")).numberValue();
        //String id = node.get( Attr.id.name() ).asText();
        String id = hasStr( node, Attr.id.name(), "-" );
        String cid = hasStr( node, Attr.cid.name(), "-" );
        Long date_as_timestamp = hasDate( node, Attr.date.name(), 0L );

        Reservation r = new Reservation( id, cid, new Date(date_as_timestamp) );

        //deserialize notes.
        JsonNode n1 = node.get( Attr.aids.name() );
        if( n1 != null && n1.isArray() ) {	// n1 is JSON ArrayNode
            if( n1.size() > 0 ) {
                r.getArticleIds().clear();
            }
            for( final JsonNode objNode : n1 ) {
                String aid = objNode.asText();
                r.getArticleIds().add( aid );
            }
        }

        //Reservation.ReservationStatus status = node.has( Attr.status.name() )? ( Reservation.ReservationStatus.valueOf( node.get( Attr.status.name() ).asText() ) ) : Reservation.ReservationStatus.ACTIVE;
        Reservation.ReservationStatus status = Reservation.ReservationStatus.valueOf(
                hasStr( node, Attr.status.name(), Reservation.ReservationStatus.ACTIVE.name() ) );
        r.setStatus( status );
        return r;
    }


    /*
     * Private methods.
     */

    private String hasStr( JsonNode node, String attrName, String defaultValue ) {
        // Test whether attrName is found in JSON to avoid null being returned.
        return node.has( attrName )? node.get( attrName ).asText() : defaultValue;
    }

    private Long hasDate( JsonNode node, String attrName, Long defaultValue ) {
        // Test whether attrName is found in JSON to avoid null being returned.
        return node.has( attrName )? node.get( attrName ).asLong() : defaultValue;
    }

}
