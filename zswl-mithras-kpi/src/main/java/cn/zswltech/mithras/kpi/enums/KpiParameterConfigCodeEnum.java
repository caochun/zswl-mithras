package cn.zswltech.mithras.kpi.enums;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ProjectPaymentBonusRadioConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ProjectRadioConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ProjectScaleRadioConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ProjectTypeRadioConfig;
import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/2/6
 * @description
 */
@Getter
@AllArgsConstructor
public enum KpiParameterConfigCodeEnum implements PullDown {
    TAX_RATE("税率维护", null),
    PROVISION_RADIO("拨备计提比例", null),
    EXPENSE_RADIO("费用计提比例", null),
    PROJECT_RADIO("项目提奖-基础提奖比例", ProjectRadioConfig.class),
    PROJECT_RADIO_TYPE("项目提奖-项目类型系数", ProjectTypeRadioConfig.class),
    PROJECT_RADIO_SCALE("项目提奖-项目规模系数", ProjectScaleRadioConfig.class),
    PROJECT_RADIO_PAYMENT("项目提奖-投放奖金系数", ProjectPaymentBonusRadioConfig.class),
    DEPT_PROFIT_FINISH_RADIO("部门利润完成率系数", null),
    CAREER_LEVEL("职等系数", null),
    BUSINESS_DEPT_ASSESS("业务部门综合考评系数", null),
    FINANCIAL_MARKET_DEPT_RADIO("金融市场部提奖比例", null),
    PROFIT_ADJUST("公司利润调节系数", null),
    FINANCIAL_MARKET_DEPT_ASSESS("金融市场部综合考评系数", null),
    MIDDLE_BACK_DEPT_ASSESS("中后台部门综合考评系数", null);

    private final String desc;

    private Class mainMapperClass;

    public static List<KpiParameterConfigCodeEnum> getKpiEnum() {
        return ListUtil.toList(PROJECT_RADIO, PROJECT_RADIO_TYPE, PROJECT_RADIO_SCALE, PROJECT_RADIO_PAYMENT);
    }

    @Override
    public String display() {
        return desc;
    }
}
