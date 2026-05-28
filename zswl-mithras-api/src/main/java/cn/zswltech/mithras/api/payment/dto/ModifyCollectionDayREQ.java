package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName ModifyCollectionDayREQ
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/4/2 5:10 下午
 * @Version 1.0
 **/
@Data
public class ModifyCollectionDayREQ {
    @ApiModelProperty(value = "默认收款日")
    private Integer defaultCollectionDay;

    @ApiModelProperty(value = "付款id")
    @NotNull(message = "付款id不能为空")
    private Long paymentId;

    @ApiModelProperty(value = "是否同起租日")
    private Boolean sameStartDate;
}
