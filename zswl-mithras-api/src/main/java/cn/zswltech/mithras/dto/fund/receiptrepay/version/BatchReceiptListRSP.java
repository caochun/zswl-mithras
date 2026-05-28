package cn.zswltech.mithras.dto.fund.receiptrepay.version;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 批量界面列表返回值
 *
 * @author wangchuanhao
 * @date 2023/2/18 5:07 PM
 */
@Data
@ApiModel("批量界面列表返回值")
public class BatchReceiptListRSP {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("收付款编号")
    private String receiptRepayCode;

    @ApiModelProperty("融资机构id")
    private List<Long> financingOrgId;

    @ApiModelProperty("融资机构名称")
    private List<String> financingOrgName;

    @ApiModelProperty("融资编号")
    private String financingCode;

    @ApiModelProperty("融资金额(元)")
    private Long financingAmount;

    @ApiModelProperty("累计已还本金(元)")
    private Long paidPrincipal;

    @ApiModelProperty("累计已还利息(元)")
    private Long paidInterest;

    @ApiModelProperty("配套项目名称列表")
    private List<String> projNameList;

    @ApiModelProperty("质押合同编号列表")
    private List<String> pledgeContractCodeList;

    @ApiModelProperty("借款日期")
    private LocalDate borrowingDate;

    @ApiModelProperty("到期日期")
    private LocalDate expirationDate;

    @ApiModelProperty("本月计划还款合计(元)")
    private Long planedRepayAmount;

    @ApiModelProperty("本月计划还款本金(元)")
    private Long planedRepayPrincipal;

    @ApiModelProperty("本月计划还款利息(元)")
    private Long planedRepayInterest;

    @ApiModelProperty("计划还本日")
    private LocalDate planedRepayPrincipleDate;

    @ApiModelProperty("计划还息日")
    private LocalDate planedRepayInterestDate;

}
