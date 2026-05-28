package cn.zswltech.mithras.dto.client;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * @description 客户工商信息处理意见表
 * @author vico
 * @date 2023-09-11
 */
@Data
@ApiModel("客户工商信息处理意见表新增-请求体")
public class ClientBusinessOpinionAddREQ {

    /**
    * 流程id
    */
    @ApiModelProperty(value = "流程id")
    @NotNull(message = "流程id不能为空")
    private String flowId;

    /**
     * 流程名称
     */
    @ApiModelProperty(value = "流程名称")
    @NotNull(message = "流程名称不能为空")
    private String moduleName;

    /**
    * 处理节点名称
    */
    @ApiModelProperty(value = "处理节点名称")
    @NotNull(message = "节点名称不能为空")
    private String nodeName;

    @ApiModelProperty(value = "合同ID")
    @NotNull(message = "合同ID不能为空")
    private Long contractId;

    /**
    * 处理意见
    */
    @ApiModelProperty(value = "处理意见")
    @NotNull(message = "处理意见不能为空")
    private String opinion;

}
