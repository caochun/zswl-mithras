package cn.zswltech.mithras.dto.third.financial;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @ClassName ThirdPanmentDetail
 * @Description
 * @Author jackerhe
 * @Date 2022/10/17 2:38 下午
 * @Version 1.0
 **/
@Data
public class ThirdPaymentDetailREQ extends ThirdBaseREQ {

    /**
     * 付款方式
     */
    @ApiModelProperty("付款方式")
    private String paymentMethod;

    /**
     * 实付金额
     */
    @ApiModelProperty("实付金额")
    private BigDecimal paidInAmount;

    @ApiModelProperty("流水Id")
    private String pknumber;

    /**
     * 实付日期
     */
    @ApiModelProperty("实付日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paidInDate;

    @ApiModelProperty("我方账户id")
    private Long ourAccountId;
    @ApiModelProperty("我方账户名")
    private String ourAccountName;
    @ApiModelProperty("我方账号")
    private String ourAccountNumber;
    @ApiModelProperty("我方开户行")
    private String ourAccountBank;

    @ApiModelProperty("对方账户名")
    private String oppositeAccountName;
    @ApiModelProperty("收款人银行账号")
    private String oppositeAccountNumber;
    @ApiModelProperty("收款人开户行")
    private String oppositeAccountBank;

    /**
     * 现金流项目
     */
    @ApiModelProperty("现金流项目")
    private String cashFlowItem;

    @ApiModelProperty("付款编号")
    private String collectionCode;

    private Long financeFlowId;
    private String bankDetailNo;

    private boolean reqFromCq = true;
    private boolean reqNeedHandle = true;
    private String infoSource;
    private Long recordId;
    private Long unConfirmedId;
    private String capitalSource;
    private String financingCode;
}
