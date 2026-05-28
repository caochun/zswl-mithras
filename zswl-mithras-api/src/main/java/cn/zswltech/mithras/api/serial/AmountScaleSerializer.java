package cn.zswltech.mithras.api.serial;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import java.io.IOException;
import java.util.Objects;

/**
 * @author luyi
 */
public class AmountScaleSerializer extends JsonSerializer<Long> implements ContextualSerializer {
    private Long unit;

    public AmountScaleSerializer() {
    }

    public AmountScaleSerializer(Long unit) {
        this.unit = unit;
    }

    @Override
    public void serialize(Long value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (value == null) {
            jsonGenerator.writeNull();
        } else {
            if (unit == null) {
                jsonGenerator.writeNumber(value);
            } else {
                if (unit > 0) {
                    jsonGenerator.writeNumber(value / unit);
                } else {
                    throw new IllegalArgumentException("unit can only grater than 0");
                }
            }
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        //判断beanProperty是不是空
        if (property == null) {
            return prov.findNullValueSerializer(property);
        }
        //判断类型是否是BigDecimal
        if (Objects.equals(property.getType().getRawClass(), Long.class)) {
            AmountScale annotation = property.getAnnotation(AmountScale.class);
            if (annotation != null) {
                return new AmountScaleSerializer(annotation.unit());
            }
        }
        return prov.findValueSerializer(property.getType(), property);
    }
}
