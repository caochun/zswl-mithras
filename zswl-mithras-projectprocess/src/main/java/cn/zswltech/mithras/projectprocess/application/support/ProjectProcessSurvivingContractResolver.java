package cn.zswltech.mithras.projectprocess.application.support;

import cn.zswltech.mithras.api.contract.ContractBaseInfoRSP;

import java.util.List;

/**
 * Contract lookup boundary used by project-process factoring price views.
 */
public interface ProjectProcessSurvivingContractResolver {

    List<ContractBaseInfoRSP> resolveProjEstablishSurvivingContracts(Long projEstablishId);

    List<ContractBaseInfoRSP> resolveProjPricingSurvivingContracts(Long projectId);

    List<ContractBaseInfoRSP> resolveProjReviewSurvivingContracts(Long projectId);
}
