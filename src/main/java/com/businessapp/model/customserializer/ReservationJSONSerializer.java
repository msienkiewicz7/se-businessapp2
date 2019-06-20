package com.businessapp.model.customserializer;

import com.businessapp.model.Article;
import com.businessapp.model.Reservation;
import com.businessapp.model.Note;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;


/**
 * Custom-Serialization for Reservation class. Class is attached to Reservation class
 * by @JsonSerialize(using = ReservationJSONSerializer.class)
 * \\
 * Source: https://www.baeldung.com/jackson-custom-serialization
 * https://www.baeldung.com/jackson-deserialization
 *
 * @author Sven Graupner
 *
 */
public class ReservationJSONSerializer extends StdSerializer<Reservation> {
    private static final long serialVersionUID = 1L;

    enum Attr { id, cid, date, aids, status };

    /**
     * Public constructor.
     */
    public ReservationJSONSerializer() {
        this( null );
    }

    /**
     * Public constructor.
     *
     * @param e entity object to serialize.
     */
    public ReservationJSONSerializer(Class<Reservation> e ) {
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
    public void serialize( Reservation entity, JsonGenerator jgen, SerializerProvider provider ) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeStringField( Attr.id.name(), entity.getId() );
        jgen.writeStringField( Attr.cid.name(), entity.getCustomerId() );
        jgen.writeNumberField( Attr.date.name(), entity.getDateAsTimestamp());

        //serialize articles.
        if( entity.getArticleIds().size() > 0 ) {
            jgen.writeArrayFieldStart( Attr.aids.name() );
            // jgen.writeRaw( "\n" );
            for( final String articleId : entity.getArticleIds() ) {
                jgen.writeRaw( "\n\t" );
                jgen.writeString( articleId );
            }
            jgen.writeEndArray();
        }

        jgen.writeStringField( Attr.status.name(), entity.getStatus().name() );
        //jgen.writeNumberField( "id", entity.id );
        jgen.writeEndObject();
    }
}
