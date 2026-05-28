package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junke
 */
@ApiModel("融租易APP我的客户拜访客户下拉框名称详情-返回体")
@Data
public class AppClientFrequentRSP {
    @ApiModelProperty("客户id")
    private Long clientId;
    @ApiModelProperty("客户名称")
    private String companyName;
    @ApiModelProperty("拜访次数")
    private Integer visitCount;
}
