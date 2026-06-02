package cn.zswltech.mithras.service.service.monthly;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.kpi.parameterconfig.TaxRateConfig;
import cn.zswltech.mithras.dto.monthly.*;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.contract.enums.OverdueTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.IncomeConfirmTypeEnum;
import cn.zswltech.mithras.service.enums.kpi.KpiParameterConfigCodeEnum;
import cn.zswltech.mithras.service.enums.kpi.config.TaxRateEnum;
import cn.zswltech.mithras.service.enums.monthly.MonthlyModuleTypeEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.contract.mapper.contract.ContractIncomeSharingMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.kpi.mapper.model.KpiParameterConfig;
import cn.zswltech.mithras.service.mapper.model.monthly.MonthlyManageBaseModel;
import cn.zswltech.mithras.service.mapper.model.monthly.MonthlyManagementBaseInfo;
import cn.zswltech.mithras.service.mapper.model.monthly.MonthlyManagementRpRecord;
import cn.zswltech.mithras.service.mapper.monthly.MonthlyManagementRpRecordMapper;
import cn.zswltech.mithras.service.mapper.monthly.MonthlyUserRecordMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.contract.ContractAocPriceService;
import cn.zswltech.mithras.service.service.contract.ContractFactoringPriceService;
import cn.zswltech.mithras.service.service.contract.ContractLeasePriceService;
import cn.zswltech.mithras.service.service.contract.ContractRentActualService;
import cn.zswltech.mithras.kpi.service.KpiParameterConfigService;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @description 针对表【monthly_management_rp_record(剩余本金法-记录表)】的数据库操作Service实现
 * @createDate 2024-07-24 15:12:50
 */
