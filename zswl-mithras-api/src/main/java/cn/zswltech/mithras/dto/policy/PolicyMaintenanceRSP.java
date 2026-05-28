package cn.zswltech.mithras.dto.policy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @create: 2023-06-18
 **/

@Data
@ApiModel("待维护保单项目列表-返回体")
public class PolicyMaintenanceRSP {

    @ApiModelProperty(value = "保单id")
    private Long id;

    private Long contractId;

    @ApiModelProperty("数据来源")
    private String dataSource;

    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty("剩余未还本金")
    private Long remainingUnpaidPrincipal;

    @ApiModelProperty("合同到期日")
    private LocalDate contractExpirationDate;

    private LocalDate actualFinishDate;

    @ApiModelProperty(value = "客户id")
    private Long clientId;

    @ApiModelProperty(value = "客户名")
    private String clientName;

    @ApiModelProperty(value = "项目名称")
    private String projName;

    @ApiModelProperty(value = "projId")
    private Long projId;

    @ApiModelProperty(value = "推送保单id")
    private Long policyId;

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
     * 保单编号
     */
    @ApiModelProperty(value = "保单编号")
    private String policyCode;

    /**
     * 保险公司名称
     */
    @ApiModelProperty(value = "保险公司名称")
    private String insuranceCompany;

    /**
     * 保单种类
     * PolicyTypeEnum
     */
    @ApiModelProperty("保险种类")
    private String policyType;

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

    @ApiModelProperty("付款编号")
    private String paymentCode;

    @ApiModelProperty("付款id")
    private String paymentId;

    @ApiModelProperty("是否自动推送，0 手动 1 自动")
    private Integer automatic;

    private Integer noticeFlag;

    /**
     * 逾期天数
     **/
    private Long overdueDays;

    /**
     * 保单金额
     **/
    private Long policyAmount;

    /**
     * 标识信息
     */
    @ApiModelProperty(value = "标识信息")
    private String identificationInformation;

}
