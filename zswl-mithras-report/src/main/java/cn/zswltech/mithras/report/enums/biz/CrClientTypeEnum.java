package cn.zswltech.mithras.report.enums.biz;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import cn.zswltech.mithras.service.config.enumscan.PullDownExt;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 征信报送客户类型
 *
 * @author wangchuanhao
 * @date 2022/10/10 10:36 AM
 */
@AllArgsConstructor
@Getter
@PullDownExt("crClientType")
public enum CrClientTypeEnum implements PullDown {

    NORMAL("1", "自然人"),
    CORPORATION("2", "组织机构"),

    ;

    private String value;
    private String display;

    public static String convert(String originCode) {
        if (StringUtils.isBlank(originCode)) {
            return null;
        }
        switch (originCode) {
            case "CORPORATION":
                return CORPORATION.value;
            case "NORMAL":
                return NORMAL.value;
        }
        return null;
    }

    public static CrClientTypeEnum getByValue(String value) {
        for (CrClientTypeEnum crClientTypeEnum : CrClientTypeEnum.values()) {
            if (crClientTypeEnum.value.equals(value)) {
                return crClientTypeEnum;
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
