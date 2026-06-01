package cn.zswltech.mithras.blackgray.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description 黑灰名单人工出库表
 * @author
 * @date 2023-11-29
 */
@Data
@ApiModel("黑灰名单人工出库表编辑-请求体")
public class BlackGrayManualOutboundModifyREQ {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "黑灰名单ID")
    @NotNull(message = "申请突破信息不能为空")
    private Long blackGrayId;

    /**
     * 记录状态
     */
    @ApiModelProperty(value = "记录状态")
    private String manualOutboundStatus;


    /**
     * 申请原因
     */
    @ApiModelProperty(name = "申请原因")
    private List<BlackGrayManualOutboundAddREQ.Reason> applyReason;


    /**
     * 申请文件keys
     */
    @ApiModelProperty(name = "申请文件keys")
    private List<String> applyFileKeys;

    @ApiModelProperty(value = "出库说明")
    private String message;


}
