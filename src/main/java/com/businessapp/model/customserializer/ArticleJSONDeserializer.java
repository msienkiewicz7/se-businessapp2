package com.businessapp.model.customserializer;

import com.businessapp.model.Article;
import com.businessapp.model.Note;
import com.businessapp.model.customserializer.ArticleJSONSerializer.Attr;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;


/**
 * Custom-Deserialization for Article class. Class is attached to Article class
 * by @JsonDeserialize(using = ArticleJSONDeserializer.class).
 * \\
 * Source: https://www.baeldung.com/jackson-custom-serialization
 * https://www.baeldung.com/jackson-deserialization
 *
 * @author Sven Graupner
 *
 */
public class ArticleJSONDeserializer extends StdDeserializer<Article> {
    private static final long serialVersionUID = 1L;

    /**
     * Public constructor.
     */
    public ArticleJSONDeserializer() {
        this( null );
    }

    /**
     * Public constructor.
     *
     * @param clazz class used to deserialize object from Jackson parser.
     */
    public ArticleJSONDeserializer(Class<?> clazz ) {
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
    public Article deserialize( JsonParser jp, DeserializationContext ctxt ) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree( jp );
        //int id = (Integer) ((IntNode) node.get("id")).numberValue();
        //String id = node.get( Attr.id.name() ).asText();
        String id = hasStr( node, Attr.id.name(), "-" );
        String name = hasStr( node, Attr.name.name(), "-" );
        String short_name = hasStr( node, Attr.short_name.name(), "-");
        Double price = hasNumber( node, Attr.price.name(), 0.0);

        return new Article( id, name , short_name, price );
    }


    /*
     * Private methods.
     */

    private String hasStr( JsonNode node, String attrName, String defaultValue ) {
        // Test whether attrName is found in JSON to avoid null being returned.
        return node.has( attrName )? node.get( attrName ).asText() : defaultValue;
    }

    private Double hasNumber( JsonNode node, String attrName, Double defaultValue ) {
        // Test whether attrName is found in JSON to avoid null being returned.
        return node.has( attrName )? node.get( attrName ).asDouble() : defaultValue;
    }

}
