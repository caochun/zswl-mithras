package cn.zswltech.mithras.dto.fund.financing.fee;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 直接融资-费用明细
 * @date 2023-06-17
 */
@Data
@ApiModel("直接融资-费用明细编辑-请求体")
public class FundFinancingFeeDetailModifyREQ {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "机构id")
    @NotNull(message = "机构id不能为空")
    private Long organizationId;

    @ApiModelProperty(value = "机构名称")
    @NotNull(message = "机构名称不能为空")
    private String organizationName;

    /**
    * 费用类型
    */
    @ApiModelProperty(value = "费用类型")
    private String expenseType;

    /**
     * 金额（万元）
     */
    @ApiModelProperty(value = "金额（万元）")
    private Long amount;

    /**
    * 支付方式
    */
    @ApiModelProperty(value = "支付方式")
    private String paymentMethod;

    /**
     * 支付时间
     */
    @ApiModelProperty(value = "支付时间")
    private LocalDate payDate;

    /**
    * 备注
    */
    @ApiModelProperty(value = "备注")
    private String remark;

}
