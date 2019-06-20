package com.businessapp.model.customserializer;

import com.businessapp.model.Article;
import com.businessapp.model.Note;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;


/**
 * Custom-Serialization for Article class. Class is attached to Article class
 * by @JsonSerialize(using = ArticleJSONSerializer.class)
 * \\
 * Source: https://www.baeldung.com/jackson-custom-serialization
 * https://www.baeldung.com/jackson-deserialization
 *
 * @author Sven Graupner
 *
 */
public class ArticleJSONSerializer extends StdSerializer<Article> {
    private static final long serialVersionUID = 1L;

    enum Attr { id, name, short_name, price };

    /**
     * Public constructor.
     */
    public ArticleJSONSerializer() {
        this( null );
    }

    /**
     * Public constructor.
     *
     * @param e entity object to serialize.
     */
    public ArticleJSONSerializer(Class<Article> e ) {
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
    public void serialize( Article entity, JsonGenerator jgen, SerializerProvider provider ) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeStringField( Attr.id.name(), entity.getId() );
        jgen.writeStringField( Attr.name.name(), entity.getName() );
        jgen.writeStringField( Attr.short_name.name(), entity.getShortName() );
        jgen.writeNumberField( Attr.price.name(), entity.getPrice() );

        jgen.writeEndObject();
    }
}