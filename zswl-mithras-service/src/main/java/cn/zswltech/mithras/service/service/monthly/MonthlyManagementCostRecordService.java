package cn.zswltech.mithras.service.service.monthly;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.monthly.MonthlyCostREQ;
import cn.zswltech.mithras.dto.monthly.MonthlyCostRSP;
import cn.zswltech.mithras.dto.monthly.MonthlyFreshREQ;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingTimeLimitTypeEnum;
import cn.zswltech.mithras.service.enums.monthly.MonthlyModuleTypeEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.monthly.*;
import cn.zswltech.mithras.service.mapper.monthly.FundsDailyCostMapper;
import cn.zswltech.mithras.service.mapper.monthly.MonthlyManagementCostRecordMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @description 针对表【monthly_management_cost_record(成本计提-记录表)】的数据库操作Service实现
 * @createDate 2024-07-24 15:12:50
 */
@Slf4j
@Service
public class MonthlyManagementCostRecordService extends ServiceImpl<MonthlyManagementCostRecordMapper, MonthlyManagementCostRecord>
        implements IService<MonthlyManagementCostRecord> {

    private static final String BEGINNING_ITEM_TEXT = "期初余额";
    @Resource
    private FundFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundDirectFinancingBaseInfoService fundDirectFinancingBaseInfoService;
    @Resource
    private FundsDailyCostService fundsDailyCostService;
    @Resource
    private MonthlyManagementCostRecordService costRecordService;
    @Resource
    private MonthlyManagementBaseInfoService baseInfoService;
    @Autowired
    private FundFinancingBaseInfoService fundFinancingBaseInfoService;

    @Transactional(rollbackFor = Throwable.class)
    public MonthlyManagementCostRecord freshCostList(MonthlyFreshREQ req) {
        String yearAndMonth = req.getYearAndMonth();
        if (StringUtils.isEmpty(yearAndMonth)) {
            throw new MithrasException("月份时间格式为空");
        }
        MonthlyManagementBaseInfo baseInfo = baseInfoService.getMonthlyManagementBaseInfo(yearAndMonth);
        LocalDate localDate = LocalDateTimeUtil.parseDate(yearAndMonth, DatePattern.NORM_MONTH_PATTERN);
        LocalDate endOfMonthDay = LocalDate.of(localDate.getYear(), localDate.getMonthValue(), localDate.lengthOfMonth());
        List<FundsDailyCost> fundsDailyCostList = fundsDailyCostService.getByInterestDate(endOfMonthDay, endOfMonthDay);
        if (CollectionUtil.isEmpty(fundsDailyCostList)) {
            log.info("{}不存在任何成本计提数据", yearAndMonth);
            return null;
        }
        if (Objects.nonNull(req.getSourceId())) {
            fundsDailyCostList.removeIf(e -> !Objects.equals(req.getSourceId(), e.getMainId()));
        }
        Map<Long, List<FundsDailyCost>> monthSumMap = fundsDailyCostList.stream().collect(Collectors.groupingBy(FundsDailyCost::getMainId));
        // 取主表
        List<FundsDailyCostMain> mainList = SpringUtil.getBean(FundsDailyCostMainService.class).listByIds(fundsDailyCostList.stream().map(FundsDailyCost::getMainId).collect(Collectors.toSet()));
        if (CollectionUtil.isEmpty(mainList)) {
            log.info("FundsDailyCostMain表不存在目标数据，不执行后续逻辑");
            return null;
        }
        Map<Long, List<FundsDailyCost>> fundsDailyCostListMap = fundsDailyCostList.stream().collect(Collectors.groupingBy(FundsDailyCost::getMainId));
        // 取当年统计数据
        List<FundsDailyCostMapper.SumGroupMainBO> yearSumList = SpringUtil.getBean(FundsDailyCostMapper.class).selectSumGroupByMainId(LocalDate.of(endOfMonthDay.getYear(), 1, 1), LocalDate.of(endOfMonthDay.getYear(), 12, 31));
        Map<Long, FundsDailyCostMapper.SumGroupMainBO> yearSumMap = yearSumList.stream().collect(Collectors.toMap(FundsDailyCostMapper.SumGroupMainBO::getMainId, e -> e));
        // 找融资数据
        Set<Long> indirectIds = mainList.stream().filter(e -> StrUtil.equals(e.getFinancingType(), "DK")).map(FundsDailyCostMain::getFinancingId).collect(Collectors.toSet());
        Set<Long> directIds = mainList.stream().filter(e -> StrUtil.equals(e.getFinancingType(), "ZR")).map(FundsDailyCostMain::getFinancingId).collect(Collectors.toSet());
        Map<Long, FundFinancingBaseInfo> fundFinancingId2Bean = fundFinancingBaseInfoService.listByIds(indirectIds).stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, e -> e));
        Map<Long, FundDirectFinancingBaseInfo> fundDirectFinancingId2Bean = fundDirectFinancingBaseInfoService.listByIds(directIds).stream().collect(Collectors.toMap(FundDirectFinancingBaseInfo::getId, e -> e));
        // 找到当前月份已经存在的数据
        List<MonthlyManagementCostRecord> existCostList = costRecordService.list(Wrappers.<MonthlyManagementCostRecord>lambdaQuery()
                .eq(MonthlyManagementCostRecord::getMainId, baseInfo.getMainId()));
        List<Long> existedIdList = new ArrayList<>();
        Map<Long, MonthlyManagementCostRecord> existedMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(existCostList)) {
            existedIdList = existCostList.stream().map(MonthlyManagementCostRecord::getSourceId).collect(Collectors.toList());
            existedMap = existCostList.stream().collect(Collectors.toMap(MonthlyManagementCostRecord::getSourceId, Function.identity(), (v1, v2) -> v1));
        }
        List<MonthlyManagementCostRecord> allList = new LinkedList<>();
        List<MonthlyManagementCostRecord> needInsertList = new LinkedList<>();
        List<MonthlyManagementCostRecord> needUpdateList = new LinkedList<>();
        for (FundsDailyCostMain main : mainList) {
            MonthlyManagementCostRecord costRecord = new MonthlyManagementCostRecord();
            costRecord.setBusinessType(main.getFinancingType());
            costRecord.setFinancingId(main.getFinancingId());
            costRecord.setOrganizationName(main.getFinancingChannel());
            costRecord.setPropertyType(main.getLeaseType());
            if (StringUtils.isNotBlank(main.getLeaseType())) {
                costRecord.setPropertyType(main.getLeaseType());
                LeaseType leaseType = LeaseType.of(main.getLeaseType());
                if (leaseType != null) {
                    costRecord.setPropertyTypeDisplay(leaseType.display());
                }
            }
            if (StrUtil.isBlank(costRecord.getPropertyTypeDisplay()) && StrUtil.isNotBlank(main.getBizType())) {
                costRecord.setPropertyType(main.getBizType());
                ProjectBizType projectBizType = ProjectBizType.of(main.getBizType());
                if (Objects.nonNull(projectBizType)) {
                    costRecord.setPropertyTypeDisplay(projectBizType.display());
                }
            }
            if ("DK".equalsIgnoreCase(main.getFinancingType())) {
                FundFinancingBaseInfo fundFinancingBaseInfo = fundFinancingId2Bean.get(main.getFinancingId());
                costRecord.setFinancingCode(fundFinancingBaseInfo.getFinancingCode());
                costRecord.setFinancingAmount(fundFinancingBaseInfo.getFinancingAmount());
                costRecord.setValueDate(fundFinancingBaseInfo.getActualLoanDate());
                costRecord.setLoanProperty(fundFinancingBaseInfo.getTimeLimitType());
            } else if ("ZR".equalsIgnoreCase(main.getFinancingType())) {
                FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo = fundDirectFinancingId2Bean.get(main.getFinancingId());
                costRecord.setFinancingCode(fundDirectFinancingBaseInfo.getFinancingCode());
                costRecord.setFinancingAmount(fundDirectFinancingBaseInfo.getFinancingAmount() * 10000);
                costRecord.setValueDate(fundDirectFinancingBaseInfo.getDurationFrom());
                costRecord.setLoanProperty(FundFinancingTimeLimitTypeEnum.BOND_PAYABLE.name());
            }
            costRecord.setTermCapitalCost(Optional.ofNullable(monthSumMap.get(main.getId())).map(e -> e.stream().filter(item -> Objects.nonNull(item.getFinancingCost())).mapToLong(FundsDailyCost::getFinancingCost).sum()).orElse(0L));
            costRecord.setTermCapitalCostAfterTax(Optional.ofNullable(monthSumMap.get(main.getId())).map(e -> e.stream().filter(item -> Objects.nonNull(item.getFinancingCostAfterTax())).mapToLong(FundsDailyCost::getFinancingCostAfterTax).sum()).orElse(0L));
            costRecord.setTotalCapitalCost(Optional.ofNullable(yearSumMap.get(main.getId())).map(FundsDailyCostMapper.SumGroupMainBO::getSumAmount).orElse(0L));
            costRecord.setTotalCapitalCostAfterTax(Optional.ofNullable(yearSumMap.get(main.getId())).map(FundsDailyCostMapper.SumGroupMainBO::getSumAmountAfterTax).orElse(0L));
            costRecord.setFinancingRate(main.getFinancingRate());
            costRecord.setDailyRate(MonthlyManageService.calculateFinancingRateDaily(main.getFinancingRate()).intValue());
            costRecord.setFinancingCostDiff(Optional.ofNullable(monthSumMap.get(main.getId())).map(e -> e.stream().filter(item -> Objects.nonNull(item.getFinancingCostDiff())).mapToLong(FundsDailyCost::getFinancingCostDiff).sum()).orElse(0L));
            costRecord.setBeginOfPeriodInterestBalance(Optional.ofNullable(monthSumMap.get(main.getId())).map(e -> e.stream().filter(item -> Objects.nonNull(item.getBeginOfPeriodInterestBalance())).mapToLong(FundsDailyCost::getBeginOfPeriodInterestBalance).sum()).orElse(0L));
            costRecord.setEndOfPeriodInterestBalance(Optional.ofNullable(monthSumMap.get(main.getId())).map(e -> e.stream().filter(item -> Objects.nonNull(item.getEndOfPeriodInterestBalance())).mapToLong(FundsDailyCost::getEndOfPeriodInterestBalance).sum()).orElse(0L));
            List<FundsDailyCost> costList = fundsDailyCostListMap.get(main.getId());
            if (CollectionUtil.isNotEmpty(costList)) {
                long balance = costList.stream().filter(e -> Objects.nonNull(e.getFinancingAmount())).mapToLong(FundsDailyCost::getFinancingAmount).sum();
                costRecord.setRemainingAmount(balance);
            } else {
                costRecord.setRemainingAmount(costRecord.getFinancingAmount());
            }
            costRecord.setSourceId(main.getId());
            costRecord.setMainId(baseInfo.getMainId());
            costRecord.setNewUpdate(YesOrNoNumberEnum.NO.getCode());
            if (costRecord.getTermCapitalCost() <= 0 || costRecord.getTermCapitalCostAfterTax() <= 0) {
                log.warn("当期计提利息小于等于0，丢弃该数据[{}]", JSONUtil.toJsonStr(costRecord));
                continue;
            }
            allList.add(costRecord);
        }
        if (!allList.isEmpty() && Objects.isNull(req.getSourceId())) {
            for (MonthlyManagementCostRecord collect : allList) {
                MonthlyManagementCostRecord costRecord = existedMap.get(collect.getSourceId());
                if (existedIdList.contains(collect.getSourceId())) {
                        // 根据状态是否确认，判断是否需要更新
                        boolean needChange = YesOrNoNumberEnum.YES.getCode().equals(costRecord.getIsConfirmed());
                        if (needChange) {
                            return null;
                        }
                }
                if (existedIdList.contains(collect.getSourceId())) {
                    collect.setId(costRecord.getId());
                    collect.setIsConfirmed(collect.getIsConfirmed());
                    needUpdateList.add(collect);
                } else {
                    collect.setId(null);
                    collect.setIsConfirmed(YesOrNoNumberEnum.NO.getCode());
                    needInsertList.add(collect);
                }
            }
            if (!needUpdateList.isEmpty()) {
                costRecordService.updateBatchById(needUpdateList);
            }
            if (!needInsertList.isEmpty()) {
                costRecordService.saveBatch(needInsertList);
            }
        }
        if (!allList.isEmpty() && Objects.nonNull(req.getSourceId())){
            return allList.get(0);
        }
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public List<FundsDailyCost> getIncrementFunds(List<FundsDailyCost> todoList) {
        List<FundsDailyCost> fundsRes = new ArrayList<>();
        //查询直融
        LambdaQueryWrapper<FundDirectFinancingBaseInfo> directQuery = Wrappers.lambdaQuery();
        directQuery.eq(FundDirectFinancingBaseInfo::getObsolete, false);
        List<FundDirectFinancingBaseInfo> fundDirectFinancingBaseInfoList = fundDirectFinancingBaseInfoService.list(directQuery);
        Set<Long> directFinancingFilterIds = new HashSet<>();
        List<FundDirectFinancingBaseInfo> directRes = new ArrayList<>();
        if (fundDirectFinancingBaseInfoList != null && !fundDirectFinancingBaseInfoList.isEmpty()) {
            for (FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo : fundDirectFinancingBaseInfoList) {
                for (FundsDailyCost fundsDailyCost : todoList) {
                    if (fundDirectFinancingBaseInfo.getId().equals(fundsDailyCost.getFinancingId())
                            && "ZR".equalsIgnoreCase(fundsDailyCost.getType())) {
                        directFinancingFilterIds.add(fundDirectFinancingBaseInfo.getId());
                    }
                }
            }
            for (FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo : fundDirectFinancingBaseInfoList) {
                boolean contains = false;
                for (Long directFinancingFilterId : directFinancingFilterIds) {
                    if (directFinancingFilterId.equals(fundDirectFinancingBaseInfo.getId())) {
                        contains = true;
                        break;
                    }
                }
                if (!contains) {
                    directRes.add(fundDirectFinancingBaseInfo);
                }
            }
        }

        //查询间融
        LambdaQueryWrapper<FundFinancingBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name());
        List<FundFinancingBaseInfo> fundFinancingBaseInfoList = financingBaseInfoService.list(query);
        Set<Long> financingFilterIds = new HashSet<>();
        List<FundFinancingBaseInfo> res = new ArrayList<>();
        if (fundFinancingBaseInfoList != null && !fundFinancingBaseInfoList.isEmpty()) {
            for (FundFinancingBaseInfo fundFinancingBaseInfo : fundFinancingBaseInfoList) {
                for (FundsDailyCost fundsDailyCost : todoList) {
                    if (fundFinancingBaseInfo.getId().equals(fundsDailyCost.getFinancingId())
                            && "DK".equalsIgnoreCase(fundsDailyCost.getType())) {
                        financingFilterIds.add(fundFinancingBaseInfo.getId());
                    }
                }
            }
            for (FundFinancingBaseInfo fundFinancingBaseInfo : fundFinancingBaseInfoList) {
                boolean contains = false;
                for (Long financingFilterId : financingFilterIds) {
                    if (financingFilterId.equals(fundFinancingBaseInfo.getId())) {
                        contains = true;
                        break;
                    }
                }
                if (!contains) {
                    res.add(fundFinancingBaseInfo);
                }
            }
        }

        //直融入库
        if (!directRes.isEmpty()) {
            List<FundsDailyCost> directFundsDailyCostList = new ArrayList<>();
            for (FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo : directRes) {
                FundsDailyCost fundsDailyCost = new FundsDailyCost();
                fundsDailyCost.setItemText(BEGINNING_ITEM_TEXT);
                fundsDailyCost.setFinancingId(fundDirectFinancingBaseInfo.getId());
                fundsDailyCost.setType("ZR");
                fundsDailyCost.setFinancingAmount(fundDirectFinancingBaseInfo.getFinancingAmount());
                fundsDailyCost.setInterestDate(fundDirectFinancingBaseInfo.getDurationFrom() != null ?
                        fundDirectFinancingBaseInfo.getDurationFrom() : LocalDate.now());
                directFundsDailyCostList.add(fundsDailyCost);
            }
            fundsRes.addAll(directFundsDailyCostList);
            fundsDailyCostService.saveBatch(directFundsDailyCostList);
        }

        //间融入库
        if (!res.isEmpty()) {
            List<FundsDailyCost> fundsDailyCostList = new ArrayList<>();
            for (FundFinancingBaseInfo fundFinancingBaseInfo : res) {
                FundsDailyCost fundsDailyCost = new FundsDailyCost();
                fundsDailyCost.setItemText(BEGINNING_ITEM_TEXT);
                fundsDailyCost.setFinancingId(fundFinancingBaseInfo.getId());
                fundsDailyCost.setType("DK");
                fundsDailyCost.setFinancingAmount(fundFinancingBaseInfo.getFinancingAmount());
                fundsDailyCost.setInterestDate(fundFinancingBaseInfo.getActualLoanDate() != null
                        ? fundFinancingBaseInfo.getActualLoanDate() : LocalDate.now());
                fundsDailyCostList.add(fundsDailyCost);
            }
            fundsRes.addAll(fundsDailyCostList);
            fundsDailyCostService.saveBatch(fundsDailyCostList);
        }
        fundsRes.addAll(todoList);
        return fundsRes;
    }

    public PageR<MonthlyCostRSP> costList(MonthlyCostREQ req) {
        MonthlyManagementBaseInfo baseInfo = baseInfoService.getMonthlyManagementBaseInfo(req.getYearAndMonth());
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(String.format("%s的月结主数据不存在, 请先创建月结主信息", req.getYearAndMonth()));
        }
        Page<MonthlyManagementCostRecord> page = new Page<>(req.getPage(), req.getPageSize());
        Page<MonthlyManagementCostRecord> recordPage = costRecordService.page(page, Wrappers.<MonthlyManagementCostRecord>lambdaQuery()
                .like(CharSequenceUtil.isNotBlank(req.getFinancingCode()), MonthlyManagementCostRecord::getFinancingCode, req.getFinancingCode())
                .like(CharSequenceUtil.isNotBlank(req.getOrganizationName()), MonthlyManagementCostRecord::getOrganizationName, req.getOrganizationName())
                .eq(MonthlyManagementCostRecord::getMainId, baseInfo.getMainId())
                .eq(CharSequenceUtil.isNotBlank(req.getBatchNumber()), MonthlyManageBaseModel::getBatchNumber, req.getBatchNumber()));

        if (recordPage.getRecords().isEmpty()) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }

        List<MonthlyCostRSP> rspList = recordPage.getRecords().stream()
                .map(dto -> {
                    MonthlyCostRSP rsp = new MonthlyCostRSP();
                    BeanUtil.copyProperties(dto, rsp);
                    rsp.setYearAndMonth(String.format("%d-%02d", baseInfo.getYear(), baseInfo.getMonth()));
                    rsp.setTabType(MonthlyModuleTypeEnum.COST.name());
                    rsp.setNewUpdated(dto.getNewUpdate());
                    return rsp;
                }).collect(Collectors.toList());

        return PageR.of(rspList, recordPage.getTotal(), recordPage.getPages(), recordPage.getCurrent(), recordPage.getSize());
    }
}




