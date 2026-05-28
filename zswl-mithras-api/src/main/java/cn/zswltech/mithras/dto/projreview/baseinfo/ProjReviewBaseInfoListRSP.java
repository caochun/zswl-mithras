package cn.zswltech.mithras.dto.projreview.baseinfo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Data
@ApiModel("立项基本信息表列表-返回体")
public class ProjReviewBaseInfoListRSP {
    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;
    /**
     * 项目名称
     */
    @ApiModelProperty(value = "项目名称")
    private String projName;

    /**
     * 业务类型。租赁、保理、转租赁
     */
    @ApiModelProperty(value = "业务类型。租赁、保理、转租赁")
    private String bizType;

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

    /**
     * 业务部门id
     */
    @ApiModelProperty(value = "业务部门id")
    private Long bizDeptId;

    @ApiModelProperty(value = "业务部门名称")
    private String bizDeptName;

    /**
     * 项目主办用户id
     */
    @ApiModelProperty(value = "项目主办用户id")
    private Long projSponsorUserId;

    @ApiModelProperty(value = "项目主办用户名称")
    private String projSponsorUserName;

    /**
     * 项目协办方用户id列表
     */
    @ApiModelProperty(value = "项目协办方用户id列表")
    private List<Long> projCosponsorUserIds;
    /**
     * 项目协办方用户id列表
     */
    @ApiModelProperty(value = "项目协办方用户名列表")
    private List<String> projCosponsorUserNames;

    /**
     * 流程状态
     */
    @ApiModelProperty(value = "评审状态")
    private String projReviewStatus;
    /**
     * 流程状态
     */
    @ApiModelProperty(value = "流程状态")
    private String projReviewProcessStatus;

    @ApiModelProperty("项目立项id")
    private Long projEstablishId;

    @ApiModelProperty("集团授信评审id")
    private Long groupCreditReviewId;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

}
