package cn.zswltech.mithras.application.orchestration.contract.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.dto.contract.rent.ContractRentEstimateExportREQ;
import cn.zswltech.mithras.dto.contract.rent.ContractRentEstimateGenerateREQ;
import cn.zswltech.mithras.dto.projreview.cashflowplan.IRRCalculateResultRSP;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.contract.convert.contract.ContractRentConvert;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.RepayRateEnum;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.application.orchestration.export.excel.ExcelExporterFactory;
import cn.zswltech.mithras.projectprocess.excel.exporter.IRRCalculateExcelExporter;
import cn.zswltech.mithras.projectprocess.excel.importer.CashFlowExcelImporter;
import cn.zswltech.mithras.projectprocess.excel.model.CashFlowExcelModel;
import cn.zswltech.mithras.projectprocess.excel.model.IRRCalculateExcelModel;
import cn.zswltech.mithras.contract.mapper.contract.ContractRentEstimateMapper;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.contract.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.contract.model.contract.ContractRentEstimate;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewCashFlowPlan;
import cn.zswltech.mithras.projectprocess.application.bo.CashFlowBO;
import cn.zswltech.mithras.projectprocess.application.bo.CashFlowCalculateBO;
import cn.zswltech.mithras.projectprocess.application.bo.CashFlowIRRBO;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.bo.*;
import cn.zswltech.mithras.contract.core.dto.ContractPriceHelperBO;
import cn.zswltech.mithras.contract.core.impl.ContractRentBasicService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractLeasePriceService;
import cn.zswltech.mithras.contract.core.ContractPriceService;
import cn.zswltech.mithras.contract.core.ContractRentEstimateService;
import cn.zswltech.mithras.contract.versioning.application.ContractBaseInfoLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractRentEstimateLibService;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractBaseInfoLibHandler;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.application.orchestration.util.FinancialUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.foundation.exception.MithrasException.err;
import cn.zswltech.mithras.contract.core.dto.ContractEstimateCashFlowExporterBO;

/**
 * @author dingqi
 * @date 2022/8/12
 * @description
 */
