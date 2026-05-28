package cn.zswltech.mithras.dto.kpi.parameterconfig;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/2/13
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("项目提奖比例-基础提奖比例")
public class ProjectRadioConfig extends KpiParameterConfigBase {

    @ApiModelProperty("参数配置id")
    private Long parameterBaseId;

    @ApiModelProperty("基础提奖比例参数值")
    private List<Data> configValue;

    @EqualsAndHashCode(callSuper = true)
    @lombok.Data
    public static class Data extends DataBase {
        @ApiModelProperty("项目类型")
        private String projectType;

        @ApiModelProperty("项目来源")
        private String projectSource;

        @ApiModelProperty("项目提奖比例")
        private String projectRadio;
    }

    public String getConfig(String projClassify, LocalDate paymentDate, LocalDate calculationDate) {
        if (ObjectUtil.isEmpty(this.configValue) || ObjectUtil.isEmpty(paymentDate) || ObjectUtil.isEmpty(calculationDate)) {
            return null;
        }
        String projSource = ((calculationDate.getYear() - paymentDate.getYear()) * 12 + calculationDate.getMonthValue() - paymentDate.getMonthValue() + 1) > 13 ? "HISTORY" : "NEW";
        for (Data e : this.configValue) {
            if (ObjectUtil.equals(e.getProjectSource(), projSource) && ObjectUtil.equals(e.getProjectType(), projClassify)) {
                return e.getProjectRadio();
            }
        }
        return null;
    }
}
