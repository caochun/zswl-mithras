package cn.zswltech.mithras.dto.newftp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yangxiong
 * @date 2024/3/25/14:23
 * @description
 */
@Data
public class NewFtpParameterDTO implements Serializable {
    private static final long serialVersionUID = -6561666332910781986L;

    @ApiModelProperty(value = "波动值公式")
    private String fluctuationFormula;

    @ApiModelProperty(value = "映射值")
    private String mappingVal;
}
