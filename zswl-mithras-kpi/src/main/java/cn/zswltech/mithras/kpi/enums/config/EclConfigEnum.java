package cn.zswltech.mithras.kpi.enums.config;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import cn.zswltech.mithras.kpi.bo.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2023/2/13
 * @description 项目提奖比例参数值枚举
 */
@Getter
@AllArgsConstructor
public enum EclConfigEnum implements PullDown {
    RATING_MAPPING("国内评级与穆迪评级映射", EclRatingMappingBO.class),
    BREACH_MAPPING("穆迪评级和违约概率映射", EclBreachMappingBO.class),
    FORWARD_Z("前瞻调整因子Z", EclForwardZBO.class),
    LOSS_LGD("违约损失率LGD", EclLossLgdBO.class),
    SCENARIO_WEIGHT("情景权重", EclScenarioWeightBO.class),
    INNER_BREACH_MAPPING("内部评级和违约概率映射", EclInnerBreachMappingBO.class),
    ;

    private final String display;

    //关联类
    private Class relationClass;

    public static EclConfigEnum ofName(String name) {
        for (EclConfigEnum businessTypeEnum : EclConfigEnum.values()) {
            if (businessTypeEnum.name().equals(name)) {
                return businessTypeEnum;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return this.display;
    }
}
