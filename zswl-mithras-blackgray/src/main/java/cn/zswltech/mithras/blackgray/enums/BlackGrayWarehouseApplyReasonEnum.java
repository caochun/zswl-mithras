package cn.zswltech.mithras.blackgray.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName BlackgrayBusinessENUM
 * @Description
 * @Date 2023/11/29 4:56 下午
 * @Version 1.0
 **/
@Getter
@AllArgsConstructor
public enum BlackGrayWarehouseApplyReasonEnum implements PullDown {
    REASON1("原因1"),
    REASON2("原因2"),
    REASON3("原因3"),
    ;

    private final String desc;

    public static BlackGrayWarehouseApplyReasonEnum of(String code) {
        for (BlackGrayWarehouseApplyReasonEnum value : BlackGrayWarehouseApplyReasonEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static List<DictionaryDTO> toDictionaryDTO(){
        return Arrays.stream(values()).map(t ->
                DictionaryDTO.builder()
                        .value(t.name())
                        .label(t.getDesc())
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public String display() {
        return this.desc;
    }
}
