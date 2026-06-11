package cn.zswltech.mithras.projectprocess.application.lib.projreview.impl;

import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanListREQ;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanListRSP;
import cn.zswltech.mithras.projectprocess.mapper.lib.projreview.ProjReviewCashFlowPlanLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewCashFlowPlan;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewCashFlowPlanLib;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.ProjReviewCashFlowPlanLibService;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.handler.impl.ProjReviewCashFlowPlanLibHandler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjReviewCashFlowPlanLibServiceImpl extends ServiceImpl<ProjReviewCashFlowPlanLibMapper, ProjReviewCashFlowPlanLib> implements ProjReviewCashFlowPlanLibService {

    @Resource
    private ProjReviewCashFlowPlanLibHandler cashFlowPlanLibHandler;

    @Override
    public List<ProjReviewCashFlowPlanListRSP> list(ProjReviewCashFlowPlanListREQ req) {
        List<ProjReviewCashFlowPlanLib> dataList = baseMapper.selectList(Wrappers.<ProjReviewCashFlowPlanLib>lambdaQuery().eq(ProjReviewCashFlowPlanLib::getProjectId, req.getId()).eq(ProjReviewCashFlowPlanLib::getVersion, req.getVersion()));
        return dataList.stream().map(cashFlowPlanLibHandler::actualLib2Rsp).collect(Collectors.toList());
    }

    @Override
    public List<ProjReviewCashFlowPlanLib> listByProjReviewIdAndVersion(Long projReviewId, String version) {
        LambdaQueryWrapper<ProjReviewCashFlowPlanLib> query = Wrappers.lambdaQuery();
        query.eq(ProjReviewCashFlowPlan::getProjectId, projReviewId);
        query.eq(ProjReviewCashFlowPlanLib::getVersion, version);
        query.orderByAsc(ProjReviewCashFlowPlan::getCashFlowPhase);
        return this.list(query);
    }
}
