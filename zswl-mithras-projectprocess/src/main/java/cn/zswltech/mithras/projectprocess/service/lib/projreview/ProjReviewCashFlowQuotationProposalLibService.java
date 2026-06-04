package cn.zswltech.mithras.projectprocess.service.lib.projreview;

import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanListREQ;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanListRSP;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewCashFlowQuotationProposalLib;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 
 * @author: jackerhe 
 * @date: 2022/8/2 10:39 上午
 **/
public interface ProjReviewCashFlowQuotationProposalLibService extends IService<ProjReviewCashFlowQuotationProposalLib> {
    List<ProjReviewCashFlowPlanListRSP> list(ProjReviewCashFlowPlanListREQ req);

    List<ProjReviewCashFlowQuotationProposalLib> listByProjReviewIdAndVersion(Long projReviewId, String version);
}
