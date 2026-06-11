package cn.zswltech.mithras.dto.client;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 客户工商信息处理意见表
 * @author vico
 * @date 2023-09-11
 */
@Data
@ApiModel("客户工商信息处理意见表编辑-请求体")
public class ClientBusinessOpinionModifyREQ {

    /**
    * 主键
    */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
    * 流程id
    */
    @ApiModelProperty(value = "流程id")
    private String flowId;

    /**
    * 处理节点名称
    */
    @ApiModelProperty(value = "处理节点名称")
    private String nodeName;

    /**
    * 处理意见
    */
    @ApiModelProperty(value = "处理意见")
    private String opinion;

}
