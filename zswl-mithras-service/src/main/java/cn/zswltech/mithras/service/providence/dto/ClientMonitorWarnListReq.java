package cn.zswltech.mithras.service.providence.dto;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/12/23 15:25
 */
@ApiModel()
@Data
public class ClientMonitorWarnListReq extends PageReq {
    @ApiModelProperty("客户名称")
    private String clientName;
    @ApiModelProperty("标题")
    private String warnTitle;
    @ApiModelProperty("编号")
    private String warnCode;
    @ApiModelProperty("状态")
    private String warnStatus;
    private List<Integer> warnLevels;
}
