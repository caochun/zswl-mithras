package cn.zswltech.mithras.others.hand.extract.contract;

import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contract.JointGuaranteeMarkEnum;
import cn.zswltech.mithras.contract.enums.contract.LPRTypeEnum;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RateType;
import cn.zswltech.mithras.others.service.mapper.FixDataMapper;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.mapper.CollectionRecordInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.*;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.*;
import cn.zswltech.mithras.payment.mapper.lib.PaymentBaseInfoLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projestablish.ProjEstablishBaseInfoLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projestablish.ProjEstablishLeasePriceLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projreview.ProjReviewBaseInfoLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projreview.ProjReviewLeasePriceLibMapper;
import cn.zswltech.mithras.margin.mapper.MarginBaseInfoMapper;
import cn.zswltech.mithras.margin.mapper.MarginRecordInfoMapper;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.mapper.model.CollectionRecordInfo;
import cn.zswltech.mithras.contract.model.contract.*;
import cn.zswltech.mithras.margin.mapper.model.MarginBaseInfo;
import cn.zswltech.mithras.margin.mapper.model.MarginRecordInfo;
import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfoLib;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfoLib;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishLeasePrice;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishLeasePriceLib;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfoLib;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewLeasePriceLib;
import cn.zswltech.mithras.payment.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishLeasePriceMapper;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewLeasePriceMapper;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.contract.core.application.ContractMortgageService;
import cn.zswltech.mithras.contract.core.application.ContractPledgeService;
import cn.zswltech.mithras.contract.archive.handler.impl.ContractMortgageItemLibHandler;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractMortgageLibHandler;
import cn.zswltech.mithras.contract.archive.handler.impl.ContractPledgeItemLibHandler;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractPledgeLibHandler;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.junit.Test;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.hutool.core.text.CharSequenceUtil.join;
import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.zswltech.mithras.others.hand.extract.ImportCommonHelper.extractNumber;
import static java.math.RoundingMode.HALF_UP;

/**
 * 数据修复
 *
 * @author wangchuanhao
 * @date 2022/10/18 4:01 PM
 */
public class ContractDataFix extends ApplicationTest {

    private static Pattern seqPattern = Pattern.compile("\\(([^}]*)\\)");
    private static Pattern yearPattern = Pattern.compile("(.*)【(.*)】(.*)");

    @Resource
    private ContractLeasePriceMapper contractLeasePriceMapper;
    @Resource
    private ContractLeasePriceLibMapper contractLeasePriceLibMapper;
    @Resource
    private ContractRentActualMapper contractRentActualMapper;
    @Resource
    private ContractRentActualLibMapper contractRentActualLibMapper;
    @Resource
    private ContractRentEstimateMapper contractRentEstimateMapper;
    @Resource
    private ContractRentEstimateLibMapper contractRentEstimateLibMapper;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private CollectionRecordInfoMapper collectionRecordInfoMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private PaymentBaseInfoLibMapper paymentBaseInfoLibMapper;
    @Resource
    private PaymentActualDetailMapper paymentActualDetailMapper;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ContractBaseInfoLibMapper contractBaseInfoLibMapper;
    @Resource
    private MarginBaseInfoMapper marginBaseInfoMapper;
    @Resource
    private MarginRecordInfoMapper marginRecordInfoMapper;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private ProjReviewBaseInfoLibMapper projReviewBaseInfoLibMapper;
    @Resource
    private ProjReviewLeasePriceMapper projReviewLeasePriceMapper;
    @Resource
    private ContractReceiptMapper contractReceiptMapper;
    @Resource
    private ContractReceiptLibMapper contractReceiptLibMapper;
    @Resource
    private ContractGuarantorMapper contractGuarantorMapper;
    @Resource
    private ContractGuarantorLibMapper contractGuarantorLibMapper;
    @Resource
    private ProjReviewLeasePriceLibMapper projReviewLeasePriceLibMapper;
    @Resource
    private ProjEstablishLeasePriceMapper projEstablishLeasePriceMapper;
    @Resource
    private ProjEstablishLeasePriceLibMapper projEstablishLeasePriceLibMapper;
    @Resource
    private ProjEstablishBaseInfoMapper projEstablishBaseInfoMapper;
    @Resource
    private ProjEstablishBaseInfoLibMapper projEstablishBaseInfoLibMapper;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private FixDataMapper fixDataMapper;
    @Resource
    private ContractMortgageService contractMortgageService;
    @Resource
    private CommonVersionMapper commonVersionMapper;
    @Resource
    private ContractMortgageMapper contractMortgageMapper;
    @Resource
    private ContractMortgageLibMapper contractMortgageLibMapper;
    @Resource
    private ContractMortgageItemMapper contractMortgageItemMapper;
    @Resource
    private ContractMortgageItemLibMapper contractMortgageItemLibMapper;
    @Resource
    private ContractMortgageLibHandler contractMortgageLibHandler;
    @Resource
    private ContractMortgageItemLibHandler contractMortgageItemLibHandler;
    @Resource
    private ContractPledgeMapper contractPledgeMapper;
    @Resource
    private ContractPledgeLibMapper contractPledgeLibMapper;
    @Resource
    private ContractPledgeItemMapper contractPledgeItemMapper;
    @Resource
    private ContractPledgeItemLibMapper contractPledgeItemLibMapper;
    @Resource
    private ContractPledgeLibHandler contractPledgeLibHandler;
    @Resource
    private ContractPledgeItemLibHandler contractPledgeItemLibHandler;
    @Resource
    private ContractPledgeService contractPledgeService;
    @Resource
    private CreditReportDataFix creditReportDataFix;


