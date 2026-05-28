package cn.zswltech.mithras.dto.client.clientversion;

import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Data
public class ClientVersionListRSP extends CommonVersionListRSP {

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("操作人id")
    private Long operatorId;

    @ApiModelProperty("操作人名称")
    private String operatorName;

    @ApiModelProperty("变更时间")
    private LocalDateTime gmtModify;

}