@Slf4j
@Service
public class ContractRentEstimateServiceImpl extends ServiceImpl<ContractRentEstimateMapper, ContractRentEstimate> implements ContractRentEstimateService {
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractBaseInfoLibService contractBaseInfoLibService;
    @Resource
    private ContractBaseInfoLibHandler contractBaseInfoLibHandler;
    @Resource
    private CashFlowExcelImporter cashFlowExcelImporter;
    @Resource
    private ContractRentBasicService contractRentBasicService;
    @Resource
    private ContractLeasePriceService contractLeasePriceService;
    @Resource
    private IRRCalculateExcelExporter irrCalculateExcelExporter;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private ContractRentEstimateLibService contractRentEstimateLibService;
    @Resource
    private ContractPriceService contractPriceService;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void importExcel(MultipartFile file, Long contractId, String planStartDate, String scene) throws Exception {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        // 解析Excel
        List<CashFlowExcelModel> cashFlowExcelModelList = Assert.notEmpty(cashFlowExcelImporter.parse(file.getInputStream()), () -> MithrasException.newException("没有找到符合条件的导入数据"));
        // 过滤非租金现金流
        List<CashFlowExcelModel> filterCashFlowExcelModelList = cashFlowExcelModelList.stream().filter(item -> Objects.nonNull(item.getCashFlowPhase()) && item.getCashFlowPhase() > 0).collect(Collectors.toList());
        // 校验数据
        this.check(contractBaseInfo, filterCashFlowExcelModelList);
        // 模型转换
        Map<Integer, ContractRentEstimate> importMap = filterCashFlowExcelModelList.stream().map(excelModel -> {
            ContractRentEstimate contractRentEstimate = ContractRentConvert.toContractRentEstimate(excelModel);
            contractRentEstimate.setContractId(contractId);
            return contractRentEstimate;
        }).collect(Collectors.toMap(ContractRentEstimate::getCashFlowPhase, item -> item));
        // 覆盖概算租金表
        SpringUtil.getBean(ContractRentEstimateService.class).doOverwriteRentEstimate(importMap, contractId, planStartDate, scene);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void doOverwriteRentEstimate(Map<Integer, ContractRentEstimate> importMap, Long contractId, String planStartDate, String scene) {
        // 已有数据
        List<ContractRentEstimate> existList = this.listByContractId(contractId, null);
        Map<Integer, ContractRentEstimate> dbMap = existList.stream().collect(Collectors.toMap(ContractRentEstimate::getCashFlowPhase, item -> item));
        // dbMap和importMap同时存在的数据，用importMap的数据更新
        // dbMap有但是importMap没有的，删除dbMap的数据
        // dbMap没有但是importMap有的，则新增
        List<ContractRentEstimate> toSaveOrUpdateList = new LinkedList<>();
        List<Long> toRemoveIdList = new LinkedList<>();
        List<Integer> dbMapKeyList = new ArrayList<>(dbMap.keySet());
        dbMapKeyList.sort(Comparator.comparingInt(Integer::intValue));
        int dbPhaseMax = dbMapKeyList.size() == 0 ? 0 : dbMapKeyList.get(dbMapKeyList.size() - 1);
        List<Integer> importMapKeyList = new ArrayList<>(importMap.keySet());
        importMapKeyList.sort(Comparator.comparingInt(Integer::intValue));
        int importPhaseMax = importMapKeyList.get(importMapKeyList.size() - 1);
        int loopEnd = Math.max(dbPhaseMax, importPhaseMax);
        int i = 0;
        while (i <= loopEnd) {
            i++;
            ContractRentEstimate dbData = dbMap.get(i);
            ContractRentEstimate importData = importMap.get(i);
            if (Objects.isNull(dbData) && Objects.isNull(importData)) {
                continue;
            }
            if (Objects.nonNull(dbData) && Objects.nonNull(importData)) {
                // 使用import数据更新db数据
                importData.setId(dbData.getId());
                importData.setCreateBy(dbData.getCreateBy());
                importData.setCreateTime(dbData.getCreateTime());
                importData.setUpdateBy(AccountUtil.getLoginInfo().getId());
                importData.setUpdateTime(LocalDateTime.now());
                toSaveOrUpdateList.add(importData);
            } else if (Objects.nonNull(dbData)) {
                // 删除db数据
                toRemoveIdList.add(dbData.getId());
            } else {
                // 新增import数据
                toSaveOrUpdateList.add(importData);
            }
        }
        if (!CollectionUtils.isEmpty(toSaveOrUpdateList)) {
            this.saveOrUpdateBatch(toSaveOrUpdateList);
        }
        if (!CollectionUtils.isEmpty(toRemoveIdList)) {
            this.removeByIds(toRemoveIdList);
        }
        // 更新主合同表
        ContractBaseInfo toUpdateModel = new ContractBaseInfo();
        toUpdateModel.setId(contractId);
        toUpdateModel.setEstimatedLeaseDate(LocalDateTimeUtil.parse(planStartDate, DatePattern.NORM_DATE_PATTERN).toLocalDate());
        // 状态维护统一到ContractOperationPrepare中了
//        if (RentImportSceneEnum.CREATE.name().equals(scene)) {
//            toUpdateModel.setContractProcessStatus(ContractProcessStatusEnum.NEW_UNCOMMIT.name());
//        } else if (RentImportSceneEnum.CHANGE_OTHER.name().equals(scene)) {
//            toUpdateModel.setContractProcessStatus(ContractProcessStatusEnum.CHANGE_UNCOMMIT.name());
//            toUpdateModel.setContractProcessChangeStatus(ContractChangeTypeEnum.OTHER.name());
//        }
        contractBaseInfoService.updateById(toUpdateModel);
    }

    @Override
    public ContractRentEstimate getLastOne(Long contractId) {
        LambdaQueryWrapper<ContractRentEstimate> query = Wrappers.lambdaQuery();
        query.eq(ContractRentEstimate::getContractId, contractId);
        query.orderByDesc(ContractRentEstimate::getCashFlowDate);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    @Override
    public List<ContractRentEstimate> listByContractId(Long contractId, String version) {
        if (StrUtil.isBlank(version)) {
            LambdaQueryWrapper<ContractRentEstimate> query = Wrappers.lambdaQuery();
            query.eq(ContractRentEstimate::getContractId, contractId);
            query.orderByAsc(ContractRentEstimate::getCashFlowPhase);
            return this.list(query);
        } else {
            return contractRentEstimateLibService.getByVersion(contractId, version);
        }
    }

    @Override
    public void exportExcel(ContractRentEstimateExportREQ req, OutputStream outputStream) throws Exception {
        ContractEstimateCashFlowExporterBO bo = this.build(req);
        ProjectBizType projectBizType = ProjectBizType.of(bo.getContractBaseInfo().getBizType());
        if (Objects.isNull(projectBizType)) {
            throw new MithrasException("未定义的业务类型");
        }
        ExcelExporterFactory.getContractEstimateRentExcelExporter(projectBizType).exportExcel(this.toExcelDataList(bo.getContractRentEstimateList()), outputStream);
    }

    @Override
    public void exportRichExcel(ContractRentEstimateExportREQ req, OutputStream outputStream) {
        ContractEstimateCashFlowExporterBO bo = this.build(req);
        ProjectBizType projectBizType = ProjectBizType.of(bo.getContractBaseInfo().getBizType());
        if (Objects.isNull(projectBizType)) {
            throw new MithrasException("未定义的业务类型");
        }
        ExcelExporterFactory.getContractEstimateCashFlowExcelExporter(projectBizType).export(outputStream, bo);
    }

    private ContractEstimateCashFlowExporterBO build(ContractRentEstimateExportREQ req) {
        ContractBaseInfo contractBaseInfo;
        List<ContractRentEstimate> contractRentEstimateList;
        if (StrUtil.isBlank(req.getVersion())) {
            contractBaseInfo = Assert.notNull(contractBaseInfoService.getById(req.getContractId()), () -> MithrasException.newException("合同信息不存在"));
            contractRentEstimateList = Assert.notEmpty(this.listByContractId(req.getContractId(), null), () -> MithrasException.newException("暂无可导出的数据"));
        } else {
            ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibService.getByOriginIdVersion(req.getContractId(), req.getVersion());
            Assert.notNull(contractBaseInfoLib, () -> MithrasException.newException("对应版本的合同信息不存在"));
            contractBaseInfo = contractBaseInfoLibHandler.actualLib2Entity(contractBaseInfoLib);
            contractRentEstimateList = contractRentEstimateLibService.getByVersion(req.getContractId(), req.getVersion());
        }
        List<ContractRentEstimate> list = new ArrayList<>();
        ContractLeasePrice contractLeasePrice = contractLeasePriceService.lambdaQuery().eq(ContractLeasePrice::getContractId, req.getContractId()).one();
        if (!ObjectUtils.isEmpty(contractRentEstimateList)&&!ObjectUtils.isEmpty(contractLeasePrice) && !ObjectUtils.isEmpty(contractLeasePrice.getFirstInstallmentInterest())
                && contractLeasePrice.getFirstInstallmentInterest()>0){
            // 增加零期现金流
            ContractRentEstimate contractRentEstimate = new ContractRentEstimate();
            contractRentEstimate.setContractId(req.getContractId());
            contractRentEstimate.setCashFlowDate(contractBaseInfo.getEstimatedLeaseDate());
            contractRentEstimate.setCashFlowPhase(0);
            contractRentEstimate.setRent(contractLeasePrice.getFirstInstallmentInterest());
            contractRentEstimate.setPrincipal(0L);
            contractRentEstimate.setInterest(contractLeasePrice.getFirstInstallmentInterest());
            contractRentEstimate.setRemainingPrincipal(0L);
            list.add(contractRentEstimate);
            list.addAll(contractRentEstimateList);
            contractRentEstimateList = list;
        }
        ContractEstimateCashFlowExporterBO bo = new ContractEstimateCashFlowExporterBO();
        bo.setContractBaseInfo(contractBaseInfo);
        bo.setContractRentEstimateList(contractRentEstimateList);
        bo.setVersion(req.getVersion());
        return bo;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void generate(ContractRentEstimateGenerateREQ req) {
        Long contractId = req.getId();
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        Assert.notNull(contractBaseInfo, () -> MithrasException.newException("合同信息不存在"));
        // 更新计划起租日
        contractBaseInfo.setEstimatedLeaseDate(LocalDateTimeUtil.parseDate(req.getPlanStartDate(), DatePattern.NORM_DATE_PATTERN));
        contractBaseInfoService.updateById(contractBaseInfo);
        ContractLeasePrice leasePrice = this.basicPreCheck(contractBaseInfo);
        // 生成现金流
        CashFlowCalculateBO cashFlowCalculateBO = ContractRentConvert.toCashFlowCalculateBO(leasePrice);
        // 填充计划起租日
        LocalDate estimatedLeaseDate = contractBaseInfo.getEstimatedLeaseDate();
        cashFlowCalculateBO.setStartDate(estimatedLeaseDate);
        List<CashFlowBO> cashFlowBOList = FinancialUtil.calcCashFlow(cashFlowCalculateBO);
        Assert.notEmpty(cashFlowBOList, () -> MithrasException.newException("没有成功生成现金流"));
        List<ContractRentEstimate> contractRentEstimateList = cashFlowBOList.stream()
                .filter(item -> item.getCashFlowPhase() > 0)
                .map(item -> ContractRentConvert.toContractRentEstimate(contractId, item))
                .collect(Collectors.toList());
        Map<Integer, ContractRentEstimate> importMap = contractRentEstimateList.stream().collect(Collectors.toMap(ContractRentEstimate::getCashFlowPhase, item -> item));
        // 覆盖概算租金表
        SpringUtil.getBean(ContractRentEstimateService.class).doOverwriteRentEstimate(importMap, contractId, req.getPlanStartDate(), req.getScene());
    }

    @Override
    public IRRCalculateResultRSP calculateIRR(SinglePkREQ req) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getId());
        Assert.notNull(contractBaseInfo, () -> MithrasException.newException("合同信息不存在"));
        Assert.notNull(contractBaseInfo.getEstimatedLeaseDate(), () -> MithrasException.newException("计划起租日为空，无法计算"));
//        ContractLeasePrice leasePrice = this.basicPreCheck(contractBaseInfo);
        List<ContractRentEstimate> contractRentEstimateList = this.listByContractId(req.getId(), null);
        Assert.notEmpty(contractRentEstimateList, () -> MithrasException.newException("概算表为空，无法计算"));
        // 报价方案
        ContractPriceDetailREQ contractPriceDetailREQ = new ContractPriceDetailREQ();
        contractPriceDetailREQ.setContractId(contractBaseInfo.getId());
        ContractPriceDetailRSP contractPriceDetailRSP = contractPriceService.detail(contractPriceDetailREQ);
        if (Objects.isNull(contractPriceDetailRSP.getApplyCreditAmount())) {
            throw new MithrasException("合同金额不能为空");
        }
        if (Objects.isNull(contractPriceDetailRSP.getConsultingFee())) {
            throw new MithrasException("咨询费/服务费/手续费不能为空");
        }
        if (Objects.isNull(contractPriceDetailRSP.getEarnestMoney())) {
            throw new MithrasException("保证金不能为空");
        }
        if (Objects.isNull(contractPriceDetailRSP.getDownPayment())) {
            throw new MithrasException("首期租金不能为空");
        }
        if (Objects.isNull(contractPriceDetailRSP.getNominalPrice())) {
            throw new MithrasException("名义价款不能为空");
        }
        if (Objects.isNull(contractPriceDetailRSP.getRepayRate())) {
            throw new MithrasException("还款频率不能为空");
        }
        List<CashFlowBO> cashFlowBOList = new LinkedList<>();
        // 第0期数据
        CashFlowBO zeroPhase = new CashFlowBO();
        zeroPhase.setCashFlowDate(contractBaseInfo.getEstimatedLeaseDate());
        zeroPhase.setCashFlowPhase(0);
        zeroPhase.setCashFlowAmount(contractPriceDetailRSP.getApplyCreditAmount() * -1 + contractPriceDetailRSP.getConsultingFee() + contractPriceDetailRSP.getEarnestMoney() + contractPriceDetailRSP.getDownPayment() + contractPriceDetailRSP.getCommission() + contractPriceDetailRSP.getFirstInstallmentInterest());
        cashFlowBOList.add(zeroPhase);
        // 填充概算表数据
        for (ContractRentEstimate contractRentEstimate : contractRentEstimateList) {
            cashFlowBOList.add(ContractRentConvert.toCashFlowBO(contractRentEstimate));
        }
        cashFlowBOList.sort(Comparator.comparing(CashFlowBO::getCashFlowPhase));
        // 调整最后一期现金流
        CashFlowBO last = cashFlowBOList.get(cashFlowBOList.size() - 1);
        long lastAmount = last.getCashFlowAmount() - contractPriceDetailRSP.getEarnestMoney() + contractPriceDetailRSP.getNominalPrice();
        last.setCashFlowAmount(lastAmount);
        RepayRateEnum repayRateEnum = RepayRateEnum.of(contractPriceDetailRSP.getRepayRate());
        if (Objects.isNull(repayRateEnum)) {
            throw new MithrasException("没有找到对应的还款频率类型");
        }
        // 计算IRR
        CashFlowIRRBO cashFlowIRRBO = FinancialUtil.calculateIRR(contractPriceDetailRSP.getMonthCount(), repayRateEnum, cashFlowBOList);
        // 生成计算详情文件
        List<IRRCalculateExcelModel.CashFlowAdjustExcelModel> excelDataList = cashFlowIRRBO.getCashFlowAdjustList().stream().map(item -> {
            IRRCalculateExcelModel.CashFlowAdjustExcelModel excelModel = new IRRCalculateExcelModel.CashFlowAdjustExcelModel();
            excelModel.setCashFlowDate(item.getCashFlowDate());
            excelModel.setAdjustCashFlowDate(item.getAdjustCashFlowDate());
            excelModel.setCashFlowPhase(item.getCashFlowPhase());
            excelModel.setAdjustCashFlowPhase(item.getAdjustCashFlowPhase());
            excelModel.setCashFlowAmount(BigDecimal.valueOf(item.getCashFlowAmount()));
            excelModel.setAdjustCashFlowAmount(item.getAdjustCashFlowAmount());
            return excelModel;
        }).collect(Collectors.toList());
        IRRCalculateExcelModel excelModel = new IRRCalculateExcelModel();
        excelModel.setIrrPerPhase(cashFlowIRRBO.getIrrPerPhase());
        excelModel.setIrr(cashFlowIRRBO.getIrr());
        excelModel.setCashFlowAdjustExcelModelList(excelDataList);
        ByteArrayOutputStream baos = null;
        ByteArrayInputStream bais = null;
        Long fileId;
        try {
            baos = new ByteArrayOutputStream();
            irrCalculateExcelExporter.export(baos, excelModel);
            bais = IoUtil.toStream(baos);
            // 上传临时文件
            fileId = materialsListService.add(bais, UUID.randomUUID().toString() + GlobalConstants.OFFICE_EXCEL_SUFFIX, req.getId(), "CONTRACT_ESTIMATE_IRR_CALCULATE", "TMP");
        } finally {
            if (Objects.nonNull(baos)) {
                try {
                    baos.close();
                } catch (IOException e) {
                    log.error("关闭输出流异常", e);
                }
            }
            if (Objects.nonNull(bais)) {
                try {
                    bais.close();
                } catch (IOException e) {
                    log.error("关闭输入流异常", e);
                }
            }
        }
        // 拼装返回结果
        IRRCalculateResultRSP rsp = new IRRCalculateResultRSP();
        rsp.setFileId(fileId);
        rsp.setIrr(excelModel.getIrr().setScale(4, RoundingMode.HALF_UP).toPlainString());
        return rsp;
    }

    private ContractLeasePrice basicPreCheck(ContractBaseInfo contractBaseInfo) {
        boolean hitBizType = Objects.equals(contractBaseInfo.getBizType(), ProjectBizType.ZL.name()) || Objects.equals(contractBaseInfo.getBizType(), ProjectBizType.ZZ.name());
        Assert.isTrue(hitBizType, () -> MithrasException.newException("该功能暂时只支持租赁和转租赁类型"));
        ContractLeasePrice leasePrice = contractLeasePriceService.getByContractId(contractBaseInfo.getId());
        Assert.notNull(leasePrice, () -> MithrasException.newException("报价方案不存在"));
        Assert.notNull(contractBaseInfo.getEstimatedLeaseDate(), () -> MithrasException.newException("计划起租日不能为空"));
        Assert.notNull(leasePrice.getApplyCreditAmount(), () -> MithrasException.newException("申报授信金额不能为空"));
        Assert.notNull(leasePrice.getEarnestMoney(), () -> MithrasException.newException("保证金不能为空"));
        Assert.notNull(leasePrice.getDownPayment(), () -> MithrasException.newException("首期租金不能为空"));
        Assert.notNull(leasePrice.getConsultingFee(), () -> MithrasException.newException("服务费/咨询费不能为空"));
        Assert.notNull(leasePrice.getNominalPrice(), () -> MithrasException.newException("名义价款不能为空"));
        Assert.notNull(leasePrice.getRepayTimesTotal(), () -> MithrasException.newException("还款期数不能为空"));
        Assert.notBlank(leasePrice.getRepayRate(), () -> MithrasException.newException("还款频率不能为空"));
//        Assert.notNull(leasePrice.getLeaseRatePercent(), () -> MithrasException.newException("租赁利率不能为空"));
        Assert.notNull(leasePrice.getLprPercent(), () -> MithrasException.newException("LPR不能为空"));
        Assert.notNull(leasePrice.getLprAddPercent(), () -> MithrasException.newException("LPR加点不能为空"));
//        Assert.isTrue(Objects.equals(leasePrice.getRentalCalcType(), RentalCalcType.EQUIVALENT_RENTAL.name()), () -> MithrasException.newException("该功能暂时只支持租金计算方式为等额租金的项目"));
        err(!StrUtil.equalsAny(leasePrice.getRentalCalcType(), RepayCalcType.DEBX.name(), RepayCalcType.DEBJ.name()),
                "该功能暂时只支持租金计算方式为[等额本息、等额本金]的项目");//        Assert.isTrue(Objects.equals(leasePrice.getPayType(), PayType.AFTERWARD.name()), () -> MithrasException.newException("该功能暂时只支持支付方式为后付的项目"));
        Assert.isTrue(RepayRateEnum.isByRule(leasePrice.getRepayRate()), () -> MithrasException.newException("该功能暂时只支持还款频率为规则还款的项目"));
        return leasePrice;
    }

    private List<CashFlowExcelModel> toExcelDataList(List<ContractRentEstimate> contractRentEstimateList) {
        return contractRentEstimateList.stream().map(ContractRentConvert::toCashFlowExcelModel).collect(Collectors.toList());
    }

    private void check(ContractBaseInfo contractBaseInfo, List<CashFlowExcelModel> cashFlowExcelModelList) {
        // 查询报价方案
        ContractPriceHelperBO contractPriceHelperBO = contractRentBasicService.getContractPriceItemByPlan(contractBaseInfo);
        Assert.notNull(contractPriceHelperBO.getContractAmount(), () -> MithrasException.newException("请先维护合同金额"));
        Assert.notNull(contractPriceHelperBO.getDownPayment(), () -> MithrasException.newException("请先维护首期租金"));
        BigDecimal contractAmount = NumberUtil.div(contractPriceHelperBO.getContractAmount().toString(), GlobalConstants.MONEY_MULTIPLE, 2);
        BigDecimal downPayment = NumberUtil.div(contractPriceHelperBO.getDownPayment().toString(), GlobalConstants.MONEY_MULTIPLE, 2);
        // 整体校验
        Set<Integer> phaseSet = new HashSet<>();
        BigDecimal principalSum = BigDecimal.valueOf(0L).setScale(2, RoundingMode.HALF_UP);
        for (CashFlowExcelModel cashFlowExcelModel : cashFlowExcelModelList) {
            // 填充数据
            if (Objects.isNull(cashFlowExcelModel.getPrincipal())) {
                cashFlowExcelModel.setPrincipal(BigDecimal.valueOf(0L).setScale(2, RoundingMode.HALF_UP));
            }
            if (Objects.isNull(cashFlowExcelModel.getRent())) {
                cashFlowExcelModel.setRent(BigDecimal.valueOf(0L).setScale(2, RoundingMode.HALF_UP));
            }
            if (Objects.isNull(cashFlowExcelModel.getInterest())) {
                cashFlowExcelModel.setInterest(BigDecimal.valueOf(0L).setScale(2, RoundingMode.HALF_UP));
            }
            // 单行数据校验
            cashFlowExcelModel.checkEstimateRent();
            // 校验期项
            Assert.isTrue(!phaseSet.contains(cashFlowExcelModel.getCashFlowPhase()), () -> MithrasException.newException("期项不能重复"));
            phaseSet.add(cashFlowExcelModel.getCashFlowPhase());
            // 校验剩余本金
            principalSum = principalSum.add(cashFlowExcelModel.getPrincipal());
            if (cashFlowExcelModel.getCashFlowPhase() == 1 && Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
                // 如果直租类型则不校验第1期剩余本金
                continue;
            }
            BigDecimal r = contractAmount.subtract(downPayment).subtract(principalSum);
            Assert.isTrue(r.equals(cashFlowExcelModel.getRemainingPrincipal()), () -> MithrasException.newException("期项为" + cashFlowExcelModel.getCashFlowPhase() + "的一行剩余本金计算结果不正确"));
        }
    }
}
