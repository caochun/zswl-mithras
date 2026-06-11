package cn.zswltech.mithras.dto.contract.baseinfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDate;
import java.util.List;

/**
 * @description 合同基本信息表
 * @author vico
 * @date 2022-08-12
 */
@Data
@ApiModel("合同基本信息表列表-返回体")
public class ContractBaseInfoListRSP {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "客户id")
    private Long clientId;

    @ApiModelProperty(value = "客户Name")
    private String clientName;

    /**
    * 合同编号
    */
    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty("咨询合同编号")
    private String consultingContractCode;

    @ApiModelProperty(value = "授信金额")
    private Long applyCreditAmount;

    /**
    * 剩余可用额度(元)
    */
    @ApiModelProperty(value = "剩余可用额度(元)")
    private Long remainAvailableQuota;

    /**
    * 项目名称
    */
    @ApiModelProperty(value = "项目名称")
    private String projName;

    /**
    * 项目编号
    */
    @ApiModelProperty(value = "项目编号")
    private String projCode;

    /**
    * 业务类型。租赁、保理、转租赁
    */
    @ApiModelProperty(value = "业务类型。租赁、保理、转租赁")
    private String bizType;

    /**
    * 租赁类型。直租、回租、经营性租赁
    */
    @ApiModelProperty(value = "租赁类型。直租、回租、经营性租赁")
    private String leaseType;

    @ApiModelProperty(value = "保理类型")
    private String factoringType;

    @ApiModelProperty(value = "转让类型")
    private String zrType;

    /**
     * 转租赁业务专用字段
     */
    @ApiModelProperty(value = "转让方")
    private String assignor;

    /**
    * 项目类型：公共事业类、省内国（央）企、其他
    */
    @ApiModelProperty(value = "项目类型：公共事业类、省内国（央）企、其他")
    private String projectType;

    /**
    * 风险等级
    */
    @ApiModelProperty(value = "风险等级")
    private String riskLevel;

    /**
    * 项目来源：存量翻单、渠道介绍、自主开发
    */
    @ApiModelProperty(value = "项目来源：存量翻单、渠道介绍、自主开发")
    private String projSource;

    /**
    * 资金用途
    */
    @ApiModelProperty(value = "资金用途")
    private String fundsPurpose;

    /**
    * 项目背景
    */
    @ApiModelProperty(value = "项目背景")
    private String projBackground;

    /**
    * 项目主办用户id
    */
    @ApiModelProperty(value = "项目主办用户id")
    private Long projSponsorUserId;

    /**
     * 项目主办用户姓名
     */
    @ApiModelProperty(value = "项目主办用户姓名")
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
    * 业务部门id
    */
    @ApiModelProperty(value = "业务部门id")
    private Long bizDeptId;


    @ApiModelProperty(value = "业务部门名称")
    private String bizDeptName;

    /**
     * 合同状态
     */
    @ApiModelProperty(value = "合同状态")
    private String contractStatus;

    /**
     * 流程状态
     */
    @ApiModelProperty(value = "流程状态")
    private String contractProcessStatus;

    /**
     * 流程状态名称
     */
    @ApiModelProperty(value = "流程状态名称")
    private String contractProcessName;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private String createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private String updateTime;

    /**
    * 关联的评审id
    */
    @ApiModelProperty(value = "关联的评审id")
    private Long projReviewId;

    /**
     *实际起租日
     **/
    @ApiModelProperty(value ="计划付款日期（合同生效时间）")
    @JsonIgnore
    private LocalDate paymentPlanDate;

    @ApiModelProperty(value = "业务流程版本号")
    private String businessVersion;

    @ApiModelProperty(value = "是否签约")
    private Integer isSigned;
}