    /**
     * 修复金额精度，产品说都保留2位小数
     */
    @Test
    public void fixAmountScale() {
        List<ContractLeasePrice> contractLeasePriceList = contractLeasePriceMapper.selectList(Wrappers.lambdaQuery());
        contractLeasePriceList.forEach(c -> {
            c.setApplyCreditAmount(fixAmount2Scale(c.getApplyCreditAmount()));
            c.setProjCreditAmount(fixAmount2Scale(c.getProjCreditAmount()));
            c.setEarnestMoney(fixAmount2Scale(c.getEarnestMoney()));
            c.setProjEarnestMoney(fixAmount2Scale(c.getProjEarnestMoney()));
            c.setDownPayment(fixAmount2Scale(c.getDownPayment()));
            c.setProjDownPayment(fixAmount2Scale(c.getProjDownPayment()));
            c.setConsultingFee(fixAmount2Scale(c.getConsultingFee()));
            c.setProjConsultingFee(fixAmount2Scale(c.getProjConsultingFee()));
            c.setNominalPrice(fixAmount2Scale(c.getNominalPrice()));
            c.setLeaseRatePercent(fixAmount2Scale(c.getLeaseRatePercent()));
            c.setLprPercent(fixAmount2Scale(c.getLprPercent()));
            c.setLprAddPercent(fixAmount2Scale(c.getLprAddPercent()));
            c.setDefaultInterestRate(fixAmount2Scale(c.getDefaultInterestRate()));
            c.setIrrPercent(fixAmount2Scale(c.getIrrPercent()));
            c.setProjIrrPercent(fixAmount2Scale(c.getProjIrrPercent()));
            contractLeasePriceMapper.updateById(c);
        });
        List<ContractLeasePriceLib> contractLeasePriceLibList = contractLeasePriceLibMapper.selectList(Wrappers.lambdaQuery());
        contractLeasePriceLibList.forEach(c -> {
            c.setApplyCreditAmount(fixAmount2Scale(c.getApplyCreditAmount()));
            c.setProjCreditAmount(fixAmount2Scale(c.getProjCreditAmount()));
            c.setEarnestMoney(fixAmount2Scale(c.getEarnestMoney()));
            c.setProjEarnestMoney(fixAmount2Scale(c.getProjEarnestMoney()));
            c.setDownPayment(fixAmount2Scale(c.getDownPayment()));
            c.setProjDownPayment(fixAmount2Scale(c.getProjDownPayment()));
            c.setConsultingFee(fixAmount2Scale(c.getConsultingFee()));
            c.setProjConsultingFee(fixAmount2Scale(c.getProjConsultingFee()));
            c.setNominalPrice(fixAmount2Scale(c.getNominalPrice()));
            c.setLeaseRatePercent(fixAmount2Scale(c.getLeaseRatePercent()));
            c.setLprPercent(fixAmount2Scale(c.getLprPercent()));
            c.setLprAddPercent(fixAmount2Scale(c.getLprAddPercent()));
            c.setDefaultInterestRate(fixAmount2Scale(c.getDefaultInterestRate()));
            c.setIrrPercent(fixAmount2Scale(c.getIrrPercent()));
            c.setProjIrrPercent(fixAmount2Scale(c.getProjIrrPercent()));
            contractLeasePriceLibMapper.updateById(c);
        });

        List<ContractRentActual> contractRentActualList = contractRentActualMapper.selectList(Wrappers.lambdaQuery());
        contractRentActualList.forEach(c -> {
            c.setRent(fixAmount2Scale(c.getRent()));
            c.setPrincipal(fixAmount2Scale(c.getPrincipal()));
            c.setInterest(fixAmount2Scale(c.getInterest()));
            c.setRemainingPrincipal(fixAmount2Scale(c.getRemainingPrincipal()));
            contractRentActualMapper.updateById(c);
        });
        List<ContractRentActualLib> contractRentActualLibList = contractRentActualLibMapper.selectList(Wrappers.lambdaQuery());
        contractRentActualLibList.forEach(c -> {
            c.setRent(fixAmount2Scale(c.getRent()));
            c.setPrincipal(fixAmount2Scale(c.getPrincipal()));
            c.setInterest(fixAmount2Scale(c.getInterest()));
            c.setRemainingPrincipal(fixAmount2Scale(c.getRemainingPrincipal()));
            contractRentActualLibMapper.updateById(c);
        });

        List<ContractRentEstimate> contractRentEstimateList = contractRentEstimateMapper.selectList(Wrappers.lambdaQuery());
        contractRentEstimateList.forEach(c -> {
            c.setRent(fixAmount2Scale(c.getRent()));
            c.setPrincipal(fixAmount2Scale(c.getPrincipal()));
            c.setInterest(fixAmount2Scale(c.getInterest()));
            c.setRemainingPrincipal(fixAmount2Scale(c.getRemainingPrincipal()));
            contractRentEstimateMapper.updateById(c);
        });
        List<ContractRentEstimateLib> contractRentEstimateLibList = contractRentEstimateLibMapper.selectList(Wrappers.lambdaQuery());
        contractRentEstimateLibList.forEach(c -> {
            c.setRent(fixAmount2Scale(c.getRent()));
            c.setPrincipal(fixAmount2Scale(c.getPrincipal()));
            c.setInterest(fixAmount2Scale(c.getInterest()));
            c.setRemainingPrincipal(fixAmount2Scale(c.getRemainingPrincipal()));
            contractRentEstimateLibMapper.updateById(c);
        });

        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoMapper.selectList(Wrappers.lambdaQuery());
        collectionBaseInfoList.forEach(c -> {
            c.setCollectionAmount(fixAmount2Scale(c.getCollectionAmount()));
            c.setPrincipal(fixAmount2Scale(c.getPrincipal()));
            c.setInterest(fixAmount2Scale(c.getInterest()));
            c.setPenaltyInterest(fixAmount2Scale(c.getPenaltyInterest()));
            c.setPenaltyInterestDeductionAmount(fixAmount2Scale(c.getPenaltyInterestDeductionAmount()));
            c.setPlanCollectionAmount(fixAmount2Scale(c.getPlanCollectionAmount()));
            c.setCashFlowAmount(fixAmount2Scale(c.getCashFlowAmount()));
            c.setCollectionPrincipal(fixAmount2Scale(c.getCollectionPrincipal()));
            c.setCollectionInterest(fixAmount2Scale(c.getCollectionInterest()));
            c.setCollectionPenaltyInterest(fixAmount2Scale(c.getCollectionPenaltyInterest()));
            collectionBaseInfoMapper.updateById(c);
        });

        List<CollectionRecordInfo> collectionRecordInfoList = collectionRecordInfoMapper.selectList(Wrappers.lambdaQuery());
        collectionRecordInfoList.forEach(c -> {
            c.setCollectionAmount(fixAmount2Scale(c.getCollectionAmount()));
            c.setPrincipal(fixAmount2Scale(c.getPrincipal()));
            c.setInterest(fixAmount2Scale(c.getInterest()));
            c.setPenaltyInterest(fixAmount2Scale(c.getPenaltyInterest()));
            collectionRecordInfoMapper.updateById(c);
        });

        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoMapper.selectList(Wrappers.lambdaQuery());
        paymentBaseInfoList.forEach(c -> {
            c.setApplyPaymentAmount(fixAmount2Scale(c.getApplyPaymentAmount()));
            c.setEarnestMoney(fixAmount2Scale(c.getEarnestMoney()));
            c.setDownPayment(fixAmount2Scale(c.getDownPayment()));
            c.setConsultingFee(fixAmount2Scale(c.getConsultingFee()));
            c.setNominalPrice(fixAmount2Scale(c.getNominalPrice()));
            c.setConApplyCreditAmount(fixAmount2Scale(c.getConApplyCreditAmount()));
            paymentBaseInfoMapper.updateById(c);
        });
        List<PaymentBaseInfoLib> paymentBaseInfoLibList = paymentBaseInfoLibMapper.selectList(Wrappers.lambdaQuery());
        paymentBaseInfoLibList.forEach(c -> {
            c.setApplyPaymentAmount(fixAmount2Scale(c.getApplyPaymentAmount()));
            c.setEarnestMoney(fixAmount2Scale(c.getEarnestMoney()));
            c.setDownPayment(fixAmount2Scale(c.getDownPayment()));
            c.setConsultingFee(fixAmount2Scale(c.getConsultingFee()));
            c.setNominalPrice(fixAmount2Scale(c.getNominalPrice()));
            c.setConApplyCreditAmount(fixAmount2Scale(c.getConApplyCreditAmount()));
            paymentBaseInfoLibMapper.updateById(c);
        });

        List<PaymentActualDetail> paymentActualDetailList = paymentActualDetailMapper.selectList(Wrappers.lambdaQuery());
        paymentActualDetailList.forEach(c -> {
            c.setPaidInAmount(fixAmount2Scale(c.getPaidInAmount()));
            paymentActualDetailMapper.updateById(c);
        });

        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoMapper.selectList(Wrappers.lambdaQuery());
        contractBaseInfoList.forEach(c -> {
            c.setApplyCreditAmount(fixAmount2Scale(c.getApplyCreditAmount()));
            c.setRemainAvailableQuota(fixAmount2Scale(c.getRemainAvailableQuota()));
            contractBaseInfoMapper.updateById(c);
        });

        List<ContractBaseInfoLib> contractBaseInfoLibList = contractBaseInfoLibMapper.selectList(Wrappers.lambdaQuery());
        contractBaseInfoLibList.forEach(c -> {
            c.setApplyCreditAmount(fixAmount2Scale(c.getApplyCreditAmount()));
            c.setRemainAvailableQuota(fixAmount2Scale(c.getRemainAvailableQuota()));
            contractBaseInfoLibMapper.updateById(c);
        });

        List<MarginBaseInfo> marginBaseInfoList = marginBaseInfoMapper.selectList(Wrappers.lambdaQuery());
        marginBaseInfoList.forEach(c -> {
            c.setCollectionAmount(fixAmount2Scale(c.getCollectionAmount()));
            c.setBackAmount(fixAmount2Scale(c.getBackAmount()));
            c.setDeductAmount(fixAmount2Scale(c.getDeductAmount()));
            c.setPlanMarginAmount(fixAmount2Scale(c.getPlanMarginAmount()));
            marginBaseInfoMapper.updateById(c);
        });
        List<MarginRecordInfo> marginRecordInfoList = marginRecordInfoMapper.selectList(Wrappers.lambdaQuery());
        marginRecordInfoList.forEach(c -> {
            c.setCollectionAmount(fixAmount2Scale(c.getCollectionAmount()));
            c.setDeductPrincipal(fixAmount2Scale(c.getDeductPrincipal()));
            c.setDeductInterest(fixAmount2Scale(c.getDeductInterest()));
            c.setDeductPenaltyInterest(fixAmount2Scale(c.getDeductPenaltyInterest()));
            c.setDeductRent(fixAmount2Scale(c.getDeductRent()));
            marginRecordInfoMapper.updateById(c);
        });

        List<ProjReviewLeasePrice> projReviewLeasePriceList = projReviewLeasePriceMapper.selectList(Wrappers.lambdaQuery());
        projReviewLeasePriceList.forEach(c -> {
            c.setApplyCreditAmount(fixAmount2Scale(c.getApplyCreditAmount()));
            c.setEarnestMoney(fixAmount2Scale(c.getEarnestMoney()));
            c.setDownPayment(fixAmount2Scale(c.getDownPayment()));
            c.setConsultingFee(fixAmount2Scale(c.getConsultingFee()));
            c.setNominalPrice(fixAmount2Scale(c.getNominalPrice()));
            c.setLeaseRatePercent(fixAmount2Scale(c.getLeaseRatePercent()));
            c.setIrrPercent(fixAmount2Scale(c.getIrrPercent()));
            projReviewLeasePriceMapper.updateById(c);
        });
        List<ProjReviewLeasePriceLib> projReviewLeasePriceLibList = projReviewLeasePriceLibMapper.selectList(Wrappers.lambdaQuery());
        projReviewLeasePriceLibList.forEach(c -> {
            c.setApplyCreditAmount(fixAmount2Scale(c.getApplyCreditAmount()));
            c.setEarnestMoney(fixAmount2Scale(c.getEarnestMoney()));
            c.setDownPayment(fixAmount2Scale(c.getDownPayment()));
            c.setConsultingFee(fixAmount2Scale(c.getConsultingFee()));
            c.setNominalPrice(fixAmount2Scale(c.getNominalPrice()));
            c.setLeaseRatePercent(fixAmount2Scale(c.getLeaseRatePercent()));
            c.setIrrPercent(fixAmount2Scale(c.getIrrPercent()));
            projReviewLeasePriceLibMapper.updateById(c);
        });

        List<ProjEstablishLeasePrice> projEstablishLeasePriceList = projEstablishLeasePriceMapper.selectList(Wrappers.lambdaQuery());
        projEstablishLeasePriceList.forEach(c -> {
            c.setApplyCreditAmount(fixAmount2Scale(c.getApplyCreditAmount()));
            c.setEarnestMoney(fixAmount2Scale(c.getEarnestMoney()));
            c.setDownPayment(fixAmount2Scale(c.getDownPayment()));
            c.setConsultingFee(fixAmount2Scale(c.getConsultingFee()));
            c.setNominalPrice(fixAmount2Scale(c.getNominalPrice()));
            c.setLeaseRatePercent(fixAmount2Scale(c.getLeaseRatePercent()));
            c.setIrrPercent(fixAmount2Scale(c.getIrrPercent()));
            projEstablishLeasePriceMapper.updateById(c);
        });

        List<ProjEstablishLeasePriceLib> projEstablishLeasePriceLibList = projEstablishLeasePriceLibMapper.selectList(Wrappers.lambdaQuery());
        projEstablishLeasePriceLibList.forEach(c -> {
            c.setApplyCreditAmount(fixAmount2Scale(c.getApplyCreditAmount()));
            c.setEarnestMoney(fixAmount2Scale(c.getEarnestMoney()));
            c.setDownPayment(fixAmount2Scale(c.getDownPayment()));
            c.setConsultingFee(fixAmount2Scale(c.getConsultingFee()));
            c.setNominalPrice(fixAmount2Scale(c.getNominalPrice()));
            c.setLeaseRatePercent(fixAmount2Scale(c.getLeaseRatePercent()));
            c.setIrrPercent(fixAmount2Scale(c.getIrrPercent()));
            projEstablishLeasePriceLibMapper.updateById(c);
        });
    }

    /**
     * 修复名义价款的核销信息
     * 非结清的合同 名义价款核销信息不做处理
     */
    @Test
    public void fixNominalPrice() {
        Set<String> needHandleContractCodeSet = new HashSet<>(Arrays.asList("浙商租【2021】租字第(CF-0005)号", "浙商租【2021】租字第(CF-0003)号", "浙商租【2021】租字第(CF-0002)号", "浙商租【2021】租字第(CF-0001)号"));
        List<CollectionBaseInfo> nominalCollectionList = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.NOMINAL_PRICE.name())
                .in(CollectionBaseInfo::getContractCode, needHandleContractCodeSet)
        );
        nominalCollectionList.forEach(c -> {
            ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(c.getContractId());
            LambdaUpdateWrapper<CollectionBaseInfo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(CollectionBaseInfo::getId, c.getId());
            if (!ContractStatus.SETTLE.name().equals(contractBaseInfo.getContractStatus())) {
                updateWrapper.set(CollectionBaseInfo::getCollectionDate, null)
                        .set(CollectionBaseInfo::getCollectionAmount, null)
                        .set(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.UNCOLLECTION.name());
                collectionRecordInfoMapper.delete(Wrappers.<CollectionRecordInfo>lambdaQuery().eq(CollectionRecordInfo::getCollectionId, c.getId()));
            }
            // 期项取实际租金表最后一期
            ContractRentActual latestRentActual = contractRentActualMapper.selectOne(Wrappers.<ContractRentActual>lambdaQuery()
                    .eq(ContractRentActual::getContractId, c.getContractId())
                    .orderByDesc(ContractRentActual::getCashFlowPhase)
                    .last("LIMIT 1")
            );
            updateWrapper.set(CollectionBaseInfo::getPhase, latestRentActual.getCashFlowPhase());
            collectionBaseInfoMapper.update(c, updateWrapper);
        });
    }

    /**
     * 已结清 处理名义货价
     */
    @Test
    public void settleNominalPrice() {
        Set<String> needHandleContractCodeSet = new HashSet<>(Arrays.asList("浙商租【2021】租字第(A-0022)号"));
        List<CollectionBaseInfo> nominalCollectionList = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.NOMINAL_PRICE.name())
                .in(CollectionBaseInfo::getContractCode, needHandleContractCodeSet)
        );
        nominalCollectionList.forEach(c -> {
            ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(c.getContractId());
            LambdaUpdateWrapper<CollectionBaseInfo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(CollectionBaseInfo::getId, c.getId());
            if (ContractStatus.SETTLE.name().equals(contractBaseInfo.getContractStatus())) {
                updateWrapper.set(CollectionBaseInfo::getCollectionDate, c.getPlanCollectionDate())
                        .set(CollectionBaseInfo::getCollectionAmount, c.getPlanCollectionAmount())
                        .set(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name());
//                collectionRecordInfoMapper.delete(Wrappers.<CollectionRecordInfo>lambdaQuery().eq(CollectionRecordInfo::getCollectionId, c.getId()));
                // 新增一条操作记录
                CollectionRecordInfo recordInfo = ContractPaymentImporterHelper.buildCollectionRecord(c);
                recordInfo.setCollectionDate(c.getPlanCollectionDate());
                recordInfo.setCollectionAmount(c.getPlanCollectionAmount());
                collectionRecordInfoMapper.insert(recordInfo);
            }
            collectionBaseInfoMapper.update(c, updateWrapper);
        });
    }

    /**
     * 测试wrapper用法
     */
    @Test
    public void test() {
        LambdaUpdateWrapper<ContractBaseInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ContractBaseInfo::getId, 51L);
        updateWrapper.set(ContractBaseInfo::getUpdateTime, null);
