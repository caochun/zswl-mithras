package cn.zswltech.mithras.dto.liquiditymanage.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * AccountBalanceListRSP
 *
 * @author chenyifei
 * @since 2024/12/16
 */
@Data
@ApiModel(value = "账户余额明细列表请求参数")
public class AccountBalanceDetailListRSP {

    @ApiModelProperty(value = "列表-以账户和日期为维度")
    private List<AccountBalanceDetail> list;

    @ApiModelProperty(value = "合计-以日期为维度")
    private List<AccountBalanceSum> sum;

    @Data
    @ApiModel(value = "账户余额明细列表")
    public static class AccountBalanceSum{

        @ApiModelProperty(value = "数据时点")
        private LocalDate date;

        @ApiModelProperty(value = "总合计")
        private AccountBalanceDetail allSum;

        @ApiModelProperty(value = "监管户合计")
        private AccountBalanceDetail supervisionSum;

        @ApiModelProperty(value = "非监管户合计")
        private AccountBalanceDetail noSupervisionSum;

    }


    @Data
    @ApiModel(value = "账户余额明细列表")
    public static class AccountBalanceDetail{
        /**
         * Id
         */
        @ApiModelProperty(value = "id")
        private Long id;

        /**
         * 数据时点
         */
        @ApiModelProperty(value = "数据时点")
        private LocalDate date;

        /**
         * 账户基本表id
         */
        @ApiModelProperty(value = "账户基本表id")
        private Long accountId;

        /**
         * 开户银行
         */
        @ApiModelProperty(value = "开户银行")
        private String accountBank;

        /**
         * 银行账号
         */
        @ApiModelProperty(value = "银行账号")
        private String accountNumber;

        /**
         * 账户性质
         */
        @ApiModelProperty(value = "账户性质")
        private String accountType;

        /**
         * 提款(编辑字段)
         */
        @ApiModelProperty(value = "提款")
        private Long drawingsAmount;

        /**
         * 租金回流
         */
        @ApiModelProperty(value = "租金回流")
        private Long rentReflowAmount;

        /**
         * 其他流入(编辑字段)
         */
        @ApiModelProperty(value = "其他流入")
        private Long otherFlowAmount;

        /**
         * 投放
         */
        @ApiModelProperty(value = "投放")
        private Long paymentAmount;

        /**
         * 还本付息
         */
        @ApiModelProperty(value = "还本付息")
        private Long repayAmount;

        /**
         * 还本付息-调整(编辑字段)
         */
        @ApiModelProperty(value = "还本付息-调整(编辑字段)")
        private Long repayEditAmount;

        /**
         * 还本付息-abs
         */
        @ApiModelProperty(value = "还本付息-abs")
        private Long repayAbsAmount;

        /**
         * 还本付息-非abs
         */
        @ApiModelProperty(value = "还本付息-非abs")
        private Long repayNoAbsAmount;

        /**
         * 刚性支出(编辑字段)
         */
        @ApiModelProperty(value = "刚性支出")
        private Long mustExpenseAmount;

        /**
         * 其他支出(编辑字段)
         */
        @ApiModelProperty(value = "其他支出")
        private Long otherExpenseAmount;

        /**
         * 结余-预估
         */
        @ApiModelProperty(value = "结余-预估")
        private Long estimateBalanceAmount;

        /**
         * 结余受限-预估
         */
        @ApiModelProperty(value = "结余受限-预估")
        private Long estimateBalanceLimitAmount;

        /**
         * 结余-实际(编辑字段)
         */
        @ApiModelProperty(value = "结余-实际")
        private Long actualBalanceAmount;

        /**
         * 差额
         */
        @ApiModelProperty(value = "差额")
        private Long diffAmount;

        /**
         * 颜色
         */
        @ApiModelProperty(value = "颜色")
        private String color;
    }
}
