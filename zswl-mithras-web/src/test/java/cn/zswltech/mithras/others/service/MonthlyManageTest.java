package cn.zswltech.mithras.others.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.dto.monthly.*;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.monthly.enums.MonthlyManagementStatusEnum;
import cn.zswltech.mithras.monthly.enums.MonthlyModuleTypeEnum;
import cn.zswltech.mithras.monthly.mapper.model.*;
import cn.zswltech.mithras.application.orchestration.monthly.*;
import cn.zswltech.mithras.web.MithrasApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yangxiong
 * @date 2024/8/14/16:19
 * @description
 */
@RunWith(SpringRunner.class)
@ActiveProfiles(value = "uat")
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MonthlyManageTest {

    @Resource
    private MonthlyManageService monthlyManageService;
    @Resource
    private MonthlyManagementBaseInfoService baseInfoService;
    @Resource
    private MonthlyManagementAirRecordService airRecordService;
    @Resource
    private MonthlyManagementRpRecordService rpRecordService;
    @Resource
    private MonthlyManagementCostRecordService costRecordService;
    @Resource
    private MonthlyFinanceStampDutyService financeStampDutyService;
    @Resource
    private MonthlyProjStampDutyService projStampDutyService;

    @Test
    public void freshCostListTest() {
        MonthlyFreshREQ req = new MonthlyFreshREQ();
        req.setYearAndMonth("2026-02");
        req.setTabType(MonthlyModuleTypeEnum.COST.name());
        costRecordService.freshCostList(req);
    }

    @Test
    public void initHistoryData() {
//        // 遍历历史数据
//        monthlyManageService.listPage(new MonthlyListREQ()).getList().forEach(item -> {
//
//            // 1、保存基本信息
//            MonthlyManagementBaseInfo baseInfo = BeanUtil.copyProperties(item, MonthlyManagementBaseInfo.class, "id", "createBy", "createTime", "updateBy", "updateTime", "deleted", "closeDate", "actualLeaseDate");
//            baseInfo.setMainId(System.currentTimeMillis());
//            String aa = item.getYearAndMonth();
//            String[] split = aa.split("-");
//            String yearAndMonth = String.format("%d-%02d", Integer.parseInt(split[0]), Integer.parseInt(split[1]));
//            LocalDate date = LocalDateTimeUtil.parseDate(yearAndMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
//            baseInfo.setMonth(date.getMonthValue());
//            baseInfo.setYear(date.getYear());
//            baseInfo.setStatus(MonthlyManagementStatusEnum.CLOSED_AMOUNT.name());
//
//            // AIR信息
//            MonthlyAIRListREQ airListReq = new MonthlyAIRListREQ();
//            airListReq.setYearAndMonth(yearAndMonth);
//            airListReq.setPage(1);
//            airListReq.setPageSize(Integer.MAX_VALUE);
//            List<MonthlyManagementAirRecord> airRecordList = new ArrayList<>(64);
//            monthlyManageService.airList(airListReq).getList().forEach(airItem -> {
//                MonthlyManagementAirRecord airRecord = BeanUtil.copyProperties(airItem, MonthlyManagementAirRecord.class, "id", "createBy", "createTime", "updateBy", "updateTime", "deleted", "actualLeaseDate");
//                airRecord.setMainId(baseInfo.getMainId());
//                airRecord.setNewUpdate(0);
//                airRecord.setIsConfirmed(1);
//                airRecord.setIsEffect(1);
//                airRecord.setIsSendCq(1);
//                airRecord.setSourceId(airItem.getReceiptId());
//                airRecord.setActualLeaseDate(airItem.getActualLeaseDate().atStartOfDay());
//                airRecordList.add(airRecord);
//            });
//
//            // RP信息
//            List<MonthlyManagementRpRecord> rpRecordList = new ArrayList<>(64);
//            MonthlyRPListREQ rpListReq = new MonthlyRPListREQ();
//            rpListReq.setPage(1);
//            rpListReq.setPageSize(Integer.MAX_VALUE);
//            rpListReq.setYearAndMonth(yearAndMonth);
//            monthlyManageService.rpList(rpListReq).getList().forEach(rpItem -> {
//                MonthlyManagementRpRecord rpRecord = BeanUtil.copyProperties(rpItem, MonthlyManagementRpRecord.class, "id", "createBy", "createTime", "updateBy", "updateTime", "deleted", "actualLeaseDate", "theLatestFullRefundRentDate");
//                rpRecord.setMainId(baseInfo.getMainId());
//                rpRecord.setNewUpdate(0);
//                rpRecord.setIsConfirmed(1);
//                rpRecord.setIsEffect(1);
//                rpRecord.setIsSendCq(1);
//                rpRecord.setSourceId(rpItem.getReceiptId());
//                rpRecord.setActualLeaseDate(rpItem.getActualLeaseDate().atStartOfDay());
//                rpRecord.setTheLatestFullRefundRentDate(rpItem.getTheLatestFullRefundRentDate().atStartOfDay());
//                rpRecordList.add(rpRecord);
//            });
//
//            // COST信息
//            List<MonthlyManagementCostRecord> costRecordList = new ArrayList<>(64);
//            MonthlyCostREQ costReq = new MonthlyCostREQ();
//            costReq.setYearAndMonth(yearAndMonth);
//            costReq.setPage(1);
//            costReq.setPageSize(Integer.MAX_VALUE);
//            monthlyManageService.costList(costReq).forEach(costItem -> {
//                MonthlyManagementCostRecord costRecord = BeanUtil.copyProperties(costItem, MonthlyManagementCostRecord.class, GlobalConstants.COPY_IGNORE_COMMON_FIELD);
//                costRecord.setMainId(baseInfo.getMainId());
//                costRecord.setNewUpdate(0);
//                costRecord.setIsConfirmed(1);
//                costRecord.setIsEffect(1);
//                costRecord.setIsSendCq(1);
//                costRecord.setSourceId(costItem.getId());
//                costRecordList.add(costRecord);
//            });
//
//            // FiNANCE_STAMP_DUTY信息
//            List<MonthlyFinanceStampDuty> financeStampDutyList = new ArrayList<>(64);
//            MonthlyStampDutyFinREQ financeStampDutyReq = new MonthlyStampDutyFinREQ();
//            financeStampDutyReq.setYearAndMonth(yearAndMonth);
//            financeStampDutyReq.setPage(1);
//            financeStampDutyReq.setPageSize(Integer.MAX_VALUE);
//            monthlyManageService.finPage(financeStampDutyReq).getList().forEach(financeStampDutyItem -> {
//                MonthlyFinanceStampDuty financeStampDuty = BeanUtil.copyProperties(financeStampDutyItem, MonthlyFinanceStampDuty.class, GlobalConstants.COPY_IGNORE_COMMON_FIELD);
//                financeStampDuty.setMainId(baseInfo.getMainId());
//                financeStampDuty.setNewUpdate(0);
//                financeStampDuty.setIsConfirmed(1);
//                financeStampDuty.setIsEffect(1);
//                financeStampDuty.setIsSendCq(1);
//                financeStampDuty.setSourceId(financeStampDutyItem.getId());
//                financeStampDutyList.add(financeStampDuty);
//            });
//
//            // PROJ_STAMP_DUTY信息
//            List<MonthlyProjStampDuty> projStampDutyList = new ArrayList<>(64);
//            MonthlyStampDutyProjREQ projStampDutyReq = new MonthlyStampDutyProjREQ();
//            projStampDutyReq.setYearAndMonth(yearAndMonth);
//            projStampDutyReq.setPage(1);
//            projStampDutyReq.setPageSize(Integer.MAX_VALUE);
//            monthlyManageService.projPage(projStampDutyReq).getList().forEach(projStampDutyItem -> {
//                MonthlyProjStampDuty projStampDuty = BeanUtil.copyProperties(projStampDutyItem, MonthlyProjStampDuty.class, GlobalConstants.COPY_IGNORE_COMMON_FIELD);
//                projStampDuty.setMainId(baseInfo.getMainId());
//                projStampDuty.setNewUpdate(0);
//                projStampDuty.setIsConfirmed(1);
//                projStampDuty.setIsEffect(1);
//                projStampDuty.setIsSendCq(1);
//                projStampDuty.setSourceId(projStampDutyItem.getId());
//                projStampDutyList.add(projStampDuty);
//            });
//
//            // 保存数据
//            baseInfoService.save(baseInfo);
//            if (!airRecordList.isEmpty()) {
//                airRecordService.saveBatch(airRecordList);
//            }
//            if (!rpRecordList.isEmpty()) {
//                rpRecordService.saveBatch(rpRecordList);
//            }
//            if (!costRecordList.isEmpty()) {
//                costRecordService.saveBatch(costRecordList);
//            }
//            if (!financeStampDutyList.isEmpty()) {
//                financeStampDutyService.saveBatch(financeStampDutyList);
//            }
//            if (!projStampDutyList.isEmpty()) {
//                projStampDutyService.saveBatch(projStampDutyList);
//            }
//        });
    }
}
