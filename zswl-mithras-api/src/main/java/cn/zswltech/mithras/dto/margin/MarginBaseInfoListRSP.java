package cn.zswltech.mithras.dto.margin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @create: 2022-08-15
 **/

@Data
@ApiModel("保证金列表-返回体")
public class MarginBaseInfoListRSP {

    @ApiModelProperty("保证金id")
    private Long id;

    @ApiModelProperty("保证金编号")
    private String code;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("合同id")
    private Long contractId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("收款日期")
    private LocalDate collectionDate;

    @ApiModelProperty("保证金金额")
    private Long marginAmount;

    @ApiModelProperty("已退金额")
    private Long backAmount;

    @ApiModelProperty("已抵扣金额")
    private Long deductAmount;

    @ApiModelProperty("可退金额")
    private Long canBackAmount;
}
