package cn.zswltech.mithras.report.enums.biz;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.common.enums.PullDown;
import cn.zswltech.mithras.service.config.enumscan.PullDownExt;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 抵押物识别号类型
 *
 * @author wangchuanhao
 * @date 2022/10/10 10:36 AM
 */
@AllArgsConstructor
@Getter
@PullDownExt("crMortgageModelType")
public enum MortgageModelTypeEnum implements PullDown {

    FCQ("1", "房产权证号", ListUtil.toList("房产权正号")),
    TDSYQ("2", "土地使用权证号", ListUtil.toList("地使用权证号")),
    JSYDGHXK("3", "建设用地规划许可证号", new ArrayList<>()),
    JTGJFDJH("4", "交通工具识别号", new ArrayList<>()),
    JTGJYYZZ("5", "交通工具运营执照号", new ArrayList<>()),
    JQSBGHXH("6", "机器设备规划型号", new ArrayList<>()),

    ;

    private String value;
    private String display;
    private List<String> aliasList;

    public static Map<String, MortgageModelTypeEnum> displayMap;
    public static Map<String, MortgageModelTypeEnum> aliasMap;

    static {
        displayMap = Stream.of(MortgageModelTypeEnum.values()).collect(Collectors.toMap(MortgageModelTypeEnum::getDisplay, Function.identity(), (k1,k2)->k1));
        aliasMap = new HashMap<>();
        for (MortgageModelTypeEnum modelTypeEnum : MortgageModelTypeEnum.values()) {
            for (String aliasName : modelTypeEnum.getAliasList()) {
                aliasMap.put(aliasName, modelTypeEnum);
            }
        }
    }

    public static String convert(String originCode) {
        String convertCode = Optional.ofNullable(displayMap.get(originCode)).map(MortgageModelTypeEnum::getValue).orElse(null);
        if (convertCode == null) {
            convertCode = Optional.ofNullable(aliasMap.get(originCode)).map(MortgageModelTypeEnum::getValue).orElse(null);
        }
        return convertCode;
    }

    public static MortgageModelTypeEnum getByValue(String value) {
        for (MortgageModelTypeEnum anEnum : MortgageModelTypeEnum.values()) {
            if (anEnum.getValue().equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return display;
    }

    @Override
    public String valueKey() {
        return value;
    }
}
