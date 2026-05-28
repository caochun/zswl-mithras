package cn.zswltech.mithras.kpi.bo;

import cn.zswltech.mithras.dto.kpi.parameterconfig.ProjectPaymentBonusRadioConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ProjectRadioConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ProjectScaleRadioConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ProjectTypeRadioConfig;
import lombok.Data;

@Data
public class KpiParameterConfigBo {

    private int batchNumber;

    //项目提奖比例-投放奖金系数
    private ProjectPaymentBonusRadioConfig projectPaymentBonusRadioConfig;

    //项目提奖比例-基础提奖比例
    private ProjectRadioConfig projectRadioConfig;

    //项目提奖比例-项目规模系数
    private ProjectScaleRadioConfig projectScaleRadioConfig;

    //项目提奖比例-项目类型系数
    private ProjectTypeRadioConfig projectTypeRadioConfig;

}