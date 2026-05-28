package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @description 绩效-拨备表
 * @author vico
 * @date 2023-06-19
 */
@Data
@ApiModel("绩效-拨备表列表导出-请求体")
public class KpiProvisionBaseInfoDetailExportREQ {

    @NotBlank
    @ApiModelProperty("下载类型；1：当前页下载；2：下载所有")
    private String downloadType;

    @NotBlank
    @ApiModelProperty("原列表接口参数json")
    private String originJson;

}
