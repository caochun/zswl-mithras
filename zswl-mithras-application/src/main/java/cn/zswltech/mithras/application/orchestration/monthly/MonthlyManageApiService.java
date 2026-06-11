package cn.zswltech.mithras.application.orchestration.monthly;

import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.monthly.*;
import cn.zswltech.mithras.contract.enums.OverdueTypeEnum;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.monthly.enums.MonthlyManagementStatusEnum;
import cn.zswltech.mithras.monthly.enums.MonthlyModuleTypeEnum;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.monthly.excel.exporter.*;
import cn.zswltech.mithras.monthly.excel.model.*;
import cn.zswltech.mithras.monthly.mapper.model.MonthlyManagementBaseInfo;
import cn.zswltech.mithras.monthly.service.MonthlyFinanceStampDutyService;
import cn.zswltech.mithras.monthly.service.MonthlyProjStampDutyService;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @date 2024/7/25/16:44
 * @description
 */
@Slf4j
@Service
public class MonthlyManageApiService {

    private static final String LOCK_KEY = "monthly_manage_submit_key";
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private MonthlyManagementBaseInfoService baseInfoService;
    @Resource
    private MonthlyManagementAirRecordService airRecordService;
    @Resource
    private MonthlyManagementCostRecordService costRecordService;
    @Resource
    private MonthlyManagementRpRecordService rpRecordService;
    @Resource
    private MonthlyFinanceStampDutyService financeStampDutyService;
    @Resource
    private MonthlyProjStampDutyService projStampDutyService;
    @Resource
    private MonthlyAIRExcelManagerExporter monthlyAirExcelManagerExporter;
    @Resource
    private MonthlyRPExcelManagerExporter monthlyRpExcelManagerExporter;
    @Resource
    private MonthlyCostExcelManagerExporter monthlyCostExcelManagerExporter;
    @Resource
    private MonthlyDutyProjExcelManagerExporter monthlyDutyProjExcelManagerExporter;
    @Resource
    private MonthlyDutyFinExcelManagerExporter monthlyDutyFinExcelManagerExporter;


    public PageR<MonthlyListRSP> listPage(MonthlyListREQ req) {
        return baseInfoService.listPage(req);
    }

    public Long addBaseInfo(MonthlyAddREQ req) {
        return baseInfoService.addBaseInfo(req);
    }

    public PageR<MonthlyAIRListRSP> airList(MonthlyAIRListREQ req) {
        return airRecordService.airList(req);
    }

    public PageR<MonthlyRPListRSP> rpList(MonthlyRPListREQ req) {
        return rpRecordService.rpList(req);
    }

    public PageR<MonthlyStampDutyProjRSP> projPage(MonthlyStampDutyProjREQ req) {
        return projStampDutyService.projPage(req);
    }

    public PageR<MonthlyStampDutyFinRSP> finPage(MonthlyStampDutyFinREQ req) {
        return financeStampDutyService.finPage(req);
    }

    public PageR<MonthlyCostRSP> costList(MonthlyCostREQ req) {
        return costRecordService.costList(req);
    }

//    public void validate(MonthlyCostValidateREQ req) {
//        MonthlyAddREQ verificationReq = new MonthlyAddREQ();
//        verificationReq.setYearAndMonth(req.getYearAndMonth());
//    }

    public void submit(MonthlySubmitREQ req) {
        boolean lockFlag = false;
        String currentUserName = Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getAccount).orElse("未知用户");
        try {
            lockFlag = redissonClient.getLock(LOCK_KEY).tryLock(1000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("{}提交月结获得锁失败......", currentUserName, e);
            throw new MithrasException("处理失败");
        }
        if (!lockFlag) {
            log.error("{}提交月结获得锁失败......", currentUserName);
            throw new MithrasException("请勿重复提交");
        }
        log.info("{}提交月结获得锁成功......", currentUserName);
        try {
            baseInfoService.submit(req);
        } finally {
            redissonClient.getLock(LOCK_KEY).unlock();
            log.info("释放月结管理锁成功......");
        }
    }

