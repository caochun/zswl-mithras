package cn.zswltech.mithras.blackgray.enums;

import cn.zswltech.decision.engine.api.common.dto.DictionaryDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum BlackGrayOrgEnum {

   /* ZSJK("10000079","浙商金控"),
    ZSBX("10000097","浙商保险"),
    ZSHR("10000099","浙商汇融"),*/
    ZSZL("10000396","浙商租赁"),
   /* ZSDD("10000395","浙商典当"),
    ZSZQ("10000393","浙商证券"),*/
    ;

    private final String code;
    private final String desc;

    public static BlackGrayOrgEnum ofCode(String code) {
        for (BlackGrayOrgEnum value : BlackGrayOrgEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static BlackGrayOrgEnum ofDesc(String desc) {
        for (BlackGrayOrgEnum value : BlackGrayOrgEnum.values()) {
            if (value.getDesc().equals(desc)) {
                return value;
            }
        }
        return null;
    }

    public static List<DictionaryDTO> toDictionaryDTO(){
        return Arrays.stream(values()).map(t ->
                DictionaryDTO.builder()
                        .value(t.getCode())
                        .label(t.getDesc())
                        .build()
        ).collect(Collectors.toList());
    }

}
