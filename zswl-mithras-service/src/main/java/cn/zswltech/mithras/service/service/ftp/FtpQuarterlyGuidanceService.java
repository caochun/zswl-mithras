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
import cn.zswltech.mithras.dto.ftp.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.convert.TypeConversionWorker;
import cn.zswltech.mithras.service.convert.ftp.FtpQuarterlyGuidanceConverter;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.VersionTypeEnum;
import cn.zswltech.mithras.ftp.enums.CreditTerm;
import cn.zswltech.mithras.ftp.enums.EnterpriseType;
import cn.zswltech.mithras.ftp.enums.FtpProcessStatus;
import cn.zswltech.mithras.ftp.enums.MonthType;
import cn.zswltech.mithras.service.enums.projreview.ProjectClassify;
import cn.zswltech.mithras.ftp.mapper.FtpQuarterlyGuidanceMapper;
import cn.zswltech.mithras.ftp.service.FtpQuarterlyBasePricingService;
import cn.zswltech.mithras.ftp.service.FtpQuarterlyCustomerPrincipalPricingService;
import cn.zswltech.mithras.ftp.service.FtpQuarterlyEnterprisePricingService;
import cn.zswltech.mithras.ftp.service.FtpQuarterlyMonthPricingService;
import cn.zswltech.mithras.ftp.service.FtpQuarterlyPricingService;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.ftp.model.*;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.flow.FlowEndEventProcessor;
import cn.zswltech.mithras.service.service.ftp.fms.FtpContext;
import cn.zswltech.mithras.service.service.ftp.fms.FtpEvent;
import cn.zswltech.mithras.service.service.ftp.fms.FtpQuarterlyGuidanceStateMachine;
import cn.zswltech.mithras.ftp.lib.FtpQuarterlyBasePricingLibService;
import cn.zswltech.mithras.ftp.lib.FtpQuarterlyCustomerPrincipalPricingLibService;
import cn.zswltech.mithras.ftp.lib.FtpQuarterlyEnterprisePricingLibService;
import cn.zswltech.mithras.ftp.lib.FtpQuarterlyMonthPricingLibService;
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
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.enums.common.RecordStatus.NEW;
import static cn.zswltech.mithras.service.enums.common.RecordStatus.TAKE_EFFECT;

/**
 * @author zhaozhengkang
 * @description 季度最低收益率指导
 * @date 2023-01-09
 */
