package cn.zswltech.mithras.ftp.oldftp.service;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.ftp.FtpGuidanceIdReq;
import cn.zswltech.mithras.dto.ftp.FtpMonthlyGuidanceDetailRsp;
import cn.zswltech.mithras.dto.ftp.FtpMonthlyGuidanceListReq;
import cn.zswltech.mithras.dto.ftp.FtpMonthlyGuidanceListRsp;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.ftp.oldftp.FtpGuidance;
import cn.zswltech.mithras.ftp.oldftp.convert.FtpMonthlyGuidanceConverter;
import cn.zswltech.mithras.foundation.enums.VersionTypeEnum;
import cn.zswltech.mithras.ftp.oldftp.enums.CreditTerm;
import cn.zswltech.mithras.ftp.oldftp.enums.EnterpriseType;
import cn.zswltech.mithras.ftp.oldftp.enums.FtpProcessStatus;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjectClassify;
import cn.zswltech.mithras.ftp.oldftp.mapper.FtpMonthlyGuidanceMapper;
import cn.zswltech.mithras.ftp.oldftp.mapper.lib.FtpMonthlyGuidanceLibMapper;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.ftp.oldftp.model.FtpMonthlyGuidance;
import cn.zswltech.mithras.ftp.oldftp.model.FtpMonthlyGuidanceLib;
import cn.zswltech.mithras.ftp.oldftp.model.FtpMonthlyPricing;
import cn.zswltech.mithras.ftp.oldftp.model.FtpMonthlyValuation;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.port.UserNameResolver;
import cn.zswltech.mithras.foundation.port.AdminAuthResolver;
import cn.zswltech.mithras.foundation.port.CurrentUserDeptResolver;
import cn.zswltech.mithras.ftp.oldftp.fms.FtpContext;
import cn.zswltech.mithras.ftp.oldftp.fms.FtpEvent;
import cn.zswltech.mithras.ftp.oldftp.fms.FtpMonthlyGuidanceStateMachine;
import cn.zswltech.mithras.ftp.oldftp.service.port.FtpGuidanceProcessInfo;
import cn.zswltech.mithras.ftp.oldftp.service.port.FtpGuidanceWorkflowPort;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.foundation.enums.common.RecordStatus.NEW;
import static cn.zswltech.mithras.foundation.enums.common.RecordStatus.TAKE_EFFECT;

/**
 * @author zhaozhengkang
 * @description 月度指导
 * @date 2023-01-10
 */
@Service
public class FtpMonthlyGuidanceService extends ServiceImpl<FtpMonthlyGuidanceMapper, FtpMonthlyGuidance> implements FtpGuidance {

    @Resource
    private UserNameResolver userNameResolver;
    @Resource
    private FtpMonthlyGuidanceConverter mainConverter;
    @Resource
    private FtpMonthlyValuationService valuationService;
    @Resource
    private FtpMonthlyPricingService pricingService;
    @Resource
    private FtpMonthlyGuidanceStateMachine stateMachine;
    @Resource
    private FtpMonthlyGuidanceVersionService versionService;
    @Resource
    private FtpMonthlyGuidanceLibMapper libMapper;
    @Resource
    private CurrentUserDeptResolver currentUserDeptResolver;
    
    private AdminAuthResolver adminAuthResolver;

    @Resource
    private FtpGuidanceWorkflowPort ftpGuidanceWorkflowPort;

    private static Map<String, String> rowMap = new HashMap<>();
    private static Map<String, String> columnMap = new HashMap<>();

    static {
        rowMap.put(CreditTerm.ONE_YEAR.name(), "R3");
        rowMap.put(CreditTerm.ONE_TO_THREE_YEARS.name(), "R4");
        rowMap.put(CreditTerm.MORE_THAN_THREE_YEARS.name(), "R5");

        columnMap.put("financingCost", "C1");
        columnMap.put("guaranteeCost", "C2");
        columnMap.put("subtotalCost", "C3");
        columnMap.put("discountRate", "C4");
        columnMap.put("discountRateWeight", "C5");
        columnMap.put("shiborRate", "C6");
        columnMap.put("shiborRateWeight", "C7");
        columnMap.put("lprRate", "C8");
        columnMap.put("lprRateWeight", "C9");
        columnMap.put("financeCostTrends", "C10");
        columnMap.put("financeCostTrendsWeight", "C11");
        columnMap.put("subtotalRate", "C12");
        columnMap.put("subtotalAdjustmentValuation", "C13");
        columnMap.put("encourageValuation", "C14");
        columnMap.put("moderateSupportValuation", "C15");
        columnMap.put("cautiousValuation", "C16");
        columnMap.put("stateOwnListedValuation", "C17");
        columnMap.put("otherValuation", "C18");
    }

