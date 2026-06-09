package cn.zswltech.mithras.service.service.kpi.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionListREQ;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ProjectPaymentBonusRadioConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ProjectRadioConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ProjectScaleRadioConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ProjectTypeRadioConfig;
import cn.zswltech.mithras.kpi.bo.KpiFinanceProjectProfitRecordBo;
import cn.zswltech.mithras.kpi.bo.KpiParameterConfigBo;
import cn.zswltech.mithras.kpi.bo.KpiPaymentAmountBo;
import cn.zswltech.mithras.kpi.bo.KpiProjectDistributionRecordBo;
import cn.zswltech.mithras.kpi.mapper.model.KpiFinanceProjectProfitRecord;
import cn.zswltech.mithras.kpi.mapper.model.KpiParameterConfig;
import cn.zswltech.mithras.kpi.mapper.model.KpiParameterConfigRecord;
import cn.zswltech.mithras.kpi.mapper.model.KpiPaymentAmountRecord;
import cn.zswltech.mithras.kpi.service.KpiFinanceProjectProfitRecordService;
import cn.zswltech.mithras.kpi.service.KpiParameterBaseService;
import cn.zswltech.mithras.kpi.service.KpiParameterConfigRecordService;
import cn.zswltech.mithras.kpi.service.KpiPaymentAmountRecordService;
import cn.zswltech.mithras.kpi.service.KpiProjectDistributionRecordService;
import cn.zswltech.mithras.kpi.convert.KpiParameterConfigConvert;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.kpi.application.KpiProjGuessCollectService;
import cn.zswltech.mithras.kpi.enums.KpiParameterConfigCodeEnum;
import cn.zswltech.mithras.finance.mapper.finance.FinanceProjectProfitDetailReceiptMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceProjectProfit;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceProjectProfitDetailReceipt;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionBaseInfoLib;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.contract.core.application.ContractBaseInfoService;
import cn.zswltech.mithras.finance.service.FinanceProjectProfitService;
import cn.zswltech.mithras.kpi.service.lib.KpiProjectDistributionBaseInfoLibService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewPriceService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author: jackerhe
 * @date: 2024/9/26 09:15
 * 用于收集/管理所有绩效测试表需要的业务数据，
 **/
@Slf4j
@Service
public class KpiProjGuessCollectServiceImpl implements KpiProjGuessCollectService {

    @Resource
    private KpiProjectDistributionBaseInfoLibService kpiProjectDistributionBaseInfoLibService;
    @Resource
    private FinanceProjectProfitDetailReceiptMapper financeProjectProfitDetailReceiptMapper;
    @Resource
    private FinanceProjectProfitService financeProjectProfitService;
    @Resource
    private KpiFinanceProjectProfitRecordService kpiFinanceProjectProfitRecordService;
    @Resource
    private KpiParameterBaseService kpiParameterBaseService;
    @Resource
    private KpiParameterConfigRecordService kpiParameterConfigRecordService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private KpiPaymentAmountRecordService kpiPaymentAmountRecordService;
    @Resource
    private KpiProjectDistributionRecordService kpiProjectDistributionRecordService;
    @Resource
    private ProjReviewPriceService projReviewPriceService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;


