package cn.zswltech.mithras.service.enums.payment.pubinfo;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bigbear
 * @date 2024/9/10 14:35
 * @description
 */
@Getter
@AllArgsConstructor
public enum InvestigationResultEnum implements PullDown {
    WITHOUT_ABNORMALITY("无重大异常"),
    WITHOUT_SUPPLEMENT("无补充"),
    WITH_SUPPLEMENT("有补充"),
    WITH_EXPLANATION("需解释说明"),
    NO_INVOLVE("不涉及"),
    ;
    private final String display;

    public static InvestigationResultEnum find(String name) {
        for (InvestigationResultEnum value : InvestigationResultEnum.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return display;
    }
}
