package cn.zswltech.mithras.service.service.lib.projreview;

import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanListREQ;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanListRSP;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewCashFlowPlanLib;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 
 * @author: jackerhe 
 * @date: 2022/8/2 10:39 上午
 **/
public interface ProjReviewCashFlowPlanLibService extends IService<ProjReviewCashFlowPlanLib> {
    List<ProjReviewCashFlowPlanListRSP> list(ProjReviewCashFlowPlanListREQ req);

    List<ProjReviewCashFlowPlanLib> listByProjReviewIdAndVersion(Long projReviewId, String version);
}
