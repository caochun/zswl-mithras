package cn.zswltech.mithras.blackgray.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName 金控业务类型 数据库中black_gray_business_dict有相同编码
 * @Description
 * @Date 2023/11/29 4:56 下午
 * @Version 1.0
 **/
@Getter
@AllArgsConstructor
public enum BlackGrayBusinessTypeEnum implements PullDown {
    INFORMATION_RELATED("信用类"), // 涉信类
    //INSURANCE_CATEGORY("保险类"),
    //OTHER( "通用类"), // 通用类 其他类（20240624修改）
    ;

    private final String desc;

    public static BlackGrayBusinessTypeEnum of(String code) {
        for (BlackGrayBusinessTypeEnum value : BlackGrayBusinessTypeEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static BlackGrayBusinessTypeEnum ofByDesc(String desc) {
        for (BlackGrayBusinessTypeEnum value : BlackGrayBusinessTypeEnum.values()) {
            if (value.getDesc().equals(desc)) {
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
