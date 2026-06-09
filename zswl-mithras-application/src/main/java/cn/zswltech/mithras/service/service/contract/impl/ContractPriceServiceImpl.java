package cn.zswltech.mithras.service.service.contract.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.util.ApplicationContextUtil;
import cn.zswltech.mithras.dto.contract.price.*;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingPriceDetailRSP;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.contract.convert.contract.ContractPriceConverter;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.third.enums.capital.BizTypeEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractReceiptMapper;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractAocPriceLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractFactoringPriceLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractLeasePriceLibMapper;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewAocPrice;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewFactoringPrice;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.contract.event.ContractPriceChangeEvent;
import cn.zswltech.mithras.contract.core.application.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.application.ContractAocPriceService;
import cn.zswltech.mithras.contract.core.application.ContractFactoringPriceService;
import cn.zswltech.mithras.contract.core.application.ContractLeasePriceService;
import cn.zswltech.mithras.contract.core.application.ContractPriceService;
import cn.zswltech.mithras.contract.versioning.application.ContractPriceAmountResolver;
import cn.zswltech.mithras.contract.core.application.ContractReceiptService;
import cn.zswltech.mithras.contract.versioning.application.ContractAocPriceLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractFactoringPriceLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractLeasePriceLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractReceiptLibService;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractAocPriceLibHandler;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractFactoringPriceLibHandler;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractLeasePriceLibHandler;
import cn.zswltech.mithras.service.service.projpricing.ProjPricingBaseInfoService;
import cn.zswltech.mithras.service.service.projpricing.ProjPricingPriceService;
import cn.zswltech.mithras.projectprocess.service.projreview.ProjReviewAocPriceService;
import cn.zswltech.mithras.projectprocess.service.projreview.ProjReviewFactoringPriceService;
import cn.zswltech.mithras.projectprocess.service.projreview.ProjReviewLeasePriceService;
import cn.zswltech.mithras.service.util.FinancialUtil;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName ContractPriceServiceImpl
 * @Description
 * @Author jackerhe
 * @Date 2022/8/16 2:26 下午
 * @Version 1.0
 **/
@Service
public class ContractPriceServiceImpl implements ContractPriceService, ContractPriceAmountResolver {

    @Lazy
    @Autowired
    private ContractBaseInfoService baseInfoService;
    @Autowired
    private ContractLeasePriceService leasePriceService;
    @Autowired
    private ContractAocPriceService aocPriceService;
    @Autowired
    private ContractFactoringPriceService factoringPriceService;
    @Autowired
    private ContractLeasePriceLibService leasePriceLibService;
    @Resource
    private ContractFactoringPriceLibService contractFactoringPriceLibService;
    @Resource
    private ContractAocPriceLibService contractAocPriceLibService;
    @Resource
    private CommonVersionMapper commonVersionMapper;
    @Resource
    private ContractPriceConverter priceConverter;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ProjReviewFactoringPriceService projReviewFactoringPriceService;
    @Resource
    private ProjReviewAocPriceService projReviewAocPriceService;
    @Resource
    private ProjReviewLeasePriceService projReviewLeasePriceService;
    @Resource
    private ContractLeasePriceLibMapper leasePriceLibMapper;
    @Resource
    private ContractFactoringPriceLibMapper factoringPriceLibMapper;
    @Resource
    private ContractAocPriceLibMapper aocPriceLibMapper;
    @Resource
    private ContractLeasePriceLibHandler leasePriceLibHandler;
    @Resource
    private ContractFactoringPriceLibHandler factoringPriceLibHandler;
    @Resource
    private ContractAocPriceLibHandler aocPriceLibHandler;
    @Resource
    private ProjPricingPriceService projPricingPriceService;


