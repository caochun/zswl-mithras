package cn.zswltech.mithras.dto.ftp;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

/**
 * @description 季度指导列表返回
 * @author zhaozhengkang
 * @date 2023-01-09
 */
@Data
@ApiModel("季度指导列表-返回体")
public class FtpQuarterlyGuidanceListRsp {
    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "列表'时间'字段")
    private String timeDisplay;
    @ApiModelProperty(value = "创建人Name")
    private String createByName;
    @ApiModelProperty(value = "审批状态")
    private String guidanceProcessStatus;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
}