    /**
     * 项目利润分配表
     * @param flash ture 使用最新数据
     **/
    @Override
    public List<KpiFinanceProjectProfitRecordBo> getProjectProfitRecords(List<Long> contractId, Integer year, Integer month, boolean flash) {
        List<KpiFinanceProjectProfitRecord> kpiFinanceProjectProfitRecords = kpiFinanceProjectProfitRecordService.lastByYearAndMonth(year, month, contractId);
        if(flash || ObjectUtil.isEmpty(kpiFinanceProjectProfitRecords)) {
            FinanceProjectProfit lastEffect = financeProjectProfitService.getOne(Wrappers.<FinanceProjectProfit>lambdaQuery()
                    .eq(FinanceProjectProfit::getYear, year)
                    .eq(FinanceProjectProfit::getMonth, month)
                    .eq(FinanceProjectProfit::getIsConfirmed, YesOrNoNumberEnum.YES.getCode())
                    .last(StringUtil.mysqlLimitOne()));
            if(ObjectUtil.isEmpty(lastEffect)) {
                throw new MithrasException(year + "年" + month + "月" + "无已确认项目利润");
            }
            //借据维度数据
            List<FinanceProjectProfitDetailReceipt> financeProjectProfitDetailReceipts = financeProjectProfitDetailReceiptMapper.selectList(Wrappers.<FinanceProjectProfitDetailReceipt>lambdaQuery()
                    .eq(FinanceProjectProfitDetailReceipt::getProjectProfitId, lastEffect.getId()));
            if(ObjectUtil.isEmpty(financeProjectProfitDetailReceipts)) {
                throw new MithrasException(year + "年" + month + "月" + "已确认项目利润无数据");
            }
            //查询每个借据的首次投放时间
            Map<Long, LocalDate> receiptFirstPaymentDate = paymentBaseInfoService.getReceiptFirstPaymentDate(financeProjectProfitDetailReceipts.stream().map(FinanceProjectProfitDetailReceipt::getReceiptId).collect(Collectors.toSet()));
            //保存新数据
            int batchNumber = kpiFinanceProjectProfitRecords == null ? 1 : kpiFinanceProjectProfitRecords.get(0).getBatchNumber() + 1;
            kpiFinanceProjectProfitRecords = BeanUtil.copyToList(financeProjectProfitDetailReceipts, KpiFinanceProjectProfitRecord.class);
            List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.listByIds(kpiFinanceProjectProfitRecords.stream().map(KpiFinanceProjectProfitRecord::getContractId).collect(Collectors.toList()));
            Map<Long, ContractBaseInfo> contractId2Bean = contractBaseInfos.stream().collect(Collectors.toMap(ContractBaseInfo::getId, e -> e, (a, b) -> a));
            Map<Long, Long> longLongMap = projReviewPriceService.newestPrice(contractBaseInfos.stream().map(ContractBaseInfo::getProjReviewId).collect(Collectors.toSet()));
            kpiFinanceProjectProfitRecords.forEach(e -> {
                e.setBatchNumber(batchNumber);
                e.setProfitDetailId(e.getId());
                ContractBaseInfo contractBaseInfo = contractId2Bean.get(e.getContractId());
                if(ObjectUtil.isNotEmpty(contractBaseInfo)) {
                    e.setApplyCreditAmount(longLongMap.get(contractBaseInfo.getProjReviewId()));
                    e.setBizType(contractBaseInfo.getBizType());
                    e.setLeaseType(contractBaseInfo.getLeaseType());
                    if(ObjectUtil.isEmpty(e.getAssessDeptId())) {
                        e.setAssessDeptId(contractBaseInfo.getBizDeptId());
                    }
                }
                e.setReceiptFirstPaymentDate(receiptFirstPaymentDate.get(e.getReceiptId()));
                e.setId(null);
            });
            kpiFinanceProjectProfitRecordService.saveBatch(kpiFinanceProjectProfitRecords);
        }
        return BeanUtil.copyToList(kpiFinanceProjectProfitRecords, KpiFinanceProjectProfitRecordBo.class);
    }