    @Override
    public void modify(ContractPriceModifyREQ req) {
        ContractBaseInfo baseInfo = baseInfoService.getById(req.getContractId());
        if (ObjectUtil.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }

        if (req.getAocPriceModifyREQ() != null) {
            /*ProjReviewAocPrice projReviewAocPrice = projReviewAocPriceService.getByProjectId(baseInfo.getProjReviewId());
            //检查合同金额金额
            checkPublic(req.getAocPriceModifyREQ().getContractAmount(), baseInfo, req.getAocPriceModifyREQ().getCreditAmountLoop(),projReviewAocPrice.getApplyCreditAmount());*/
            aocPriceService.modify(req.getAocPriceModifyREQ());
            //baseInfoService.updateDeclaredAmount(req.getProjectId(),req.getAocPriceModifyREQ().getApplyCreditAmount());
        }
        if (req.getFactoringPriceModifyREQ() != null) {
            /*ProjReviewFactoringPrice projReviewFactoringPrice = projReviewFactoringPriceService.getByProjectId(baseInfo.getProjReviewId());
            checkPublic(req.getFactoringPriceModifyREQ().getContractAmount(), baseInfo, req.getFactoringPriceModifyREQ().getCreditAmountLoop(),projReviewFactoringPrice.getApplyCreditAmount());*/
            factoringPriceService.modify(req.getFactoringPriceModifyREQ());
            //baseInfoService.updateDeclaredAmount(req.getProjectId(),req.getFactoringPriceModifyREQ().getApplyCreditAmount());
        }
        if (req.getLeasePriceModifyREQ() != null) {
            /*ProjReviewLeasePrice byProjectId = projReviewLeasePriceService.getByProjectId(baseInfo.getProjReviewId());
            checkPublic(req.getLeasePriceModifyREQ().getApplyCreditAmount(), baseInfo, req.getLeasePriceModifyREQ().getCreditAmountLoop(),byProjectId.getApplyCreditAmount());*/
            leasePriceService.modify(req.getLeasePriceModifyREQ());
            if (ProjectBizType.BL.name().equals(baseInfo.getBizType())) {
                return;
            } else {
                baseInfoService.updatePay(req.getContractId(), req.getLeasePriceModifyREQ().getApplyCreditAmount(), null);
            }
        }
        //修改金额进行通知
        ApplicationContextUtil.getApplicationContext().publishEvent(new ContractPriceChangeEvent(this, req.getContractId()));
    }

    private void checkPublic(Long contractAmount, ContractBaseInfo baseInfo, Integer isLoop, Long projAmount) {
        //剩余金额
        long remainAvailableQuota = projAmount - baseInfoService.getRemainAvailableQuota(baseInfo.getId(), isLoop, baseInfoService.selectListByProjId(baseInfo.getProjReviewId()));
        // 合同金额
        if (contractAmount <= 0 || contractAmount > remainAvailableQuota) {
            throw new MithrasException("合同金额需大于0且小于等于项目剩余可用金额" + Util.mithrasLong2BigDecimal(remainAvailableQuota).toPlainString());
        }
    }

