package cn.zswltech.mithras.report.enums.biz;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import cn.zswltech.mithras.service.config.enumscan.PullDownExt;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 保证表联保标志
 *
 * @author wangchuanhao
 * @date 2022/10/10 4:58 PM
 */
@AllArgsConstructor
@Getter
@PullDownExt("crGuarantorJointFlag")
public enum GuarantorJointFlagEnum implements PullDown {

    flb("0", "非联保"),
    lb("1", "联保"),
    ;

    private final String value;
    private final String display;

    private static Map<String, GuarantorJointFlagEnum> nameMap;

    static {
        nameMap = Stream.of(GuarantorJointFlagEnum.values()).collect(Collectors.toMap(GuarantorJointFlagEnum::name, Function.identity(), (k1, k2)->k1));
    }

    public static String convert(String originCode) {
        if (StringUtils.isBlank(originCode)) {
            return null;
        }
        switch (originCode) {
            case "SINGLE": return flb.value;
            case "MULTIPLE_SEPARATE": return flb.value;
            case "JOINT": return lb.value;
        }
        return null;
    }

    public static GuarantorJointFlagEnum getByValue(String value) {
        for (GuarantorJointFlagEnum guarantorJointFlagEnum : GuarantorJointFlagEnum.values()) {
            if (guarantorJointFlagEnum.value.equals(value)) {
                return guarantorJointFlagEnum;
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
