/**
 * 
 */
package org.corpus_tools.atomic.tagset;

import java.io.IOException;

import org.corpus_tools.atomic.tagset.impl.JavaTagsetImpl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class TagsetDeserializer extends JsonDeserializer<Tagset> {

	@Override
	public Tagset deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		ObjectMapper mapper = (ObjectMapper) p.getCodec();
        ObjectNode root = mapper.readTree(p);
        return mapper.readValue(root.toString(), JavaTagsetImpl.class);
	}

}
