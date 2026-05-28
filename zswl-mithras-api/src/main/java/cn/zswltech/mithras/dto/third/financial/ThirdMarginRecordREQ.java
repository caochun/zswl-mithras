package cn.zswltech.mithras.dto.third.financial;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @ClassName ThirdMarginRecordREQ
 * @Description
 * @Author jackerhe
 * @Date 2022/10/18 2:31 下午
 * @Version 1.0
 **/
@Data
public class ThirdMarginRecordREQ extends ThirdBaseREQ {

    @ApiModelProperty("信息来源")
    private String dataSource;

    @NotNull(message = "退款类型不能为空")
    @ApiModelProperty("保证金处理类型，REFUND_MARGIN(保证金退款), REFUND_MARGIN_DEDUCT(保证金抵扣)")
    private String collectionType;

    @ApiModelProperty("收款编号")
    @NotNull(message = "收款编号不能为空")
    private String collectionCode;

    @ApiModelProperty("租金收款编号")
    private String rentCollectionCode;

    @NotNull(message = "实退日期不能为空")
    @ApiModelProperty("实退日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate collectionDate;

    @NotNull(message = "实退金额不能为空")
    @ApiModelProperty("实退金额")
    private BigDecimal collectionAmount;

    @ApiModelProperty("我方账户名")
    private String ourAccountName;
    @ApiModelProperty("我方银行账号")
    private String ourAccountNumber;
    @ApiModelProperty("我方开户行")
    private String ourAccountBank;

    @ApiModelProperty("对方账户名")
    private String oppositeAccountName;
    @ApiModelProperty("收款人银行账号")
    private String oppositeAccountNumber;
    @ApiModelProperty("收款人开户行")
    private String oppositeAccountBank;

    @ApiModelProperty("保证金退抵流程通过标记，true的时候  对应的退款比推苍穹，不被未归0限制")
    private boolean processPassFlag = false;

    private String bankDetailNo;
    private Long financeFlowId;

    private String pknumber;

    private String collectionId;

}
