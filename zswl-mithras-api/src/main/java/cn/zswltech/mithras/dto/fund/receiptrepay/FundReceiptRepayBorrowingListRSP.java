package cn.zswltech.mithras.dto.fund.receiptrepay;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 借款流入
 * @date 2023-02-20
 */
@Data
@ApiModel("借款流入列表-返回体")
public class FundReceiptRepayBorrowingListRSP extends ListBaseRSP {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "现金流编号")
    private String cashFlowCode;

    @ApiModelProperty(value = "期项")
    private Integer term;

    @ApiModelProperty(value = "本金")
    private Long principal;

    @ApiModelProperty(value = "核销状态")
    private String writeOffState;

    @ApiModelProperty(value = "实际贷款日期")
    private LocalDate actualLoanDate;

    @ApiModelProperty(value = "备注")
    private String remark;

}