@Service
public class FtpQuarterlyGuidanceService
        extends ServiceImpl<FtpQuarterlyGuidanceMapper, FtpQuarterlyGuidance> implements FlowEndEventProcessor, FtpGuidance {

    @Resource
    private FtpQuarterlyPricingService quarterlyPricingService;
    @Resource
    private FtpQuarterlyBasePricingService basePricingService;
    @Resource
    private FtpQuarterlyMonthPricingService monthPricingService;
    @Resource
    private FtpQuarterlyEnterprisePricingService enterprisePricingService;
    @Resource
    private FtpQuarterlyCustomerPrincipalPricingService customerPricingService;
    @Resource
    private FtpQuarterlyBasePricingLibService basePricingLibService;
    @Resource
    private FtpQuarterlyMonthPricingLibService monthPricingLibService;
    @Resource
    private FtpQuarterlyEnterprisePricingLibService enterprisePricingLibService;
    @Resource
    private FtpQuarterlyCustomerPrincipalPricingLibService customerPricingLibService;
    @Resource
    private FtpQuarterlyGuidanceConverter mainConverter;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private TypeConversionWorker typeConversionWorker;
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private FtpQuarterlyGuidanceVersionService versionService;
    @Resource
    private FtpQuarterlyGuidanceStateMachine stateMachine;
    @Resource
    private OrgDOMapper orgDOMapper;
    @Resource
    private SysUserService sysUserService;

    public PageR<FtpQuarterlyGuidanceListRsp> list(FtpQuarterlyGuidanceListReq req) {
        LambdaQueryWrapper<FtpQuarterlyGuidance> qw = Wrappers.<FtpQuarterlyGuidance>lambdaQuery()
                .eq(ObjectUtil.isNotEmpty(req.getYear()), FtpQuarterlyGuidance::getYear, req.getYear())
                .eq(ObjectUtil.isNotEmpty(req.getQuarter()), FtpQuarterlyGuidance::getQuarter, req.getQuarter())
                .eq(ObjectUtil.isNotEmpty(req.getCreateBy()), FtpQuarterlyGuidance::getCreateBy, req.getCreateBy())
                .in(ObjectUtil.isNotEmpty(req.getProcessStatus()), FtpQuarterlyGuidance::getGuidanceProcessStatus,
                        req.getProcessStatus())
                .ge(ObjectUtil.isNotEmpty(req.getCreateDateFrom()), FtpQuarterlyGuidance::getCreateTime,
                        typeConversionWorker.startOfDay(req.getCreateDateFrom()))
                .le(ObjectUtil.isNotEmpty(req.getCreateDateTo()), FtpQuarterlyGuidance::getCreateTime,
                        typeConversionWorker.endOfDay(req.getCreateDateTo()))
                .ge(ObjectUtil.isNotEmpty(req.getUpdateDateFrom()), FtpQuarterlyGuidance::getUpdateTime,
                        typeConversionWorker.startOfDay(req.getUpdateDateFrom()))
                .le(ObjectUtil.isNotEmpty(req.getUpdateDateTo()), FtpQuarterlyGuidance::getUpdateTime,
                        typeConversionWorker.endOfDay(req.getUpdateDateTo()));

        if (!sysUserService.currentUserIsSpecificDept("JHCWB","ZJGLB")
                && !sysUserService.currentUserIsSpecificDept("DJWYH")
                && !sysUserService.adminAuth()) {
            qw.in(FtpQuarterlyGuidance::getGuidanceProcessStatus, java.util.Arrays.asList(FtpProcessStatus.NEW_APPROVAL_PASS.name(), FtpProcessStatus.CHANGING_APPROVAL_PASS.name()));
        }
        qw.orderByDesc(BaseModel::getUpdateTime);
        Page<FtpQuarterlyGuidance> guidancePage =
                baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), qw);
        List<FtpQuarterlyGuidanceListRsp> rspList = new ArrayList<>();
        List<Long> userIds = guidancePage.getRecords().stream().map(BaseModel::getCreateBy)
                .collect(Collectors.toList());
        Map<Long, String> userId2Name = id2NameService.sysUserId2Name(userIds);
        guidancePage.getRecords().forEach(guidance -> {
            FtpQuarterlyGuidanceListRsp rsp = mainConverter.entity2ListRsp(guidance);
            rsp.setTimeDisplay(guidance.getYear() + "年第" + guidance.getQuarter() + "季度");
            rsp.setCreateByName(userId2Name.get(guidance.getCreateBy()));
            rspList.add(rsp);
        });
        return PageR.of(guidancePage, rspList);
    }

    public List<FtpQuarterlyBasePricingRsp> detailPricingBase(FtpGuidanceIdReq req) {
        if (ObjectUtil.isNotEmpty(req.getVersion())) {
            List<FtpQuarterlyBasePricingRsp> rsps = basePricingLibService.getBaseMapper().selectList(Wrappers.<FtpQuarterlyBasePricingLib>lambdaQuery()
                            .eq(FtpQuarterlyBasePricing::getGuidanceId, req.getId())
                            .eq(FtpQuarterlyBasePricingLib::getVersion, req.getVersion()))
                    .stream().map(lib -> {
                        FtpQuarterlyBasePricingRsp rsp = mainConverter.baseLib2Rsp(lib);
                        rsp.setId(lib.getOriginId());
                        return rsp;
                    }).collect(Collectors.toList());
            return rsps;
        } else {
            return mainConverter.baseEntity2Rsp(basePricingService.selectByGuidanceId(req.getId()));
        }
    }

    public List<FtpQuarterlyMonthPricingRsp> detailPricingMonth(FtpGuidanceIdReq req) {
        if (ObjectUtil.isNotEmpty(req.getVersion())) {

            List<FtpQuarterlyMonthPricingRsp> rsps = monthPricingLibService.getBaseMapper().selectList(Wrappers.<FtpQuarterlyMonthPricingLib>lambdaQuery()
                    .eq(FtpQuarterlyMonthPricingLib::getGuidanceId, req.getId())
                    .eq(FtpQuarterlyMonthPricingLib::getVersion, req.getVersion())).stream().map(lib -> {
                FtpQuarterlyMonthPricingRsp rsp = mainConverter.monthLib2Rsp(lib);
                rsp.setId(lib.getOriginId());
                return rsp;
            }).collect(Collectors.toList());
            return rsps;
        } else {
            return mainConverter.monthEntity2Rsp(monthPricingService.selectByGuidanceId(req.getId()));
        }
    }

    public List<FtpQuarterlyCustomerPrincipalPricingRsp> detailPricingCustomer(FtpGuidanceIdReq req) {
        if (ObjectUtil.isNotEmpty(req.getVersion())) {
            List<FtpQuarterlyCustomerPrincipalPricingRsp> rsps = customerPricingLibService.getBaseMapper().selectList(Wrappers.<FtpQuarterlyCustomerPrincipalPricingLib>lambdaQuery()
                    .eq(FtpQuarterlyCustomerPrincipalPricingLib::getGuidanceId, req.getId())
                    .eq(FtpQuarterlyCustomerPrincipalPricingLib::getVersion, req.getVersion())).stream().map(lib -> {
                FtpQuarterlyCustomerPrincipalPricingRsp rsp = mainConverter.customerLib2Rsp(lib);
                rsp.setId(lib.getOriginId());
                return rsp;
            }).collect(Collectors.toList());
            return rsps;
        } else {
            return mainConverter.customerEntity2Rsp(customerPricingService.selectByGuidanceId(req.getId()));
        }
    }

    public List<FtpQuarterlyEnterprisePricingRsp> detailPricingEnterprise(FtpGuidanceIdReq req) {
        if (ObjectUtil.isNotEmpty(req.getVersion())) {
            List<FtpQuarterlyEnterprisePricingRsp> rsps = enterprisePricingLibService.getBaseMapper()
                    .selectList(Wrappers.<FtpQuarterlyEnterprisePricingLib>lambdaQuery()
                            .eq(FtpQuarterlyEnterprisePricingLib::getGuidanceId, req.getId())
                            .eq(FtpQuarterlyEnterprisePricingLib::getVersion, req.getVersion())).stream().map(lib -> {
                        FtpQuarterlyEnterprisePricingRsp rsp = mainConverter.enterpriseLib2Rsp(lib);
                        rsp.setId(lib.getOriginId());
                        return rsp;
                    }).collect(Collectors.toList());

            return rsps;
        } else {
            return mainConverter.enterpriseEntity2Rsp(enterprisePricingService.selectByGuidanceId(req.getId()));
        }
    }


    @Transactional(rollbackFor = Throwable.class)
    public void importExcel(InputStream inputStream, Long guidanceId) throws IOException {
        ProcessResp processResp = findRelatedProcess(guidanceId);
        if (Objects.nonNull(processResp)) {
            boolean isStartUserNode = FlowUtil.isStartUserNode(processResp);
            if (!isStartUserNode) {
                throw new AuthCheckException("该数据处于流程中，且流程不在发起人节点，不允许修改数据");
            }
        }
        XSSFWorkbook sheets = new XSSFWorkbook(inputStream);
        parseSheet1(sheets.getSheetAt(0), guidanceId);
        parseSheet2(sheets.getSheetAt(1), guidanceId);
        FtpQuarterlyGuidance guidance = baseMapper.selectById(guidanceId);
        stateMachine.execute(FtpContext.of(guidance, FtpEvent.MODIFY_SAVE, guidance.getProcessStatus()));
    }

    public void exportExcel(Long guidanceId, OutputStream out) {
        InputStream template = FtpQuarterlyGuidanceService.class.getResourceAsStream("/doc/ftp_quarterly.xlsx");
        Map<String, Double> exportData1 = basePricingService.selectByGuidanceId(guidanceId).stream()
                .collect(Collectors.toMap(FtpQuarterlyBasePricing::getSite,
                        item -> (item.getPercentValue() / 10000.0)));
        if (ObjectUtil.isEmpty(exportData1)) {
            throw new MithrasException("暂无数据，不能导出!");
        }
        Map<String, Double> exportData2 = customerPricingService.selectByGuidanceId(guidanceId).stream()
                .collect(Collectors.toMap(FtpQuarterlyCustomerPrincipalPricing::getSite,
                        item -> (item.getPercentValue() / 10000.0)));
        try {
            ExcelWriter excelWriter = EasyExcel.write(out).withTemplate(template).build();
            WriteSheet writeSheet1 = EasyExcel.writerSheet(0).build();
            excelWriter.fill(exportData1, writeSheet1);
            WriteSheet writeSheet2 = EasyExcel.writerSheet(1).build();
            excelWriter.fill(exportData2, writeSheet2);
            excelWriter.finish();
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private void parseSheet2(Sheet sheet2, Long guidanceId) {
        List<FtpQuarterlyMonthPricing> monthPricings = new ArrayList<>();
        List<FtpQuarterlyCustomerPrincipalPricing> customerPricings = new ArrayList<>();
        List<FtpQuarterlyEnterprisePricing> enterprisePricings = new ArrayList<>();
        int[] monthRows = new int[]{2, 3, 4};
        int[] enterpriseRows = new int[]{11, 12, 13, 14};

        for (int row : monthRows) {
            Row rowData = sheet2.getRow(row);
            for (int column = 1; column <= 16; column++) {
                if (column <= 9) {
                    FtpQuarterlyMonthPricing monthPricing = new FtpQuarterlyMonthPricing();
                    monthPricing.setGuidanceId(guidanceId);
                    monthPricing.setSite("R" + row + "C" + column);
                    if (row % 3 == 1) {
                        monthPricing.setCreditTerm(CreditTerm.ONE_YEAR.name());
                    } else if (row % 3 == 2) {
                        monthPricing.setCreditTerm(CreditTerm.ONE_TO_THREE_YEARS.name());
                    } else {
                        monthPricing.setCreditTerm(CreditTerm.MORE_THAN_THREE_YEARS.name());
                    }
                    if (column <= 3) {
                        monthPricing.setProjectClassify(ProjectClassify.ENCOURAGEMENT.name());
                    } else if (column <= 6) {
                        monthPricing.setProjectClassify(ProjectClassify.MODERATE_SUPPORT.name());
                    } else {
                        monthPricing.setProjectClassify(ProjectClassify.CAUTIOUS.name());
                    }
                    if (column % 3 == 1) {
                        monthPricing.setMonthType(MonthType.FIRST_MONTH.name());
                    } else if (column % 3 == 2) {
                        monthPricing.setMonthType(MonthType.SECOND_MONTH.name());
                    } else {
                        monthPricing.setMonthType(MonthType.THIRD_MONTH.name());
                    }
                    monthPricing.setPercentValue(getNumericCellValue(rowData, column));
                    monthPricings.add(monthPricing);
                } else if (column <= 12) {
                    FtpQuarterlyMonthPricing monthPricing = new FtpQuarterlyMonthPricing();
                    monthPricing.setGuidanceId(guidanceId);
                    monthPricing.setSite("R" + row + "C" + column);
                    monthPricing.setMonthType(MonthType.MEAN_VALUE.name());
                    if (row % 3 == 1) {
                        monthPricing.setCreditTerm(CreditTerm.ONE_YEAR.name());
                    } else if (row % 3 == 2) {
                        monthPricing.setCreditTerm(CreditTerm.ONE_TO_THREE_YEARS.name());
                    } else {
                        monthPricing.setCreditTerm(CreditTerm.MORE_THAN_THREE_YEARS.name());
                    }
                    if (column == 10) {
                        monthPricing.setProjectClassify(ProjectClassify.ENCOURAGEMENT.name());
                    } else if (column == 11) {
                        monthPricing.setProjectClassify(ProjectClassify.MODERATE_SUPPORT.name());
                    } else {
                        monthPricing.setProjectClassify(ProjectClassify.CAUTIOUS.name());
                    }
                    monthPricing.setPercentValue(getNumericCellValue(rowData, column));
                    monthPricings.add(monthPricing);
                } else {
                    FtpQuarterlyCustomerPrincipalPricing pricing = new FtpQuarterlyCustomerPrincipalPricing();
                    pricing.setGuidanceId(guidanceId);
                    pricing.setSite("R" + row + "C" + column);
                    if (row % 3 == 1) {
                        pricing.setCreditTerm(CreditTerm.ONE_YEAR.name());
                    } else if (row % 3 == 2) {
                        pricing.setCreditTerm(CreditTerm.ONE_TO_THREE_YEARS.name());
                    } else {
                        pricing.setCreditTerm(CreditTerm.MORE_THAN_THREE_YEARS.name());
                    }
                    if (column == 13) {
                        pricing.setEnterpriseType(EnterpriseType.STATE_OWNED_ENTERPRISE.name());
                    } else if (column == 14) {
                        pricing.setEnterpriseType(EnterpriseType.PRIVATE_LISTED.name());
                    } else if (column == 15) {
                        pricing.setEnterpriseType(EnterpriseType.PRIVATE_NON_LISTED.name());
                    } else {
                        pricing.setEnterpriseType(EnterpriseType.OTHER_SMALL_AND_MICRO.name());
                    }
                    pricing.setPercentValue(getNumericCellValue(rowData, column));
                    customerPricings.add(pricing);
                }
            }
        }
        for (int row : enterpriseRows) {
            Row rowData = sheet2.getRow(row);
            for (int column = 1; column <= 9; column++) {
                FtpQuarterlyEnterprisePricing pricing = new FtpQuarterlyEnterprisePricing();
                pricing.setGuidanceId(guidanceId);
                pricing.setSite("R" + row + "C" + column);
                if (row == 11) {
                    pricing.setEnterpriseType(EnterpriseType.STATE_OWNED_ENTERPRISE.name());
                } else if (row == 12) {
                    pricing.setEnterpriseType(EnterpriseType.PRIVATE_LISTED.name());
                } else if (row == 13) {
                    pricing.setEnterpriseType(EnterpriseType.PRIVATE_NON_LISTED.name());
                } else {
                    pricing.setEnterpriseType(EnterpriseType.OTHER_SMALL_AND_MICRO.name());
                }
                if (column <= 3) {
                    pricing.setProjectClassify(ProjectClassify.ENCOURAGEMENT.name());
                } else if (column <= 6) {
                    pricing.setProjectClassify(ProjectClassify.MODERATE_SUPPORT.name());
                } else {
                    pricing.setProjectClassify(ProjectClassify.CAUTIOUS.name());
                }
                if (column % 3 == 1) {
                    pricing.setCreditTerm(CreditTerm.ONE_YEAR.name());
                } else if (column % 3 == 2) {
                    pricing.setCreditTerm(CreditTerm.ONE_TO_THREE_YEARS.name());
                } else {
                    pricing.setCreditTerm(CreditTerm.MORE_THAN_THREE_YEARS.name());
                }
                pricing.setPercentValue(getNumericCellValue(rowData, column));
                enterprisePricings.add(pricing);
            }
        }
        quarterlyPricingService.saveMonthPricings(guidanceId, monthPricings);
        quarterlyPricingService.saveCustomerPricings(guidanceId, customerPricings);
        quarterlyPricingService.saveEnterprisePricings(guidanceId, enterprisePricings);
    }

    private void parseSheet1(Sheet sheet1, Long guidanceId) {
        List<FtpQuarterlyBasePricing> basePricings = new ArrayList<>();
        int[] rows = new int[]{2, 3, 4, 7, 8, 9, 12, 13, 14};
        int[] encouragementRows = new int[]{2, 7, 12};
        int[] moderateSupportRows = new int[]{3, 8, 13};
        int[] cautiousRows = new int[]{4, 9, 14};
        for (int row : rows) {
            Row rowData = sheet1.getRow(row);
            for (int column = 1; column <= 6; column++) {
                FtpQuarterlyBasePricing basePricing = new FtpQuarterlyBasePricing();
                basePricing.setGuidanceId(guidanceId);
                basePricing.setSite("R" + row + "C" + column);
                if (row <= 4) {
                    basePricing.setMonthType(MonthType.FIRST_MONTH.name());
                } else if (row <= 9) {
                    basePricing.setMonthType(MonthType.SECOND_MONTH.name());
                } else {
                    basePricing.setMonthType(MonthType.THIRD_MONTH.name());
                }
                if (org.bouncycastle.util.Arrays.contains(encouragementRows, row)) {
                    basePricing.setProjectClassify(ProjectClassify.ENCOURAGEMENT.name());
                } else if (org.bouncycastle.util.Arrays.contains(moderateSupportRows, row)) {
                    basePricing.setProjectClassify(ProjectClassify.MODERATE_SUPPORT.name());
                } else if (org.bouncycastle.util.Arrays.contains(cautiousRows, row)) {
                    basePricing.setProjectClassify(ProjectClassify.CAUTIOUS.name());
                }
                if (column <= 3) {
                    basePricing.setEnterpriseType(EnterpriseType.STATE_OWNED.name());
                } else {
                    basePricing.setEnterpriseType(EnterpriseType.OTHER.name());
                }
                if (column % 3 == 0) {
                    basePricing.setCreditTerm(CreditTerm.MORE_THAN_THREE_YEARS.name());
                } else if (column % 3 == 1) {
                    basePricing.setCreditTerm(CreditTerm.ONE_YEAR.name());
                } else {
                    basePricing.setCreditTerm(CreditTerm.ONE_TO_THREE_YEARS.name());
                }
                basePricing.setPercentValue(getNumericCellValue(rowData, column));
                basePricings.add(basePricing);
            }
        }
        quarterlyPricingService.saveBasePricings(guidanceId, basePricings);
    }

    public ProcessResp findRelatedProcess(Long id) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setBusinessKey(String.valueOf(id));
        processPageReq.setModelKeyList(BusinessModuleEnum.FTP_QUARTERLY_GUIDANCE.getModelKeyList());
        processPageReq.setProcessStatusList(java.util.Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(),
                ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = flowTaskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream().findFirst().orElse(null);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void submit(Long id) {
        FtpQuarterlyGuidance guidance = baseMapper.selectById(id);
        List<FtpQuarterlyBasePricing> pricings = basePricingService.selectByGuidanceId(id);
        if (ObjectUtil.isEmpty(pricings)) {
            throw new MithrasException("请先导入数据");
        }
        StartProcessReq startProcessReq = new StartProcessReq();
        // 判断使用创建流程还是修改流程
        if (NEW.name().equals(guidance.getGuidanceRecordStatus())) {
            startProcessReq.setModelKey(ProcessModelTypeEnum.FtpQuarterlyGuidanceCreateFlow.name());
        } else {
            startProcessReq.setModelKey(ProcessModelTypeEnum.FtpQuarterlyGuidanceModifyFlow.name());
        }
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setBusinessKey(String.valueOf(id));
        OrgDO jhcwb = orgDOMapper.queryByCode("JHCWB");
        startProcessReq.setStartUserDeptId(Optional.ofNullable(jhcwb).map(OrgDO::getId)
                .map(String::valueOf).orElse(null));
        startProcessReq.setProcessInstanceName(guidance.getYear() + "年第" + guidance.getQuarter() + "季度最低收益率指导");
        processApiService.start(startProcessReq);
        stateMachine.execute(FtpContext.of(guidance, FtpEvent.SUBMIT_APPROVAL, guidance.getProcessStatus()));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void processEnd(Long id, Integer endType, Long startUserId, String processInstanceId, String modelKey) {
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        FtpQuarterlyGuidance guidance = baseMapper.selectById(id);
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

    @Resource
    private CommonVersionMapper versionMapper;

    public FtpQuarterlyNewestRsp newestVersionData(Long id) {
        CommonVersion commonVersion = versionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, id)
                .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
                .eq(CommonVersion::getModule, BusinessModuleEnum.FTP_QUARTERLY_GUIDANCE.name())
                .orderByDesc(CommonVersion::getVersion)
                .last("LIMIT 1"));
        FtpQuarterlyNewestRsp rsp = new FtpQuarterlyNewestRsp();
        List<FtpQuarterlyBasePricingLib> basePricingLibs =
                basePricingLibService.getBaseMapper().selectList(Wrappers.<FtpQuarterlyBasePricingLib>lambdaQuery()
                        .eq(FtpQuarterlyBasePricing::getGuidanceId, id)
                        .eq(FtpQuarterlyBasePricingLib::getVersion, commonVersion.getVersion()));
        rsp.setBasePricing(mainConverter.baseLib2Rsp(basePricingLibs));
        // todo
        return rsp;
    }
}
