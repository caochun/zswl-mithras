package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author junke
 */
@ApiModel("融租易APP我的项目列表-返回体")
@Data
public class AppProjListRSP {
    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "项目阶段")
    private String stage;
    /**
     * 项目名称
     */
    @ApiModelProperty(value = "项目名称")
    private String projName;

    /**
     * 业务部门id
     */
    @ApiModelProperty(value = "业务部门id")
    private Long bizDeptId;

    @ApiModelProperty(value = "业务部门名称")
    private String bizDeptName;


    /**
     * 客户id
     */
    @ApiModelProperty(value = "客户id")
    private Long clientId;

    @ApiModelProperty(value = "客户名")
    private String clientName;

    /**
     * 去查price
     */
    @ApiModelProperty(value = "申报融资金额")
    private Long declaredAmount;

    /**
     * 项目编号
     */
    @ApiModelProperty(value = "项目编号")
    private String projCode;


    @ApiModelProperty(value = "流程状态")
    private String processStatus;

    @ApiModelProperty(value = "流程状态颜色为蓝色")
    private Boolean isBlueColor;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

}
