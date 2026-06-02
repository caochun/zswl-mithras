package cn.zswltech.mithras.service.service.ftp;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.ftp.FtpGuidanceIdReq;
import cn.zswltech.mithras.dto.ftp.FtpMonthlyGuidanceDetailRsp;
import cn.zswltech.mithras.dto.ftp.FtpMonthlyGuidanceListReq;
import cn.zswltech.mithras.dto.ftp.FtpMonthlyGuidanceListRsp;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.convert.TypeConversionWorker;
import cn.zswltech.mithras.ftp.oldftp.convert.FtpMonthlyGuidanceConverter;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.VersionTypeEnum;
import cn.zswltech.mithras.ftp.oldftp.enums.CreditTerm;
import cn.zswltech.mithras.ftp.oldftp.enums.EnterpriseType;
import cn.zswltech.mithras.ftp.oldftp.enums.FtpProcessStatus;
import cn.zswltech.mithras.service.enums.projreview.ProjectClassify;
import cn.zswltech.mithras.ftp.oldftp.mapper.FtpMonthlyGuidanceMapper;
import cn.zswltech.mithras.ftp.oldftp.mapper.lib.FtpMonthlyGuidanceLibMapper;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.ftp.oldftp.model.FtpMonthlyGuidance;
import cn.zswltech.mithras.ftp.oldftp.model.FtpMonthlyGuidanceLib;
import cn.zswltech.mithras.ftp.oldftp.model.FtpMonthlyPricing;
import cn.zswltech.mithras.ftp.oldftp.model.FtpMonthlyValuation;
import cn.zswltech.mithras.ftp.oldftp.service.FtpMonthlyPricingService;
import cn.zswltech.mithras.ftp.oldftp.service.FtpMonthlyValuationService;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.flow.FlowEndEventProcessor;
import cn.zswltech.mithras.service.service.ftp.fms.FtpContext;
import cn.zswltech.mithras.service.service.ftp.fms.FtpEvent;
import cn.zswltech.mithras.service.service.ftp.fms.FtpMonthlyGuidanceStateMachine;
import cn.zswltech.mithras.service.util.FlowUtil;
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
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.enums.common.RecordStatus.NEW;
import static cn.zswltech.mithras.service.enums.common.RecordStatus.TAKE_EFFECT;

/**
 * @author zhaozhengkang
 * @description 月度指导
 * @date 2023-01-10
 */
@Service
public class FtpMonthlyGuidanceService extends ServiceImpl<FtpMonthlyGuidanceMapper, FtpMonthlyGuidance> implements FlowEndEventProcessor, FtpGuidance {
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private FtpMonthlyGuidanceConverter mainConverter;
    @Resource
    private FtpMonthlyValuationService valuationService;
    @Resource
    private FtpMonthlyPricingService pricingService;
    @Resource
    private TypeConversionWorker typeConversionWorker;
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private FtpMonthlyGuidanceStateMachine stateMachine;
    @Resource
    private FtpMonthlyGuidanceVersionService versionService;
    @Resource
    private FtpMonthlyGuidanceLibMapper libMapper;
    @Resource
    private SysUserService sysUserService;

