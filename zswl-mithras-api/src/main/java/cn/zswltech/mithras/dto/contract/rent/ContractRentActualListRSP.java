package cn.zswltech.mithras.dto.contract.rent;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/8/15
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("实际租金表-返回体")
public class ContractRentActualListRSP extends ListBaseRSP {
    @ApiModelProperty("主合同id")
    private Long contractId;

    @ApiModelProperty("业务类型")
    private String bizType;

    @ApiModelProperty("合同状态")
    private String contractStatus;

    @ApiModelProperty("合同流程状态")
    private String contractProcessStatus;

    @ApiModelProperty("借据id")
    private Long receiptId;

    @ApiModelProperty("是否是第一个借据")
    private Integer isFirstReceipt;

    @ApiModelProperty("借据是否可删除, true-可删除， false-不可删除")
    private Boolean remove;

    @ApiModelProperty("借据编号")
    private String receiptCode;

    @ApiModelProperty("实际起租日")
    private String actualStartDate;

    @ApiModelProperty("实际irr")
    private Integer actualIrr;

    @ApiModelProperty("实际租金表")
    private List<TableData> rentActualList;

    @EqualsAndHashCode(callSuper = true)
    @lombok.Data
    @ApiModel("实际租金表列表数据-返回体")
    public static class TableData extends ListBaseRSP {
        @ApiModelProperty("现金流编号")
        private String cashFlowCode;

        @ApiModelProperty("日期")
        private String date;

        @ApiModelProperty("期项")
        private Integer phase;

        @ApiModelProperty("租金（毫厘）")
        private Long rent;

        @ApiModelProperty("本金（毫厘）")
        private Long principal;

        @ApiModelProperty("利息（毫厘）")
        private Long interest;

        @ApiModelProperty("剩余本金（毫厘）")
        private Long remainingPrincipal;

        @ApiModelProperty("是否已收款")
        private Boolean received;

        @ApiModelProperty("收款时间")
        private LocalDate receivedDate;

        @ApiModelProperty("收款金额")
        private Long receivedAmount;
    }
}