    public void download(ServletOutputStream outputStream, MonthlyExcelREQ req) {
        MonthlyModuleTypeEnum monthlyModuleTypeEnum = MonthlyModuleTypeEnum.find(req.getModuleType());
        if (monthlyModuleTypeEnum == null) {
            throw new MithrasException("模块类型错误");
        }
        int maxSize = 10000;
        switch (monthlyModuleTypeEnum) {
            case AIR:
                MonthlyAIRListREQ monthlyAirListReq = new MonthlyAIRListREQ();
                BeanUtils.copyProperties(req, monthlyAirListReq);
                monthlyAirListReq.setPage(1);
                monthlyAirListReq.setPageSize(maxSize);
                PageR<MonthlyAIRListRSP> monthlyAirListRspPage = airList(monthlyAirListReq);
                List<MonthlyAIRListRSP> monthlyAirListRspList = monthlyAirListRspPage.getList();
                if (CollectionUtils.isNotEmpty(monthlyAirListRspList)) {
                    List<MonthlyAIRExcelModel> collect = monthlyAirListRspList.stream().map(air -> {
                        MonthlyAIRExcelModel model = new MonthlyAIRExcelModel();
                        BeanUtils.copyProperties(air, model);
                        model.setOverdueType(Objects.requireNonNull(OverdueTypeEnum.find(model.getOverdueType())).display());
                        model.setLeaseType(ensureType(air.getBizType(), air.getLeaseType()));
                        model.setTaxRateExcel(Util.mithrasLong2BigDecimal(air.getTaxRate()));
                        model.setIncomeSumExcel(Util.mithrasLong2BigDecimal(air.getIncomeSum()));
                        model.setIncomeWithoutTaxSumExcel(Util.mithrasLong2BigDecimal(air.getIncomeWithoutTaxSum()));
                        return model;
                    }).collect(Collectors.toList());
                    monthlyAirExcelManagerExporter.exportExcel(collect, outputStream);
                }
                break;
            case RP:
                MonthlyRPListREQ monthlyRpListReq = new MonthlyRPListREQ();
                BeanUtils.copyProperties(req, monthlyRpListReq);
                monthlyRpListReq.setPage(1);
                monthlyRpListReq.setPageSize(maxSize);
                PageR<MonthlyRPListRSP> monthlyRpListRspPage = rpList(monthlyRpListReq);
                List<MonthlyRPListRSP> monthlyRpListRspList = monthlyRpListRspPage.getList();
                if (CollectionUtils.isNotEmpty(monthlyRpListRspList)) {
                    List<MonthlyRPExcelModel> collect = monthlyRpListRspList.stream().map(rp -> {
                        MonthlyRPExcelModel model = new MonthlyRPExcelModel();
                        BeanUtils.copyProperties(rp, model);
                        model.setOverdueType(Objects.requireNonNull(OverdueTypeEnum.find(model.getOverdueType())).display());
                        model.setLeaseType(ensureType(rp.getBizType(), rp.getLeaseType()));
                        model.setTaxRateExcel(Util.mithrasLong2BigDecimal(rp.getTaxRate()));
                        model.setContractNominalInterestRateExcel(Util.mithrasLong2BigDecimal(Long.valueOf(rp.getContractNominalInterestRate())));
                        model.setIncomeSumExcel(Util.mithrasLong2BigDecimal(rp.getIncomeSum()));
                        model.setIncomeWithoutTaxSumExcel(Util.mithrasLong2BigDecimal(rp.getIncomeWithoutTaxSum()));
                        model.setTheLatestFullRefundRentCapitalExcel(Util.mithrasLong2BigDecimal(rp.getTheLatestFullRefundRentCapital()));
                        return model;
                    }).collect(Collectors.toList());
                    monthlyRpExcelManagerExporter.exportExcel(collect, outputStream);
                }
                break;
            case COST:
                MonthlyCostREQ monthlyCostREQ = new MonthlyCostREQ();
                BeanUtils.copyProperties(req, monthlyCostREQ);
                monthlyCostREQ.setPage(1);
                monthlyCostREQ.setPageSize(maxSize);
                List<MonthlyCostRSP> monthlyCostRspList = costList(monthlyCostREQ).getList();
                if (CollectionUtils.isNotEmpty(monthlyCostRspList)) {
                    List<MonthlyCostExcelModel> collect = monthlyCostRspList.stream().map(cost -> {
                        MonthlyCostExcelModel model = new MonthlyCostExcelModel();
                        BeanUtils.copyProperties(cost, model);
                        model.setFinancingRateExcel(Util.mithrasLong2BigDecimal(Long.valueOf(cost.getFinancingRate())));
                        model.setDailyRateExcel(Util.mithrasLong2BigDecimal(Long.valueOf(cost.getDailyRate())));
                        model.setFinancingAmountExcel(Util.mithrasLong2BigDecimal(cost.getFinancingAmount()));
                        model.setRemainingAmountExcel(Util.mithrasLong2BigDecimal(cost.getRemainingAmount()));
                        model.setTotalCapitalCostExcel(Util.mithrasLong2BigDecimal(cost.getTotalCapitalCost()));
                        model.setTotalCapitalCostAfterTaxExcel(Util.mithrasLong2BigDecimal(cost.getTotalCapitalCostAfterTax()));
                        model.setTermCapitalCostExcel(Util.mithrasLong2BigDecimal(cost.getTermCapitalCost()));
                        model.setTermCapitalCostAfterTaxExcel(Util.mithrasLong2BigDecimal(cost.getTermCapitalCostAfterTax()));
                        model.setFinancingCostDiff(Util.mithrasLong2BigDecimal(cost.getFinancingCostDiff()));
                        model.setBeginOfPeriodInterestBalance(Util.mithrasLong2BigDecimal(cost.getBeginOfPeriodInterestBalance()));
                        model.setEndOfPeriodInterestBalance(Util.mithrasLong2BigDecimal(cost.getEndOfPeriodInterestBalance()));
                        return model;
                    }).collect(Collectors.toList());
                    monthlyCostExcelManagerExporter.exportExcel(collect, outputStream);
                }

                break;
            case STAMP_DUTY_PROJ:
                MonthlyStampDutyProjREQ projREQ = new MonthlyStampDutyProjREQ();
                BeanUtils.copyProperties(req, projREQ);
                projREQ.setPage(1);
                projREQ.setPageSize(maxSize);
                PageR<MonthlyStampDutyProjRSP> monthlyStampDutyProjRSPPageR = projPage(projREQ);
                List<MonthlyStampDutyProjRSP> monthlyStampDutyProjRSPList = monthlyStampDutyProjRSPPageR.getList();
                if (CollectionUtils.isNotEmpty(monthlyStampDutyProjRSPList)) {
                    List<MonthlyDutyProjExcelModel> collect = monthlyStampDutyProjRSPList.stream().map(proj -> {
                        MonthlyDutyProjExcelModel model = new MonthlyDutyProjExcelModel();
                        BeanUtils.copyProperties(proj, model);
                        model.setStampDutyExcel(Util.mithrasLong2BigDecimal(proj.getStampDuty()));
                        return model;
                    }).collect(Collectors.toList());
                    monthlyDutyProjExcelManagerExporter.exportExcel(collect, outputStream);
                }
                break;
            case STAMP_DUTY_FIN:
                MonthlyStampDutyFinREQ finREQ = new MonthlyStampDutyFinREQ();
                BeanUtils.copyProperties(req, finREQ);
                finREQ.setPage(1);
                finREQ.setPageSize(maxSize);
                PageR<MonthlyStampDutyFinRSP> monthlyStampDutyFinRSPPageR = finPage(finREQ);
                List<MonthlyStampDutyFinRSP> monthlyStampDutyFinRSPList = monthlyStampDutyFinRSPPageR.getList();
                if (CollectionUtils.isNotEmpty(monthlyStampDutyFinRSPList)) {
                    List<MonthlyDutyFinExcelModel> collect = monthlyStampDutyFinRSPList.stream().map(fin -> {
                        MonthlyDutyFinExcelModel model = new MonthlyDutyFinExcelModel();
                        BeanUtils.copyProperties(fin, model);
                        model.setStampDutyExcel(Util.mithrasLong2BigDecimal(fin.getStampDuty()));
                        return model;
                    }).collect(Collectors.toList());
                    monthlyDutyFinExcelManagerExporter.exportExcel(collect, outputStream);
                }
                break;
        }
    }