    @Override
    public ContractPriceDetailRSP detail(ContractPriceDetailREQ req) {
        ContractPriceDetailRSP res = new ContractPriceDetailRSP();
        // 处理租赁
        ContractLeasePriceDetailRSP contractLeasePriceDetailRSP = priceConverter.entityToLeaseRsp(leasePriceService.detail(req));
        if (Objects.nonNull(contractLeasePriceDetailRSP)) {
            if (Objects.nonNull(contractLeasePriceDetailRSP.getApplyCreditAmount()) && Objects.nonNull(contractLeasePriceDetailRSP.getEarnestMoney())) {
                contractLeasePriceDetailRSP.setEarnestMoneyRate(FinancialUtil.calculateFeeRate(contractLeasePriceDetailRSP.getEarnestMoney(), contractLeasePriceDetailRSP.getApplyCreditAmount()));
            }
            if (Objects.nonNull(contractLeasePriceDetailRSP.getApplyCreditAmount()) && Objects.nonNull(contractLeasePriceDetailRSP.getConsultingFee())) {
                contractLeasePriceDetailRSP.setConsultingFeeRate(FinancialUtil.calculateFeeRate(contractLeasePriceDetailRSP.getConsultingFee(), contractLeasePriceDetailRSP.getApplyCreditAmount()));
            }
            // 补充项目评审报价方案数据
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
            ProjReviewLeasePrice projReviewLeasePrice = projReviewLeasePriceService.getByProjectId(contractBaseInfo.getProjReviewId());
            if (Objects.nonNull(projReviewLeasePrice)) {
                contractLeasePriceDetailRSP.setProjCreditAmount(projReviewLeasePrice.getApplyCreditAmount());
                contractLeasePriceDetailRSP.setProjEarnestMoney(projReviewLeasePrice.getEarnestMoney());
                contractLeasePriceDetailRSP.setProjConsultingFee(projReviewLeasePrice.getConsultingFee());
                contractLeasePriceDetailRSP.setProjDownPayment(projReviewLeasePrice.getDownPayment());
                contractLeasePriceDetailRSP.setProjIrrPercent(projReviewLeasePrice.getIrrPercent());
            }
            if (ObjectUtil.isNotEmpty(contractLeasePriceDetailRSP.getStructuredInterest())) {
                contractLeasePriceDetailRSP.setStructuredInterestList(JSONUtil.toList(contractLeasePriceDetailRSP.getStructuredInterest(), StructuredInterest.class));
            }
            // 补充项目定价数据
            ProjPricingBaseInfo projPricingBaseInfo = SpringUtil.getBean(ProjPricingBaseInfoService.class).getPricingByReviewId(contractBaseInfo.getProjReviewId());
            if (Objects.nonNull(projPricingBaseInfo)) {
                ProjPricingPriceDetailRSP pricingPriceDetailRSP = projPricingPriceService.oldDetail(projPricingBaseInfo.getId());
                if (Objects.nonNull(pricingPriceDetailRSP)) {
                    contractLeasePriceDetailRSP.setPricingIrrPercent(pricingPriceDetailRSP.getIrr());
                }
            }
        }
        res.setLeasePriceModifyRSP(contractLeasePriceDetailRSP);
        // 处理保理
        ContractFactoringPriceDetailRSP contractFactoringPriceDetailRSP = priceConverter.entityToFactoringRsp(factoringPriceService.getByContractId(req));
        if (Objects.nonNull(contractFactoringPriceDetailRSP)) {
            if (Objects.nonNull(contractFactoringPriceDetailRSP.getContractAmount()) && Objects.nonNull(contractFactoringPriceDetailRSP.getEarnestMoney())) {
                contractFactoringPriceDetailRSP.setEarnestMoneyRate(FinancialUtil.calculateFeeRate(contractFactoringPriceDetailRSP.getEarnestMoney(), contractFactoringPriceDetailRSP.getContractAmount()));
            }
            if (Objects.nonNull(contractFactoringPriceDetailRSP.getContractAmount()) && Objects.nonNull(contractFactoringPriceDetailRSP.getConsultingFee())) {
                contractFactoringPriceDetailRSP.setConsultingFeeRate(FinancialUtil.calculateFeeRate(contractFactoringPriceDetailRSP.getConsultingFee(), contractFactoringPriceDetailRSP.getContractAmount()));
            }
            // 补充项目评审报价方案数据
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
            ProjReviewFactoringPrice projReviewFactoringPrice = projReviewFactoringPriceService.getByProjectId(contractBaseInfo.getProjReviewId());
            if (Objects.nonNull(projReviewFactoringPrice)) {
                contractFactoringPriceDetailRSP.setProjCreditAmount(projReviewFactoringPrice.getApplyCreditAmount());
                contractFactoringPriceDetailRSP.setProjEarnestMoney(projReviewFactoringPrice.getEarnestMoney());
                contractFactoringPriceDetailRSP.setProjConsultingFee(projReviewFactoringPrice.getConsultingFee());
                contractFactoringPriceDetailRSP.setProjDownPayment(0L);
                contractFactoringPriceDetailRSP.setProjIrrPercent(projReviewFactoringPrice.getIrrPercent());
            }
            if (ObjectUtil.isNotEmpty(contractFactoringPriceDetailRSP.getStructuredInterest())) {
                contractFactoringPriceDetailRSP.setStructuredInterestList(JSONUtil.toList(contractFactoringPriceDetailRSP.getStructuredInterest(), StructuredInterest.class));
            }
            // 补充项目定价数据
            ProjPricingBaseInfo projPricingBaseInfo = SpringUtil.getBean(ProjPricingBaseInfoService.class).getPricingByReviewId(contractBaseInfo.getProjReviewId());
            if (Objects.nonNull(projPricingBaseInfo)) {
                ProjPricingPriceDetailRSP pricingPriceDetailRSP = projPricingPriceService.oldDetail(projPricingBaseInfo.getId());
                if (Objects.nonNull(pricingPriceDetailRSP)) {
                    contractFactoringPriceDetailRSP.setPricingIrrPercent(pricingPriceDetailRSP.getIrr());
                }
            }
        }
        res.setFactoringPriceRSP(contractFactoringPriceDetailRSP);
        // 处理债权转让
        ContractAocPriceDetailRSP contractAocPriceDetailRSP = priceConverter.entityToAocRsp(aocPriceService.getByContractId(req));
        if (Objects.nonNull(contractAocPriceDetailRSP)) {
            if (Objects.nonNull(contractAocPriceDetailRSP.getContractAmount()) && Objects.nonNull(contractAocPriceDetailRSP.getEarnestMoney())) {
                contractAocPriceDetailRSP.setEarnestMoneyRate(FinancialUtil.calculateFeeRate(contractAocPriceDetailRSP.getEarnestMoney(), contractAocPriceDetailRSP.getContractAmount()));
            }
            if (Objects.nonNull(contractAocPriceDetailRSP.getContractAmount()) && Objects.nonNull(contractAocPriceDetailRSP.getConsultingFee())) {
                contractAocPriceDetailRSP.setConsultingFeeRate(FinancialUtil.calculateFeeRate(contractAocPriceDetailRSP.getConsultingFee(), contractAocPriceDetailRSP.getContractAmount()));
            }
            // 补充项目评审报价方案数据
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
            ProjReviewAocPrice projReviewAocPrice = projReviewAocPriceService.getByProjectId(contractBaseInfo.getProjReviewId());
            if (Objects.nonNull(projReviewAocPrice)) {
                contractAocPriceDetailRSP.setProjCreditAmount(projReviewAocPrice.getApplyCreditAmount());
                contractAocPriceDetailRSP.setProjEarnestMoney(projReviewAocPrice.getEarnestMoney());
                contractAocPriceDetailRSP.setProjConsultingFee(projReviewAocPrice.getConsultingFee());
                contractAocPriceDetailRSP.setProjDownPayment(0L);
                contractAocPriceDetailRSP.setProjIrrPercent(projReviewAocPrice.getIrrPercent());
            }
            if (ObjectUtil.isNotEmpty(contractAocPriceDetailRSP.getStructuredInterest())) {
                contractAocPriceDetailRSP.setStructuredInterestList(JSONUtil.toList(contractAocPriceDetailRSP.getStructuredInterest(), StructuredInterest.class));
            }
            // 补充项目定价数据
            ProjPricingBaseInfo projPricingBaseInfo = SpringUtil.getBean(ProjPricingBaseInfoService.class).getPricingByReviewId(contractBaseInfo.getProjReviewId());
            if (Objects.nonNull(projPricingBaseInfo)) {
                ProjPricingPriceDetailRSP pricingPriceDetailRSP = projPricingPriceService.oldDetail(projPricingBaseInfo.getId());
                if (Objects.nonNull(pricingPriceDetailRSP)) {
                    contractAocPriceDetailRSP.setPricingIrrPercent(pricingPriceDetailRSP.getIrr());
                }
            }
        }
        res.setAocPriceRSP(contractAocPriceDetailRSP);
        return res;
    }

