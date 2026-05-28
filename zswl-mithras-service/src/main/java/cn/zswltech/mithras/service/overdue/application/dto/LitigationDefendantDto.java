package cn.zswltech.mithras.service.overdue.application.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/31 11:18
 */
@Data
public class LitigationDefendantDto {
    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "被告名称")
    private String name;
    @ApiModelProperty(value = "被告角色")
    private String role;
    @ApiModelProperty(value = "证件类型")
    private String certificateType;
    @ApiModelProperty(value = "证件号码")
    private String certificateNumber;
}