    @SneakyThrows
    @Override
    public KpiParameterConfigBo getParameterConfig(LocalDate date, boolean flash) {
        KpiParameterConfigBo bo = kpiParameterConfigRecordService.lastByYearAndMonth(date);
        if (flash || ObjectUtil.isEmpty(bo)) {
            List<KpiParameterConfig> parameterConfigs = kpiParameterBaseService.getNewKpiParameters(date);
            if (ObjectUtil.isEmpty(parameterConfigs)) {
                throw new MithrasException(date + "无生效提奖比例");
            }
            //保存新数据
            int batchNumber = bo == null ? 1 : bo.getBatchNumber() + 1;
            bo = new KpiParameterConfigBo();
            Map<String, KpiParameterConfig> configRecordMap = parameterConfigs.stream().collect(Collectors.toMap(KpiParameterConfig::getConfigCode, e -> e, (a, b) -> a));
            //转化为各配置
            //项目提奖-基础提奖比例
            KpiParameterConfig projectRadioRecord = configRecordMap.get(KpiParameterConfigCodeEnum.PROJECT_RADIO.name());
            if (ObjectUtil.isNotEmpty(projectRadioRecord)) {
                ProjectRadioConfig projectRadioConfig = KpiParameterConfigConvert.toConfigBase(projectRadioRecord, ProjectRadioConfig.class);
                projectRadioConfig.setParameterBaseId(projectRadioRecord.getParameterBaseId());
                projectRadioConfig.setConfigValue(JSONUtil.toList(projectRadioRecord.getConfigValue(), ProjectRadioConfig.Data.class));
                bo.setBatchNumber(batchNumber);
                bo.setProjectRadioConfig(projectRadioConfig);
            }
            //项目提奖-项目类型系数
            KpiParameterConfig projectTypeRadioConfig = configRecordMap.get(KpiParameterConfigCodeEnum.PROJECT_RADIO_TYPE.name());
            if (ObjectUtil.isNotEmpty(projectTypeRadioConfig)) {
                ProjectTypeRadioConfig projectRadioConfig = KpiParameterConfigConvert.toConfigBase(projectTypeRadioConfig, ProjectTypeRadioConfig.class);
                projectRadioConfig.setParameterBaseId(projectTypeRadioConfig.getParameterBaseId());
                projectRadioConfig.setConfigValue(JSONUtil.toList(projectTypeRadioConfig.getConfigValue(), ProjectTypeRadioConfig.ProjTypeRatio.class));
                bo.setBatchNumber(batchNumber);
                bo.setProjectTypeRadioConfig(projectRadioConfig);
            }
            //项目提奖-项目规模系数
            KpiParameterConfig projectScaleRadioConfig = configRecordMap.get(KpiParameterConfigCodeEnum.PROJECT_RADIO_SCALE.name());
            if (ObjectUtil.isNotEmpty(projectScaleRadioConfig)) {
                ProjectScaleRadioConfig projectRadioConfig = KpiParameterConfigConvert.toConfigBase(projectScaleRadioConfig, ProjectScaleRadioConfig.class);
                projectRadioConfig.setParameterBaseId(projectScaleRadioConfig.getParameterBaseId());
                projectRadioConfig.setConfigValue(JSONUtil.toList(projectScaleRadioConfig.getConfigValue(), ProjectScaleRadioConfig.ProjScaleRatio.class));
                bo.setBatchNumber(batchNumber);
                bo.setProjectScaleRadioConfig(projectRadioConfig);
            }
            //项目提奖-投放奖金系数
            KpiParameterConfig projectPaymentBonusRadioConfig = configRecordMap.get(KpiParameterConfigCodeEnum.PROJECT_RADIO_PAYMENT.name());
            if (ObjectUtil.isNotEmpty(projectPaymentBonusRadioConfig)) {
                ProjectPaymentBonusRadioConfig projectRadioConfig = KpiParameterConfigConvert.toConfigBase(projectPaymentBonusRadioConfig, ProjectPaymentBonusRadioConfig.class);
                projectRadioConfig.setParameterBaseId(projectPaymentBonusRadioConfig.getParameterBaseId());
                projectRadioConfig.setConfigValue(JSONUtil.toList(projectPaymentBonusRadioConfig.getConfigValue(), ProjectPaymentBonusRadioConfig.PaymentBonusRatio.class));
                bo.setBatchNumber(batchNumber);
                bo.setProjectPaymentBonusRadioConfig(projectRadioConfig);
            }
            List<KpiParameterConfigRecord> records = BeanUtil.copyToList(parameterConfigs, KpiParameterConfigRecord.class);
            records.forEach(e -> {
                e.setParameterConfigId(e.getId());
                e.setBatchNumber(batchNumber);
                e.setCalculateDate(date);
                e.setId(null);
            });
            //保存
            kpiParameterConfigRecordService.saveBatch(records);
        }
        return bo;
    }

