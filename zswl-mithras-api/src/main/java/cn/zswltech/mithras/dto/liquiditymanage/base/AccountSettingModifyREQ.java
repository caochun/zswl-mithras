package cn.zswltech.mithras.dto.liquiditymanage.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * AccountSettingListREQ
 *
 * @author chenyifei
 * @since 2024/12/16
 */
@Data
@ApiModel(value = "回款账户配置编辑请求体")
public class AccountSettingModifyREQ {

    @ApiModelProperty("返回体")
    private List<AccountSettingDetail> list;

    @Data
    @ApiModel
    public static class AccountSettingDetail{
        /**
         * Id
         */
        @ApiModelProperty("id")
        @NotNull(message = "不得为空")
        private Long id;

        /**
         * 账户基本表id
         */
        @ApiModelProperty("账户基本表id")
        @NotNull(message = "账户id不得为空")
        private Long accountId;

        /**
         * 开户银行
         */
        @ApiModelProperty("开户银行")
        private String accountBank;

        /**
         * 银行账号
         */
        @ApiModelProperty("银行账号")
        private String accountNumber;

        /**
         * 是否模拟结清
         */
        @ApiModelProperty("是否模拟结清")
        private Boolean simulateSettle;

        /**
         * 模拟结清日期
         */
        @ApiModelProperty("模拟结清日期")
        private LocalDate settleTime;

        /**
         * 模拟结清金额
         */
        @ApiModelProperty("模拟结清金额")
        private Long settleAmount;
    }

}
