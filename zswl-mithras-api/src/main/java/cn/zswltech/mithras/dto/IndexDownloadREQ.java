package cn.zswltech.mithras.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author yibin
 */
@Data
public class IndexDownloadREQ {
    @NotBlank
    @ApiModelProperty("首页类型")
    private String indexType;
    @NotBlank
    @ApiModelProperty("下载类型；1：当前页下载；2：下载所有")
    private String downloadType;
    @NotBlank
    @ApiModelProperty("原列表接口参数json")
    private String originJson;
}
