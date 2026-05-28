package cn.zswltech.mithras.dto.liquiditymanage.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


/**
 * 账户余额明细列表请求参数
 *
 * @author chenyifei
 * @since 2024/12/17
 */
@Data
@ApiModel(value = "账户余额明细编辑请求参数")
public class AccountBalanceDetailModifyREQ {

    /**
     * list
     */
    @ApiModelProperty(value = "返回体")
    private List<AccountBalanceDetailModify> list;

    @Data
    public static class AccountBalanceDetailModify{
        /**
         * Id
         */
        @ApiModelProperty(value = "id")
        private Long id;

        /**
         * 提款(编辑字段)
         */
        @ApiModelProperty(value = "提款")
        private Long drawingsAmount;

        /**
         * 其他流入(编辑字段)
         */
        @ApiModelProperty(value = "其他流入")
        private Long otherFlowAmount;

        /**
         * 投放(编辑字段)
         */
        @ApiModelProperty(value = "投放")
        private Long paymentAmount;

        /**
         * 还本付息-调整(编辑字段)
         */
        @ApiModelProperty(value = "还本付息-调整(编辑字段)")
        private Long repayEditAmount;

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
         * 结余受限-预估
         */
        @ApiModelProperty(value = "结余受限-预估")
        private Long estimateBalanceLimitAmount;

        /**
         * 结余-实际(编辑字段)
         */
        @ApiModelProperty(value = "结余-实际")
        private Long actualBalanceAmount;
    }


}
