package cn.zswltech.mithras.dto.ftp;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

/**
 * @description 月度指导
 * @author zhaozhengkang
 * @date 2023-01-10
 */
@Data
@ApiModel("月度指导列表-返回体")
public class FtpMonthlyGuidanceListRsp {
    @ApiModelProperty(value = "id")
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
