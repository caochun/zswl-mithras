package cn.zswltech.mithras.dto.liquiditymanage.fundTransfer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * AccountBalanceListRSP
 *
 * @author chenyifei
 * @since 2024/12/16
 */
@Data
@ApiModel(value = "监管户待转资金明细列表请求参数")
public class FundTransferGraphRSP {


    @ApiModelProperty(value = "合计-以日期为维度")
    private List<AccountBalanceSum> sum;

    @Data
    @ApiModel(value = "账户余额明细列表")
    public static class AccountBalanceSum{

        @ApiModelProperty(value = "数据时点")
        private LocalDate date;

        @ApiModelProperty(value = "总合计")
        private AccountBalanceDetail allSum;

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
         * 合计值
         */
        @ApiModelProperty(value = "合计值-仅合计列表回返回")
        private Long sum;

        /**
         * 待转余额
         */
        @ApiModelProperty(value = "待转余额")
        private Long pendingBalanceAmount;

    }
}
