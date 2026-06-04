package cn.zswltech.mithras.projectprocess.service.lib.projpricing.impl;

import cn.zswltech.mithras.dto.projpricing.cashflowplan.ProjPricingCashFlowPlanListREQ;
import cn.zswltech.mithras.dto.projpricing.cashflowplan.ProjPricingCashFlowPlanListRSP;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingCashFlowPlanLib;
import cn.zswltech.mithras.projectprocess.mapper.lib.projpricing.ProjPricingCashFlowPlanLibMapper;
import cn.zswltech.mithras.projectprocess.service.lib.projpricing.ProjPricingCashFlowPlanLibService;
import cn.zswltech.mithras.projectprocess.service.lib.projpricing.handler.impl.ProjPricingCashFlowPlanLibHandler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjPricingCashFlowPlanLibServiceImpl extends ServiceImpl<ProjPricingCashFlowPlanLibMapper, ProjPricingCashFlowPlanLib> implements ProjPricingCashFlowPlanLibService {

    @Resource
    private ProjPricingCashFlowPlanLibHandler cashFlowPlanLibHandler;

    @Override
    public List<ProjPricingCashFlowPlanListRSP> list(ProjPricingCashFlowPlanListREQ req) {
        List<ProjPricingCashFlowPlanLib> dataList = baseMapper.selectList(Wrappers.<ProjPricingCashFlowPlanLib>lambdaQuery().eq(ProjPricingCashFlowPlanLib::getProjectId, req.getId()).eq(ProjPricingCashFlowPlanLib::getVersion, req.getVersion()));
        return dataList.stream().map(cashFlowPlanLibHandler::actualLib2Rsp).collect(Collectors.toList());
    }

    @Override
    public List<ProjPricingCashFlowPlanLib> listByProjPricingIdAndVersion(Long projPricingId, String version) {
        LambdaQueryWrapper<ProjPricingCashFlowPlanLib> query = Wrappers.lambdaQuery();
        query.eq(ProjPricingCashFlowPlanLib::getProjectId, projPricingId);
        query.eq(ProjPricingCashFlowPlanLib::getVersion, version);
        query.orderByAsc(ProjPricingCashFlowPlanLib::getCashFlowPhase);
        return this.list(query);
    }
}
