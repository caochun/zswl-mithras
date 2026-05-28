package cn.zswltech.mithras.dto.kpi.parameterconfig;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/2/13
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("项目提奖比例-投放奖金系数")
public class ProjectPaymentBonusRadioConfig extends KpiParameterConfigBase {

    @ApiModelProperty("参数配置id")
    private Long parameterBaseId;

    @ApiModelProperty("投放奖金系数参数值")
    private List<PaymentBonusRatio> configValue;

    // 投放奖金系数
    @EqualsAndHashCode(callSuper = true)
    @lombok.Data
    public static class PaymentBonusRatio extends DataBase {
        @ApiModelProperty("项目类型")
        private String projectType;

        @ApiModelProperty("项目提奖比例")
        private String projectRadio;
    }

    public String getConfig(String projClassify) {
        if (ObjectUtil.isEmpty(this.configValue)) {
            return null;
        }
        for (ProjectPaymentBonusRadioConfig.PaymentBonusRatio e : this.configValue) {
            if (ObjectUtil.equals(e.getProjectType(), projClassify)) {
                return e.getProjectRadio();
            }
        }
        return null;
    }

}
