package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author dingqi
 * @date 2025/9/3
 * @description
 */
@Data
public class AppraisalCompanyWhitelistAddREQ {
    @NotBlank(message = "统一社会信用代码不能为空")
    @ApiModelProperty(value = "统一社会信用代码")
    private String uscCode;
}
