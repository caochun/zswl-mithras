package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @create: 2022-08-17
 **/
@Data
public class CollectionRecordAddREQ {

    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("收款核销id")
    private Long collectionId;

    @NotNull
    @ApiModelProperty("收款类型")
    private String collectionType;

    @NotNull
    @ApiModelProperty("实收日期")
    private LocalDate collectionDate;

    @NotNull
    @ApiModelProperty("实收金额")
    private Long collectionAmount;

    @ApiModelProperty("本金")
    private Long principal;

    @ApiModelProperty("利息")
    private Long interest;

    @ApiModelProperty("罚息")
    private Long penaltyInterest;

    @ApiModelProperty("附言")
    private String postscript;

    @ApiModelProperty("附件id")
    private Long enclosureId;

    @ApiModelProperty("附件名")
    private String enclosureName;

    @ApiModelProperty("我方账户ID")
    private Long ourAccountId;

    @ApiModelProperty("我方账户名")
    private String ourAccountName;

    @ApiModelProperty("我方银行账号")
    private String ourAccountNumber;

    @ApiModelProperty("我方开户行")
    private String ourAccountBank;

}
