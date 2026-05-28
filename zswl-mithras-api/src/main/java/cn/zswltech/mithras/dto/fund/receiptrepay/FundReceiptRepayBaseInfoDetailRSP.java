package cn.zswltech.mithras.dto.fund.receiptrepay;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.dto.fund.financing.baseinfo.FundFinancingBaseInfoDetailRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/20 11:00
 */
@ApiModel("收付款详情-响应体")
@Data
public class FundReceiptRepayBaseInfoDetailRSP extends ListBaseRSP {
    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "收付款编号")
    private String receiptRepayCode;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "融资机构名称")
    private List<String> financingOrgName;
    @ApiModelProperty(value = "总授信额度")
    private Long totalCreditLimit;
    @ApiModelProperty(value = "剩余授信额度")
    private Long remainingCreditLimit;
    @ApiModelProperty(value = "融资金额")
    private Long financingAmount;
    @ApiModelProperty(value = "利息总额")
    private Long totalInterest;
    @ApiModelProperty(value = "担保详情")
    private List<FundFinancingBaseInfoDetailRSP.GuaranteeInfoRSP> guaranteeDetail;
    @ApiModelProperty(value = "保理手续费")
    private Long factoringFee;
    @ApiModelProperty(value = "开证许可证费")
    private Long licenseFee;
    @ApiModelProperty(value = "保证金金额")
    private Long cashDeposit;
    @ApiModelProperty(value = "其他费用")
    private Long otherFee;
    @ApiModelProperty(value = "资金经理")
    private String fundManager;
    @ApiModelProperty(value = "所属部门")
    private String department;
    @ApiModelProperty(value = "部门负责人")
    private String departmentLeader;
    @ApiModelProperty(value = "分管领导")
    private String chargeLeader;
    @ApiModelProperty(value = "融资编号")
    private String financingCode;
}
