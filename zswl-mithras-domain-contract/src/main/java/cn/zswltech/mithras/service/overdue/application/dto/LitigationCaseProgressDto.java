package cn.zswltech.mithras.service.overdue.application.dto;

import cn.zswltech.mithras.service.overdue.domain.litigation.LongId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/31 11:18
 */
@Data
public class LitigationCaseProgressDto {
    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "案件阶段")
    private String stage;
    @ApiModelProperty(value = "案件状态")
    private String status;
    @ApiModelProperty(value = "创建时间")
    private String createTime;
    @ApiModelProperty(value = "创建人")
    private String processPerson;
}
