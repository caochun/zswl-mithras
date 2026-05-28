package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author junke
 */
@ApiModel("融租易APP我的客户拜访详情-返回体")
@Data
public class AppClientDetailRSP {
    @ApiModelProperty("客户id")
    private Long id;
    @ApiModelProperty("客户名称")
    private String clientName;
    @ApiModelProperty("客户类型")
    private String clientType;
    @ApiModelProperty("最近跟进人")
    private String latestVisitor;
    @ApiModelProperty("最近拜访时间")
    private LocalDateTime latestCheckInDate;
    @ApiModelProperty("拜访次数")
    private Integer visitCount;
}