//        UpdateWrapper updateWrapper = new UpdateWrapper();
//        updateWrapper.eq("id", 51L);
//        updateWrapper.set("update_time", null);
        contractBaseInfoMapper.update(null, updateWrapper);
    }


    public static Long fixAmount2Scale(Long originAmount) {
        return Optional.ofNullable(originAmount).map(o -> new BigDecimal(o).divide(new BigDecimal(10000L), 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(10000L))
                .longValue()).orElse(null);
    }

    public static Integer fixAmount2Scale(Integer originAmount) {
        return Optional.ofNullable(originAmount).map(o -> new BigDecimal(o).divide(new BigDecimal(10000L), 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(10000L))
                .intValue()).orElse(null);
    }

    public static void main(String[] args) {
        Set<String> actualCodeSet = new HashSet<>(Arrays.asList("浙商租【2021】租字第(A-0021)号", "中拓租【2019】租字第(A-0005)号", "浙商租【2022】租字第(A-0053)号-HZ", "浙商租【2022】租字第(A-0032)号", "浙商租【2022】租字第(A-0008)号", "浙商租【2022】租字第(A-0026)号", "浙商租【2021】租字第(JLG-0001)号", "浙商租【2022】租字第(A-0054)号", "浙商租【2022】租字第(A-0030)号", "浙商租【2021】租字第(A-0009)号", "浙商租【2022】租字第(A-0021)号", "浙商租【2022】租字第(A-0022)号", "浙商租【2022】租字第(A-0041)号", "浙商租【2022】租字第(A-0027)号", "浙商租【2022】租字第(A-0028)号", "浙商租【2022】租字第(A-0007)号", "浙商租【2022】租字第(A-0033)号", "中拓租【2020】租字第(A-0023)号", "中拓租【2019】租字第(A-0008)号", "浙商租【2022】租字第(A-0005)号", "浙商租【2022】租字第(A-0006)号", "浙商租【2022】租字第(A-0040)号", "浙商租【2022】租字第(C-0005)号"));
        ExcelReader tzExcelReader = ExcelUtil.getReader(new File("/Users/wang/Desktop/20220831-融资租赁业务合同情况总表20220831-王传昊整理(2).xlsx"));
        Set<String> sheetNameSheet = new HashSet<>(tzExcelReader.getSheetNames());
        tzExcelReader.setSheet("业务合同情况汇总表");
        List<List<Object>> contractSheetList = tzExcelReader.read();
        for (List<Object> rowData : contractSheetList) {
            if (rowData.size() > 8
                    && rowData.get(8) instanceof String
                    && ((String) rowData.get(8)).contains("号")
            ) {
                String actualContractCode = ((String) rowData.get(8)).trim().replaceAll("（", "(").replaceAll("）", ")");
                if (!actualCodeSet.contains(actualContractCode)) {
                    continue;
                }
                tzExcelReader.setSheet(((String) rowData.get(14)).replaceAll("（", "(").replaceAll("）", ")"));
                List<List<Object>> detailSheetDataList = tzExcelReader.read();
                if (detailSheetDataList.size() < 6) {
                    log.info("该模块取不到数据:{}", actualContractCode);
                    continue;
                }
                for (int rentRow = 6; ; rentRow++) {
                    if ("合计".equals(detailSheetDataList.get(rentRow).get(11))) {
                        break;
                    }
                    // 如果有收款日期 且应收逾期利息或已收逾期利息有值 拉出来打印
                    if (filterNotBlank(detailSheetDataList.get(rentRow).get(19))
                            && (filterNotBlank(detailSheetDataList.get(rentRow).get(31)) || filterNotBlank(detailSheetDataList.get(rentRow).get(32)))) {
                        log.info("该合同编号有问题:{}", actualContractCode);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 修复罚息逻辑
     */
    @Test
    public void fixPenaltyInterest() {
        Set<String> needFixContractCodeSet = new HashSet<>(Arrays.asList("中拓租【2019】租字第(A-0013)号", "中拓租【2019】租字第(A-0014)号", "中拓租【2020】租字第(A-0012)号", "浙商租【2021】租字第(A-0016)号", "浙商租【2021】租字第(XB-0001)号", "浙商租【2021】租字第(XB-0002)号", "浙商租【2021】租字第(A-0068)号"));
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery().in(ContractBaseInfo::getContractCode, needFixContractCodeSet));
        Map<Long, ContractBaseInfo> contractBaseInfoMap = contractBaseInfoList.stream().collect(Collectors.toMap(ContractBaseInfo::getId, c -> c));
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery().eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name()).in(CollectionBaseInfo::getContractId, contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList())));
        Map<Long, List<CollectionBaseInfo>> collectionBaseInfoMap = collectionBaseInfoList.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));
        Map<Long, List<CollectionRecordInfo>> collectionRecordInfoMap = collectionRecordInfoMapper.selectList(Wrappers.<CollectionRecordInfo>lambdaQuery().in(CollectionRecordInfo::getCollectionId, collectionBaseInfoList.stream().map(CollectionBaseInfo::getId).collect(Collectors.toList()))).stream().collect(Collectors.groupingBy(CollectionRecordInfo::getCollectionId));

        ExcelReader tzExcelReader = ExcelUtil.getReader(new File("/Users/wang/Desktop/20220831-融资租赁业务合同情况总表20220831-王传昊整理.xlsx"));
        tzExcelReader.setSheet("业务合同情况汇总表");
        List<List<Object>> contractSheetList = tzExcelReader.read();
        JSONObject excelDataObj = new JSONObject();
        for (List<Object> rowData : contractSheetList) {
            if (rowData.size() > 5
                    && rowData.get(5) instanceof String
                    && ((String) rowData.get(5)).contains("号")
            ) {
                String actualContractCode = ((String) rowData.get(8)).trim().replaceAll("（", "(").replaceAll("）", ")");
                JSONObject rowDataObj = new JSONObject();
                excelDataObj.put(actualContractCode, rowDataObj);
                rowDataObj.put("detailSheetName", ((String) rowData.get(9)).replaceAll("（", "(").replaceAll("）", ")"));
            }
        }

        transactionTemplate.execute(status -> {
            try {
                for (Long contractId : collectionBaseInfoMap.keySet()) {
                    List<CollectionBaseInfo> cbiList = collectionBaseInfoMap.get(contractId);
                    ContractBaseInfo contractBaseInfo = contractBaseInfoMap.get(contractId);
                    String detailSheetName = excelDataObj.getJSONObject(contractBaseInfo.getContractCode()).getString("detailSheetName");
                    tzExcelReader.setSheet(detailSheetName);
                    List<List<Object>> detailSheetDataList = tzExcelReader.read();
                    Map<Integer, List<Object>> excelPhaseMap = new HashMap<>();
                    for (int rentRow = 6; ; rentRow++) {
                        if ("合计".equals(detailSheetDataList.get(rentRow).get(11))) {
                            break;
                        }
                        excelPhaseMap.put(Integer.valueOf(String.valueOf(detailSheetDataList.get(rentRow).get(11))), detailSheetDataList.get(rentRow));
                    }

                    for (CollectionBaseInfo collectionBaseInfo : cbiList) {
                        List<CollectionRecordInfo> collectionRecordInfoList = collectionRecordInfoMap.getOrDefault(collectionBaseInfo.getId(), new ArrayList<>());
                        // 如果有罚息，加在最后一条收款明细里
                        List<Object> rowData = excelPhaseMap.get(collectionBaseInfo.getPhase());
                        Long penaltyInterest = Optional.ofNullable(rowData.get(32)).map(d -> "-".equals(d) ? null : d).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(0L);
                        if (penaltyInterest > 0) {
                            CollectionRecordInfo lastCollectionRecordInfo = collectionRecordInfoList.get(collectionRecordInfoList.size() - 1);
                            lastCollectionRecordInfo.setPenaltyInterest(penaltyInterest);
                            lastCollectionRecordInfo.setCollectionAmount(LongUtil.null2zero(lastCollectionRecordInfo.getPrincipal()) + LongUtil.null2zero(lastCollectionRecordInfo.getInterest()) + LongUtil.null2zero(lastCollectionRecordInfo.getPenaltyInterest()));
                            collectionRecordInfoMapper.updateById(lastCollectionRecordInfo);
                        }

                        collectionBaseInfo.setCollectionPrincipal(collectionRecordInfoList.stream().filter(d -> Objects.nonNull(d.getPrincipal())).mapToLong(CollectionRecordInfo::getPrincipal).sum());
                        collectionBaseInfo.setCollectionInterest(collectionRecordInfoList.stream().filter(d -> Objects.nonNull(d.getInterest())).mapToLong(CollectionRecordInfo::getInterest).sum());
                        collectionBaseInfo.setCollectionPenaltyInterest(collectionRecordInfoList.stream().filter(d -> Objects.nonNull(d.getPenaltyInterest())).mapToLong(CollectionRecordInfo::getPenaltyInterest).sum());
                        collectionBaseInfo.setCollectionAmount(collectionBaseInfo.getCollectionPrincipal() + collectionBaseInfo.getCollectionInterest() + collectionBaseInfo.getCollectionPenaltyInterest());
                        collectionBaseInfoMapper.updateById(collectionBaseInfo);
                    }

                }
                return true;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("事务执行出错", e);
                throw e;
            }
        });
    }

    /**
     * 处理合同对应的项目评审
     */
    @Test
    public void fixContractProjReview() {
        Map<String, String> needFixContractMap = new HashMap<String, String>() {{
            put("浙商租【2021】租字第(A-0040)号", "扶绥县昊腾工程建设10万元售后回租项目");
            put("中拓租【2019】租字第(A-0010)号", "镔鑫钢铁2019年1亿元售后回租项目");
            put("中拓租【2019】租字第(A-0011)号", "镔鑫钢铁2019年1亿元售后回租项目");
            put("中拓租【2020】租字第(A-0030)号", "镔鑫钢铁2020年1亿元售后回租项目");
            put("中拓租【2020】租字第(A-0020)号", "山西高义钢铁有限公司1.12亿元售后回租项目");
            put("中拓租【2020】租字第(A-0001)号", "昌乐新迈纸业有限公司2020年售后回租项目");
            put("中拓租【2019】租字第(A-0016)号", "2019浙商中拓集团股份有限公司5000元售后回租项目");
            put("中拓租【2019】租字第(A-0015)号", "2019浙江中拓供应链管理有限公司5000元售后回租项目");
            put("中拓租【2020】租字第(A-0013)号", "新疆博海水泥有限公司2020年设备回租项目");
            put("中拓租【2020】租字第(A-0012)号", "2021河南骏化发展售后回租项目");
            put("中拓租【2020】租字第(A-0022)号", "2021宜宾丝丽雅股售后回租项目");
            put("中拓租【2019】租字第(A-0002)号", "博汇纸业1.8亿元回租项目");
            put("浙商租【2021】租字第(A-0016)号", "2021河南骏化发展售后回租项目");
            put("浙商租【2021】租字第(A-0022)号", " 京商第一建设有限公司 10万元售后回租项目");
            put("中拓租【2019】租字第(A-0013)号", "宁夏晟晏实业集团能源循环经济有限公司设备回租项目");
            put("中拓租【2019】租字第(A-0014)号", "宁夏晟晏实业集团能源循环经济有限公司设备回租项目");
        }};
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery().in(ContractBaseInfo::getContractCode, needFixContractMap.keySet()));
        Map<Long, ContractLeasePrice> contractLeasePriceMap = contractLeasePriceMapper.selectList(Wrappers.<ContractLeasePrice>lambdaQuery()).stream().collect(Collectors.toMap(ContractLeasePrice::getContractId, c -> c));
        Map<Long, ContractLeasePriceLib> contractLeasePriceLibMap = contractLeasePriceLibMapper.selectList(Wrappers.<ContractLeasePriceLib>lambdaQuery().in(ContractLeasePriceLib::getContractId, contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()))).stream().collect(Collectors.toMap(ContractLeasePriceLib::getContractId, c -> c));
        Map<Long, List<ContractBaseInfoLib>> contractBaseInfoLibMap = contractBaseInfoLibMapper.selectList(Wrappers.<ContractBaseInfoLib>lambdaQuery().in(ContractBaseInfoLib::getOriginId, contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()))).stream().collect(Collectors.groupingBy(ContractBaseInfoLib::getOriginId));
        Map<Long, ProjReviewBaseInfo> projReviewBaseInfoMap = projReviewBaseInfoMapper.selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery().in(ProjReviewBaseInfo::getId, contractBaseInfoList.stream().map(ContractBaseInfo::getProjReviewId).map(s -> s == null ? -1L : s).collect(Collectors.toSet()))).stream().collect(Collectors.toMap(ProjReviewBaseInfo::getId, p -> p));
        Map<Long, ProjReviewLeasePrice> projReviewLeasePriceMap = projReviewLeasePriceMapper.selectList(Wrappers.<ProjReviewLeasePrice>lambdaQuery().in(ProjReviewLeasePrice::getProjectId, contractBaseInfoList.stream().map(ContractBaseInfo::getProjReviewId).map(s -> s == null ? -1L : s).collect(Collectors.toSet()))).stream().collect(Collectors.toMap(ProjReviewLeasePrice::getProjectId, p -> p));
        transactionTemplate.execute(status -> {
            try {
                for (ContractBaseInfo contractBaseInfo : contractBaseInfoList) {
                    if (CollectionUtils.isEmpty(contractBaseInfoLibMap.get(contractBaseInfo.getId()))) {
                        log.info("该合同没有版本:{},{}", contractBaseInfo.getId(), contractBaseInfo.getContractCode());
                        continue;
                    }
                    ProjReviewBaseInfo projReviewBaseInfo = null;
                    ProjReviewLeasePrice projReviewLeasePrice = null;
                    if (contractBaseInfo.getProjReviewId() != null) {
                        // 已存在的数据 仅进行修正
                        projReviewBaseInfo = projReviewBaseInfoMap.get(contractBaseInfo.getProjReviewId());
                        projReviewLeasePrice = projReviewLeasePriceMap.get(projReviewBaseInfo.getId());

                    } else {
                        // 之前没存在的数据要新找
                        String projName = needFixContractMap.get(contractBaseInfo.getContractCode());
                        projReviewBaseInfo = projReviewBaseInfoMapper.selectOne(Wrappers.<ProjReviewBaseInfo>lambdaQuery().eq(ProjReviewBaseInfo::getProjName, projName).orderByDesc(ProjReviewBaseInfo::getId).last("LIMIT 1"));
                        if (projReviewBaseInfo == null) {
                            log.info("项目不存在,合同编号:{}, 项目名称:{}", contractBaseInfo.getContractCode(), contractBaseInfo.getProjName());
                            continue;
                        }
                        projReviewLeasePrice = projReviewLeasePriceMapper.selectOne(Wrappers.<ProjReviewLeasePrice>lambdaQuery().eq(ProjReviewLeasePrice::getProjectId, projReviewBaseInfo.getId()).last("LIMIT 1"));
                        contractBaseInfo.setProjReviewId(projReviewBaseInfo.getId());
                    }
                    contractBaseInfo.setProjectType(projReviewBaseInfo.getProjectType());
                    contractBaseInfo.setProjSource(projReviewBaseInfo.getProjSource());
                    contractBaseInfo.setFundsPurpose(projReviewBaseInfo.getFundsPurpose());
                    contractBaseInfo.setProjBackground(projReviewBaseInfo.getProjBackground());
                    contractBaseInfo.setCreditAmountLoop(projReviewLeasePrice.getCreditAmountLoop());
                    contractBaseInfoMapper.updateById(contractBaseInfo);

                    ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibMap.get(contractBaseInfo.getId()).get(0);
                    contractBaseInfoLib.setProjectType(projReviewBaseInfo.getProjectType());
                    contractBaseInfoLib.setProjSource(projReviewBaseInfo.getProjSource());
                    contractBaseInfoLib.setFundsPurpose(projReviewBaseInfo.getFundsPurpose());
                    contractBaseInfoLib.setProjBackground(projReviewBaseInfo.getProjBackground());
                    contractBaseInfoLib.setCreditAmountLoop(projReviewLeasePrice.getCreditAmountLoop());
                    contractBaseInfoLibMapper.updateById(contractBaseInfoLib);

                    ContractLeasePrice contractLeasePrice = contractLeasePriceMap.get(contractBaseInfo.getId());
                    contractLeasePrice.setCreditAmountLoop(projReviewLeasePrice.getCreditAmountLoop());
                    contractLeasePrice.setPayType(projReviewLeasePrice.getPayType());
                    contractLeasePrice.setProjCreditAmount(projReviewLeasePrice.getApplyCreditAmount());
                    contractLeasePrice.setProjDownPayment(projReviewLeasePrice.getDownPayment());
                    contractLeasePrice.setProjConsultingFee(projReviewLeasePrice.getConsultingFee());
                    contractLeasePrice.setProjEarnestMoney(projReviewLeasePrice.getEarnestMoney());
                    contractLeasePrice.setProjIrrPercent(projReviewLeasePrice.getIrrPercent());
                    contractLeasePrice.setProjLeaseMonthCount(projReviewLeasePrice.getLeaseMonthCount());
                    contractLeasePriceMapper.updateById(contractLeasePrice);

                    ContractLeasePriceLib contractLeasePriceLib = contractLeasePriceLibMap.get(contractBaseInfo.getId());
                    contractLeasePriceLib.setCreditAmountLoop(projReviewLeasePrice.getCreditAmountLoop());
                    contractLeasePriceLib.setPayType(projReviewLeasePrice.getPayType());
                    contractLeasePriceLib.setProjCreditAmount(projReviewLeasePrice.getApplyCreditAmount());
                    contractLeasePriceLib.setProjDownPayment(projReviewLeasePrice.getDownPayment());
                    contractLeasePriceLib.setProjConsultingFee(projReviewLeasePrice.getConsultingFee());
                    contractLeasePriceLib.setProjEarnestMoney(projReviewLeasePrice.getEarnestMoney());
                    contractLeasePriceLib.setProjIrrPercent(projReviewLeasePrice.getIrrPercent());
                    contractLeasePriceLib.setProjLeaseMonthCount(projReviewLeasePrice.getLeaseMonthCount());
                    contractLeasePriceLibMapper.updateById(contractLeasePriceLib);
                }

                return true;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("事务执行出错", e);
                throw e;
            }
        });
    }

    /**
     * 修复合同编号
     * 先要找到原来的合同id，然后依次修改合同编号、付款编号、收款编号、保证金编号、借据、实际租金表编号
     * @deprecated 直租借据改造后，脚本已经不适用最新逻辑，需要改造后使用，如不改造请勿使用！！！！！
     */
    @Deprecated
    @Test
    public void fixContractCode() {
        // 查询procdefIdList
        List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery().list();
        List<String> contractProcIdList = processDefinitionList.stream().filter(p -> BusinessModuleEnum.CONTRACT.getModelKeyList().contains(p.getKey())).map(ProcessDefinition::getId).collect(Collectors.toList());
        List<String> paymentProcIdList = processDefinitionList.stream().filter(p -> BusinessModuleEnum.PAYMENT.getModelKeyList().contains(p.getKey())).map(ProcessDefinition::getId).collect(Collectors.toList());

        Map<Long, String> fixMap = new HashMap<>();
//        fixMap.put(1140L, "浙商租【2022】租字第(A-0127)号");
//        fixMap.put(1144L, "浙商租【2022】租字第(A-0126)号");
        fixMap.put(1152L, "浙商租【2022】租字第(A-0029)号");
//        fixMap.put(1162L, "浙商租【2023】租字第(C-0001)号");



        /*fixMap.put(1163L, "浙商租【2023】租字第(A-0003)号");
        fixMap.put(1168L, "浙商租【2022】保理字第(B-0009)号");
        fixMap.put(1169L, "浙商租【2022】保理字第(B-0010)号");
        fixMap.put(1171L, "浙商租【2022】保理字第(B-0011)号");
        fixMap.put(1172L, "浙商租【2022】保理字第(B-0012)号");
        fixMap.put(1173L, "浙商租【2022】保理字第(B-0013)号");
        fixMap.put(1087L, "浙商租【2022】保理字第(B-0014)号");
        fixMap.put(1170L, "浙商租【2022】保理字第(B-0005)号");
        fixMap.put(1174L, "浙商租【2022】保理字第(B-0003)号");*/
        // 历史数据中错误的
//        fixMap.put(892L, "浙商租【2022】租字第(A-0053)号-SX");
//        fixMap.put(1003L, "中拓租【2020】租字第(A-0021)号");
//        fixMap.put(1002L, "中拓租【2020】租字第(A-0020)号");
//        fixMap.put(1019L, "浙商租【2021】租字第(A-0022)号");
//        fixMap.put(-1L, "浙商租【2022】租字第(A-0053)号-HZ");

        // 新导入数据中错误的
//        fixMap.put(1022L, "中拓租【2019】租字第(A-0005)号");
//        fixMap.put(1040L, "中拓租【2019】租字第(A-0008)号");
//        fixMap.put(1039L, "中拓租【2020】租字第(A-0023)号");
//        fixMap.put(1030L, "浙商租【2021】租字第(A-0009)号");
//        fixMap.put(1020L, "浙商租【2021】租字第(A-0021)号");
//        fixMap.put(1025L, "浙商租【2022】租字第(A-0008)号");
//        fixMap.put(1044L, "浙商租【2022】租字第(A-0040)号");
//        fixMap.put(1027L, "浙商租【2021】租字第(JLG-0001)号");
//        fixMap.put(1037L, "浙商租【2022】租字第(A-0007)号");
//        fixMap.put(1032L, "浙商租【2022】租字第(A-0021)号");
//        fixMap.put(1033L, "浙商租【2022】租字第(A-0022)号");
//        fixMap.put(1035L, "浙商租【2022】租字第(A-0027)号");
//        fixMap.put(1036L, "浙商租【2022】租字第(A-0028)号");
//        fixMap.put(1026L, "浙商租【2022】租字第(A-0026)号");
//        fixMap.put(1029L, "浙商租【2022】租字第(A-0030)号");
//        fixMap.put(1024L, "浙商租【2022】租字第(A-0032)号");
//        fixMap.put(1038L, "浙商租【2022】租字第(A-0033)号");
//        fixMap.put(1034L, "浙商租【2022】租字第(A-0041)号");
//        fixMap.put(1023L, "浙商租【2022】租字第(A-0053)号-HZ");
//        fixMap.put(1028L, "浙商租【2022】租字第(A-0054)号");
//        fixMap.put(1041L, "浙商租【2022】租字第(A-0005)号");
//        fixMap.put(1042L, "浙商租【2022】租字第(A-0006)号");
//        fixMap.put(1045L, "浙商租【2022】租字第(C-0005)号");

//        fixMap.put(914L, "浙商租【2022】租字第(C-0004)号");
//        fixMap.put(957L, "浙商租【2021】租字第(A-0002)号");
//        fixMap.put(958L, "浙商租【2021】租字第(A-0001)号");
//        fixMap.put(1022L, "中拓租【2020】租字第(A-0005)号");
//        fixMap.put(1040L, "中拓租【2020】租字第(A-0008)号");
//        fixMap.put(1045L, "浙商租【2022】租字第(A-0031)号");
//        fixMap.put(1068L, "浙商租【2022】租字第(A-0059)号");
//        fixMap.put(923L, "浙商租【2022】租字第(GCJX-C-0001)号");
//        fixMap.put(892L, "浙商租【2022】租字第(A-0053)号");
//        fixMap.put(1058L, "浙商租【2021】租字第(A-0045)号");
//        fixMap.put(946L, "浙商租【2021】租字第(A-0045)号-JS");


        // 付款编号重复 要处理付款编号的
//        fixMap.put(1004L, "中拓租【2020】租字第(A-0001)号");

        // 线上补录数据修复
//        fixMap.put(1091L, "浙商租【2022】租字第(A-0081)号");
//        fixMap.put(1092L, "浙商租【2022】租字第(A-0082)号");
//        fixMap.put(1093L, "浙商租【2022】租字第(A-0112)号");
//        fixMap.put(1094L, "浙商租【2022】租字第(A-0086)号");
//        fixMap.put(1096L, "浙商租【2022】租字第(A-0113)号");
//        fixMap.put(1097L, "浙商租【2022】租字第(A-0114)号");
//        fixMap.put(1098L, "浙商租【2022】租字第(A-0115)号");
//        fixMap.put(1099L, "浙商租【2022】租字第(A-0116)号");
//        fixMap.put(1074L, "浙商租【2022】租字第(A-0072)号");
//        fixMap.put(1095L, "浙商租【2022】租字第(A-0073)号");
//        fixMap.put(1105L, "浙商租【2022】租字第(A-0106)号");
//        fixMap.put(1101L, "浙商租【2022】租字第(A-0097)号");
//        fixMap.put(1104L, "浙商租【2022】租字第(A-0098)号");
//        fixMap.put(1106L, "浙商租【2022】租字第(A-0118)号");
//        fixMap.put(1111L, "浙商租【2022】租字第(A-0120)号");
//        fixMap.put(1110L, "浙商租【2022】租字第(A-0119)号");
//        fixMap.put(1109L, "浙商租【2022】租字第(A-0117)号");

//        fixMap.put(1107L, "浙商租【2022】租字第(A-0078)号");
//        fixMap.put(1124L, "浙商租【2022】租字第(A-0049)号");
//        fixMap.put(1141L, "浙商租【2022】租字第(A-0105)号");
//        fixMap.put(1139L, "浙商租【2022】租字第(A-0104)号");
//        fixMap.put(1137L, "浙商租【2022】租字第(A-0092)号");
//        fixMap.put(1138L, "浙商租【2022】租字第(A-0093)号");
//        fixMap.put(1133L, "浙商租【2022】租字第(A-0095)号");
//        fixMap.put(1134L, "浙商租【2022】租字第(A-0096)号");
//        fixMap.put(1135L, "浙商租【2022】租字第(A-0101)号");
//        fixMap.put(1136L, "浙商租【2022】租字第(A-0102)号");

//        fixMap.put(1115L, "浙商租【2022】租字第(C-0007)号");
//        fixMap.put(1126L, "浙商租【2022】租字第(A-0087)号");
//        fixMap.put(1125L, "浙商租【2022】租字第(A-0083)号");
//        fixMap.put(1130L, "浙商租【2022】租字第(A-0084)号");
//        fixMap.put(1131L, "浙商租【2022】租字第(A-0085)号");
//        fixMap.put(1117L, "浙商租【2022】租字第(A-0075)号");
//        fixMap.put(1120L, "浙商租【2022】租字第(A-0076)号");
//        fixMap.put(1127L, "浙商租【2022】租字第(A-0107)号");
//        fixMap.put(1128L, "浙商租【2022】租字第(A-0079)号");
//        fixMap.put(1129L, "浙商租【2022】租字第(A-0080)号");


//        fixMap.put(1107L, "浙商租【2022】租字第(A-0078)号");
//        fixMap.put(1108L, "浙商租【2022】租字第(A-0103)号");
//        fixMap.put(1123L, "浙商租【2022】租字第(A-0057)号");
//        fixMap.put(1100L, "浙商租【2022】租字第(A-0099)号");
//        fixMap.put(1114L, "浙商租【2022】保理字第(B-0016)号");
//        fixMap.put(1132L, "浙商租【2022】租字第(A-0067)号");
//        fixMap.put(1089L, "浙商租【2022】租字第(A-0060)号");
//        fixMap.put(1153L, "浙商租【2022】租字第(A-0063)号");

        for (Long needFixContractId : fixMap.keySet()) {
            String contractCode = fixMap.get(needFixContractId);
            Matcher yearMatcher = yearPattern.matcher(contractCode);
            Matcher seqMatcher = seqPattern.matcher(contractCode);
            String yearString = null;
            String seqString = null;
            while (yearMatcher.find()) {
                yearString = yearMatcher.group(2);
            }
            while (seqMatcher.find()) {
                seqString = seqMatcher.group();
            }
            seqString = seqString.substring(1, seqString.length() - 1);
            Integer year = Integer.valueOf(yearString), seq = Integer.valueOf(seqString.split("-")[1]);
            transactionTemplate.execute(status -> {
                try {
                    StringBuilder codeSuffixBuilder = new StringBuilder("");
                    if (Objects.equals(892L, needFixContractId)) {
                        codeSuffixBuilder.append("SX");
                    } else if (Objects.equals(1023L, needFixContractId)) {
                        codeSuffixBuilder.append("HZ");
                    } else if (Objects.equals(946L, needFixContractId)) {
                        codeSuffixBuilder.append("JS");
                    }
                    String codeSuffix = codeSuffixBuilder.toString();
                    // 修复合同编号
                    ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(needFixContractId);
                    contractBaseInfo.setContractCode(contractCode);
                    contractBaseInfo.setContractYear(year);
                    contractBaseInfo.setSequence(seq);
                    contractBaseInfoMapper.updateById(contractBaseInfo);
                    List<ContractBaseInfoLib> contractBaseInfoLibList = contractBaseInfoLibMapper.selectList(Wrappers.<ContractBaseInfoLib>lambdaQuery().eq(ContractBaseInfoLib::getOriginId, needFixContractId));
                    contractBaseInfoLibList.forEach(c -> {
                        c.setContractCode(contractCode);
                        c.setContractYear(year);
                        c.setSequence(seq);
                        contractBaseInfoLibMapper.updateById(c);
                    });
                    fixDataMapper.fixProcessInstanceName(contractProcIdList, contractBaseInfo.getContractCode(), contractBaseInfo.getId());
                    // 修复付款编号、借据
                    List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery().eq(PaymentBaseInfo::getContractId, needFixContractId));
                    Map<Long, PaymentBaseInfo> paymentBaseInfoMap = paymentBaseInfoList.stream().collect(Collectors.toMap(PaymentBaseInfo::getId, p -> p));
                    List<ContractReceipt> contractReceiptList = contractReceiptMapper.selectList(Wrappers.<ContractReceipt>lambdaQuery().eq(ContractReceipt::getContractId, needFixContractId));
                    Map<String, List<ContractReceipt>> contractReceiptMap = contractReceiptList.stream().collect(Collectors.groupingBy(ContractReceipt::getPaymentApplyCode));
                    Map<Long, ContractReceipt> crMap = contractReceiptList.stream().collect(Collectors.toMap(ContractReceipt::getId, c -> c));
                    List<ContractReceiptLib> contractReceiptLibList = contractReceiptLibMapper.selectList(Wrappers.<ContractReceiptLib>lambdaQuery().eq(ContractReceiptLib::getContractId, needFixContractId));
                    Map<String, List<ContractReceiptLib>> contractReceiptLibMap = contractReceiptLibList.stream().collect(Collectors.groupingBy(ContractReceiptLib::getPaymentApplyCode));
                    Map<Long, Integer> paymentSeqMap = new HashMap<>();
                    for (int i = 0; i < paymentBaseInfoList.size(); i++) {
                        PaymentBaseInfo c = paymentBaseInfoList.get(i);
                        List<ContractReceipt> crlist = contractReceiptMap.getOrDefault(c.getPaymentCode(), new ArrayList<>());
                        List<ContractReceiptLib> crLiblist = contractReceiptLibMap.getOrDefault(c.getPaymentCode(), new ArrayList<>());
                        c.setContractCode(contractCode);
                        c.setPaymentCode(PaymentBaseInfoService.generatePaymentCode(contractCode, i + 1));
                        paymentSeqMap.put(c.getId(), i + 1);
                        if (StringUtils.isNotBlank(codeSuffix)) {
                            String[] ss = c.getPaymentCode().split("-");
                            c.setPaymentCode(join("-", ss[0], codeSuffix, ss[1]));
                        }
                        paymentBaseInfoMapper.updateById(c);
                        fixDataMapper.fixProcessInstanceName(paymentProcIdList, c.getPaymentCode(), c.getId());
                        crlist.forEach(d -> {
                            d.setPaymentApplyCode(c.getPaymentCode());
                            contractReceiptMapper.updateById(d);
                        });
                        crLiblist.forEach(d -> {
                            d.setPaymentApplyCode(c.getPaymentCode());
                            contractReceiptLibMapper.updateById(d);
                        });
                    }
                    List<PaymentBaseInfoLib> paymentBaseInfoLibList = paymentBaseInfoLibMapper.selectList(Wrappers.<PaymentBaseInfoLib>lambdaQuery().eq(PaymentBaseInfo::getContractId, needFixContractId));
                    paymentBaseInfoLibList.forEach(c -> {
                        c.setContractCode(contractCode);
                        c.setPaymentCode(PaymentBaseInfoService.generatePaymentCode(contractCode, paymentSeqMap.get(c.getOriginId())));
                        if (StringUtils.isNotBlank(codeSuffix)) {
                            String[] ss = c.getPaymentCode().split("-");
                            c.setPaymentCode(join("-", ss[0], codeSuffix, ss[1]));
                        }
                        paymentBaseInfoLibMapper.updateById(c);
                    });
                    // 修复收款编号
                    List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery().eq(CollectionBaseInfo::getContractId, needFixContractId));
                    collectionBaseInfoList.forEach(c -> {
                        c.setContractCode(contractCode);
                        c.setPaymentCode(Objects.nonNull(c.getPaymentId()) ? paymentBaseInfoMap.get(c.getPaymentId()).getPaymentCode() : null);
                        c.setCode(ContractPaymentImporterHelper.getCollectionCode(c.getCashFlowItem(), c.getPaymentCode(), c.getPhase(), c.getContractCode()));
                        if (!CashFlowItemEnum.RENT.name().equals(c.getCashFlowItem())) {
                            if (StringUtils.isNotBlank(codeSuffix)) {
                                String[] ss = c.getCode().split("-");
                                List<String> ssList = Stream.of(ss).collect(Collectors.toList());
                                ssList.add(ssList.size() - 1, codeSuffix);
                                c.setCode(ssList.stream().collect(Collectors.joining("-")));
                            }
                        }
                        collectionBaseInfoMapper.updateById(c);
                    });
                    // 修复保证金
                    List<MarginBaseInfo> marginBaseInfoList = marginBaseInfoMapper.selectList(Wrappers.<MarginBaseInfo>lambdaQuery().eq(MarginBaseInfo::getContractId, needFixContractId));
                    marginBaseInfoList.forEach(c -> {
                        c.setContractCode(contractCode);
                        c.setMarginCode(ContractPaymentImporterHelper.getBzjCode(contractCode));
                        if (StringUtils.isNotBlank(codeSuffix)) {
                            String[] ss = c.getMarginCode().split("-");
                            List<String> ssList = Stream.of(ss).collect(Collectors.toList());
                            ssList.add(ssList.size() - 1, codeSuffix);
                            c.setMarginCode(ssList.stream().collect(Collectors.joining("-")));
                        }
                        marginBaseInfoMapper.updateById(c);
                    });
                    // 修复实际租金表
                    List<ContractRentActual> contractRentActualList = contractRentActualMapper.selectList(Wrappers.<ContractRentActual>lambdaQuery().eq(ContractRentActual::getContractId, needFixContractId)).stream().filter(c -> StringUtils.isNotBlank(c.getCashFlowCode())).collect(Collectors.toList());
                    contractRentActualList.forEach(c -> {
                        ContractReceipt receipt = crMap.get(c.getReceiptId());
                        if (isNotNull(receipt)) {
                            PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(receipt.getReceiptCode());
                            if (isNotNull(paymentBaseInfo)) {
                                c.setCashFlowCode(ContractPaymentImporterHelper.getCashFlowCode(paymentBaseInfo.getPaymentCode(), c.getCashFlowPhase()));
                                contractRentActualMapper.updateById(c);
                            }
                        }
                    });
                    List<ContractRentActualLib> contractRentActualLibList = contractRentActualLibMapper.selectList(Wrappers.<ContractRentActualLib>lambdaQuery().eq(ContractRentActualLib::getContractId, needFixContractId)).stream().filter(c -> StringUtils.isNotBlank(c.getCashFlowCode())).collect(Collectors.toList());
                    System.out.println(JSONUtil.toJsonStr(contractRentActualLibList));
                    System.out.println(JSONUtil.toJsonStr(crMap));
                    contractRentActualLibList.forEach(c -> {
                        ContractReceipt receipt = crMap.get(c.getReceiptId());
                        if (isNotNull(receipt)) {
                            PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(receipt.getReceiptCode());
                            if (isNotNull(paymentBaseInfo)) {
                                c.setCashFlowCode(ContractPaymentImporterHelper.getCashFlowCode(paymentBaseInfo.getPaymentCode(), c.getCashFlowPhase()));
                                contractRentActualMapper.updateById(c);
                            }
                        }
                    });
                    return true;
                } catch (Exception e) {
                    status.setRollbackOnly();
                    log.error("事务执行出错", e);
                    throw e;
                }
            });
        }

        // 征信报送模块编号处理
        creditReportDataFix.fixCode(fixMap.keySet());
    }

    /**
     * 清空流程状态
     */
    @Test
    public void fixContractProcessStatus() {
        Set<String> needHandleContractCodeSet = new HashSet<>(Arrays.asList("浙商租【2021】租字第(A-0021)号", "中拓租【2019】租字第(A-0005)号", "浙商租【2022】租字第(A-0053)号-HZ", "浙商租【2022】租字第(A-0032)号", "浙商租【2022】租字第(A-0008)号", "浙商租【2022】租字第(A-0026)号", "浙商租【2021】租字第(JLG-0001)号", "浙商租【2022】租字第(A-0054)号", "浙商租【2022】租字第(A-0030)号", "浙商租【2021】租字第(A-0009)号", "浙商租【2022】租字第(A-0021)号", "浙商租【2022】租字第(A-0022)号", "浙商租【2022】租字第(A-0041)号", "浙商租【2022】租字第(A-0027)号", "浙商租【2022】租字第(A-0028)号", "浙商租【2022】租字第(A-0007)号", "浙商租【2022】租字第(A-0033)号", "中拓租【2020】租字第(A-0023)号", "中拓租【2019】租字第(A-0008)号", "浙商租【2022】租字第(A-0005)号", "浙商租【2022】租字第(A-0006)号", "浙商租【2022】租字第(A-0040)号", "浙商租【2022】租字第(C-0005)号"));
        LambdaUpdateWrapper<ContractBaseInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(ContractBaseInfo::getContractProcessStatus, null);
        updateWrapper.set(ContractBaseInfo::getRemainAvailableQuota, null);
        updateWrapper.in(ContractBaseInfo::getContractCode, needHandleContractCodeSet);
        contractBaseInfoMapper.update(null, updateWrapper);
    }

    /**
     * 修复租赁利率
     */
    @Test
    public void fixRentRate() {
        ExcelReader tzExcelReader = ExcelUtil.getReader(new File("/Users/wang/Desktop/附件8 合同报价方案租赁利率修复-补充利率类型 LPR 加点 租赁利率.xlsx"));
        tzExcelReader.setSheet("sheet1");
        List<List<Object>> allDataList = tzExcelReader.read();
        for (List<Object> rowDataList : allDataList) {
            if (rowDataList.size() < 1 || !(rowDataList.get(0) instanceof String) || !(((String) rowDataList.get(0)).contains("租"))) {
                continue;
            }
            ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectOne(Wrappers.<ContractBaseInfo>lambdaQuery().eq(ContractBaseInfo::getContractCode, rowDataList.get(0)));
            String rateType = "固定利率".equals(((String) rowDataList.get(4)).trim()) ? RateType.FIXED.name() : RateType.FLOAT.name();
            String lprType = "一年期".equals(((String) rowDataList.get(5)).trim()) ? LPRTypeEnum.ONE_YEAR.name() : LPRTypeEnum.FIVE_YEAR.name();
            Long lprPercent = Optional.ofNullable(rowDataList.get(6)).map(d -> extractNumber(d)).map(b -> b.divide(new BigDecimal(1), 2, HALF_UP).multiply(new BigDecimal(10000L)).longValue()).orElse(null);
            Long lprAddPercent = Optional.ofNullable(rowDataList.get(7)).map(d -> extractNumber(d)).map(b -> b.divide(new BigDecimal(1), 2, HALF_UP).multiply(new BigDecimal(10000L)).longValue()).orElse(null);
            Long leaseRatePercent = Optional.ofNullable(rowDataList.get(8)).map(d -> extractNumber(d)).map(b -> b.divide(new BigDecimal(1), 2, HALF_UP).multiply(new BigDecimal(10000L)).longValue()).orElse(null);
            LambdaUpdateWrapper<ContractLeasePrice> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(ContractLeasePrice::getRateType, rateType);
            updateWrapper.set(ContractLeasePrice::getLprType, lprType);
            updateWrapper.set(ContractLeasePrice::getLprPercent, lprPercent);
            updateWrapper.set(ContractLeasePrice::getLprAddPercent, lprAddPercent);
            updateWrapper.set(ContractLeasePrice::getLeaseRatePercent, leaseRatePercent);
            updateWrapper.eq(ContractLeasePrice::getContractId, contractBaseInfo.getId());
            LambdaUpdateWrapper<ContractLeasePriceLib> libUpdateWrapper = new LambdaUpdateWrapper<>();
            libUpdateWrapper.set(ContractLeasePriceLib::getRateType, rateType);
            libUpdateWrapper.set(ContractLeasePriceLib::getLprType, lprType);
            libUpdateWrapper.set(ContractLeasePriceLib::getLprPercent, lprPercent);
            libUpdateWrapper.set(ContractLeasePriceLib::getLprAddPercent, lprAddPercent);
            libUpdateWrapper.set(ContractLeasePriceLib::getLeaseRatePercent, leaseRatePercent);
            libUpdateWrapper.eq(ContractLeasePriceLib::getContractId, contractBaseInfo.getId());
            contractLeasePriceMapper.update(null, updateWrapper);
            contractLeasePriceLibMapper.update(null, libUpdateWrapper);

        }
    }

    /**
     * 修复联保标识和金额
     */
    @Test
    public void fixGuarantorAmount() {
        List<ContractGuarantor> contractGuarantorList = contractGuarantorMapper.selectList(Wrappers.lambdaQuery());
        Map<Long, List<ContractGuarantor>> contractGuarantorMap = contractGuarantorList.stream().collect(Collectors.groupingBy(ContractGuarantor::getContractId));
        for (Long contractId : contractGuarantorMap.keySet()) {
            ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(contractId);
            List<ContractGuarantor> cgList = contractGuarantorMap.get(contractId);
            // 特殊处理的合同编号
            if ("浙商租【2021】租字第(A-0061)号".equals(contractBaseInfo.getContractCode())) {
                cgList.forEach(c -> {
                    c.setJointGuaranteeMark(JointGuaranteeMarkEnum.MULTIPLE_SEPARATE.name());
                    if ("浙商租【2021】保证字第(A-0061-01)号".equals(c.getGuarantorContractCode())) {
                        ContractGuarantor.GuaranteeMultipleJsonWrapper gm = new ContractGuarantor.GuaranteeMultipleJsonWrapper();
                        gm.setClientId(2576L);
                        gm.setAmount(76500000 * 10000L);
                        c.setGuaranteeAmountMultiple(JSON.toJSONString(gm));
                    } else if ("浙商租【2021】保证字第(A-0061-02)号".equals(c.getGuarantorContractCode())) {
                        ContractGuarantor.GuaranteeMultipleJsonWrapper gm = new ContractGuarantor.GuaranteeMultipleJsonWrapper();
                        gm.setClientId(2575L);
                        gm.setAmount(73500000 * 10000L);
                        c.setGuaranteeAmountMultiple(JSON.toJSONString(gm));
                    }
                    contractGuarantorMapper.updateById(c);
                    LambdaUpdateWrapper<ContractGuarantorLib> libUpdateWrapper = new LambdaUpdateWrapper<>();
                    libUpdateWrapper.set(ContractGuarantorLib::getGuaranteeAmountMultiple, c.getGuaranteeAmountMultiple());
                    libUpdateWrapper.set(ContractGuarantorLib::getJointGuaranteeMark, c.getJointGuaranteeMark());
                    libUpdateWrapper.eq(ContractGuarantorLib::getOriginId, c.getId());
                    contractGuarantorLibMapper.update(null, libUpdateWrapper);
                });
                continue;
            } else if ("浙商租【2021】租字第(A-0062)号".equals(contractBaseInfo.getContractCode())) {
                cgList.forEach(c -> {
                    c.setJointGuaranteeMark(JointGuaranteeMarkEnum.MULTIPLE_SEPARATE.name());
                    if ("浙商租【2021】保证字第(A-0062-01)号".equals(c.getGuarantorContractCode())) {
                        ContractGuarantor.GuaranteeMultipleJsonWrapper gm = new ContractGuarantor.GuaranteeMultipleJsonWrapper();
                        gm.setClientId(2576L);
                        gm.setAmount(76500000 * 10000L);
                        c.setGuaranteeAmountMultiple(JSON.toJSONString(gm));
                    } else if ("浙商租【2021】保证字第(A-0062-02)号".equals(c.getGuarantorContractCode())) {
                        ContractGuarantor.GuaranteeMultipleJsonWrapper gm = new ContractGuarantor.GuaranteeMultipleJsonWrapper();
                        gm.setClientId(2575L);
                        gm.setAmount(73500000 * 10000L);
                        c.setGuaranteeAmountMultiple(JSON.toJSONString(gm));
                    }
                    contractGuarantorMapper.updateById(c);
                    LambdaUpdateWrapper<ContractGuarantorLib> libUpdateWrapper = new LambdaUpdateWrapper<>();
                    libUpdateWrapper.set(ContractGuarantorLib::getGuaranteeAmountMultiple, c.getGuaranteeAmountMultiple());
                    libUpdateWrapper.set(ContractGuarantorLib::getJointGuaranteeMark, c.getJointGuaranteeMark());
                    libUpdateWrapper.eq(ContractGuarantorLib::getOriginId, c.getId());
                    contractGuarantorLibMapper.update(null, libUpdateWrapper);
                });
                continue;
            } else if ("浙商租【2022】租字第(A-0027)号".equals(contractBaseInfo.getContractCode())
                    || "浙商租【2022】租字第(A-0028)号".equals(contractBaseInfo.getContractCode())
                    || "浙商租【2022】租字第(A-0053)号-HZ".equals(contractBaseInfo.getContractCode())
                    || "浙商租【2022】租字第(A-0054)号".equals(contractBaseInfo.getContractCode())
            ) {
                cgList.forEach(c -> {
                    c.setJointGuaranteeMark(JointGuaranteeMarkEnum.SINGLE.name());
                    c.setGuaranteeAmountSingle(150000000 * 10000L);
                    contractGuarantorMapper.updateById(c);
                    LambdaUpdateWrapper<ContractGuarantorLib> libUpdateWrapper = new LambdaUpdateWrapper<>();
                    libUpdateWrapper.set(ContractGuarantorLib::getGuaranteeAmountMultiple, c.getGuaranteeAmountMultiple());
                    libUpdateWrapper.set(ContractGuarantorLib::getJointGuaranteeMark, c.getJointGuaranteeMark());
                    libUpdateWrapper.eq(ContractGuarantorLib::getOriginId, c.getId());
                    contractGuarantorLibMapper.update(null, libUpdateWrapper);
                });
                continue;
            }
            // 单人保证还是联保，根据担保人数量来判
            long guarantorCount = cgList.stream().map(g -> JSONArray.parseArray(g.getGuarantorIds(), String.class)).flatMap(Collection::stream).distinct().count();
            String jointMark = guarantorCount == 1 ? JointGuaranteeMarkEnum.SINGLE.name() : JointGuaranteeMarkEnum.JOINT.name();
            cgList.forEach(c -> {
                c.setGuaranteeAmountSingle(contractBaseInfo.getApplyCreditAmount());
                c.setJointGuaranteeMark(jointMark);
                contractGuarantorMapper.updateById(c);
                LambdaUpdateWrapper<ContractGuarantorLib> libUpdateWrapper = new LambdaUpdateWrapper<>();
                libUpdateWrapper.set(ContractGuarantorLib::getGuaranteeAmountSingle, contractBaseInfo.getApplyCreditAmount());
                libUpdateWrapper.set(ContractGuarantorLib::getJointGuaranteeMark, jointMark);
                libUpdateWrapper.eq(ContractGuarantorLib::getOriginId, c.getId());
                contractGuarantorLibMapper.update(null, libUpdateWrapper);
            });
        }
    }

    /**
     * 修复租前息利率
     * 直租场景下有租前息利率 值与租赁利率相同
     */
    @Test
    public void fixBeforeRentRate() {
        List<ContractLeasePrice> contractLeasePriceList = contractLeasePriceMapper.selectList(Wrappers.lambdaQuery());
        for (ContractLeasePrice contractLeasePrice : contractLeasePriceList) {
            ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(contractLeasePrice.getContractId());
            if (!LeaseType.zhi_zu.name().equals(contractBaseInfo.getLeaseType())) {
                continue;
            }
            contractLeasePrice.setBeforeLprPercent(contractLeasePrice.getLprPercent());
            contractLeasePrice.setBeforeLprAddPercent(contractLeasePrice.getLprAddPercent());
            contractLeasePrice.setBeforeRateType(contractLeasePrice.getRateType());
            contractLeasePrice.setBeforeLprType(contractLeasePrice.getLprType());
            contractLeasePriceMapper.updateById(contractLeasePrice);
            LambdaUpdateWrapper<ContractLeasePriceLib> libUpdateWrapper = new LambdaUpdateWrapper<>();
            libUpdateWrapper.set(ContractLeasePriceLib::getBeforeLprPercent, contractLeasePrice.getBeforeLprPercent());
            libUpdateWrapper.set(ContractLeasePriceLib::getBeforeLprAddPercent, contractLeasePrice.getBeforeLprAddPercent());
            libUpdateWrapper.set(ContractLeasePriceLib::getBeforeRateType, contractLeasePrice.getBeforeRateType());
            libUpdateWrapper.set(ContractLeasePriceLib::getBeforeLprType, contractLeasePrice.getBeforeLprType());
            libUpdateWrapper.eq(ContractLeasePriceLib::getOriginId, contractLeasePrice.getId());
            contractLeasePriceLibMapper.update(null, libUpdateWrapper);
        }
    }

    /**
     * 修复项目编号
     */
    @Test
    public void fixProjCode() {
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery());
        List<ContractBaseInfoLib> contractBaseInfoLibList = contractBaseInfoLibMapper.selectList(Wrappers.<ContractBaseInfoLib>lambdaQuery());
        Set<Long> projReviewIdSet = new HashSet<>();
        projReviewIdSet.addAll(contractBaseInfoList.stream().map(ContractBaseInfo::getProjReviewId).filter(Objects::nonNull).collect(Collectors.toList()));
        projReviewIdSet.addAll(contractBaseInfoLibList.stream().map(ContractBaseInfo::getProjReviewId).filter(Objects::nonNull).collect(Collectors.toList()));
        Map<Long, ProjReviewBaseInfo> projReviewBaseInfoMap = projReviewBaseInfoMapper.selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery().in(ProjReviewBaseInfo::getId, projReviewIdSet)).stream().collect(Collectors.toMap(ProjReviewBaseInfo::getId, p -> p));
        contractBaseInfoList.forEach(c -> {
            c.setProjCode(Optional.ofNullable(projReviewBaseInfoMap.get(c.getProjReviewId())).map(ProjReviewBaseInfo::getProjCode).orElse(c.getProjCode()));
            contractBaseInfoMapper.updateById(c);
        });
        contractBaseInfoLibList.forEach(c -> {
            c.setProjCode(Optional.ofNullable(projReviewBaseInfoMap.get(c.getProjReviewId())).map(ProjReviewBaseInfo::getProjCode).orElse(c.getProjCode()));
            contractBaseInfoLibMapper.updateById(c);
        });
    }

    /**
     * 获取项目类型 反刷项目评审
     */
    @Test
    public void fixProjectType() {
        ExcelReader tzExcelReader = ExcelUtil.getReader(new File("/Users/wang/Desktop/20220831-融资租赁业务合同情况总表20220831-王传昊整理(2).xlsx"));
        tzExcelReader.setSheet("业务合同情况汇总表");
        List<List<Object>> contractSheetList = tzExcelReader.read();
        JSONObject excelDataObj = new JSONObject();
        for (List<Object> rowData : contractSheetList) {
            if (rowData.size() > 8
                    && rowData.get(8) instanceof String
                    && ((String) rowData.get(8)).contains("号")
            ) {
                String actualContractCode = ((String) rowData.get(8)).trim().replaceAll("（", "(").replaceAll("）", ")");
                JSONObject rowDataObj = new JSONObject();
                excelDataObj.put(actualContractCode, rowDataObj);
                rowDataObj.put("detailSheetName", ((String) rowData.get(9)).replaceAll("（", "(").replaceAll("）", ")"));
                rowDataObj.put("projectType", ((String) rowData.get(4)).replaceAll("（", "(").replaceAll("）", ")"));
            }
        }
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery().isNull(ContractBaseInfo::getProjectType));
        Map<Long, ProjReviewBaseInfo> projReviewBaseInfoMap = projReviewBaseInfoMapper.selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery().in(ProjReviewBaseInfo::getId, contractBaseInfoList.stream().map(ContractBaseInfo::getProjReviewId).map(s -> s == null ? -1L : s).collect(Collectors.toList()))).stream().collect(Collectors.toMap(ProjReviewBaseInfo::getId, p -> p));
        contractBaseInfoList.forEach(c -> {
            c.setProjectType(ContractPaymentImporterHelper.extractProjType(excelDataObj.getJSONObject(c.getContractCode()).getString("projectType")));
            contractBaseInfoMapper.updateById(c);
            LambdaUpdateWrapper<ContractBaseInfoLib> cblUpdateWrapper = new LambdaUpdateWrapper<>();
            cblUpdateWrapper.eq(ContractBaseInfoLib::getOriginId, c.getId());
            cblUpdateWrapper.set(ContractBaseInfoLib::getProjectType, c.getProjectType());
            contractBaseInfoLibMapper.update(null, cblUpdateWrapper);

            ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMap.get(c.getProjReviewId());
            if (projReviewBaseInfo != null) {
                projReviewBaseInfo.setProjectType(c.getProjectType());
                projReviewBaseInfoMapper.updateById(projReviewBaseInfo);
                LambdaUpdateWrapper<ProjReviewBaseInfoLib> prbUpdateWrapper = new LambdaUpdateWrapper<>();
                prbUpdateWrapper.eq(ProjReviewBaseInfoLib::getOriginId, projReviewBaseInfo.getId());
                prbUpdateWrapper.set(ProjReviewBaseInfoLib::getProjectType, c.getProjectType());
                projReviewBaseInfoLibMapper.update(null, prbUpdateWrapper);
            }
        });

    }

    /**
     * 修复业务部门负责人 等数据
     */
    @Test
    public void fixBizLeader() {
        // 业务负责人 业务分管领导
//        sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), JobEnum.businesshead.name());
//        sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), JobEnum.leaderincharge.name());
        List<ProjEstablishBaseInfo> projEstablishBaseInfoList = projEstablishBaseInfoMapper.selectList(Wrappers.lambdaQuery());
        projEstablishBaseInfoList.forEach(c -> {
            c.setBizDeptLeaderId(Objects.isNull(c.getBizDeptId()) ? null : sysUserService.getUserIdByOrgJob(c.getBizDeptId(), JobEnum.businesshead.name()));
            c.setBizDivisionLeaderId(Objects.isNull(c.getBizDeptId()) ? null : sysUserService.getUserIdByOrgJob(c.getBizDeptId(), JobEnum.leaderincharge.name()));
            projEstablishBaseInfoMapper.updateById(c);
        });
        List<ProjEstablishBaseInfoLib> projEstablishBaseInfoLibList = projEstablishBaseInfoLibMapper.selectList(Wrappers.lambdaQuery());
        projEstablishBaseInfoLibList.forEach(c -> {
            c.setBizDeptLeaderId(Objects.isNull(c.getBizDeptId()) ? null : sysUserService.getUserIdByOrgJob(c.getBizDeptId(), JobEnum.businesshead.name()));
            c.setBizDivisionLeaderId(Objects.isNull(c.getBizDeptId()) ? null : sysUserService.getUserIdByOrgJob(c.getBizDeptId(), JobEnum.leaderincharge.name()));
            projEstablishBaseInfoLibMapper.updateById(c);
        });

        List<ProjReviewBaseInfo> projReviewBaseInfoList = projReviewBaseInfoMapper.selectList(Wrappers.lambdaQuery());
        projReviewBaseInfoList.forEach(c -> {
            c.setBizDeptLeaderId(Objects.isNull(c.getBizDeptId()) ? null : sysUserService.getUserIdByOrgJob(c.getBizDeptId(), JobEnum.businesshead.name()));
            c.setBizDivisionLeaderId(Objects.isNull(c.getBizDeptId()) ? null : sysUserService.getUserIdByOrgJob(c.getBizDeptId(), JobEnum.leaderincharge.name()));
            projReviewBaseInfoMapper.updateById(c);
        });
        List<ProjReviewBaseInfoLib> projReviewBaseInfoLibList = projReviewBaseInfoLibMapper.selectList(Wrappers.lambdaQuery());
        projReviewBaseInfoLibList.forEach(c -> {
            c.setBizDeptLeaderId(Objects.isNull(c.getBizDeptId()) ? null : sysUserService.getUserIdByOrgJob(c.getBizDeptId(), JobEnum.businesshead.name()));
            c.setBizDivisionLeaderId(Objects.isNull(c.getBizDeptId()) ? null : sysUserService.getUserIdByOrgJob(c.getBizDeptId(), JobEnum.leaderincharge.name()));
            projReviewBaseInfoLibMapper.updateById(c);
        });

        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoMapper.selectList(Wrappers.lambdaQuery());
        contractBaseInfoList.forEach(c -> {
            c.setBizDeptLeaderId(Objects.isNull(c.getBizDeptId()) ? null : sysUserService.getUserIdByOrgJob(c.getBizDeptId(), JobEnum.businesshead.name()));
            c.setBizDivisionLeaderId(Objects.isNull(c.getBizDeptId()) ? null : sysUserService.getUserIdByOrgJob(c.getBizDeptId(), JobEnum.leaderincharge.name()));
            contractBaseInfoMapper.updateById(c);
        });
        List<ContractBaseInfoLib> contractBaseInfoLibList = contractBaseInfoLibMapper.selectList(Wrappers.lambdaQuery());
        contractBaseInfoLibList.forEach(c -> {
            c.setBizDeptLeaderId(Objects.isNull(c.getBizDeptId()) ? null : sysUserService.getUserIdByOrgJob(c.getBizDeptId(), JobEnum.businesshead.name()));
            c.setBizDivisionLeaderId(Objects.isNull(c.getBizDeptId()) ? null : sysUserService.getUserIdByOrgJob(c.getBizDeptId(), JobEnum.leaderincharge.name()));
            contractBaseInfoLibMapper.updateById(c);
        });
    }

    private static boolean filterNotBlank(Object o) {
        return Objects.nonNull(o) && !"".equals(o) && !"-".equals(o) && !Objects.equals(0.0D, o);
    }

    /**
     * 抵押物导入
     */
    @Test
    public void fixMortgate() throws Exception {
//        Long contractId = 1025L;
//        ContractMortgageAddREQ contractMortgageAddREQ = new ContractMortgageAddREQ();
//        contractMortgageAddREQ.setFile(new FileInputStream("/Users/wang/Desktop/抵押措施模版-A0008.xlsx"));
//        contractMortgageAddREQ.setFile2(new FileInputStream("/Users/wang/Desktop/抵押措施模版-A0008.xlsx"));
//        contractMortgageAddREQ.setContractId(contractId);
//        contractMortgageAddREQ.setMortgageContractCode("浙商租【2022】抵字第(B-0001)号");
////        contractMortgageAddREQ.setRelatContracts();
//        contractMortgageAddREQ.setMortgageType(ClientType.CORPORATION.name());
//        contractMortgageAddREQ.setMortgageIds(ListUtil.toList(2915L));
//        contractMortgageAddREQ.setMortgageDescribe("宁波市鄞州区潘火街道宁创科技中心B1、B2、B3地下共计522个车位");
//        contractMortgageAddREQ.setAssess(1);
//        contractMortgageAddREQ.setAssessDate("2020-07-30");
//        contractMortgageAddREQ.setAppraisalCompany("宁波恒正房地产估价有限公司");
////        contractMortgageAddREQ.setAppraisalCode();
//        contractMortgageAddREQ.setHighest(0);
//        contractMortgageService.add(contractMortgageAddREQ);

//        List<CommonVersion> commonVersionList = commonVersionMapper.selectList(Wrappers.<CommonVersion>lambdaQuery()
//                .eq(CommonVersion::getMainId, contractId)
//                .eq(CommonVersion::getModule, BusinessModuleEnum.CONTRACT.name())
//        );
//        List<ContractMortgage> contractMortgageList = contractMortgageMapper.selectList(Wrappers.<ContractMortgage>lambdaQuery().eq(ContractMortgage::getContractId, contractId));
//        List<ContractMortgageItem> contractMortgageItemList = contractMortgageItemMapper.selectList(Wrappers.<ContractMortgageItem>lambdaQuery().eq(ContractMortgageItem::getContractId, contractId));
//        transactionTemplate.execute(status -> {
//           try {
//               for (CommonVersion commonVersion : commonVersionList) {
//                    for (ContractMortgage contractMortgage : contractMortgageList) {
//                        ContractMortgageLib contractMortgageLib = contractMortgageLibHandler.actualEntity2Lib(contractMortgage, commonVersion.getVersion());
//                        contractMortgageLibMapper.insert(contractMortgageLib);
//                    }
//                   for (ContractMortgageItem contractMortgageItem : contractMortgageItemList) {
//                       ContractMortgageItemLib contractMortgageItemLib = contractMortgageItemLibHandler.actualEntity2Lib(contractMortgageItem, commonVersion.getVersion());
//                       contractMortgageItemLibMapper.insert(contractMortgageItemLib);
//                   }
//               }
//               return true;
//           } catch (Exception e) {
//               status.setRollbackOnly();
//               log.error("事务执行出错", e);
//               throw e;
//           }
//        });
    }

    @Test
    public void fixPledge() throws Exception {
        Long contractId = 1123L;
//        ContractPledgeAddREQ contractPledgeAddREQ = new ContractPledgeAddREQ();
//        contractPledgeAddREQ.setContractId(contractId);
//        contractPledgeAddREQ.setPledgeContractCode("浙商租【2022】股质字第(A-0057)号");
//        contractPledgeAddREQ.setFile(new File("/Users/wang/Desktop/导入模板-质押措施补录/018-质押措施模版.xlsx"));
//        contractPledgeAddREQ.setFile2(new File("/Users/wang/Desktop/导入模板-质押措施补录/018-质押措施模版.xlsx"));
//        contractPledgeAddREQ.setPledgeType(ClientType.CORPORATION.name());
//        contractPledgeAddREQ.setPledgeIds(ListUtil.toList(3069L));
//        contractPledgeAddREQ.setPledgeDescribe("出质人将其持有的【上海浩诺供应链管理有限公司】（以下简称“目标公司”）【100】%的股\n" +
//                "权，即目标公司股东【北京聚亚科技服务有限公司】所占的所有股份比例（即出质人出资额人民币\n" +
//                "【2594.7 万元】，目标公司注册资本为人民币【4800 万元】，实收资本为人民币【2594.7 万元】,以\n" +
//                "下简称“标的股权”）全部出质给质权人，作为承租人履行其在主合同项下义务的担保，被担保主债\n" +
//                "权金额为：人民币【贰仟壹佰柒拾捌万壹仟零捌拾肆元玖角叁分】（小写：￥21,781,084.93）");
//        contractPledgeAddREQ.setHighest(0);
//        contractPledgeService.add(contractPledgeAddREQ);

//        List<CommonVersion> commonVersionList = commonVersionMapper.selectList(Wrappers.<CommonVersion>lambdaQuery()
//                .eq(CommonVersion::getMainId, contractId)
//                .eq(CommonVersion::getModule, BusinessModuleEnum.CONTRACT.name())
//        );
//        List<ContractPledge> contractPledgeList = contractPledgeMapper.selectList(Wrappers.<ContractPledge>lambdaQuery().eq(ContractPledge::getContractId, contractId));
//        List<ContractPledgeItem> contractPledgeItemList = contractPledgeItemMapper.selectList(Wrappers.<ContractPledgeItem>lambdaQuery().eq(ContractPledgeItem::getContractId, contractId));
//        transactionTemplate.execute(status -> {
//           try {
//               for (CommonVersion commonVersion : commonVersionList) {
//                    for (ContractPledge contractPledge : contractPledgeList) {
//                        ContractPledgeLib contractPledgeLib = contractPledgeLibHandler.actualEntity2Lib(contractPledge, commonVersion.getVersion());
//                        contractPledgeLibMapper.insert(contractPledgeLib);
//                    }
//                   for (ContractPledgeItem contractPledgeItem : contractPledgeItemList) {
//                       ContractPledgeItemLib contractPledgeItemLib = contractPledgeItemLibHandler.actualEntity2Lib(contractPledgeItem, commonVersion.getVersion());
//                       contractPledgeItemLibMapper.insert(contractPledgeItemLib);
//                   }
//               }
//               return true;
//           } catch (Exception e) {
//               status.setRollbackOnly();
//               log.error("事务执行出错", e);
//               throw e;
//           }
//        });
    }

}
