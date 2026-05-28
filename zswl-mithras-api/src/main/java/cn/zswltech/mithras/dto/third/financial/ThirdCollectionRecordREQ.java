package cn.zswltech.mithras.dto.third.financial;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 2022-08-17
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class ThirdCollectionRecordREQ extends ThirdBaseREQ {

    @ApiModelProperty("信息来源")
    private String dataSource;

    @ApiModelProperty("收款编号")
    @NotNull(message = "收款编号不能为空")
    private String collectionCode;

    @NotNull(message = "收款类型不能为空")
    @ApiModelProperty("收款类型")
    private String collectionType;

    @NotNull(message = "实收日期不能为空")
    @ApiModelProperty("实收日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate collectionDate;

    @NotNull(message = "实收金额不能为空")
    @ApiModelProperty("实收金额")
    private BigDecimal collectionAmount;

    /**
     * 现金流项目
     */
    @NotNull(message = "现金流项目不能为空")
    @ApiModelProperty("现金流项目")
    private String cashFlowItem;

    @ApiModelProperty(value = "是否开票 0不开 1开", example = "1")
    private Integer invoiceFlag;

    @ApiModelProperty("本金")
    private BigDecimal principal;

    @ApiModelProperty("利息")
    private BigDecimal interest;

    @ApiModelProperty("罚息")
    private BigDecimal penaltyInterest;

    @ApiModelProperty("我方账户名")
    private String ourAccountName;
    @ApiModelProperty("我方银行账号")
    private String ourAccountNumber;
    @ApiModelProperty("我方开户行")
    private String ourAccountBank;
    @ApiModelProperty("流水ID")
    private String pknumber;
    private Long recordId;

    private Long financeFlowId;
    private String bankDetailNo;

    @ApiModelProperty("抵扣保证金ID")
    private Long deductionMarginBaseId;
    @ApiModelProperty("抵扣保证金code")
    private String deductionMarginBaseCode;

}
