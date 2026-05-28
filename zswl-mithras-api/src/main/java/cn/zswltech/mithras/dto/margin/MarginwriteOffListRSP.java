package cn.zswltech.mithras.dto.margin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @create: 2022-08-16
 **/
@Data
@ApiModel("核销记录")
public class MarginwriteOffListRSP {
    @ApiModelProperty("操作")
    private String operate;

    @ApiModelProperty("保证金金额")
    private Long marginAmount;

    @ApiModelProperty("操作人")
    private String createBy;

    @ApiModelProperty("操作时间")
    private LocalDateTime createTime;

    @ApiModelProperty("被操作明细")
    private String operateDetails;
}
