package cn.zswltech.mithras.dto.margin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @create: 2022-08-16
 **/
@Data
@ApiModel("记录新增-请求体")
public class MarginRecordAddREQ {
    @ApiModelProperty("保证金id")
    private Long marginId;

    @ApiModelProperty("收款类型")
    private String collectionType;

    @ApiModelProperty("实收日期")
    private LocalDate collectionDate;

    @ApiModelProperty("实收金额")
    private Long collectionAmount;

    @ApiModelProperty("附言")
    private String postscript;

    @ApiModelProperty("我方账户ID")
    private Long ourAccountId;

    @ApiModelProperty("我方账户名")
    private String ourAccountName;

    @ApiModelProperty("我方银行账号")
    private String ourAccountNumber;

    @ApiModelProperty("我方开户行")
    private String ourAccountBank;


}
