package cn.zswltech.mithras.dto.third.financial;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * @create: 2022-08-17
 **/
@Data
public class ThirdFinancialWithdrawREQ {

    @ApiModelProperty("来源途径 ExceptionSourceENUM")
    private String source;

    @ApiModelProperty("流水类型 cqBillTypeEnum")
    private String platform;

    @ApiModelProperty("业务主建 用于寻找对应业务信息")
    private String businessKey;

    @Valid
    @ApiModelProperty("直融实际还款计划拆分反核销明细")
    private List<FundDirectRepayActualSplitRecordInfo> directRepaySplitList;

    @Data
    public static class FundDirectRepayActualSplitRecordInfo {
        @NotNull(message = "直融实际还款计划拆反分核销明细id不能为空")
        private Long id;

        @NotNull(message = "直融实际还款计划拆分反核销金额不能为空")
        private Long amount;
    }
}
