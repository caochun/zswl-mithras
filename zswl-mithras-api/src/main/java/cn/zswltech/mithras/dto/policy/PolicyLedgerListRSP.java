package cn.zswltech.mithras.dto.policy;

import cn.zswltech.mithras.dto.ListBaseRSP;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
@ApiModel("保单台账-列表-返回体")
public class PolicyLedgerListRSP extends ListBaseRSP{


    private String key;
    /**
     * 保单编号
     */
    @ApiModelProperty(value = "保单编号")
    private String policyCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "续保关系" )
    private String renewalRelationship;

    @ApiModelProperty("数据来源")
    private String dataSource;

    @ApiModelProperty(value = "projId")
    private Long projId;

    @ApiModelProperty(value = "项目名称")
    private String projName;

    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "客户id")
    private Long clientId;

    @ApiModelProperty(value = "客户名")
    private String clientName;

    /**
     * 保险起始日
     */
    @ApiModelProperty(value = "保险起始日")
    private LocalDate insuranceStartDate;

    /**
     * 保险到期日
     */
    @ApiModelProperty(value = "保险到期日")
    private LocalDate insuranceEndDate;

    @ApiModelProperty(value = "是否续保 PolicyRenewInsuranceEnum")
    private String renewInsuranceFlag;

    /**
     * 保险公司名称
     */
    @ApiModelProperty(value = "保险公司名称")
    private String insuranceCompany;

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

    @ApiModelProperty("审批状态")
    private String approvalStatus;

    /**
     * 标识信息
     */
    @ApiModelProperty(value = "标识信息")
    private String identificationInformation;

    /**
     * 逾期天数
     **/
    private Long overdueDays;

    /**
     * 续保保单反馈日
     **/
    private LocalDate renewalPolicyFeedbackDate;

    /**
     * PolicyStatusEnum
     **/
    @ApiModelProperty("保单状态")
    private String policyStatus;

    /**
     * 保单层级关系
     */
    @ApiModelProperty(value = "保单等级")
    private Integer level;

    private LocalDateTime createTime;
}
