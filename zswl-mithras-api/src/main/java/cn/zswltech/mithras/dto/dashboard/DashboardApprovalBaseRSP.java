package cn.zswltech.mithras.dto.dashboard;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DashboardApprovalBaseRSP {

    @ApiModelProperty(value = "合同id")
    private Long id;

    @ApiModelProperty(value = "流程id")
    private String flowId;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty(value = "项目编号")
    private String projCode;

    @ApiModelProperty("合同编号")
    private String contractCode;

    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty(value = "项目主办用户id")
    private Long projSponsorUserId;

    @ApiModelProperty(value = "项目主办用户名称")
    private String projSponsorUserName;

    @ApiModelProperty("业务部门id")
    private Long bizDeptId;

    @ApiModelProperty("业务部门名称")
    private String bizDeptName;

    /**
     * 风控行业分类
     */
    @ApiModelProperty(value = "风控行业分类")
    private String riskControlIndustryClassify;
    @ApiModelProperty(value = "业务组类别 业务分类 BusinessGroupEnum")
    private String businessGroup;

    @ApiModelProperty(value = "业务模式 二级模式 BusinessGroupEnum")
    private String businessModel;

    @ApiModelProperty("申请时间")
    private LocalDateTime applyTime;

    /**
     * 流程结束时间
     */
    @ApiModelProperty("流程结束时间（未结束则没有该字段）")
    private LocalDateTime endTime;

    @ApiModelProperty("租赁类型。直租、回租、经营性租赁")
    private String leaseType;


    @TableField("保理类型 FactoringType")
    private String factoringType;


    @TableField("转让类型 ZrType")
    private String zrType;



}
