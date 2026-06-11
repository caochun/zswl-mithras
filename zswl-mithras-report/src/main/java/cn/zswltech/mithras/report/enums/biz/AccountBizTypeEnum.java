package cn.zswltech.mithras.report.enums.biz;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import cn.zswltech.mithras.foundation.metadata.PullDownExt;
import cn.zswltech.mithras.projectprocess.enums.projestablish.FactoringType;
import com.alibaba.fastjson.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 账户表业务类型
 *
 * @author wangchuanhao
 * @date 2022/10/10 10:30 AM
 */
@AllArgsConstructor
@Getter
@PullDownExt("crAccountBizType")
public enum AccountBizTypeEnum implements PullDown {

    RZZL("14", "融资租赁"),
    BLRZ("13", "保理融资"),
    DK("41", "垫款"),

    ;

    private String value;
    private String display;

    /**
     *
     * // @param projectBizType 租赁、保理、转租赁
     * @param subType 保理类型 有追明保理、有追暗保理、无追明保理
     * @return
     */
    public static String convertBL(String subType) {
        if (StringUtils.isBlank(subType) || "[]".equals(subType)) {
            return null;
        }
        if (subType.startsWith("[")) {
            subType = JSONArray.parseArray(subType, String.class).get(0);
        }
        if (FactoringType.yzmbl.name().equals(subType) || FactoringType.yzabl.name().equals(subType)) {
            return BLRZ.value;
        } else if (FactoringType.wzmbl.name().equals(subType)) {
            return DK.value;
        }
        return null;
    }

    public static AccountBizTypeEnum getByValue(String value) {
        for (AccountBizTypeEnum bizTypeEnum : values()) {
            if (bizTypeEnum.value.equals(value)) {
                return bizTypeEnum;
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
