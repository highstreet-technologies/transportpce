package org.onap.ccsdk.features.sdnr.wt.odlclient.yangtools.mapperextensions;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.onap.ccsdk.features.sdnr.wt.odlclient.yangtools.YangToolsMapper;
import org.onap.ccsdk.features.sdnr.wt.odlclient.yangtools.YangToolsMapperHelper;
import org.opendaylight.yangtools.yang.binding.Identifiable;
import org.opendaylight.yangtools.yang.binding.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YangtoolsMapDeserializer<K extends Identifier<V>, V extends Identifiable<K>>
        extends JsonDeserializer<Map<K, V>> {

    private static final Logger LOG = LoggerFactory.getLogger(YangtoolsMapDeserializer.class);
     private final Class<V> clazz;
    private final ObjectMapper mapper;

    public YangtoolsMapDeserializer(Class<V> clazz){
        this(clazz, new YangToolsMapper());
    }
    public YangtoolsMapDeserializer(Class<V> clazz, ObjectMapper mapper) {
        super();
        this.clazz = clazz;
        this.mapper = mapper;
    }

    @Override
    public Map<K, V> deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        CollectionLikeType type = ctxt.getTypeFactory().constructCollectionType(List.class, clazz);
        //LOG.info("list to map for value {}",p.currentToken());
        List<V> list = mapper.readValue(p, type);
        return YangToolsMapperHelper.toMap(list);
    }

}
