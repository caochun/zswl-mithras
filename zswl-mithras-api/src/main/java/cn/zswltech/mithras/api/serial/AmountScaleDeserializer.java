package cn.zswltech.mithras.api.serial;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

import java.io.IOException;
import java.util.Objects;


public class AmountScaleDeserializer extends JsonDeserializer<Long> implements ContextualDeserializer {
    private Long unit;

    public AmountScaleDeserializer() {
    }

    public AmountScaleDeserializer(Long unit) {
        super();
        this.unit = unit;
    }

    @Override
    public Long deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        long value = jsonParser.getValueAsLong();
        if (unit == null) {
            return value;
        } else {
            if (unit > 0) {
                return value * unit;
            } else {
                throw new IllegalArgumentException("unit can only grater than 0");
            }
        }
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty property) throws JsonMappingException {
        //判断类型是否是BigDecimal
        if (Objects.equals(property.getType().getRawClass(), Long.class)) {
            AmountScale annotation = property.getAnnotation(AmountScale.class);
            if (annotation != null) {
                return new AmountScaleDeserializer(annotation.unit());
            }
        }
        return deserializationContext.findContextualValueDeserializer(property.getType(), property);
    }

}