    public PageR<FtpMonthlyGuidanceListRsp> list(FtpMonthlyGuidanceListReq req) {
        LambdaQueryWrapper<FtpMonthlyGuidance> qw = Wrappers.<FtpMonthlyGuidance>lambdaQuery()
                .eq(ObjectUtil.isNotEmpty(req.getYear()), FtpMonthlyGuidance::getYear, req.getYear())
                .eq(ObjectUtil.isNotEmpty(req.getMonth()), FtpMonthlyGuidance::getMonth, req.getMonth())
                .eq(ObjectUtil.isNotEmpty(req.getCreateBy()), FtpMonthlyGuidance::getCreateBy, req.getCreateBy())
                .eq(ObjectUtil.isNotEmpty(req.getProcessStatus()), FtpMonthlyGuidance::getGuidanceProcessStatus,
                        req.getProcessStatus())
                .ge(ObjectUtil.isNotEmpty(req.getCreateDateFrom()), FtpMonthlyGuidance::getCreateTime,
                        startOfDay(req.getCreateDateFrom()))
                .le(ObjectUtil.isNotEmpty(req.getCreateDateTo()), FtpMonthlyGuidance::getCreateTime,
                        endOfDay(req.getCreateDateTo()))
                .ge(ObjectUtil.isNotEmpty(req.getUpdateDateFrom()), FtpMonthlyGuidance::getUpdateTime,
                        startOfDay(req.getUpdateDateFrom()))
                .le(ObjectUtil.isNotEmpty(req.getUpdateDateTo()), FtpMonthlyGuidance::getUpdateTime,
                        endOfDay(req.getUpdateDateTo()));

        if (!currentUserDeptResolver.currentUserIsSpecificDept("JHCWB","ZJGLB")
                && !currentUserDeptResolver.currentUserIsSpecificDept("DJWYH")
                && !adminAuthResolver.adminAuth()) {
            qw.in(FtpMonthlyGuidance::getGuidanceProcessStatus, Arrays.asList(FtpProcessStatus.NEW_APPROVAL_PASS.name(),
                    FtpProcessStatus.CHANGING_APPROVAL_PASS.name()));
        }
        qw.orderByDesc(BaseModel::getUpdateTime);
        Page<FtpMonthlyGuidance> page = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), qw);
        List<FtpMonthlyGuidanceListRsp> rspList = new ArrayList<>();
        List<Long> userIds = page.getRecords().stream().map(BaseModel::getCreateBy)
                .collect(Collectors.toList());
        Map<Long, String> userId2Name = userNameResolver.sysUserId2Name(userIds);
        page.getRecords().forEach(guidance -> {
            FtpMonthlyGuidanceListRsp rsp = mainConverter.entity2ListRsp(guidance);
            rsp.setTimeDisplay(guidance.getYear() + "年" + guidance.getMonth() + "月");
            rsp.setCreateByName(userId2Name.get(guidance.getCreateBy()));
            rspList.add(rsp);
        });
        return PageR.of(page, rspList);
    }

    public FtpGuidanceProcessInfo findRelatedProcess(Long guidanceId) {
        return ftpGuidanceWorkflowPort.findMonthlyGuidanceProcess(guidanceId);
    }

    private void checkEditableInProcess(Long guidanceId) {
        FtpGuidanceProcessInfo processInfo = findRelatedProcess(guidanceId);
        if (Objects.nonNull(processInfo) && !processInfo.isStartUserNode()) {
            throw new AuthCheckException("该数据处于流程中，且流程不在发起人节点，不允许修改数据");
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void importExcel(InputStream inputStream, Long guidanceId) throws IOException {
        FtpMonthlyGuidance guidance = baseMapper.selectById(guidanceId);
        checkEditableInProcess(guidanceId);
        XSSFWorkbook sheets = new XSSFWorkbook(inputStream);
        Sheet sheet = sheets.getSheetAt(0);
        int[] valuationRows = new int[]{3, 4, 5};
        List<FtpMonthlyValuation> valuations = new ArrayList<>();
        for (int row : valuationRows) {
            Row rowData = sheet.getRow(row);
            FtpMonthlyValuation valuation = new FtpMonthlyValuation();
            valuation.setGuidanceId(guidanceId);
            if (row == 3) {
                valuation.setCrditTerm(CreditTerm.ONE_YEAR.name());
            } else if (row == 4) {
                valuation.setCrditTerm(CreditTerm.ONE_TO_THREE_YEARS.name());
            } else {
                valuation.setCrditTerm(CreditTerm.MORE_THAN_THREE_YEARS.name());
            }
            valuation.setFinancingCost(getNumericCellValue(rowData, 1));
            valuation.setGuaranteeCost(getNumericCellValue(rowData, 2));
            valuation.setSubtotalCost(getNumericCellValue(rowData, 3));
            valuation.setDiscountRate(getNumericCellValue(rowData, 4));
            valuation.setDiscountRateWeight(getNumericCellValue(rowData, 5));
            valuation.setShiborRate(getNumericCellValue(rowData, 6));
            valuation.setShiborRateWeight(getNumericCellValue(rowData, 7));
            valuation.setLprRate(getNumericCellValue(rowData, 8));
            valuation.setLprRateWeight(getNumericCellValue(rowData, 9));
            valuation.setFinanceCostTrends(getNumericCellValue(rowData, 10));
            valuation.setFinanceCostTrendsWeight(getNumericCellValue(rowData, 11));
            valuation.setSubtotalRate(getNumericCellValue(rowData, 12));
            valuation.setSubtotalAdjustmentValuation(getNumericCellValue(rowData, 13));
            valuation.setEncourageValuation(getNumericCellValue(rowData, 14));
            valuation.setModerateSupportValuation(getNumericCellValue(rowData, 15));
            valuation.setCautiousValuation(getNumericCellValue(rowData, 16));
            valuation.setStateOwnListedValuation(getNumericCellValue(rowData, 17));
            valuation.setOtherValuation(getNumericCellValue(rowData, 18));
            valuations.add(valuation);
        }
        List<FtpMonthlyValuation> dbVals = valuationService.selectByGuidance(guidanceId);
        if (ObjectUtil.isNotEmpty(dbVals)) {
            Map<String, FtpMonthlyValuation> dbValMap = new HashMap<>();
            dbVals.forEach(valuation -> {
                if (ObjectUtil.isNotEmpty(valuation.getCrditTerm())) {
                    dbValMap.put(valuation.getCrditTerm(), valuation);
                }
            });
            Map<String, FtpMonthlyValuation> valMap = new HashMap<>();
            valuations.forEach(valuation -> {
                if (ObjectUtil.isNotEmpty(valuation.getCrditTerm())) {
                    valMap.put(valuation.getCrditTerm(), valuation);
                }
            });
            dbValMap.forEach((creditTerm, dbVal) -> {
                FtpMonthlyValuation newPricing = valMap.get(creditTerm);
                newPricing.setId(dbVal.getId());
            });
            valuationService.updateBatchById(valMap.values());
        } else {
            valuationService.saveBatch(valuations);
        }

        List<FtpMonthlyPricing> pricings = new ArrayList<>();
        int[] pricingRows = new int[]{19, 20, 21, 23, 24, 25, 26, 27};
        for (int row : pricingRows) {
            Row rowData = sheet.getRow(row);
            for (int column = 1; column <= 3; column++) {
                FtpMonthlyPricing pricing = new FtpMonthlyPricing();
                pricing.setGuidanceId(guidanceId);
                pricing.setSite("R" + row + "C" + column);
                if (row <= 21) {
                    pricing.setEnterpriseType(EnterpriseType.STATE_OWNED_AND_LISTED.name());
                } else {
                    pricing.setEnterpriseType(EnterpriseType.OTHER.name());
                }
                if (row == 19 || row == 23) {
                    pricing.setProjectClassify(ProjectClassify.ENCOURAGEMENT.name());
                } else if (row == 20 || row == 24) {
                    pricing.setProjectClassify(ProjectClassify.MODERATE_SUPPORT.name());
                } else if (row == 21 || row == 25) {
                    pricing.setProjectClassify(ProjectClassify.CAUTIOUS.name());
                } else if (row == 26) {
                    pricing.setProjectClassify(ProjectClassify.CONSTRUCTION_MACHINERY.name());
                } else {
                    pricing.setProjectClassify(ProjectClassify.INTRA_GROUP_COLLABORATION.name());
                }
                if (column == 1) {
                    pricing.setCreditTerm(CreditTerm.ONE_YEAR.name());
                } else if (column == 2) {
                    pricing.setCreditTerm(CreditTerm.ONE_TO_THREE_YEARS.name());
                } else {
                    pricing.setCreditTerm(CreditTerm.MORE_THAN_THREE_YEARS.name());
                }
                pricing.setValue(getNumericCellValue(rowData, column));
                pricings.add(pricing);
            }
        }
        List<FtpMonthlyPricing> dbPricings = pricingService.selectByGuidance(guidanceId);
        if (ObjectUtil.isNotEmpty(dbPricings)) {
            Map<String, FtpMonthlyPricing> dbPricingMap = new HashMap<>();
            dbPricings.forEach(pricing -> {
                if (ObjectUtil.isNotEmpty(pricing.getSite())) {
                    dbPricingMap.put(pricing.getSite(), pricing);
                }
            });
            Map<String, FtpMonthlyPricing> pricingMap = new HashMap<>();
            pricings.forEach(pricing -> {
                if (ObjectUtil.isNotEmpty(pricing.getSite())) {
                    pricingMap.put(pricing.getSite(), pricing);
                }
            });
            dbPricingMap.forEach((site, dbPricing) -> {
                FtpMonthlyPricing newPricing = pricingMap.get(site);
                dbPricing.setValue(newPricing.getValue());
            });
            pricingService.updateBatchById(dbPricingMap.values());
        } else {
            pricingService.saveBatch(pricings);
        }

        guidance.setOneYearEarningsGuidance(getNumericCellValue(sheet.getRow(30), 1));
        guidance.setOneToThreeEarningsGuidance(getNumericCellValue(sheet.getRow(30), 2));
        guidance.setMoreThanThreeEarningsGuidance(getNumericCellValue(sheet.getRow(30), 3));
        guidance.setSellingPrice(getNumericCellValue(sheet.getRow(33), 0));
        guidance.setBuyingPrice(getNumericCellValue(sheet.getRow(33), 2));
        baseMapper.updateById(guidance);
        stateMachine.execute(FtpContext.of(guidance, FtpEvent.MODIFY_SAVE, guidance.getProcessStatus()));
    }


    public void exportExcel(Long guidanceId, OutputStream outputStream) {
        InputStream in = FtpMonthlyGuidanceService.class.getResourceAsStream("/doc/ftp_monthly.xlsx");
        Map<String, Double> exportData = new HashMap<>();
        List<FtpMonthlyValuation> valuations = valuationService.selectByGuidance(guidanceId);
        if (ObjectUtil.isEmpty(valuations)) {
            throw new MithrasException("暂无数据，不能导出!");
        }
        valuations.forEach(valuation -> {
            String s = rowMap.get(valuation.getCrditTerm());
            Field[] declaredFields = FtpMonthlyValuation.class.getDeclaredFields();
            for (Field field : declaredFields) {
                String fieldName = field.getName();
                if (columnMap.containsKey(fieldName)) {
                    try {
                        field.setAccessible(true);
                        Integer anInt = (Integer) field.get(valuation);
                        exportData.put(s + columnMap.get(fieldName), anInt / 10000.0);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        List<FtpMonthlyPricing> pricings = pricingService.getBaseMapper().selectList(Wrappers.<FtpMonthlyPricing>lambdaQuery()
                .eq(FtpMonthlyPricing::getGuidanceId, guidanceId)
                .in(FtpMonthlyPricing::getProjectClassify, Arrays.asList(ProjectClassify.CONSTRUCTION_MACHINERY.name(),
                        ProjectClassify.INTRA_GROUP_COLLABORATION.name())));
        pricings.forEach(pricing -> exportData.put(pricing.getSite(), pricing.getValue() / 10000.0));

        FtpMonthlyGuidance guidance = baseMapper.selectById(guidanceId);
        exportData.put("sellingPrice", guidance.getSellingPrice() / 10000.0);
        exportData.put("buyingPrice", guidance.getBuyingPrice() / 10000.0);

        try {
            ExcelWriter excelWriter = EasyExcel.write(outputStream).withTemplate(in).build();
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            excelWriter.fill(exportData, writeSheet);
            excelWriter.finish();
            outputStream.flush();
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void submit(Long id) {
        FtpMonthlyGuidance guidance = baseMapper.selectById(id);
        List<FtpMonthlyPricing> pricings = pricingService.selectByGuidance(id);
        if (ObjectUtil.isEmpty(pricings)) {
            throw new MithrasException("请先导入数据");
        }
        // 判断使用创建流程还是修改流程
        if (NEW.name().equals(guidance.getGuidanceRecordStatus())) {
            ftpGuidanceWorkflowPort.startMonthlyGuidanceCreateFlow(id, guidance.getYear(), guidance.getMonth());
        } else {
            ftpGuidanceWorkflowPort.startMonthlyGuidanceModifyFlow(id, guidance.getYear(), guidance.getMonth());
        }
        stateMachine.execute(FtpContext.of(guidance, FtpEvent.SUBMIT_APPROVAL, guidance.getProcessStatus()));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void processEnd(Long id, boolean processPass, boolean processCancel, Long startUserId, String processInstanceId) {
        FtpMonthlyGuidance guidance = baseMapper.selectById(id);
        // 修改状态
        if (processPass) {
            // 审批通过 新增版本
            stateMachine.execute(FtpContext.of(guidance, FtpEvent.APPROVAL_PASS, guidance.getProcessStatus()));
        } else {
            if (processCancel) {
                if (TAKE_EFFECT.name().equals(guidance.getGuidanceRecordStatus())) {
                    stateMachine.execute(FtpContext.of(guidance, FtpEvent.MODIFY_WITHDRAW, guidance.getProcessStatus()));
                } else {
                    stateMachine.execute(FtpContext.of(guidance, FtpEvent.NEW_WITHDRAW, guidance.getProcessStatus()));
                }
            }
        }
        // 记录版本
        int versionType = processPass ? VersionTypeConstants.NORMAL : VersionTypeConstants.INVALID;
        versionService.recordVersion(id, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, versionType);

        if (!processPass && processCancel) {
            versionService.reset(id);
        }
    }

    public FtpMonthlyGuidanceDetailRsp detail(FtpGuidanceIdReq req) {
        FtpMonthlyGuidance guidance = baseMapper.selectById(req.getId());
        if (ObjectUtil.isEmpty(guidance)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (ObjectUtil.isNotEmpty(req.getVersion())) {
            FtpMonthlyGuidanceLib guidanceLib = libMapper.selectOne(Wrappers.<FtpMonthlyGuidanceLib>lambdaQuery()
                    .eq(FtpMonthlyGuidanceLib::getVersion, req.getVersion())
                    .eq(FtpMonthlyGuidanceLib::getOriginId, req.getId())
                    .last("LIMIT 1"));
            FtpMonthlyGuidanceDetailRsp rsp = mainConverter.lib2DetailRsp(guidanceLib);
            rsp.setId(guidanceLib.getOriginId());
            return rsp;
        } else {
            return mainConverter.entity2DetailRsp(guidance);
        }
    }

    private LocalDateTime startOfDay(LocalDate date) {
        return date == null ? null : date.atStartOfDay();
    }

    private LocalDateTime endOfDay(LocalDate date) {
        return date == null ? null : date.atTime(23, 59, 59);
    }
}
