package cn.zswltech.mithras.dto.kpi.parameterconfig;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/2/13
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("项目提奖比例-项目规模系数")
public class ProjectScaleRadioConfig extends KpiParameterConfigBase {

    @ApiModelProperty("参数配置id")
    private Long parameterBaseId;

    @ApiModelProperty("项目规模系数参数值")
    private List<ProjScaleRatio> configValue;

    //项目规模系数
    @EqualsAndHashCode(callSuper = true)
    @lombok.Data
    public static class ProjScaleRatio extends DataBase {
        @ApiModelProperty("项目规模")
        private String projectScale;

        @ApiModelProperty("项目提奖比例")
        private String projectRadio;
    }

    //
    public String getConfig(BigDecimal applyCreditAmount){
        if (ObjectUtil.isEmpty(this.configValue) || ObjectUtil.isEmpty(applyCreditAmount)) {
            return null;
        }
        applyCreditAmount = applyCreditAmount.divide(new BigDecimal(10000), 10, RoundingMode.HALF_UP);
        for (ProjectScaleRadioConfig.ProjScaleRatio e : this.configValue) {
            if (e.getProjectScale().startsWith(">") || e.getProjectScale().startsWith("＞")) { //＞
                if (getValue(e.getProjectScale()).compareTo(applyCreditAmount) < 0){
                    return e.getProjectRadio();
                }
            } else if (e.getProjectScale().startsWith(">=") || e.getProjectScale().startsWith("≥")) {
                if (getValue(e.getProjectScale()).compareTo(applyCreditAmount) <= 0){
                    return e.getProjectRadio();
                }
            }else if (e.getProjectScale().startsWith("<")) {
                if (getValue(e.getProjectScale()).compareTo(applyCreditAmount) > 0){
                    return e.getProjectRadio();
                }
            }else if (e.getProjectScale().startsWith("<=") || e.getProjectScale().startsWith("≤")) {
                if (getValue(e.getProjectScale()).compareTo(applyCreditAmount) >= 0){
                    return e.getProjectRadio();
                }
            }
        }
        return "1";
    }

    private BigDecimal getValue(String projectScale) {
        String s = projectScale.replaceAll("<", "");
        s = s.replaceAll("<=", "");
        s = s.replaceAll(">", "");
        s = s.replaceAll("＞", "");
        s = s.replaceAll(">=", "");
        s = s.replaceAll("≥", "");
        s = s.replaceAll("≤", "");
        BigDecimal bigDecimal = new BigDecimal(s);
        return bigDecimal;
    }

}
