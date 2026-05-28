package cn.zswltech.mithras.dto.margin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @create: 2022-08-16
 **/
@Data
@ApiModel("记录详情-返回体")
public class MarginRecordDetailRSP {
    @ApiModelProperty("信息来源")
    private String dataSource;

    @ApiModelProperty("收款类型")
    private String collectionType;

    @ApiModelProperty("日期")
    private LocalDate collectionDate;

    @ApiModelProperty("金额")
    private Long collectionAmount;

    @ApiModelProperty("核销状态")
    private String writeOffStatus;

    @ApiModelProperty("附言")
    private String postscript;

    @ApiModelProperty("附件id")
    private Long enclosureId;

    @ApiModelProperty("附件名")
    private String enclosureName;

    @ApiModelProperty("我方账户")
    private BankInfo ourBankInfo;

    @ApiModelProperty("对方账户")
    private BankInfo otherBankInfo;

    @ApiModelProperty("抵扣现金流编号")
    private String rentCollectionCode;

    @Data
    public static class BankInfo{
        @ApiModelProperty("账户ID")
        private Long accountId;

        @ApiModelProperty("账户名")
        private String accountName;

        @ApiModelProperty("银行账号")
        private String accountNumber;

        @ApiModelProperty("开户行")
        private String accountBank;
    }
}
