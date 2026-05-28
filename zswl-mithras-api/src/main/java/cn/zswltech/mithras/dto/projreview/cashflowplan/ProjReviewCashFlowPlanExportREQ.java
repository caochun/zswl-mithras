package cn.zswltech.mithras.dto.projreview.cashflowplan;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2022/9/6
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("现金流计划表导出-请求体")
public class ProjReviewCashFlowPlanExportREQ extends VersionBaseREQ {
    private Long projReviewId;
}
