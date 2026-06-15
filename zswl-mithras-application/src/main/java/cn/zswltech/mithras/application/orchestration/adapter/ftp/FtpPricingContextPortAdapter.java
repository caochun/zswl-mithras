package cn.zswltech.mithras.application.orchestration.adapter.ftp;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractPriceService;
import cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractGuarantor;
import cn.zswltech.mithras.contract.model.contract.ContractGuarantorLib;
import cn.zswltech.mithras.contract.model.contract.ContractTenantry;
import cn.zswltech.mithras.contract.model.contract.ContractTenantryLib;
import cn.zswltech.mithras.contract.versioning.service.ContractGuarantorLibService;
import cn.zswltech.mithras.contract.versioning.service.ContractTenantryLibService;
import cn.zswltech.mithras.customer.application.client.CorpAddressInfoService;
import cn.zswltech.mithras.customer.application.client.CorpCommerceInfoService;
import cn.zswltech.mithras.customer.enums.CorpAddressType;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.customer.model.client.CorpAddressInfo;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.StringUtil;
import cn.zswltech.mithras.ftp.newftp.service.port.FtpPricingContextPort;
import cn.zswltech.mithras.ftp.oldftp.bo.CashFtpInfluenceBO;
import cn.zswltech.mithras.projectprocess.mapper.projpricing.ProjPricingBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class FtpPricingContextPortAdapter implements FtpPricingContextPort {

    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractPriceService contractPriceService;
    @Resource
    private CorpCommerceInfoService corpCommerceInfoService;
    @Resource
    private CorpAddressInfoService corpAddressInfoService;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private ProjPricingBaseInfoMapper projPricingBaseInfoMapper;
    @Resource
    private ContractTenantryLibService contractTenantryLibService;
    @Resource
    private ContractGuarantorLibService contractGuarantorLibService;

    @Override
    public CashFtpInfluenceBO assembleCashFtpInfluence(Long contractId, LocalDate targetDate) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        Assert.notNull(contractBaseInfo, () -> MithrasException.newException("合同信息不存在"));
        CashFtpInfluenceBO cashFtpInfluenceBO = new CashFtpInfluenceBO();
        cashFtpInfluenceBO.setBizType(contractBaseInfo.getBizType());

        ContractPriceDetailREQ req = new ContractPriceDetailREQ();
        req.setContractId(contractId);
        ContractPriceDetailRSP contractPriceDetailRSP = contractPriceService.detail(req);
        if (Objects.nonNull(contractPriceDetailRSP)) {
            cashFtpInfluenceBO.setContractMonthCount(contractPriceDetailRSP.getMonthCount());
        }

        List<CorpCommerceInfo> corpCommerceInfoList = corpCommerceInfoService.findByClientId(contractBaseInfo.getClientId());
        if (CollectionUtil.isNotEmpty(corpCommerceInfoList)) {
            CorpCommerceInfo corpCommerceInfo = corpCommerceInfoList.get(0);
            cashFtpInfluenceBO.setRiskControlIndustryClassify(corpCommerceInfo.getRiskControlIndustryClassify());
            cashFtpInfluenceBO.setEnterpriseNature(corpCommerceInfo.getEnterpriseNature());
        }

        cashFtpInfluenceBO.setZhejiang(isZhejiang(contractBaseInfo.getClientId()));

        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(contractBaseInfo.getProjReviewId());
        if (Objects.nonNull(projReviewBaseInfo)) {
            ProjPricingBaseInfo projPricingBaseInfo = getPricingByReview(projReviewBaseInfo);
            if (Objects.nonNull(projPricingBaseInfo)) {
                cashFtpInfluenceBO.setFtpIndustryCategory(projPricingBaseInfo.getFtpIndustryCategory());
                cashFtpInfluenceBO.setAssetIndustryClassify(projPricingBaseInfo.getProjectClassify());
                cashFtpInfluenceBO.setRegionClassify(projPricingBaseInfo.getRegionalProjectClassify());
                cashFtpInfluenceBO.setRegionalDivision(projPricingBaseInfo.getRegionalDivision());
                cashFtpInfluenceBO.setProjectManageLevel(projPricingBaseInfo.getProjectManageLevel());
                cashFtpInfluenceBO.setIsAAA(projPricingBaseInfo.getIsAAA());
                cashFtpInfluenceBO.setEvaluationSubjectId(projPricingBaseInfo.getEvaluationSubjectId());
            }
        }
        cashFtpInfluenceBO.setTargetDate(targetDate);

        ContractTenantryLib tenantryLib = contractTenantryLibService.getOne(Wrappers.<ContractTenantryLib>lambdaQuery()
                .eq(ContractTenantryLib::getContractId, contractId)
                .eq(ContractTenantry::getLesseeType, LesseeTypeEnum.MAIN_LESSSEE.name())
                .eq(ContractTenantryLib::getVersionType, VersionTypeConstants.NORMAL)
                .orderByDesc(ContractTenantryLib::getVersion)
                .last(StringUtil.mysqlLimitOne()));
        if (Objects.nonNull(tenantryLib)) {
            cashFtpInfluenceBO.setTenantId(tenantryLib.getLesseeId());
        }

        ContractGuarantorLib contractGuarantorLib = contractGuarantorLibService.getOne(Wrappers.<ContractGuarantorLib>lambdaQuery()
                .eq(ContractGuarantorLib::getContractId, contractId)
                .eq(ContractGuarantorLib::getVersionType, VersionTypeConstants.NORMAL)
                .eq(ContractGuarantor::getGuarantorType, ClientType.CORPORATION.name())
                .orderByDesc(ContractGuarantorLib::getVersion)
                .last(StringUtil.mysqlLimitOne()));
        if (Objects.nonNull(contractGuarantorLib) && StrUtil.isNotBlank(contractGuarantorLib.getGuarantorIds())) {
            List<Long> ids = JSONUtil.toList(contractGuarantorLib.getGuarantorIds(), Long.class);
            if (CollectionUtil.isNotEmpty(ids)) {
                cashFtpInfluenceBO.setGuarantorIdList(ids);
            }
        }
        return cashFtpInfluenceBO;
    }

    private boolean isZhejiang(Long clientId) {
        List<CorpAddressInfo> corpAddressInfoList = corpAddressInfoService.listCorpAddressInfo(clientId);
        if (CollectionUtil.isNotEmpty(corpAddressInfoList)) {
            Map<String, List<CorpAddressInfo>> map = corpAddressInfoList.stream().collect(Collectors.groupingBy(CorpAddressInfo::getAddressType));
            List<CorpAddressInfo> workAddressList = map.get(CorpAddressType.WORK_ADDRESS.name());
            List<CorpAddressInfo> registryAddressList = map.get(CorpAddressType.REGISTRY_ADDRESS.name());
            if (CollectionUtil.isNotEmpty(workAddressList)) {
                for (CorpAddressInfo corpAddressInfo : workAddressList) {
                    if (Objects.equals(corpAddressInfo.getProvince(), "330000")) {
                        return true;
                    }
                }
                for (CorpAddressInfo corpAddressInfo : registryAddressList) {
                    if (Objects.equals(corpAddressInfo.getProvince(), "330000")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private ProjPricingBaseInfo getPricingByReview(ProjReviewBaseInfo reviewBaseInfo) {
        return projPricingBaseInfoMapper.selectOne(Wrappers.<ProjPricingBaseInfo>lambdaQuery()
                .eq(reviewBaseInfo.getProjEstablishId() != null, ProjPricingBaseInfo::getProjEstablishId, reviewBaseInfo.getProjEstablishId())
                .eq(reviewBaseInfo.getGroupCreditReviewId() != null, ProjPricingBaseInfo::getGroupCreditReviewId, reviewBaseInfo.getGroupCreditReviewId())
                .eq(ProjPricingBaseInfo::getProjName, reviewBaseInfo.getProjName())
                .ne(ProjPricingBaseInfo::getProjPricingStatus, RecordStatus.CLOSED.name()));
    }
}
