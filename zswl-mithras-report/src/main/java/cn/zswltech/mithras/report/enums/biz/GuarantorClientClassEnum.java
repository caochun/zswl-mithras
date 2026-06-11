package cn.zswltech.mithras.report.enums.biz;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import cn.zswltech.mithras.foundation.metadata.PullDownExt;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 保证表 客户类型
 *
 * @author wangchuanhao
 * @date 2022/10/10 10:30 AM
 */
@AllArgsConstructor
@Getter
@PullDownExt("crGuarantorClientClass")
public enum GuarantorClientClassEnum implements PullDown {

    JOINT_LESSEE("1", "共同借款人"),
    GUARANTOR("2", "保证人"),

    ;

    private String value;
    private String display;

    public static GuarantorClientClassEnum getByValue(String value) {
        for (GuarantorClientClassEnum item : values()) {
            if (item.value.equals(value)) {
                return item;
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
