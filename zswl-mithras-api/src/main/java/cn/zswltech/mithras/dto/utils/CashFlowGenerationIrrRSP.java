package cn.zswltech.mithras.dto.utils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yibin
 */
@Data
public class CashFlowGenerationIrrRSP {
    @ApiModelProperty("irr值；直接返回字符串显示")
    private String irr;
    @ApiModelProperty("预览文件名称")
    private Long fileId;
}
