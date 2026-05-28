package cn.zswltech.mithras.dto.basedata;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author dingqi
 * @date 2022/9/16
 * @description
 */
@Data
@ApiModel("保存LPR-请求体")
public class BaseDataLprSaveREQ {
    @ApiModelProperty("id")
    private Long id;

    @NotBlank(message = "LPR报价日不能为空")
    @ApiModelProperty("LPR报价日，格式：yyyy-MM-dd")
    private String lprDate;

    @NotBlank(message = "1年期LPR不能为空")
    @ApiModelProperty("1年期LPR，单位：百分比")
    private String oneYear;

    @NotBlank(message = "5年期LPR不能为空")
    @ApiModelProperty("5年期LPR，单位：百分比")
    private String fiveYear;
}
