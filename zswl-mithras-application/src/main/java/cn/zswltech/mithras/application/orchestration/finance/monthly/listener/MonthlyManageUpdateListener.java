package cn.zswltech.mithras.application.orchestration.finance.monthly.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.monthly.MonthlyFreshREQ;
import cn.zswltech.mithras.dto.monthly.MonthlyQuery;
import cn.zswltech.mithras.dto.monthly.MonthlyQueryResult;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.finance.monthly.event.MonthlyManageUpdateEvent;
import cn.zswltech.mithras.finance.monthly.enums.MonthlyManagementStatusEnum;
import cn.zswltech.mithras.finance.monthly.enums.MonthlyModuleTypeEnum;
import cn.zswltech.mithras.contract.mapper.contract.ContractIncomeSharingMapper;
import cn.zswltech.mithras.finance.monthly.mapper.model.*;
import cn.zswltech.mithras.finance.monthly.service.MonthlyFinanceStampDutyService;
import cn.zswltech.mithras.finance.monthly.service.MonthlyProjStampDutyService;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.finance.monthly.*;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @date 2024/8/4/16:56
 * @description
 */
@Slf4j
@Component
public class MonthlyManageUpdateListener implements ApplicationListener<MonthlyManageUpdateEvent> {

    private static final String LOG_TEXT = "月结管理监听到数据修改，但是传递参数对应数据不存在";
    @Resource
    private MonthlyManagementAirRecordService airRecordService;
    @Resource
    private MonthlyManagementRpRecordService rpRecordService;
    @Resource
    private ContractIncomeSharingMapper contractIncomeSharingMapper;
    @Resource
    private MonthlyManagementCostRecordService costRecordService;
    @Resource
    private MonthlyFinanceStampDutyService financeStampDutyService;
    @Resource
    private MonthlyProjStampDutyService projStampDutyService;
    @Resource
    private MonthlyManagementBaseInfoService baseInfoService;
    @Resource
    private MonthlyStampDutyService stampDutyService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void onApplicationEvent(MonthlyManageUpdateEvent updateEvent) {
        try {
            log.info("MonthlyManageUpdateListener onApplicationEvent：监听到数据修改事件：{}", JSONUtil.toJsonStr(updateEvent));
            MonthlyManageUpdateEvent.DataObject dataObject = updateEvent.getDataObject();
            MonthlyModuleTypeEnum moduleTypeEnum = MonthlyModuleTypeEnum.find(dataObject.getModelType());
            if (Objects.isNull(moduleTypeEnum)) {
                throw new MithrasException("模块类型不存在");
            }
            if (!CharSequenceUtil.equalsAny(moduleTypeEnum.name(), MonthlyModuleTypeEnum.AIR.name(), MonthlyModuleTypeEnum.RP.name())
                    && CharSequenceUtil.isBlank(dataObject.getYearAndMonth())) {
                throw new MithrasException("年月参数不能为空");
            }

            MonthlyManagementBaseInfo baseInfo = new MonthlyManagementBaseInfo();
            if (!CharSequenceUtil.equalsAny(moduleTypeEnum.name(), MonthlyModuleTypeEnum.AIR.name(), MonthlyModuleTypeEnum.RP.name())) {
                baseInfo = baseInfoService.getMonthlyManagementBaseInfo(dataObject.getYearAndMonth());
            }
            if (Objects.isNull(baseInfo)) {
                throw new MithrasException("对应月份月结数据不存在");
            }
            MonthlyFreshREQ req = new MonthlyFreshREQ();
            req.setSourceId(dataObject.getRecordId());
            req.setYearAndMonth(dataObject.getYearAndMonth());
            switch (moduleTypeEnum) {
                case AIR: {
                    List<Long> airRecordIdList = new ArrayList<>();
                    // 收入分摊表需要自己手动找到当前未关账的月结主表信息
                    baseInfo = baseInfoService.getOne(Wrappers.<MonthlyManagementBaseInfo>lambdaQuery()
                            .ne(MonthlyManagementBaseInfo::getStatus, MonthlyManagementStatusEnum.CLOSED_AMOUNT.name())
                            .orderByDesc(MonthlyManagementBaseInfo::getYear)
                            .orderByAsc(MonthlyManagementBaseInfo::getMonth)
                            .last(StringUtil.mysqlLimitOne()));
                    //找到现有数据
                    List<MonthlyManagementAirRecord> airRecordList = airRecordService.list(Wrappers.<MonthlyManagementAirRecord>lambdaQuery()
                            .eq(MonthlyManageBaseModel::getSourceId, dataObject.getRecordId()));
                    if (CollUtil.isEmpty(airRecordList)) {
                        log.error("{}", MonthlyModuleTypeEnum.AIR.display() + LOG_TEXT);
                        throw new MithrasException(MonthlyModuleTypeEnum.AIR.display() + LOG_TEXT);
                    }
                    MonthlyQuery query = new MonthlyQuery();
                    query.setReceiptId(dataObject.getRecordId());
                    LocalDate month = LocalDate.of(baseInfo.getYear(), baseInfo.getMonth(), 1);
                    query.setBeginMonth(month);
                    query.setEndMonth(month.plusMonths(1).minusDays(1));
                    Page<MonthlyQueryResult> page = new Page<>(1, Integer.MAX_VALUE);
                    List<MonthlyQueryResult> resultList = contractIncomeSharingMapper.queryMonthlyData(page, query).getRecords();
                    if (resultList.isEmpty()) {
                        log.error("{}", MonthlyModuleTypeEnum.AIR.display() + LOG_TEXT);
                        throw new MithrasException(MonthlyModuleTypeEnum.AIR.display() + LOG_TEXT);
                    }
                    Map<Long, MonthlyManagementAirRecord> airRecordMap = airRecordList.stream().collect(Collectors.toMap(MonthlyManagementAirRecord::getReceiptId, Function.identity(), (a, b) -> a));
                    for (MonthlyQueryResult queryResult : resultList) {
                        if (Objects.isNull(queryResult.getIncomeDate())) {
                            log.error("{}", MonthlyModuleTypeEnum.AIR.display() + ": 收入日期为空");
                            return;
                        }
                        MonthlyManagementAirRecord airRecord = airRecordMap.get(queryResult.getReceiptId());
                        if (Objects.isNull(airRecord)) {
                            log.error("{}", MonthlyModuleTypeEnum.AIR.display() + ": 找不到原始数据");
                            return;
                        }
                        //做比较
                        boolean isChanged = (!Objects.equals(airRecord.getIncomeSum(), queryResult.getIncomeSum())
                                || !Objects.equals(airRecord.getIncomeWithoutTaxSum(), queryResult.getIncomeWithoutTaxSum()))
                                && Objects.equals(MonthlyManagementStatusEnum.CONFIRMED.name(), baseInfo.getStatus());
                        if (isChanged) {
                            //做更新操作
                            airRecordIdList.add(airRecord.getId());
                        }
                    }
                    if (CollUtil.isNotEmpty(airRecordIdList)) {
                        airRecordService.lambdaUpdate()
                                .in(MonthlyManageBaseModel::getId, airRecordIdList)
                                .set(MonthlyManageBaseModel::getNewUpdate, YesOrNoNumberEnum.YES.getCode())
                                .update();
                    }
                    break;
                }
                case COST: {
                    MonthlyManagementCostRecord costRecord = costRecordService.getOne(Wrappers.<MonthlyManagementCostRecord>lambdaQuery()
                            .eq(MonthlyManageBaseModel::getSourceId, dataObject.getRecordId()));
                    if (Objects.isNull(costRecord)) {
                        log.error("{}", MonthlyModuleTypeEnum.COST.display() + LOG_TEXT);
                        throw new MithrasException(MonthlyModuleTypeEnum.COST.display() + LOG_TEXT);
                    }
                    //找到新的数据
                    MonthlyFreshREQ freshReq = new MonthlyFreshREQ(dataObject.getYearAndMonth(), dataObject.getRecordId(), MonthlyModuleTypeEnum.COST.name());
                    MonthlyManagementCostRecord freshCostList = costRecordService.freshCostList(freshReq);
                    if (Objects.isNull(freshCostList)) {
                        log.error("{}", MonthlyModuleTypeEnum.COST.display() + LOG_TEXT);
                        throw new MithrasException(MonthlyModuleTypeEnum.COST.display() + LOG_TEXT);
                    }
                    // 做比较
                    boolean isChanged = (!Objects.equals(costRecord.getFinancingCost(), freshCostList.getFinancingCost())
                            || !Objects.equals(costRecord.getFinancingRate(), freshCostList.getFinancingRate())
                            || !Objects.equals(costRecord.getFinancingAmount(), freshCostList.getFinancingAmount())
                            || !Objects.equals(costRecord.getTermCapitalCost(), freshCostList.getTermCapitalCost())
                            || !Objects.equals(costRecord.getTermCapitalCostAfterTax(), freshCostList.getTermCapitalCostAfterTax())
                            || !Objects.equals(costRecord.getTotalCapitalCost(), freshCostList.getTotalCapitalCost())
                            || !Objects.equals(costRecord.getTotalCapitalCostAfterTax(), freshCostList.getTotalCapitalCostAfterTax()))
                            && Objects.equals(MonthlyManagementStatusEnum.CONFIRMED.name(), baseInfo.getStatus());
                    if (isChanged) {
                        //做更新操作
                        MonthlyManagementCostRecord newRecord = new MonthlyManagementCostRecord();
                        newRecord.setId(costRecord.getId());
                        newRecord.setNewUpdate(YesOrNoNumberEnum.YES.getCode());
                        costRecordService.updateById(costRecord);
                    }
                    break;
                }
                case RP: {
                    baseInfo = baseInfoService.getOne(Wrappers.<MonthlyManagementBaseInfo>lambdaQuery()
                            .ne(MonthlyManagementBaseInfo::getStatus, MonthlyManagementStatusEnum.CLOSED_AMOUNT.name())
                            .orderByDesc(MonthlyManagementBaseInfo::getYear)
                            .orderByAsc(MonthlyManagementBaseInfo::getMonth)
                            .last(StringUtil.mysqlLimitOne()));
                    //找到现有数据
                    List<MonthlyManagementRpRecord> rpRecordList = rpRecordService.list(Wrappers.<MonthlyManagementRpRecord>lambdaQuery()
                            .eq(MonthlyManageBaseModel::getSourceId, dataObject.getRecordId()));
                    if (CollUtil.isEmpty(rpRecordList)) {
                        log.error("{}", MonthlyModuleTypeEnum.RP.display() + LOG_TEXT);
                        throw new MithrasException(MonthlyModuleTypeEnum.RP.display() + LOG_TEXT);
                    }
                    MonthlyQuery query = new MonthlyQuery();
                    query.setReceiptId(dataObject.getRecordId());
                    LocalDate month = LocalDate.of(baseInfo.getYear(), baseInfo.getMonth(), 1);
                    query.setBeginMonth(month);
                    query.setEndMonth(month.plusMonths(1).minusDays(1));
                    Page<MonthlyQueryResult> page = new Page<>(1, Integer.MAX_VALUE);
                    List<MonthlyQueryResult> resultList = contractIncomeSharingMapper.queryMonthlyData(page, query).getRecords();
                    if (CollUtil.isEmpty(resultList)) {
                        return;
                    }
                    Map<Long, MonthlyManagementRpRecord> rpRecordMap = rpRecordList.stream().collect(Collectors.toMap(MonthlyManagementRpRecord::getReceiptId, Function.identity(), (o, o2) -> o));
                    List<Long> rpRecordIdList = new ArrayList<>();
                    for (MonthlyQueryResult res : resultList) {
                        MonthlyManagementRpRecord rpRecord = rpRecordMap.get(res.getReceiptId());
                        if (Objects.isNull(rpRecord)) {
                            log.error("{}", MonthlyModuleTypeEnum.RP.display() + ": 找不到原始数据");
                            return;
                        }
                        //做比较
                        boolean isChanged = (!Objects.equals(rpRecord.getIncomeSum(), res.getIncomeSum())
                                || !Objects.equals(rpRecord.getIncomeWithoutTaxSum(), res.getIncomeWithoutTaxSum()))
                                && Objects.equals(MonthlyManagementStatusEnum.CONFIRMED.name(), baseInfo.getStatus());
                        if (isChanged) {
                            rpRecordIdList.add(rpRecord.getId());
                        }
                    }
                    //做更新操作
                    if (CollUtil.isNotEmpty(rpRecordIdList)) {
                        rpRecordService.lambdaUpdate()
                                .in(MonthlyManageBaseModel::getId, rpRecordIdList)
                                .set(MonthlyManageBaseModel::getNewUpdate, YesOrNoNumberEnum.YES.getCode())
                                .update();
                    }
                    break;
                }
                case STAMP_DUTY_FIN: {
                    MonthlyFinanceStampDuty financeStampDuty = financeStampDutyService.getOne(Wrappers.<MonthlyFinanceStampDuty>lambdaQuery()
                            .eq(MonthlyManageBaseModel::getSourceId, dataObject.getRecordId()));
                    if (Objects.isNull(financeStampDuty)) {
                        log.error(MonthlyModuleTypeEnum.STAMP_DUTY_FIN.display(), LOG_TEXT);
                        throw new MithrasException(MonthlyModuleTypeEnum.STAMP_DUTY_FIN.display() + LOG_TEXT);
                    }
                    //数据存在，找到原本的数据
                    MonthlyStampDuty stampDuty = stampDutyService.getById(dataObject.getRecordId());
                    if (Objects.isNull(stampDuty)) {
                        log.error("{}", MonthlyModuleTypeEnum.STAMP_DUTY_FIN.display() + LOG_TEXT);
                        throw new MithrasException(MonthlyModuleTypeEnum.STAMP_DUTY_FIN.display() + LOG_TEXT);
                    }
                    if (Objects.equals(MonthlyManagementStatusEnum.CONFIRMED.name(), baseInfo.getStatus())) {
                        //做比较
                        boolean isChanged = Objects.equals(financeStampDuty.getStampDuty(), stampDuty.getStampDuty());
                        if (!isChanged) {
                            //做更新操作
                            MonthlyFinanceStampDuty newRecord = new MonthlyFinanceStampDuty();
                            newRecord.setId(financeStampDuty.getId());
                            newRecord.setNewUpdate(YesOrNoNumberEnum.YES.getCode());
                            financeStampDutyService.updateById(newRecord);
                        }
                    }
                    break;
                }
                case STAMP_DUTY_PROJ: {
                    MonthlyProjStampDuty projStampDuty = projStampDutyService.getOne(Wrappers.<MonthlyProjStampDuty>lambdaQuery()
                            .eq(MonthlyManageBaseModel::getSourceId, dataObject.getRecordId()));
                    if (Objects.isNull(projStampDuty)) {
                        log.error("{}", MonthlyModuleTypeEnum.STAMP_DUTY_PROJ.display() + LOG_TEXT);
                        throw new MithrasException(MonthlyModuleTypeEnum.STAMP_DUTY_PROJ.display() + LOG_TEXT);
                    }
                    MonthlyStampDuty stampDuty = stampDutyService.getById(dataObject.getRecordId());
                    if (Objects.isNull(stampDuty)) {
                        log.error("{}", MonthlyModuleTypeEnum.STAMP_DUTY_PROJ.display() + LOG_TEXT);
                        throw new MithrasException(MonthlyModuleTypeEnum.STAMP_DUTY_PROJ.display() + LOG_TEXT);
                    }
                    if (Objects.equals(MonthlyManagementStatusEnum.CONFIRMED.name(), baseInfo.getStatus())) {
                        //做比较
                        boolean isChanged = Objects.equals(projStampDuty.getStampDuty(), stampDuty.getStampDuty());
                        if (!isChanged) {
                            //做更新操作
                            MonthlyProjStampDuty newRecord = new MonthlyProjStampDuty();
                            newRecord.setId(projStampDuty.getId());
                            newRecord.setNewUpdate(YesOrNoNumberEnum.YES.getCode());
                            projStampDutyService.updateById(newRecord);
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        } catch (Exception e) {
            log.error("月结管理数据监听：数据更新失败  DATA: {}", updateEvent.getDataObject(), e);
        }
    }
}
