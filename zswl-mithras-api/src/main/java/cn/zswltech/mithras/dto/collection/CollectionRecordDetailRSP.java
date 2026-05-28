package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @create: 2022-08-16
 **/
@Data
@ApiModel("记录详情-返回体")
public class CollectionRecordDetailRSP {
    @ApiModelProperty("记录id")
    private Long id;

    @ApiModelProperty("信息来源")
    private String dataSource;

    @ApiModelProperty("收款类型")
    private String collectionType;

    @ApiModelProperty("日期")
    private LocalDate collectionDate;

    @ApiModelProperty("金额")
    private Long collectionAmount;

//    @ApiModelProperty("核销状态")
//    private String writeOffStatus;

    @ApiModelProperty("是否开票")
    private String invoice;

//    @ApiModelProperty("附言")
//    private String postscript;
//
//    @ApiModelProperty("附件id")
//    private Long enclosureId;
//
//    @ApiModelProperty("附件名")
//    private String enclosureName;

    @ApiModelProperty("本金")
    private Long principal;

    @ApiModelProperty("利息")
    private Long interest;

    @ApiModelProperty("罚息")
    private Long penaltyInterest;

    @ApiModelProperty("我方账户")
    private BankInfo ourBankInfo;

    @Data
    public static class BankInfo{
        @ApiModelProperty("我方账户ID")
        private Long ourAccountId;

        @ApiModelProperty("我方账户名")
        private String ourAccountName;

        @ApiModelProperty("我方银行账号")
        private String ourAccountNumber;

        @ApiModelProperty("我方开户行")
        private String ourAccountBank;

    }
}