@Slf4j
@Service
public class MonthlyManagementRpRecordService extends ServiceImpl<MonthlyManagementRpRecordMapper, MonthlyManagementRpRecord>
        implements IService<MonthlyManagementRpRecord> {

    @Resource
    private MonthlyManagementRpRecordService rpRecordService;
    @Resource
    private MonthlyManagementBaseInfoService baseInfoService;
    @Resource
    private ContractIncomeSharingMapper contractIncomeSharingMapper;
    @Resource
    private ContractRentActualService contractRentActualService;
    @Resource
    private KpiParameterConfigService kpiParameterConfigService;
    @Resource
    private ContractFactoringPriceService factoringPriceService;
    @Resource
    private ContractAocPriceService aocPriceService;
    @Resource
    private ContractLeasePriceService leasePriceService;
    @Resource
    private MonthlyUserRecordMapper userRecordMapper;

    public Void freshRpList(MonthlyFreshREQ req) {
        MonthlyManagementBaseInfo baseInfo = baseInfoService.getMonthlyManagementBaseInfo(req.getYearAndMonth());
        LocalDate month = baseInfoService.handleDate(req.getYearAndMonth());
        Map<Long, ContractBaseInfo> contract = baseInfoService.getContract(IncomeConfirmTypeEnum.RP, month, false);
        MonthlyQuery monthlyQuery = new MonthlyQuery();
        monthlyQuery.setEndMonth(month.with(TemporalAdjusters.lastDayOfMonth()));
        monthlyQuery.setBeginMonth(month.with(TemporalAdjusters.firstDayOfMonth()));
        monthlyQuery.setContractIdList(contract.keySet());
        Page<MonthlyQueryResult> page = new Page<>(1, Integer.MAX_VALUE);
        Page<MonthlyQueryResult> list = contractIncomeSharingMapper.queryMonthlyData(page, monthlyQuery);
        List<Long> contractIdList = list.getRecords().stream().map(MonthlyQueryResult::getContractId).collect(Collectors.toList());
        Map<Long, ContractRentActual> rentActualMap = getRent(contractIdList);
        Map<Long, Integer> contractLprMap = getContractLpr(contractIdList);
        // 找到现有数据
        List<MonthlyManagementRpRecord> existedRpList = rpRecordService.list(Wrappers.<MonthlyManagementRpRecord>lambdaQuery()
                .eq(MonthlyManagementRpRecord::getMainId, baseInfo.getMainId()));
        List<Long> longList = new ArrayList<>();
        Map<Long, MonthlyManagementRpRecord> longMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(existedRpList)) {
            longList = existedRpList.stream().map(MonthlyManagementRpRecord::getReceiptId).collect(Collectors.toList());
            longMap = existedRpList.stream().collect(Collectors.toMap(MonthlyManagementRpRecord::getReceiptId, Function.identity(), (v1, v2) -> v1));
        }
        List<Long> existedIdList = longList;
        Map<Long, MonthlyManagementRpRecord> existedMap = longMap;
        List<MonthlyManagementRpRecord> needInsertList = new ArrayList<>(16);
        List<MonthlyManagementRpRecord> needUpdateList = new ArrayList<>(16);
        KpiParameterConfig oneByConfigCode = kpiParameterConfigService.getOneByConfigCode(KpiParameterConfigCodeEnum.TAX_RATE);
        List<TaxRateConfig.Data> dataList = JSONUtil.toList(oneByConfigCode.getConfigValue(), TaxRateConfig.Data.class);
        Map<String, TaxRateConfig.Data> configMap = dataList.stream().collect(Collectors.toMap(a -> a.getTaxType() + (CharSequenceUtil.isBlank(a.getBizType()) ? "" : "_" + a.getBizType()), Function.identity(), (v1, v2) -> v1));
        list.getRecords().forEach(collect -> {
            MonthlyManagementRpRecord rpRecord = existedMap.get(collect.getReceiptId());
            if (existedIdList.contains(collect.getReceiptId())) {
                boolean isEqual = true;
                if (!collect.getIncomeSum().equals(rpRecord.getIncomeSum())) {
                    isEqual = false;
                }
                if (!collect.getIncomeWithoutTaxSum().equals(rpRecord.getIncomeWithoutTaxSum())) {
                    isEqual = false;
                }
                if (!isEqual) {
                    // 根据状态是否确认，判断是否需要更新
                    boolean needChange = YesOrNoNumberEnum.YES.getCode().equals(rpRecord.getIsConfirmed());
                    if (needChange) {
                        //已经确认的数据，变化了，需要再查看更新类型
                        return;
                    }
                }
            }
            Long contractId = collect.getContractId();
            ContractRentActual contractRentActual = Optional.ofNullable(rentActualMap.get(contractId)).orElse(new ContractRentActual());
            MonthlyManagementRpRecord rsp = new MonthlyManagementRpRecord();
            BeanUtils.copyProperties(collect, rsp, GlobalConstants.COPY_IGNORE_COMMON_FIELD);
            TaxRateEnum taxRateEnum = getConfigName(collect.getBizType(), collect.getLeaseType());
            TaxRateConfig.Data data = configMap.get(taxRateEnum.name());
            BigDecimal taxRate = BigDecimal.ZERO;
            if (Objects.equals(data.getBizType(), taxRateEnum.getBizType()) && Objects.equals(data.getTaxType(), taxRateEnum.getTaxType())) {
                taxRate = new BigDecimal(data.getTaxRate()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            }
            rsp.setTaxRate(Util.toMithrasUnit(taxRate.multiply(BigDecimal.valueOf(100))));
            // 以将逾期的数据过滤
            rsp.setOverdueType(OverdueTypeEnum.NOT_OVERDUE.name());
            rsp.setContractNominalInterestRate(contractLprMap.get(contractId));
            rsp.setTheLatestFullRefundRentPeriod(contractRentActual.getCashFlowPhase());
            rsp.setTheLatestFullRefundRentDate(contractRentActual.getCashFlowDate().atStartOfDay());
            rsp.setTheLatestFullRefundRentCapital(contractRentActual.getRemainingPrincipal());
            rsp.setMainId(baseInfo.getMainId());
            rsp.setClientId(collect.getClientId());
            rsp.setSourceId(collect.getReceiptId());
            rsp.setActualLeaseDate(collect.getActualLeaseDate().atStartOfDay());
            rsp.setNewUpdate(YesOrNoNumberEnum.NO.getCode());
            if (existedIdList.contains(collect.getReceiptId())) {
                rsp.setId(rpRecord.getId());
                needUpdateList.add(rsp);
            } else {
                needInsertList.add(rsp);
            }
        });
        if (CollectionUtils.isNotEmpty(needUpdateList)) {
            rpRecordService.updateBatchById(needUpdateList);
        }
        if (CollectionUtils.isNotEmpty(needInsertList)) {
            rpRecordService.saveBatch(needInsertList);
        }
        return null;
    }

    private TaxRateEnum getConfigName(String bizType, String leaseType) {
        TaxRateEnum taxRateEnum = null;
        if (Objects.equals(bizType, ProjectBizType.ZR.name())) {
            taxRateEnum = TaxRateEnum.ZZS_ZR;
        } else if (Objects.equals(bizType, ProjectBizType.BL.name())) {
            taxRateEnum = TaxRateEnum.ZZS_BL;
        } else {
            if (Objects.equals(leaseType, LeaseType.jyx_zu.name())) {
                taxRateEnum = TaxRateEnum.ZZS_ZL_JYX;
            } else if (Objects.equals(leaseType, LeaseType.zhi_zu.name())) {
                taxRateEnum = TaxRateEnum.XMS_ZL_ZZ;
            } else {
                taxRateEnum = TaxRateEnum.XMS_ZL_HZ;
            }
        }
        return taxRateEnum;
    }

    private Map<Long, ContractRentActual> getRent(List<Long> contractId) {
        List<ContractRentActual> list = contractRentActualService.list(Wrappers.<ContractRentActual>lambdaQuery()
                .in(CollectionUtils.isNotEmpty(contractId), ContractRentActual::getContractId, contractId)
                .le(ContractRentActual::getCashFlowDate, LocalDate.now())
                .orderByDesc(ContractRentActual::getCashFlowPhase));
        Map<Long, List<ContractRentActual>> collect = list.stream().collect(Collectors.groupingBy(ContractRentActual::getContractId));
        if (CollectionUtils.isNotEmpty(collect)) {
            return collect.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get(0)));
        } else {
            return new HashMap<>();
        }
    }

    private Map<Long, Integer> getContractLpr(List<Long> contractId) {
        List<ContractLeasePrice> leasePrice = leasePriceService.list(Wrappers.<ContractLeasePrice>lambdaQuery()
                .in(CollectionUtils.isNotEmpty(contractId), ContractLeasePrice::getContractId, contractId));
        List<ContractAocPrice> aocPrice = aocPriceService.list(Wrappers.<ContractAocPrice>lambdaQuery()
                .in(CollectionUtils.isNotEmpty(contractId), ContractAocPrice::getContractId, contractId));
        List<ContractFactoringPrice> factoringPrice = factoringPriceService.list(Wrappers.<ContractFactoringPrice>lambdaQuery()
                .in(CollectionUtils.isNotEmpty(contractId), ContractFactoringPrice::getContractId, contractId));
        Map<Long, Integer> map = new HashMap<>();
        leasePrice.forEach(price ->
                map.put(price.getContractId(), Optional.ofNullable(price.getLprPercent()).orElse(0) + Optional.ofNullable(price.getLprAddPercent()).orElse(0)));
        aocPrice.forEach(price ->
                map.put(price.getContractId(), Optional.ofNullable(price.getLprPercent()).orElse(0) + Optional.ofNullable(price.getLprAddPercent()).orElse(0)));
        factoringPrice.forEach(price ->
                map.put(price.getContractId(), Optional.ofNullable(price.getLprPercent()).orElse(0) + Optional.ofNullable(price.getLprAddPercent()).orElse(0)));
        return map;
    }

    public PageR<MonthlyRPListRSP> rpList(MonthlyRPListREQ req) {
        MonthlyManagementBaseInfo baseInfo = baseInfoService.getMonthlyManagementBaseInfo(req.getYearAndMonth());
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException("未找到该月份的月度管理数据");
        }

        Page<MonthlyManagementRpRecord> page = new Page<>(req.getPage(), req.getPageSize());
        Page<MonthlyManagementRpRecord> pageList = rpRecordService.page(page, Wrappers.<MonthlyManagementRpRecord>lambdaQuery()
                .eq(Objects.nonNull(req.getClientId()), MonthlyManagementRpRecord::getClientId, req.getClientId())
                .like(CharSequenceUtil.isNotBlank(req.getContractCode()), MonthlyManagementRpRecord::getContractCode, req.getContractCode())
                .eq(CharSequenceUtil.isNotBlank(req.getOverdueType()), MonthlyManagementRpRecord::getOverdueType, req.getOverdueType())
                .eq(MonthlyManagementRpRecord::getMainId, baseInfo.getMainId())
                .eq(CharSequenceUtil.isNotBlank(req.getBatchNumber()), MonthlyManageBaseModel::getBatchNumber, req.getBatchNumber()));

        if (pageList.getRecords().isEmpty()) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }

        List<MonthlyRPListRSP> result = pageList.getRecords().stream()
                .map(dto -> {
                    MonthlyRPListRSP rsp = new MonthlyRPListRSP();
                    BeanUtils.copyProperties(dto, rsp);
                    rsp.setYearAndMonth(String.format("%d-%02d", baseInfo.getYear(), baseInfo.getMonth()));
                    rsp.setActualLeaseDate(Optional.ofNullable(dto.getActualLeaseDate()).map(LocalDateTime::toLocalDate).orElse(null));
                    rsp.setTabType(MonthlyModuleTypeEnum.RP.name());
                    rsp.setNewUpdated(dto.getNewUpdate());
                    return rsp;
                }).collect(Collectors.toList());
        return PageR.of(result, pageList.getTotal(), pageList.getCurrent(), pageList.getSize());
    }
}




