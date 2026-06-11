package cn.zswltech.mithras.application.orchestration.adapter.projectprocess;

import cn.zswltech.mithras.api.contract.ContractBaseInfoRSP;
import cn.zswltech.mithras.projectprocess.application.support.ProjectProcessSurvivingContractResolver;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishPriceService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projpricing.ProjPricingPriceService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewPriceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ProjectProcessSurvivingContractResolverAdapter implements ProjectProcessSurvivingContractResolver {

    @Resource
    private ProjEstablishPriceService projEstablishPriceService;
    @Resource
    private ProjPricingPriceService projPricingPriceService;
    @Resource
    private ProjReviewPriceService projReviewPriceService;

    @Override
    public List<ContractBaseInfoRSP> resolveProjEstablishSurvivingContracts(Long projEstablishId) {
        return projEstablishPriceService.getSurvivingContract(projEstablishId);
    }

    @Override
    public List<ContractBaseInfoRSP> resolveProjPricingSurvivingContracts(Long projectId) {
        return projPricingPriceService.getSurvivingContract(projectId);
    }

    @Override
    public List<ContractBaseInfoRSP> resolveProjReviewSurvivingContracts(Long projectId) {
        return projReviewPriceService.getSurvivingContract(projectId);
    }
}
