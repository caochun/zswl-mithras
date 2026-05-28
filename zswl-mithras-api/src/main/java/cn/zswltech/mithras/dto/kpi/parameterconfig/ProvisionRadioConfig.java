package cn.zswltech.mithras.dto.kpi.parameterconfig;

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
@ApiModel("拨备计提比例")
public class ProvisionRadioConfig extends KpiParameterConfigBase {
    @ApiModelProperty("参数值")
    private List<Data> configValue;

    @EqualsAndHashCode(callSuper = true)
    @lombok.Data
    public static class Data extends DataBase {
        @ApiModelProperty("项目类型")
        private String projectType;

        @ApiModelProperty("五级分类类型")
        private String assetClassify;

        @ApiModelProperty("拨备计提比例")
        private String provisionRadio;
    }
}
