package cn.zswltech.mithras.service.service.contract.effectcheck;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.common.enums.ProjectBizType;
import cn.zswltech.mithras.service.enums.contract.ContractConstitutionFileTypeEnum;
import cn.zswltech.mithras.service.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.service.enums.contract.CreditorDebtorTypeEnum;
import cn.zswltech.mithras.service.enums.riskcontrol.RiskControlIndustryClassify;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.service.mapper.model.contract.*;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.contract.*;
import cn.zswltech.mithras.service.service.lib.client.CorpCommerceInfoLibService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/11/9
 * @description
 */
public abstract class AbstractContractEffectCheck implements ContractEffectCheck {
    @Resource
    protected MaterialsListService materialsListService;
    @Resource
    protected ContractGuarantorService contractGuarantorService;
    @Resource
    protected ContractMortgageService contractMortgageService;
    @Resource
    protected ContractPledgeService contractPledgeService;
    @Resource
    protected ContractTenantryService contractTenantryService;
    @Resource
    protected ContractRentActualService contractRentActualService;
    @Resource
    protected ContractPriceService contractPriceService;
    @Resource
    protected ContractService contractService;
    @Resource
    private ContractConstitutionFileService contractConstitutionFileService;
    @Resource
    private CorpCommerceInfoLibService corpCommerceInfoLibService;


    protected void creditDebtorCheck(ContractBaseInfo contractBaseInfo) {
        List<ContractTenantry> contractTenantryList = contractTenantryService.listByContractId(contractBaseInfo.getId());
        Assert.notEmpty(contractTenantryList, () -> MithrasException.newException("至少存在一个债权人"));
        boolean existCreditor = false;
        for (ContractTenantry contractTenantry : contractTenantryList) {
//            Assert.notNull(contractTenantry.getContactId(), () -> MithrasException.newException("债权人/债务人'指定联系人'不能为空"));
            Assert.notNull(contractTenantry.getIsReport(), () -> MithrasException.newException("债权人/债务人'是否上报征信'不能为空"));
            if (CreditorDebtorTypeEnum.CREDITOR.name().equals(contractTenantry.getLesseeType())) {
                existCreditor = true;
                break;
            }
        }
        Assert.isTrue(existCreditor, () -> MithrasException.newException("至少存在一个债权人"));
    }

