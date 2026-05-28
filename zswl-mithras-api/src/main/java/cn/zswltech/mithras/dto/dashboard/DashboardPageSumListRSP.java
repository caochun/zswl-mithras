package cn.zswltech.mithras.dto.dashboard;

import cn.zswltech.mithras.api.common.PageR;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author lizhao
 * @date 2024/7/02
 * @description
 */
@Data
public class DashboardPageSumListRSP {
    @ApiModelProperty("所有客户分页列表")
    private PageR<DashboardClientOverviewAllRSP> allPageList;

    @ApiModelProperty("合计")
    private DashboardPageSumListRSP.SumData sumData;

    @Data
    public static class SumData{
        @ApiModelProperty("授信金额")
        private BigDecimal creditAmount;
        @ApiModelProperty("剩余本金")
        private BigDecimal principalBalanceAmount;
        @ApiModelProperty("存量风险敞口")
        private BigDecimal stockRiskExposure;

    }
}
