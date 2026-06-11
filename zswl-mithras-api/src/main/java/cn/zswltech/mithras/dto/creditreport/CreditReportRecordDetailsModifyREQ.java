package cn.zswltech.mithras.dto.creditreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 征信报告-信贷记录明细表
 * @author vico
 * @date 2025-12-01
 */
@Data
@ApiModel("征信报告-信贷记录明细表编辑-请求体")
public class CreditReportRecordDetailsModifyREQ {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
    * 查询编号
    */
    @ApiModelProperty(value = "查询编号")
    private Long creditCode;

    /**
    * 征信报告基本表id
    */
    @ApiModelProperty(value = "征信报告基本表id")
    private Long creditReportId;

    /**
    * 账户编号
    */
    @ApiModelProperty(value = "账户编号")
    private String accountNumber;

    /**
    * 债权机构
    */
    @ApiModelProperty(value = "债权机构")
    private String creditorInstitution;

    /**
    * 业务种类
    */
    @ApiModelProperty(value = "业务种类")
    private String businessType;

    /**
    * 开立日期
    */
    @ApiModelProperty(value = "开立日期")
    private String openingDate;

    /**
    * 到期日
    */
    @ApiModelProperty(value = "到期日")
    private String expirationDate;

    /**
    * 币种
    */
    @ApiModelProperty(value = "币种")
    private String currency;

    /**
    * 借款金额
    */
    @ApiModelProperty(value = "借款金额")
    private String loanAmount;

    /**
    * 发放形式
    */
    @ApiModelProperty(value = "发放形式")
    private String distributionMethod;

    /**
    * 担保方式
    */
    @ApiModelProperty(value = "担保方式")
    private String guaranteeMethod;

    /**
    * 余额
    */
    @ApiModelProperty(value = "余额")
    private String balance;

    /**
    * 五级分类
    */
    @ApiModelProperty(value = "五级分类")
    private String fiveClassification;

    /**
    * 逾期总额
    */
    @ApiModelProperty(value = "逾期总额")
    private String totalOverdueAmount;

    /**
    * 逾期本金
    */
    @ApiModelProperty(value = "逾期本金")
    private String overduePrincipal;

    /**
    * 逾期月数
    */
    @ApiModelProperty(value = "逾期月数")
    private Integer overdueMonth;

    /**
    * 最近一次还款日期
    */
    @ApiModelProperty(value = "最近一次还款日期")
    private String lastRepaymentDate;

    /**
    * 最近一次还款总额
    */
    @ApiModelProperty(value = "最近一次还款总额")
    private String lastRepaymentAmount;

    /**
    * 最近一次还款形式
    */
    @ApiModelProperty(value = "最近一次还款形式")
    private String lastRepaymentType;

    /**
    * 特定交易提示
    */
    @ApiModelProperty(value = "特定交易提示")
    private String specificTransactionPrompts;

    /**
    * 授信协议编号
    */
    @ApiModelProperty(value = "授信协议编号")
    private String creditAgreementNumber;

    /**
    * 信息报告日期
    */
    @ApiModelProperty(value = "信息报告日期")
    private String informationReportDate;

    /**
    * 逻辑删除，0-未删除，1-已删除
    */
    @ApiModelProperty(value = "逻辑删除，0-未删除，1-已删除")
    private Integer deleted;

}
