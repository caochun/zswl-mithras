package cn.zswltech.mithras.projectprocess.versioning.projreview.impl;

import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanListREQ;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanListRSP;
import cn.zswltech.mithras.projectprocess.mapper.lib.projreview.ProjReviewCashFlowQuotationProposalLibMapper;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewCashFlowQuotationProposalLib;
import cn.zswltech.mithras.projectprocess.versioning.projreview.ProjReviewCashFlowQuotationProposalLibService;
import cn.zswltech.mithras.projectprocess.versioning.projreview.handler.impl.ProjReviewCashFlowQuotationProposalLibHandler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjReviewCashFlowQuotationProposalLibServiceImpl extends ServiceImpl<ProjReviewCashFlowQuotationProposalLibMapper, ProjReviewCashFlowQuotationProposalLib> implements ProjReviewCashFlowQuotationProposalLibService {

    @Resource
    private ProjReviewCashFlowQuotationProposalLibHandler cashFlowPlanLibHandler;

    @Override
    public List<ProjReviewCashFlowPlanListRSP> list(ProjReviewCashFlowPlanListREQ req) {
        List<ProjReviewCashFlowQuotationProposalLib> dataList = baseMapper.selectList(Wrappers.<ProjReviewCashFlowQuotationProposalLib>lambdaQuery().eq(ProjReviewCashFlowQuotationProposalLib::getProjectId, req.getId()).eq(ProjReviewCashFlowQuotationProposalLib::getVersion, req.getVersion()));
        return dataList.stream().map(cashFlowPlanLibHandler::actualLib2Rsp).collect(Collectors.toList());
    }

    @Override
    public List<ProjReviewCashFlowQuotationProposalLib> listByProjReviewIdAndVersion(Long projReviewId, String version) {
        LambdaQueryWrapper<ProjReviewCashFlowQuotationProposalLib> query = Wrappers.lambdaQuery();
        query.eq(ProjReviewCashFlowQuotationProposalLib::getProjectId, projReviewId);
        query.eq(ProjReviewCashFlowQuotationProposalLib::getVersion, version);
        query.orderByAsc(ProjReviewCashFlowQuotationProposalLib::getCashFlowPhase);
        return this.list(query);
    }
}