    private String ensureType(String bizType, String leaseType) {
        if (Objects.equals(ProjectBizType.BL.name(), bizType)) {
            return ProjectBizType.BL.display();
        } else if (Objects.equals(ProjectBizType.ZR.name(), bizType)) {
            return ProjectBizType.ZR.display();
        } else {
            return Optional.ofNullable(LeaseType.of(leaseType)).map(LeaseType::display).orElse("");
        }
    }

    public void freshById(MonthlyFreshDataREQ req) {
        MonthlyManagementBaseInfo baseInfo = baseInfoService.getOne(Wrappers.<MonthlyManagementBaseInfo>lambdaQuery()
                .eq(MonthlyManagementBaseInfo::getMainId, req.getMainId())
                .last(StringUtil.mysqlLimitOne()));
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException("主数据不存在");
        }
        baseInfoService.asyncFreshTabData(baseInfo);
    }

    public void updateStatus(MonthlyUpdateStatusREQ req) {
        MonthlyManagementBaseInfo baseInfo = baseInfoService.getOne(Wrappers.<MonthlyManagementBaseInfo>lambdaQuery()
                .eq(MonthlyManagementBaseInfo::getMainId, req.getMainId())
                .last(StringUtil.mysqlLimitOne()));
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException("主数据不存在");
        }
        baseInfoService.updateStatus(baseInfo, req);
    }

    /**
     * 月结关账
     *
     * @param req 请求体
     */
    public void close(MonthlyCloseREQ req) {
        MonthlyManagementBaseInfo baseInfo = checkBeforeCheck(req);
        baseInfoService.close(baseInfo);
    }

    public @NotNull MonthlyManagementBaseInfo checkBeforeCheck(MonthlyCloseREQ req) {
        MonthlyManagementBaseInfo baseInfo = baseInfoService.getOne(Wrappers.<MonthlyManagementBaseInfo>lambdaQuery()
                .eq(MonthlyManagementBaseInfo::getMainId, req.getMainId())
                .last(StringUtil.mysqlLimitOne()));
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException("主数据不存在");
        }
        if (Objects.equals(baseInfo.getStatus(), MonthlyManagementStatusEnum.CLOSED_AMOUNT.name())) {
            throw new MithrasException("该月结数据已关闭");
        }
        return baseInfo;
    }

    public void closeValidate(MonthlyCloseREQ req) {
        MonthlyManagementBaseInfo baseInfo = checkBeforeCheck(req);
        baseInfoService.closeCheck(baseInfo);
    }

    public void updateSingle(MonthlyFreshSingleREQ req) {
        baseInfoService.updateSingle(req);
    }

    public void pushSingle(MonthlySubmitSingleREQ req) {
        baseInfoService.pushSingle(req);
    }
}
