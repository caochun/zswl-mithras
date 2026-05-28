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
@ApiModel("项目提奖比例-项目类型系数")
public class ProjectTypeRadioConfig extends KpiParameterConfigBase {

    @ApiModelProperty("参数配置id")
    private Long parameterBaseId;

    @ApiModelProperty("项目类型系数参数值")
    private List<ProjTypeRatio> configValue;

    //  项目类型系数
    @EqualsAndHashCode(callSuper = true)
    @lombok.Data
    public static class ProjTypeRatio extends DataBase {
        @ApiModelProperty("项目类型")
        private String projectType;

        @ApiModelProperty("项目提奖比例")
        private String projectRadio;
    }

    public String getConfig(String bizType, String leaseType) {
        for (ProjectTypeRadioConfig.ProjTypeRatio e : this.configValue) {
            if (ObjectUtil.equals(e.getProjectType(), "=回租") || ObjectUtil.equals(e.getProjectType(), "=回租/保理")) {
                if (ObjectUtil.equals(leaseType, "hui_zu") || ObjectUtil.equals(bizType, "BL")) {
                    return e.getProjectRadio();
                }
            } else {
                return e.getProjectRadio();
            }
        }
        return null;
    }

}
