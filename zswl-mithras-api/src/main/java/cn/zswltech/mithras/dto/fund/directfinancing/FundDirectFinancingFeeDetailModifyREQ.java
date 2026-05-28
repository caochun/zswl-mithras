package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 直接融资-费用明细
 * @date 2023-06-17
 */
@Data
@ApiModel("直接融资-费用明细编辑-请求体")
public class FundDirectFinancingFeeDetailModifyREQ {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
    * 中介机构
    */
    @ApiModelProperty(value = "中介机构")
    private String intermediaries;

    /**
    * 机构名称
    */
    @ApiModelProperty(value = "机构名称")
    private String institutionName;

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
    * 备注
    */
    @ApiModelProperty(value = "备注")
    private String remark;


    /**
     * 支付时间
     */
    @ApiModelProperty(value = "支付时间")
    private LocalDate payDate;
}