    @Override
    public ContractPriceDetailRSP editionDetail(ContractPriceDetailREQ req) {
        ContractPriceDetailRSP res = new ContractPriceDetailRSP();
        CommonVersion commonVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, req.getContractId())
                .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
                .eq(CommonVersion::getModule, BusinessModuleEnum.CONTRACT.name())
                //有版本，取上一个有效版本做比对
                .lt(ObjectUtil.isNotNull(req.getVersion()), CommonVersion::getVersion, req.getVersion())
                .orderByDesc(CommonVersion::getVersion)
                .last("LIMIT 1"));
        if (ObjectUtil.isNull(commonVersion)) {
            throw new MithrasException("此合同没有版本数据");
        }
        ContractLeasePrice oldData = leasePriceLibService.getByVersion(req.getContractId(), commonVersion.getVersion());
        ContractFactoringPrice factoringPriceLib = contractFactoringPriceLibService.getByVersion(req.getContractId(), commonVersion.getVersion());
        ContractAocPrice aocPriceLib = contractAocPriceLibService.getByVersion(req.getContractId(), commonVersion.getVersion());
        // 处理租赁
        if (ObjectUtil.isNotEmpty(oldData)) {
            ContractLeasePriceDetailRSP priceDetailRSP = priceConverter.entityToLeaseRsp(oldData);
            if (ObjectUtil.isNotEmpty(priceDetailRSP.getStructuredInterest())) {
                priceDetailRSP.setStructuredInterestList(JSONUtil.toList(priceDetailRSP.getStructuredInterest(), StructuredInterest.class));
            }
            res.setLeasePriceModifyRSP(priceDetailRSP);
        }
        // 处理保理
        if (Objects.nonNull(factoringPriceLib)) {
            ContractFactoringPriceDetailRSP contractFactoringPriceDetailRSP = priceConverter.entityToFactoringRsp(factoringPriceLib);
            if (ObjectUtil.isNotEmpty(contractFactoringPriceDetailRSP.getStructuredInterest())) {
                contractFactoringPriceDetailRSP.setStructuredInterestList(JSONUtil.toList(contractFactoringPriceDetailRSP.getStructuredInterest(), StructuredInterest.class));
            }
            res.setFactoringPriceRSP(contractFactoringPriceDetailRSP);
        }
        // 处理债权转让
        if (Objects.nonNull(aocPriceLib)) {
            ContractAocPriceDetailRSP contractAocPriceDetailRSP = priceConverter.entityToAocRsp(aocPriceLib);
            if (ObjectUtil.isNotEmpty(contractAocPriceDetailRSP.getStructuredInterest())) {
                contractAocPriceDetailRSP.setStructuredInterestList(JSONUtil.toList(contractAocPriceDetailRSP.getStructuredInterest(), StructuredInterest.class));
            }
            res.setAocPriceRSP(contractAocPriceDetailRSP);
        }
        return res;
    }


    @Override
    public void add(BaseModel price) {
        price.setCreateTime(null);
        price.setUpdateTime(null);
        price.setUpdateBy(null);
        if (price instanceof ContractAocPrice) {
            aocPriceService.save((ContractAocPrice) price);
        } else if (price instanceof ContractFactoringPrice) {
            factoringPriceService.add((ContractFactoringPrice) price);
        } else if (price instanceof ContractLeasePrice) {
            leasePriceService.save((ContractLeasePrice) price);
        }
    }

    @Override
    public Long sumApplyByContractId(List<Long> ids) {
        return leasePriceService.sumApplyByContractId(ids) + aocPriceService.sumApplyByContractId(ids) + factoringPriceService.sumApplyByContractId(ids);
    }

    @Override
    public Map<Long, ContractLeasePrice> listByContractIds(List<Long> contractIds) {
        Map<Long, ContractLeasePrice> res = new HashMap<>(16);
        if (ObjectUtil.isEmpty(contractIds)) {
            return res;
        }
        List<ContractLeasePrice> contractLeasePrices = leasePriceService.getBaseMapper()
                .selectList(Wrappers.<ContractLeasePrice>lambdaQuery()
                        .in(ContractLeasePrice::getContractId, contractIds));
        for (ContractLeasePrice leasePrice : contractLeasePrices) {
            res.put(leasePrice.getContractId(), leasePrice);
        }
        return res;
    }

    @Override
    public ContractPriceDetailRSP detailVersion(Long contractId, String version) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        ContractPriceDetailRSP res = new ContractPriceDetailRSP();
        // 处理租赁
        ContractLeasePriceLib leasePriceLib = leasePriceLibMapper.selectOne(Wrappers.<ContractLeasePriceLib>lambdaQuery().eq(ContractLeasePriceLib::getContractId, contractId).eq(ContractLeasePriceLib::getVersion, version).last("LIMIT 1"));
        res.setLeasePriceModifyRSP(Optional.ofNullable(leasePriceLib).map(leasePriceLibHandler::actualLib2Rsp).orElse(null));
        // 补充项目定价数据
        ProjPricingBaseInfo projPricingBaseInfo = SpringUtil.getBean(ProjPricingBaseInfoService.class).getPricingByReviewId(contractBaseInfo.getProjReviewId());
        if (Objects.nonNull(projPricingBaseInfo)) {
            ProjPricingPriceDetailRSP pricingPriceDetailRSP = projPricingPriceService.oldDetail(projPricingBaseInfo.getId());
            if (Objects.nonNull(pricingPriceDetailRSP)) {
                res.getLeasePriceModifyRSP().setPricingIrrPercent(pricingPriceDetailRSP.getIrr());
            }
        }
        // 处理保理
        ContractFactoringPriceLib factoringPriceLib = factoringPriceLibMapper.selectOne(Wrappers.<ContractFactoringPriceLib>lambdaQuery().eq(ContractFactoringPriceLib::getContractId, contractId).eq(ContractFactoringPriceLib::getVersion, version).last("LIMIT 1"));
        res.setFactoringPriceRSP(Optional.ofNullable(factoringPriceLib).map(factoringPriceLibHandler::actualLib2Rsp).orElse(null));
        if (Objects.nonNull(res.getFactoringPriceRSP())) {
            // 补充项目评审报价方案数据
//            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
            ProjReviewFactoringPrice projReviewFactoringPrice = projReviewFactoringPriceService.getByProjectId(contractBaseInfo.getProjReviewId());
            Assert.notNull(projReviewFactoringPrice, () -> MithrasException.newException("项目评审报价方案不存在"));
            res.getFactoringPriceRSP().setProjCreditAmount(projReviewFactoringPrice.getApplyCreditAmount());
            res.getFactoringPriceRSP().setProjEarnestMoney(projReviewFactoringPrice.getEarnestMoney());
            res.getFactoringPriceRSP().setProjConsultingFee(projReviewFactoringPrice.getConsultingFee());
            res.getFactoringPriceRSP().setProjDownPayment(0L);
            res.getFactoringPriceRSP().setProjIrrPercent(projReviewFactoringPrice.getIrrPercent());
            // 补充项目定价数据
            if (Objects.nonNull(projPricingBaseInfo)) {
                ProjPricingPriceDetailRSP pricingPriceDetailRSP = projPricingPriceService.oldDetail(projPricingBaseInfo.getId());
                if (Objects.nonNull(pricingPriceDetailRSP)) {
                    res.getFactoringPriceRSP().setPricingIrrPercent(pricingPriceDetailRSP.getIrr());
                }
            }
        }
        // 处理债权转让
        ContractAocPriceLib aocPriceLib = aocPriceLibMapper.selectOne(Wrappers.<ContractAocPriceLib>lambdaQuery().eq(ContractAocPriceLib::getContractId, contractId).eq(ContractAocPriceLib::getVersion, version).last("LIMIT 1"));
        res.setAocPriceRSP(Optional.ofNullable(aocPriceLib).map(aocPriceLibHandler::actualLib2Rsp).orElse(null));
        if (Objects.nonNull(res.getAocPriceRSP())) {
            // 补充项目评审报价方案数据
//            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
            ProjReviewAocPrice projReviewAocPrice = projReviewAocPriceService.getByProjectId(contractBaseInfo.getProjReviewId());
            Assert.notNull(projReviewAocPrice, () -> MithrasException.newException("项目评审报价方案不存在"));
            res.getAocPriceRSP().setProjCreditAmount(projReviewAocPrice.getApplyCreditAmount());
            res.getAocPriceRSP().setProjEarnestMoney(projReviewAocPrice.getEarnestMoney());
            res.getAocPriceRSP().setProjConsultingFee(projReviewAocPrice.getConsultingFee());
            res.getAocPriceRSP().setProjDownPayment(0L);
            res.getAocPriceRSP().setProjIrrPercent(projReviewAocPrice.getIrrPercent());
            // 补充项目定价数据
            if (Objects.nonNull(projPricingBaseInfo)) {
                ProjPricingPriceDetailRSP pricingPriceDetailRSP = projPricingPriceService.oldDetail(projPricingBaseInfo.getId());
                if (Objects.nonNull(pricingPriceDetailRSP)) {
                    res.getAocPriceRSP().setPricingIrrPercent(pricingPriceDetailRSP.getIrr());
                }
            }
        }
        return res;
    }

    @Override
    public void saveIrr(Long contractId, Integer irr) {
        ContractBaseInfo contractBaseInfo = baseInfoService.getById(contractId);
        Assert.notNull(contractBaseInfo, () -> MithrasException.newException("合同不存在"));
        if (Objects.equals(ProjectBizType.BL.name(), contractBaseInfo.getBizType())) {
            ContractFactoringPrice contractFactoringPrice = factoringPriceService.getByContractId(contractId);
            Assert.notNull(contractFactoringPrice, () -> MithrasException.newException("报价方案不存在"));
//            ProjReviewFactoringPrice projReviewFactoringPrice = projReviewFactoringPriceService.getByProjectId(contractBaseInfo.getProjReviewId());
//            if (Objects.nonNull(projReviewFactoringPrice) && Objects.nonNull(projReviewFactoringPrice.getIrrPercent())) {
//                Assert.isTrue(irr >= projReviewFactoringPrice.getIrrPercent(), () -> MithrasException.newException("IRR必须大于等于项目中的IRR"));
//            }
            contractFactoringPrice.setIrrPercent(irr);
            factoringPriceService.updateById(contractFactoringPrice);
        } else if (Objects.equals(ProjectBizType.ZR.name(), contractBaseInfo.getBizType())) {
            ContractAocPrice contractAocPrice = aocPriceService.getByContractId(contractId);
            Assert.notNull(contractAocPrice, () -> MithrasException.newException("报价方案不存在"));
//            ProjReviewAocPrice projReviewAocPrice = projReviewAocPriceService.getByProjectId(contractBaseInfo.getProjReviewId());
//            if (Objects.nonNull(projReviewAocPrice) && Objects.nonNull(projReviewAocPrice.getIrrPercent())) {
//                Assert.isTrue(irr >= projReviewAocPrice.getIrrPercent(), () -> MithrasException.newException("IRR必须大于等于项目中的IRR"));
//            }
            contractAocPrice.setIrrPercent(irr);
            aocPriceService.updateById(contractAocPrice);
        } else {
            ContractLeasePrice contractLeasePrice = leasePriceService.getByContractId(contractId);
            Assert.notNull(contractLeasePrice, () -> MithrasException.newException("报价方案不存在"));
//            ProjReviewLeasePrice projReviewLeasePrice = projReviewLeasePriceService.getByProjectId(contractBaseInfo.getProjReviewId());
//            if (Objects.nonNull(projReviewLeasePrice) && Objects.nonNull(projReviewLeasePrice.getIrrPercent())) {
//                Assert.isTrue(irr >= projReviewLeasePrice.getIrrPercent(), () -> MithrasException.newException("IRR必须大于等于项目中的IRR"));
//            }
            contractLeasePrice.setIrrPercent(irr);
            leasePriceService.updateById(contractLeasePrice);
        }
    }

    @Override
    public List<ContractPrice> queryNewestPrice(Set<Long> contractIds) {
        List<ContractPrice> prices = new ArrayList<>();
        if (ObjectUtil.isEmpty(contractIds)) {
            return prices;
        }
        prices.addAll(contractAocPriceLibService.queryNewestLib(contractIds));
        prices.addAll(contractFactoringPriceLibService.queryNewestLib(contractIds));
        prices.addAll(leasePriceLibService.queryNewestLib(contractIds));
        return prices;
    }

    @Override
    public Map<Long, Integer> queryNewestIrr(Set<Long> contractIds) {
        return queryNewestPrice(contractIds).stream().filter(priceLib -> priceLib.getIrrPercent() != null)
                .collect(Collectors.toMap(ContractPrice::getContractId, ContractPrice::getIrrPercent));
    }

    @Override
    public Map<Long, Integer> queryNewestReceiptIrr(Set<Long> receiptIds) {
        if (ObjectUtil.isEmpty(receiptIds)) {
            return MapUtil.empty();
        }
        Map<Long, Integer> result = new HashMap<>();
        List<ContractReceiptLib> receiptLibList = SpringUtil.getBean(ContractReceiptMapper.class).queryNewestReceiptIrr(receiptIds);
        if (CollUtil.isNotEmpty(receiptLibList)) {
            result.putAll(receiptLibList.stream()
                    .filter(receipt -> ObjectUtil.isNotEmpty(receipt.getActualIrr()))
                    .collect(Collectors.toMap(ContractReceiptLib::getOriginId, ContractReceiptLib::getActualIrr, (k1, k2) -> k1)));
        }
        // 如果数据够了不再去找概算irr
        if (Objects.equals(result.size(), receiptIds.size())) {
            return result;
        }
        // 根据借据ID去找合同的概算irr
        List<Long> needSelectIds = receiptIds.stream().filter(receiptId -> !result.containsKey(receiptId)).collect(Collectors.toList());
        List<ContractReceipt> contractReceipts = SpringUtil.getBean(ContractReceiptMapper.class).selectBatchIds(needSelectIds);
        Map<Long, Long> map = contractReceipts.stream().collect(Collectors.toMap(ContractReceipt::getId, ContractReceipt::getContractId));
        // 合同报价方案
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.listByIds(contractReceipts.stream().map(ContractReceipt::getContractId).distinct().collect(Collectors.toList()));
        Map<Long, Integer> tmpMap = new HashMap<>();
        if (CollUtil.isNotEmpty(contractBaseInfos)) {
            Map<String, List<ContractBaseInfo>> collect = contractBaseInfos.stream().collect(Collectors.groupingBy(ContractBaseInfo::getBizType));
            for (Map.Entry<String, List<ContractBaseInfo>> entry : collect.entrySet()) {
                if (CharSequenceUtil.equalsAny(entry.getKey(), ProjectBizType.ZL.name(), ProjectBizType.ZZ.name())) {
                    Map<Long, Integer> integerMap = leasePriceService.listByContractIds(entry.getValue().stream().map(ContractBaseInfo::getId).distinct().collect(Collectors.toList()))
                            .stream().collect(Collectors.toMap(ContractLeasePrice::getContractId, ContractLeasePrice::getIrrPercent, (v1, v2) -> v1));
                    tmpMap.putAll(integerMap);
                } else if (ProjectBizType.BL.name().equals(entry.getKey())) {
                    Map<Long, Integer> integerMap = factoringPriceService.listByContractIds(entry.getValue().stream().map(ContractBaseInfo::getId).distinct().collect(Collectors.toList()))
                            .stream().collect(Collectors.toMap(ContractFactoringPrice::getContractId, ContractFactoringPrice::getIrrPercent, (v1, v2) -> v1));
                    tmpMap.putAll(integerMap);
                } else if (ProjectBizType.ZR.name().equals(entry.getKey())) {
                    Map<Long, Integer> integerMap = aocPriceService.listByContractIds(entry.getValue().stream().map(ContractBaseInfo::getId).distinct().collect(Collectors.toList()))
                            .stream().collect(Collectors.toMap(ContractAocPrice::getContractId, ContractAocPrice::getIrrPercent, (v1, v2) -> v1));
                    tmpMap.putAll(integerMap);
                }
            }
        }

        // 遍历需要填充的借据ids
        for (Long receiptId : needSelectIds) {
            Long contractId = map.get(receiptId);
            if (ObjectUtil.isNotEmpty(contractId)) {
                Integer irrPercent = tmpMap.get(contractId);
                if (ObjectUtil.isNotEmpty(irrPercent)) {
                    result.put(receiptId, irrPercent);
                }
            }
        }
        // 理论上传进来的数据都有数据返回，不然就是有BUG
        return result;
    }

    @Override
    public Map<Long, Pair<Long, Integer>> queryNewestRate(Set<Long> contractIds) {
        return queryNewestPrice(contractIds).stream()
                .filter(priceLib -> priceLib.getLprPercent() != null && priceLib.getLprAddPercent() != null)
                .collect(Collectors.toMap(ContractPrice::getContractId,
                        item -> Pair.of(item.getContractAmount(), item.getLprPercent() + item.getLprAddPercent())));
    }

    @Override
    public Map<Long, Pair<Long, Integer>> queryNewestIrrRate(Set<Long> contractIds) {
        return queryNewestPrice(contractIds).stream()
                .filter(lib -> lib.getIrrPercent() != null)
                .collect(Collectors.toMap(ContractPrice::getContractId,
                        item -> Pair.of(item.getContractAmount(), item.getIrrPercent())));
    }

    @Override
    public Map<Long, Long> queryNewestContractAmount(Set<Long> contractIds) {
        return queryNewestPrice(contractIds).stream().filter(lib -> lib.getContractAmount() != null)
                .collect(Collectors.toMap(ContractPrice::getContractId, ContractPrice::getContractAmount));
    }

    @Override
    public Map<Long, Long> projUseApplyCreditAmount(List<Long> projReviewIds) {
        //查询所有非关闭 作废合同
        List<ContractBaseInfo> contractBaseInfos = baseInfoService.listByProjReviewIds(projReviewIds);
        if (ObjectUtil.isEmpty(contractBaseInfos)) {
            return MapUtil.empty();
        }
        Map<Long, List<ContractBaseInfo>> projReviewMap = contractBaseInfos.stream().collect(Collectors.groupingBy(ContractBaseInfo::getProjReviewId));
        Map<Long, Long> projReviewAmountMap = new HashMap<>();
        projReviewMap.forEach((k, v) -> {
            projReviewAmountMap.put(k, v.stream().map(ContractBaseInfo::getApplyCreditAmount).map(LongUtil::null2zero).reduce(Long::sum).orElse(null));
        });
        return projReviewAmountMap;
    }
}
