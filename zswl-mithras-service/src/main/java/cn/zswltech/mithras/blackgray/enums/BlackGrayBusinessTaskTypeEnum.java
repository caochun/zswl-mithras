package cn.zswltech.mithras.blackgray.enums;

import cn.zswltech.decision.engine.api.common.dto.DictionaryDTO;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum BlackGrayBusinessTaskTypeEnum implements PullDown {
    TIMED("定期任务"),
    NOT_TIMED("不定期任务"),
    ;

    private final String desc;

    public static BlackGrayBusinessTaskTypeEnum of(String code) {
        for (BlackGrayBusinessTaskTypeEnum value : BlackGrayBusinessTaskTypeEnum.values()) {
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