    @Resource
    private FlowTaskApiService taskApiService;

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
                        typeConversionWorker.startOfDay(req.getCreateDateFrom()))
                .le(ObjectUtil.isNotEmpty(req.getCreateDateTo()), FtpMonthlyGuidance::getCreateTime,
                        typeConversionWorker.endOfDay(req.getCreateDateTo()))
                .ge(ObjectUtil.isNotEmpty(req.getUpdateDateFrom()), FtpMonthlyGuidance::getUpdateTime,
                        typeConversionWorker.startOfDay(req.getUpdateDateFrom()))
                .le(ObjectUtil.isNotEmpty(req.getUpdateDateTo()), FtpMonthlyGuidance::getUpdateTime,
                        typeConversionWorker.endOfDay(req.getUpdateDateTo()));

        if (!sysUserService.currentUserIsSpecificDept("JHCWB","ZJGLB")
                && !sysUserService.currentUserIsSpecificDept("DJWYH")
                && !sysUserService.adminAuth()) {
            qw.in(FtpMonthlyGuidance::getGuidanceProcessStatus, Arrays.asList(FtpProcessStatus.NEW_APPROVAL_PASS.name(),
                    FtpProcessStatus.CHANGING_APPROVAL_PASS.name()));
        }
        qw.orderByDesc(BaseModel::getUpdateTime);
        Page<FtpMonthlyGuidance> page = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), qw);
        List<FtpMonthlyGuidanceListRsp> rspList = new ArrayList<>();
        List<Long> userIds = page.getRecords().stream().map(BaseModel::getCreateBy)
                .collect(Collectors.toList());
        Map<Long, String> userId2Name = id2NameService.sysUserId2Name(userIds);
        page.getRecords().forEach(guidance -> {
            FtpMonthlyGuidanceListRsp rsp = mainConverter.entity2ListRsp(guidance);
            rsp.setTimeDisplay(guidance.getYear() + "年" + guidance.getMonth() + "月");
            rsp.setCreateByName(userId2Name.get(guidance.getCreateBy()));
            rspList.add(rsp);
        });
        return PageR.of(page, rspList);
    }

    public ProcessResp findRelatedProcess(Long guidanceId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setBusinessKey(String.valueOf(guidanceId));
        processPageReq.setModelKeyList(BusinessModuleEnum.FTP_MONTHLY_GUIDANCE.getModelKeyList());
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream().findFirst().orElse(null);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void importExcel(InputStream inputStream, Long guidanceId) throws IOException {
        FtpMonthlyGuidance guidance = baseMapper.selectById(guidanceId);
        ProcessResp processResp = findRelatedProcess(guidanceId);
        if (Objects.nonNull(processResp)) {
            boolean isStartUserNode = FlowUtil.isStartUserNode(processResp);
            if (!isStartUserNode) {
                throw new AuthCheckException("该数据处于流程中，且流程不在发起人节点，不允许修改数据");
            }
        }
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

    @Resource
    private OrgDOMapper orgDOMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void submit(Long id) {
        FtpMonthlyGuidance guidance = baseMapper.selectById(id);
        List<FtpMonthlyPricing> pricings = pricingService.selectByGuidance(id);
        if (ObjectUtil.isEmpty(pricings)) {
            throw new MithrasException("请先导入数据");
        }
        StartProcessReq startProcessReq = new StartProcessReq();
        // 判断使用创建流程还是修改流程
        if (NEW.name().equals(guidance.getGuidanceRecordStatus())) {
            startProcessReq.setModelKey(ProcessModelTypeEnum.FtpMonthlyGuidanceCreateFlow.name());
        } else {
            startProcessReq.setModelKey(ProcessModelTypeEnum.FtpMonthlyGuidanceModifyFlow.name());
        }
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setBusinessKey(String.valueOf(id));
        startProcessReq.setProcessInstanceName(guidance.getYear() + "年" + guidance.getMonth() + "月ftp定价指导");
        OrgDO jhcwb = orgDOMapper.queryByCode("JHCWB");
        startProcessReq.setStartUserDeptId(Optional.ofNullable(jhcwb).map(OrgDO::getId)
                .map(String::valueOf).orElse(null));
        processApiService.start(startProcessReq);
        stateMachine.execute(FtpContext.of(guidance, FtpEvent.SUBMIT_APPROVAL, guidance.getProcessStatus()));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void processEnd(Long id, Integer endType, Long startUserId, String processInstanceId, String modelKey) {
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        FtpMonthlyGuidance guidance = baseMapper.selectById(id);
        // 修改状态
        if (processPass) {
            // 审批通过 新增版本
            stateMachine.execute(FtpContext.of(guidance, FtpEvent.APPROVAL_PASS, guidance.getProcessStatus()));
        } else {
            if (ProcessBusinessStatusEnum.CANCEL.getType().equals(endType)) {
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

        if (!processPass && ProcessBusinessStatusEnum.CANCEL.getType().equals(endType)) {
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
}
