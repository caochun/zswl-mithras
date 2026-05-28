package cn.zswltech.mithras.dto.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @description 客户工商信息处理意见表
 * @author vico
 * @date 2023-09-11
 */
@Data
@ApiModel("客户工商信息处理意见表列表-返回体")
public class ClientBusinessOpinionListRSP {

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
     * 流程名称
     */
    @ApiModelProperty(value = "流程名称")
    private String moduleName;

    /**
    * 处理节点名称
    */
    @ApiModelProperty(value = "处理节点名称 FlowNodeEnum")
    private String nodeName;

    /**
    * 处理意见
    */
    @ApiModelProperty(value = "处理意见")
    private String opinion;

    private LocalDateTime createTime;

    private LocalDate createDate;

    @ApiModelProperty(value = "创建人")
    private Long createBy;

    @ApiModelProperty(value = "创建人")
    private String createByName;

}
