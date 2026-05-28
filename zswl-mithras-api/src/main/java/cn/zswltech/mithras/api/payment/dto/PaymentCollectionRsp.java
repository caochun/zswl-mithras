package cn.zswltech.mithras.api.payment.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class PaymentCollectionRsp {

    @ApiModelProperty(value = "付款id")
    private Long paymentId;

    @ApiModelProperty("首期租金")
    private Long downPayment = 0L;

    @ApiModelProperty("服务费/咨询费")
    private Long consultingFee = 0L;

    @ApiModelProperty(value = "手续费(元)")
    private Long commission = 0L;

    @ApiModelProperty(value = "首期利息(元)")
    private Long firstInstallmentInterest = 0L;

    @ApiModelProperty("保证金")
    private Long earnestMoney = 0L;

    @ApiModelProperty("质保金")
    private Long retentionMoney = 0L;

    @ApiModelProperty("质保金收款方式")
    private Integer warrantyPayWay;

    @ApiModelProperty("厂商质保金退还日期")
    private LocalDate warrantyReturnDate;

}