    protected void commonContractFileCheck(ContractBaseInfo contractBaseInfo, boolean inProcess) {
        // 报价方案
        ContractPriceDetailREQ priceReq = new ContractPriceDetailREQ();
        priceReq.setContractId(contractBaseInfo.getId());
        ContractPriceDetailRSP priceRsp = contractPriceService.detail(priceReq);
        Assert.isTrue(!priceRsp.isNull(), () -> MithrasException.newException("报价方案不能为空"));
        Assert.notNull(priceRsp.getApplyCreditAmount(), () -> MithrasException.newException("合同金额不能为空"));
        Assert.notNull(priceRsp.getIrr(), () -> MithrasException.newException("IRR不能为空"));
        // 承租人/债权人/债务人
        List<ContractTenantry> contractTenantryList = contractTenantryService.listByContractId(contractBaseInfo.getId());
        if (CollectionUtil.isNotEmpty(contractTenantryList)) {
            for (ContractTenantry contractTenantry : contractTenantryList) {
                //只有租赁需要校验承租人
                if (ProjectBizType.ZL.name().equals(contractBaseInfo.getBizType())
                        || ProjectBizType.ZZ.name().equals(contractBaseInfo.getBizType())) {
                    int count = contractConstitutionFileService.count(Wrappers.<ContractConstitutionFile>lambdaQuery()
                            .eq(ContractConstitutionFile::getContractId, contractBaseInfo.getId())
                            .eq(ContractConstitutionFile::getFileType, ContractConstitutionFileTypeEnum.TENANT)
                            .eq(ContractConstitutionFile::getTenantryId, contractTenantry.getId()));
                    Assert.isFalse(count <= 0, () -> MithrasException.newException("承租人章程文件不能为空"));
                    Assert.notNull(contractTenantry.getResolutionType(), () -> MithrasException.newException("承租人决议类型不能为空"));

                    CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibService.getNewestOne(contractBaseInfo.getClientId());
                    String classify = Optional.ofNullable(corpCommerceInfoLib).map(CorpCommerceInfo::getRiskControlIndustryClassify).orElse(null);
                    if (!RiskControlIndustryClassify.PUBLIC_UTILITIES.name().equals(classify) &&
                            !RiskControlIndustryClassify.CIVIL_CONSUMPTION.name().equals(classify) &&
                            !RiskControlIndustryClassify.TRAVEL.name().equals(classify)) {
                        Assert.notNull(contractTenantry.getLeaseItemFileType(), () -> MithrasException.newException("承租人租赁物文件类型不能为空"));
                    }
                }
                if (Objects.equals(contractTenantry.getIsReport(), YesOrNoNumberEnum.YES.getCode())) {
                    contractService.checkZhongZhengCode(contractTenantry.getLesseeId());
                }
            }
        }
        // 实际租金表/实际支付表
        Assert.notEmpty(contractRentActualService.listByContract(contractBaseInfo.getId()), () -> MithrasException.newException("实际租金表/支付表不能为空"));
        // 合同文本类型
        ContractTextInfo contractTextInfo = SpringUtil.getBean(ContractTextInfoService.class).getOneByContractId(contractBaseInfo.getId());
        if (Objects.isNull(contractTextInfo) || StrUtil.isBlank(contractTextInfo.getTextType())) {
            throw new MithrasException("合同文本类型不能为空");
        }
        // 合同文本
        List<ContractGuarantor> contractGuarantorList = contractGuarantorService.listByContractId(contractBaseInfo.getId());
        if (CollectionUtil.isNotEmpty(contractGuarantorList)) {
            for (ContractGuarantor contractGuarantor : contractGuarantorList) {
                Assert.notBlank(contractGuarantor.getGuarantorContractCode(), () -> MithrasException.newException("保证合同编号不能为空"));
            }
            //将法人筛选出来
            List<ContractGuarantor> list = contractGuarantorList.stream().filter(item -> "CORPORATION".equals(item.getGuarantorType())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(list)) {
                for (ContractGuarantor contractGuarantor : list) {
                    Assert.notBlank(contractGuarantor.getResolutionType(), () -> MithrasException.newException("担保措施决议类型不能为空"));
                    int count = contractConstitutionFileService.count(Wrappers.<ContractConstitutionFile>lambdaQuery()
                            .eq(ContractConstitutionFile::getContractId, contractBaseInfo.getId())
                            .eq(ContractConstitutionFile::getFileType, ContractConstitutionFileTypeEnum.GUARANTOR)
                            .in(ContractConstitutionFile::getTenantryId, contractGuarantor.getId()));
                    Assert.isFalse(count <= 0, () -> MithrasException.newException("担保措施章程文件不能为空"));
                }
            }
        }
        List<ContractMortgage> contractMortgageList = contractMortgageService.listByContractId(contractBaseInfo.getId());
        if (CollectionUtil.isNotEmpty(contractMortgageList)) {
            for (ContractMortgage contractMortgage : contractMortgageList) {
                Assert.notBlank(contractMortgage.getMortgageContractCode(), () -> MithrasException.newException("抵押合同编号不能为空"));
            }
        }
        List<ContractPledge> contractPledgeList = contractPledgeService.listByContractId(contractBaseInfo.getId());
        if (CollectionUtil.isNotEmpty(contractPledgeList)) {
            for (ContractPledge contractPledge : contractPledgeList) {
                Assert.notBlank(contractPledge.getPledgeDescribe(), () -> MithrasException.newException("质押物描述不能为空"));
                Assert.notBlank(contractPledge.getPledgeContractCode(), () -> MithrasException.newException("质押合同编号不能为空"));
            }
        }
        if (!inProcess) {
            List<MaterialsList> materials = materialsListService.listBy(BusinessModuleEnum.CONTRACT.name(), contractBaseInfo.getId());
            Map<String, List<MaterialsList>> map = materials.stream().collect(Collectors.groupingBy(MaterialsList::getMaterialsType));
            Assert.notEmpty(map.get(ContractTypeEnum.MAIN_CONTRACT.name()), () -> MithrasException.newException("主合同文本不能为空"));
            if (CollectionUtil.isNotEmpty(contractGuarantorList)) {
                Assert.notNull(map.get(ContractTypeEnum.GUARANTEE_CONTRACT.name()), () -> MithrasException.newException("保证合同文本不能为空"));
            }
            if (CollectionUtil.isNotEmpty(contractMortgageList)) {
                Assert.notNull(map.get(ContractTypeEnum.MORTGAGE_CONTRACT.name()), () -> MithrasException.newException("抵押合同文本不能为空"));
            }
            if (CollectionUtil.isNotEmpty(contractPledgeList)) {
                Assert.notNull(map.get(ContractTypeEnum.PLEDGE_CONTRACT.name()), () -> MithrasException.newException("质押合同文本不能为空"));
            }
            if (Objects.equals(contractBaseInfo.getBizType(), ProjectBizType.ZL.name()) || Objects.equals(contractBaseInfo.getBizType(), ProjectBizType.ZZ.name())) {
                if (Objects.nonNull(priceRsp.getConsultingFee()) && priceRsp.getConsultingFee() > 0) {
                    Assert.notNull(map.get(ContractTypeEnum.CONSULTING_CONTRACT.name()), () -> MithrasException.newException("咨询合同不能为空"));
                }
            }
        }
    }
}
