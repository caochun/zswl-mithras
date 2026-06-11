package cn.zswltech.mithras.kpi.bo;

import cn.zswltech.mithras.dto.kpi.KpiExpectedLossDecisionQuery;
import cn.zswltech.mithras.dto.rating.decision.DecisionExecuteEclResult;
import cn.zswltech.mithras.kpi.model.KpiProvisionDetail;
import lombok.Data;

/**
 * @Description 违约损失率LGD
 * @Author jackerhe
 * @Date 2025/9/24 15:24
 * @Version 1.0
 **/
@Data
public class EclResultBO {

    //拨备信息
    private KpiProvisionDetail kpiProvisionDetail;
    //请求参数
    private KpiExpectedLossDecisionQuery query;
    //返回结果
    private DecisionExecuteEclResult result;

}
