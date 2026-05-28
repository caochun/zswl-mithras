package cn.zswltech.mithras.dto.utils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yibin
 */
@Data
public class CashFlowGenerationIrrFilePreviewRSP {
    @ApiModelProperty("预览地址")
    private String url;
}