    @Override
    public List<KpiProjectDistributionRecordBo> getProjectDistributionWeight(List<Long> contractIds, LocalDate date, boolean flash) {
        List<KpiProjectDistributionRecordBo> bos = kpiProjectDistributionRecordService.getLastByDate(date, contractIds);
        int batchNumber = bos == null ? 1 : bos.get(0).getBatchNumber() + 1;
        if (flash || ObjectUtil.isEmpty(bos)) {
            KpiProjectDistributionListREQ distributionListREQ = new KpiProjectDistributionListREQ();
            distributionListREQ.setPage(1);
            distributionListREQ.setPageSize(Integer.MAX_VALUE);
            distributionListREQ.setDistributionStatus(YesOrNoNumberEnum.YES.getCode());
            //最新生效版本数据

            List<KpiProjectDistributionBaseInfoLib> kpiProjectDistributionBaseInfoLibs = kpiProjectDistributionBaseInfoLibService.listEffectByContractCodes(date, ObjectUtil.isEmpty(contractIds) ? null : contractBaseInfoService.listByIds(contractIds).stream().map(ContractBaseInfo::getContractCode).collect(Collectors.toList()));
            kpiProjectDistributionRecordService.record(kpiProjectDistributionBaseInfoLibs, date, batchNumber);
            //转化
            bos = kpiProjectDistributionRecordService.getLastByDate(date, contractIds);
        }
        return bos;
    }

    @Override
    public List<KpiPaymentAmountBo> getContractPaymentAmount(List<Long> contractIds, LocalDate date, boolean flash) {
        List<KpiPaymentAmountRecord> kpiPaymentAmountRecords = kpiPaymentAmountRecordService.lastByYearAndMonth(date, contractIds);
        LocalDateTime startOfDay = date.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusMonths(1);
        int batchNumber = kpiPaymentAmountRecords == null ? 1 : kpiPaymentAmountRecords.get(0).getBatchNumber() + 1;
        if (flash || ObjectUtil.isEmpty(kpiPaymentAmountRecords)) {
            LambdaQueryWrapper<PaymentActualDetail> lambdaQueryWrapper = Wrappers.<PaymentActualDetail>lambdaQuery()
                    .ge(PaymentActualDetail::getPaidInDate, startOfDay)
                    .lt(PaymentActualDetail::getPaidInDate, endOfDay)
                    .in(ObjectUtil.isNotEmpty(contractIds), PaymentActualDetail::getContractId, contractIds);
            List<PaymentActualDetail> paymentActualDetails = paymentActualDetailService.list(lambdaQueryWrapper);
            if (ObjectUtil.isEmpty(paymentActualDetails)) {
                return Collections.emptyList();
            }
            Map<Long, Long> contractId2Proj = contractBaseInfoService.listByIds(paymentActualDetails.stream().map(PaymentActualDetail::getContractId).collect(Collectors.toList()))
                    .stream().collect(Collectors.toMap(ContractBaseInfo::getId, ContractBaseInfo::getProjReviewId, (a, b) -> b));
            Map<Long, List<PaymentActualDetail>> collect = paymentActualDetails.stream().collect(Collectors.groupingBy(PaymentActualDetail::getContractId));
            List<KpiPaymentAmountRecord> finalKpiPaymentAmountRecords = new ArrayList<>();
            collect.forEach((k, v) -> {
                KpiPaymentAmountRecord record = new KpiPaymentAmountRecord();
                record.setContractId(k);
                record.setEffectMonth(date);
                record.setBatchNumber(batchNumber);
                record.setProjReviewId(contractId2Proj.get(k));
                record.setPaymentAmount(v.stream().map(PaymentActualDetail::getPaidInAmount).filter(ObjectUtil::isNotEmpty).reduce(Long::sum).orElse(0L));
                finalKpiPaymentAmountRecords.add(record);
            });
            kpiPaymentAmountRecords = finalKpiPaymentAmountRecords;
            Map<Long, Long> contractId2Review = contractBaseInfoService.listByIds(paymentActualDetails.stream().map(PaymentActualDetail::getContractId).collect(Collectors.toList()))
                    .stream().collect(Collectors.toMap(ContractBaseInfo::getId, ContractBaseInfo::getProjReviewId, (a, b) -> a));
            kpiPaymentAmountRecords.forEach(e -> {
                e.setBatchNumber(batchNumber);
                e.setEffectMonth(date);
                e.setId(null);
                e.setProjReviewId(contractId2Review.get(e.getContractId()));
            });
            kpiPaymentAmountRecordService.saveBatch(kpiPaymentAmountRecords);
        }
        return BeanUtil.copyToList(kpiPaymentAmountRecords, KpiPaymentAmountBo.class);
    }

}
