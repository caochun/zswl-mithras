package cn.zswltech.mithras.dto.liquidityrisk;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @create: 2023-05-15
 **/

@Data
public class CapitalFlowSettingReq {

    @ApiModelProperty("期初现金流余额")
    private Long beginCashflowAmount;

    @ApiModelProperty("其他收入")
    private Long otherIncome;

    @ApiModelProperty("其他支出")
    private Long otherExpenses;

    @ApiModelProperty("融资明细")
    private List<Detail> inDetail;

    @ApiModelProperty("项目投放明细")
    private List<Detail> outDetail;

    @Data
    public static class Detail{
        @ApiModelProperty("金额")
        private Long amount;

        @ApiModelProperty("日期")
        private LocalDate date;

        @ApiModelProperty("备注")
        private String remark;
    }

}
