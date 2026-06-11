package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author junke
 */
@ApiModel("融租易APP我的客户拜访详情-返回体")
@Data
public class AppClientPdfRSP {
    @ApiModelProperty("转二进制文件")
    private byte[] bytes;
}
