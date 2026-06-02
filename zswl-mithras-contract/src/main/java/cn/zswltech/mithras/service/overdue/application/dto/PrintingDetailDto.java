package cn.zswltech.mithras.service.overdue.application.dto;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/4 09:48
 */
@Data
@ApiModel(value = "文书用印详情响应")
public class PrintingDetailDto extends ListBaseRSP {

    @ApiModelProperty(value = "文书用印类型")
    private String type;
    @ApiModelProperty(value = "文书用印原因")
    private String reason;
    @ApiModelProperty(value = "申请人")
    private String applyName;
    @ApiModelProperty(value = "申请部门")
    private String applyDeptName;
}
