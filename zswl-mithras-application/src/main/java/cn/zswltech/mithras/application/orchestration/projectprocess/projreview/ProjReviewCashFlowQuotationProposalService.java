package cn.zswltech.mithras.application.orchestration.projectprocess.projreview;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.projreview.cashflowplan.IRRCalculateResultRSP;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanExportREQ;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailRSP;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.projectprocess.convert.projreview.ProjReviewCashFlowPlanConverter;
import cn.zswltech.mithras.projectprocess.convert.projreview.ProjReviewCashFlowQuotationProposalConverter;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.RepayRateEnum;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.application.orchestration.export.excel.ExcelExporterFactory;
import cn.zswltech.mithras.projectprocess.excel.exporter.IRRCalculateExcelExporter;
import cn.zswltech.mithras.projectprocess.excel.importer.CashFlowExcelImporter;
import cn.zswltech.mithras.projectprocess.excel.model.CashFlowExcelModel;
import cn.zswltech.mithras.projectprocess.excel.model.IRRCalculateExcelModel;
import cn.zswltech.mithras.projectprocess.model.projreview.*;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewCashFlowQuotationProposalMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.projectprocess.application.model.CashFlowBO;
import cn.zswltech.mithras.projectprocess.application.model.CashFlowIRRBO;
import cn.zswltech.mithras.projectprocess.application.model.ProjReviewCashFlowExporterBO;
import cn.zswltech.mithras.projectprocess.versioning.projreview.ProjReviewBaseInfoLibService;
import cn.zswltech.mithras.projectprocess.versioning.projreview.ProjReviewCashFlowQuotationProposalLibService;
import cn.zswltech.mithras.projectprocess.versioning.projreview.handler.impl.ProjReviewBaseInfoLibHandler;
import cn.zswltech.mithras.projectprocess.versioning.projreview.handler.impl.ProjReviewCashFlowQuotationProposalLibHandler;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewLeasePriceService;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewUpdateAdvice;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.application.orchestration.util.FinancialUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.foundation.exception.MithrasException.err;

/**
 * @author dingqi
 * @date 2022/8/1
 * @description
 */
@Service
public class ProjReviewCashFlowQuotationProposalService extends ServiceImpl<ProjReviewCashFlowQuotationProposalMapper, ProjReviewCashFlowQuotationProposal>
        implements ProjReviewUpdateAdvice {
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private ProjReviewBaseInfoLibService projReviewBaseInfoLibService;
    @Resource
    private ProjReviewBaseInfoLibHandler projReviewBaseInfoLibHandler;
    @Resource
    private CashFlowExcelImporter cashFlowExcelImporter;
    @Resource
    private ProjReviewPriceService projReviewPriceService;
    @Resource
    private ProjReviewLeasePriceService projReviewLeasePriceService;
    @Resource
    private IRRCalculateExcelExporter irrCalculateExcelExporter;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private ProjReviewCashFlowQuotationProposalLibService projReviewCashFlowQuotationProposalLibService;
    @Resource
    private ProjReviewCashFlowQuotationProposalLibHandler projReviewCashFlowQuotationProposalLibHandler;


    public IRRCalculateResultRSP calculateIRR(Long projReviewId) {
//        ProjReviewLeasePrice leasePrice = this.basicPreCheck(projReviewId);
        List<ProjReviewCashFlowQuotationProposal> cashFlowPlanList = this.listByProjReviewId(projReviewId, null);
        Assert.notEmpty(cashFlowPlanList, () -> MithrasException.newException("现金流为空，无法计算"));
        ProjReviewPriceDetailRSP projReviewPriceDetailRSP = projReviewPriceService.detail(projReviewId);
        if (Objects.isNull(projReviewPriceDetailRSP.getPlanStartDate())) {
            throw new MithrasException("计划起租日为空，无法计算");
        }
        if (Objects.isNull(projReviewPriceDetailRSP.getApplyCreditAmount())) {
            throw new MithrasException("授信金额为空，无法计算");
        }
        if (Objects.isNull(projReviewPriceDetailRSP.getEarnestMoney())) {
            throw new MithrasException("保证金为空，无法计算");
        }
        if (Objects.isNull(projReviewPriceDetailRSP.getConsultingFee())) {
            throw new MithrasException("咨询费/服务费/手续费为空，无法计算");
        }
        if (Objects.isNull(projReviewPriceDetailRSP.getDownPayment())) {
            throw new MithrasException("首期租金为空，无法计算");
        }
        if (StrUtil.isBlank(projReviewPriceDetailRSP.getRepayRate())) {
            throw new MithrasException("还款频率为空，无法计算");
        }
        List<CashFlowBO> cashFlowBOList = new ArrayList<>(cashFlowPlanList.size() + 1);
        // 补全第0期
        CashFlowBO zeroCashFlowBO = new CashFlowBO();
        zeroCashFlowBO.setCashFlowDate(projReviewPriceDetailRSP.getPlanStartDate());
        zeroCashFlowBO.setCashFlowPhase(0);
        zeroCashFlowBO.setCashFlowAmount(projReviewPriceDetailRSP.getApplyCreditAmount() * -1 + projReviewPriceDetailRSP.getEarnestMoney() + projReviewPriceDetailRSP.getConsultingFee() + projReviewPriceDetailRSP.getDownPayment() + projReviewPriceDetailRSP.getCommission() + projReviewPriceDetailRSP.getFirstInstallmentInterest());
        cashFlowBOList.add(zeroCashFlowBO);
        // 填充完整现金流
        for (ProjReviewCashFlowQuotationProposal projReviewCashFlowPlan : cashFlowPlanList) {
            cashFlowBOList.add(ProjReviewCashFlowQuotationProposalConverter.toCashFlowBO(projReviewCashFlowPlan));
        }
        RepayRateEnum repayRateEnum = RepayRateEnum.of(projReviewPriceDetailRSP.getRepayRate());
        if (Objects.isNull(repayRateEnum)) {
            throw new MithrasException("没有找到对应的还款频率类型");
        }
        // 计算IRR
        CashFlowIRRBO cashFlowIRRBO = FinancialUtil.calculateIRR(projReviewPriceDetailRSP.getMonthCount(), repayRateEnum, cashFlowBOList);
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
            fileId = materialsListService.add(bais, UUID.randomUUID().toString() + GlobalConstants.OFFICE_EXCEL_SUFFIX, projReviewId, "IRR_DETAIL", "TMP");
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

    @Transactional(rollbackFor = Throwable.class)
    public void generate(Long projReviewId) {
        ProjReviewLeasePrice leasePrice = this.basicPreCheck(projReviewId);
        LocalDate plannedStartingDate = leasePrice.getPlannedStartingDate();
        // 生成现金流
        List<CashFlowBO> cashFlowBOList = FinancialUtil.calcCashFlow(ProjReviewCashFlowPlanConverter.toCashFlowCalculateBO(leasePrice));
        Assert.notEmpty(cashFlowBOList, () -> MithrasException.newException("没有成功生成现金流"));
        List<ProjReviewCashFlowQuotationProposal> cashFlowPlanList = cashFlowBOList.stream()
                .filter(item -> item.getCashFlowPhase() > 0)
                .map(item -> ProjReviewCashFlowQuotationProposalConverter.toProjReviewCashFlowPlan(projReviewId, item))
                .collect(Collectors.toList());
        Map<Integer, ProjReviewCashFlowQuotationProposal> map = cashFlowPlanList.stream().collect(Collectors.toMap(ProjReviewCashFlowQuotationProposal::getCashFlowPhase, item -> item));
        // 覆盖现金流计划表
        SpringUtil.getBean(ProjReviewCashFlowQuotationProposalService.class).doOverwriteCashFlowPlan(map, projReviewId);
    }

    public void exportRent(ProjReviewCashFlowPlanExportREQ req, OutputStream outputStream) throws Exception {
        ProjReviewCashFlowExporterBO bo = this.build(req);
        // 模型转换
        List<CashFlowExcelModel> cashFlowExcelModelList = bo.getProjReviewCashFlowPlanList().stream().map(ProjReviewCashFlowPlanConverter::toCashFlowExcelModel).collect(Collectors.toList());
        // 执行导出
        ProjectBizType projectBizType = ProjectBizType.of(bo.getProjReviewBaseInfo().getBizType());
        ExcelExporterFactory.getProjReviewRentExcelExporter(projectBizType).exportExcel(cashFlowExcelModelList, outputStream);
    }

    public void exportCashFlow(ProjReviewCashFlowPlanExportREQ req, OutputStream outputStream) {
        ProjReviewCashFlowExporterBO bo = this.build(req);
        ProjectBizType projectBizType = ProjectBizType.of(bo.getProjReviewBaseInfo().getBizType());
        // 执行导出
        ExcelExporterFactory.getProjReviewCashFlowExcelExporter(projectBizType).export(outputStream, bo);
    }

    public List<ProjReviewCashFlowQuotationProposal> listByProjReviewId(Long projReviewId, String version) {
        if (StrUtil.isBlank(version)) {
            LambdaQueryWrapper<ProjReviewCashFlowQuotationProposal> query = Wrappers.lambdaQuery();
            query.eq(ProjReviewCashFlowQuotationProposal::getProjectId, projReviewId);
            query.orderByAsc(ProjReviewCashFlowQuotationProposal::getCashFlowPhase);
            return this.list(query);
        } else {
            List<ProjReviewCashFlowQuotationProposalLib> projReviewCashFlowPlanLibList = projReviewCashFlowQuotationProposalLibService.listByProjReviewIdAndVersion(projReviewId, version);
            return projReviewCashFlowPlanLibList.stream().map(projReviewCashFlowQuotationProposalLibHandler::actualLib2Entity).collect(Collectors.toList());
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void importExcel(InputStream inputStream, Long projReviewId) {
        // 找到指定的租金偿还方案对应的数据
        List<CashFlowExcelModel> dataList = cashFlowExcelImporter.parse(inputStream);
        if (CollectionUtils.isEmpty(dataList)) {
            throw new MithrasException("没有在导入的文件中找到指定格式的租金偿还方案表，请检查后重新上传");
        }
        // 过滤无期项的和第0期
        List<CashFlowExcelModel> filterList = dataList.stream().filter(item -> Objects.nonNull(item.getCashFlowPhase()) && item.getCashFlowPhase() > 0).collect(Collectors.toList());
        // 校验
        this.check(projReviewId, filterList);
        Map<Integer, ProjReviewCashFlowQuotationProposal> importMap = filterList.stream()
                .filter(item -> Objects.nonNull(item.getCashFlowPhase()) && item.getCashFlowPhase() > 0)
                .map(item -> {
                    ProjReviewCashFlowQuotationProposal projReviewCashFlowPlan = this.toProjReviewCashFlowQuotationProposal(item);
                    projReviewCashFlowPlan.setProjectId(projReviewId);
                    return projReviewCashFlowPlan;
                }).collect(Collectors.toMap(ProjReviewCashFlowQuotationProposal::getCashFlowPhase, item -> item));
        // 覆盖现金流计划表
        SpringUtil.getBean(ProjReviewCashFlowQuotationProposalService.class).doOverwriteCashFlowPlan(importMap, projReviewId);
        recordStatus(projReviewId);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void doOverwriteCashFlowPlan(Map<Integer, ProjReviewCashFlowQuotationProposal> importMap, Long projReviewId) {
        List<ProjReviewCashFlowQuotationProposal> existDataList = this.listByProjReviewId(projReviewId, null);
        Map<Integer, ProjReviewCashFlowQuotationProposal> dbMap = existDataList.stream().collect(Collectors.toMap(ProjReviewCashFlowQuotationProposal::getCashFlowPhase, item -> item));
        // dbMap和importMap同时存在的数据，用importMap的数据更新
        // dbMap有但是importMap没有的，删除dbMap的数据
        // dbMap没有但是importMap有的，则新增
        List<ProjReviewCashFlowQuotationProposal> toSaveOrUpdateList = new LinkedList<>();
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
            ProjReviewCashFlowQuotationProposal dbData = dbMap.get(i);
            ProjReviewCashFlowQuotationProposal importData = importMap.get(i);
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
    }
    private ProjReviewCashFlowExporterBO build(ProjReviewCashFlowPlanExportREQ req) {
        List<ProjReviewCashFlowQuotationProposal> projReviewCashFlowPlanList;
        ProjReviewBaseInfo projReviewBaseInfo;
        if (StrUtil.isBlank(req.getVersion())) {
            projReviewBaseInfo = projReviewBaseInfoService.getById(req.getProjReviewId());
            Assert.notNull(projReviewBaseInfo, () -> MithrasException.newException("项目评审信息不存在"));
        } else {
            ProjReviewBaseInfoLib projReviewBaseInfoLib = projReviewBaseInfoLibService.getByOriginIdAndVersion(req.getProjReviewId(), req.getVersion());
            Assert.notNull(projReviewBaseInfoLib, () -> MithrasException.newException("对应版本的项目评审数据不存在"));
            projReviewBaseInfo = projReviewBaseInfoLibHandler.actualLib2Entity(projReviewBaseInfoLib);
        }
        projReviewCashFlowPlanList = this.listByProjReviewId(req.getProjReviewId(), req.getVersion());
        List<ProjReviewCashFlowQuotationProposal> list = new ArrayList<>();
        ProjReviewLeasePrice projReviewLeasePrice = projReviewLeasePriceService.lambdaQuery().eq(ProjReviewLeasePrice::getProjectId, req.getProjReviewId()).one();
        if (!ObjectUtils.isEmpty(projReviewCashFlowPlanList) && !ObjectUtils.isEmpty(projReviewLeasePrice) && !ObjectUtils.isEmpty(projReviewLeasePrice.getFirstInstallmentInterest())
                && projReviewLeasePrice.getFirstInstallmentInterest()>0) {
            //  增加零期现金流
            ProjReviewCashFlowQuotationProposal projReviewCashFlowPlan = new ProjReviewCashFlowQuotationProposal();
            projReviewCashFlowPlan.setProjectId(req.getProjReviewId());
            projReviewCashFlowPlan.setCashFlowDate(projReviewLeasePrice.getPlannedStartingDate());
            projReviewCashFlowPlan.setCashFlowPhase(0);
            projReviewCashFlowPlan.setRent(projReviewLeasePrice.getFirstInstallmentInterest());
            projReviewCashFlowPlan.setPrincipal(0L);
            projReviewCashFlowPlan.setInterest(projReviewLeasePrice.getFirstInstallmentInterest());
            projReviewCashFlowPlan.setRemainingPrincipal(0L);
            list.add(projReviewCashFlowPlan);
            list.addAll(projReviewCashFlowPlanList);
            projReviewCashFlowPlanList = list;
        }
        ProjReviewCashFlowExporterBO projReviewCashFlowExporterBO = new ProjReviewCashFlowExporterBO();
        projReviewCashFlowExporterBO.setProjReviewBaseInfo(projReviewBaseInfo);
        projReviewCashFlowExporterBO.setProjReviewCashFlowPlanList(BeanUtil.copyToList(projReviewCashFlowPlanList, ProjReviewCashFlowPlan.class));
        projReviewCashFlowExporterBO.setVersion(req.getVersion());
        return projReviewCashFlowExporterBO;
    }

    private void check(Long projReviewId, List<CashFlowExcelModel> cashFlowExcelModelList) {
        // 查询基本信息
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(projReviewId);
        // 查询报价方案
        Long applyCreditAmountL = null;
        Long downPaymentL = null;
        ProjReviewPriceDetailRSP rsp = projReviewPriceService.detail(projReviewId);
        Assert.notNull(rsp, () -> MithrasException.newException("请先维护报价方案"));
        if (Objects.nonNull(rsp.getLeasePriceDetailRSP())) {
            applyCreditAmountL = rsp.getLeasePriceDetailRSP().getApplyCreditAmount();
            downPaymentL = rsp.getLeasePriceDetailRSP().getDownPayment();
        } else if (Objects.nonNull(rsp.getFactoringPriceDetailRSP())) {
            applyCreditAmountL = rsp.getFactoringPriceDetailRSP().getApplyCreditAmount();
            downPaymentL = 0L;
        } else if (Objects.nonNull(rsp.getAocPriceDetailRSP())) {
            applyCreditAmountL = rsp.getAocPriceDetailRSP().getApplyCreditAmount();
            downPaymentL = 0L;
        }
        Assert.notNull(applyCreditAmountL, () -> MithrasException.newException("请先维护申请授信金额"));
        Assert.notNull(downPaymentL, () -> MithrasException.newException("请先维护首期租金"));
        BigDecimal applyCreditAmount = NumberUtil.div(applyCreditAmountL.toString(), GlobalConstants.MONEY_MULTIPLE, 2);
        BigDecimal downPayment = NumberUtil.div(downPaymentL.toString(), GlobalConstants.MONEY_MULTIPLE, 2);
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
            cashFlowExcelModel.projReviewCashFlowCheck();
            // 校验期项
            Assert.isTrue(!phaseSet.contains(cashFlowExcelModel.getCashFlowPhase()), () -> MithrasException.newException("期项不能重复"));
            phaseSet.add(cashFlowExcelModel.getCashFlowPhase());
            // 校验剩余本金
            principalSum = principalSum.add(cashFlowExcelModel.getPrincipal());
            if (cashFlowExcelModel.getCashFlowPhase() == 1 && StrUtil.isNotBlank(projReviewBaseInfo.getLeaseTypes()) && projReviewBaseInfo.getLeaseTypes().contains(LeaseType.zhi_zu.name())) {
                // 如果有直租类型则不校验第1期剩余本金
                continue;
            }
            BigDecimal r = applyCreditAmount.subtract(downPayment).subtract(principalSum);
            Assert.isTrue(r.equals(cashFlowExcelModel.getRemainingPrincipal()), () -> MithrasException.newException("期项为" + cashFlowExcelModel.getCashFlowPhase() + "的一行剩余本金计算结果不正确"));
        }
        // 最后一期剩余本金需要等于0
        CashFlowExcelModel last = cashFlowExcelModelList.get(cashFlowExcelModelList.size() - 1);
        Assert.isTrue(last.getRemainingPrincipal().compareTo(BigDecimal.valueOf(0L)) == 0, () -> MithrasException.newException("最后一期剩余本金应等于0"));
    }

    private ProjReviewCashFlowQuotationProposal toProjReviewCashFlowQuotationProposal(CashFlowExcelModel excelModel) {
        ProjReviewCashFlowQuotationProposal projReviewCashFlowPlan = new ProjReviewCashFlowQuotationProposal();
        projReviewCashFlowPlan.setCashFlowDate(excelModel.getCashFlowDate());
        projReviewCashFlowPlan.setCashFlowPhase(excelModel.getCashFlowPhase());
        if (Objects.nonNull(excelModel.getCashFlowAmount())) {
            projReviewCashFlowPlan.setCashFlowAmount(excelModel.getCashFlowAmount().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue());
        }
        if (Objects.nonNull(excelModel.getRent())) {
            projReviewCashFlowPlan.setRent(excelModel.getRent().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue());
        }
        if (Objects.nonNull(excelModel.getPrincipal())) {
            projReviewCashFlowPlan.setPrincipal(excelModel.getPrincipal().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue());
        }
        if (Objects.nonNull(excelModel.getInterest())) {
            projReviewCashFlowPlan.setInterest(excelModel.getInterest().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue());
        }
        if (Objects.nonNull(excelModel.getRemainingPrincipal())) {
            projReviewCashFlowPlan.setRemainingPrincipal(excelModel.getRemainingPrincipal().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue());
        }
        return projReviewCashFlowPlan;
    }

    private ProjReviewLeasePrice basicPreCheck(Long projReviewId) {
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(projReviewId);
        Assert.notNull(projReviewBaseInfo, () -> MithrasException.newException("项目评审信息不存在"));
        boolean hitBizType = Objects.equals(projReviewBaseInfo.getBizType(), ProjectBizType.ZL.name()) || Objects.equals(projReviewBaseInfo.getBizType(), ProjectBizType.ZZ.name());
        Assert.isTrue(hitBizType, () -> MithrasException.newException("该功能暂时只支持租赁和转租赁类型"));
        ProjReviewLeasePrice leasePrice = projReviewLeasePriceService.getByProjectId(projReviewId);
        Assert.notNull(leasePrice, () -> MithrasException.newException("报价方案不存在"));
        Assert.notNull(leasePrice.getPlannedStartingDate(), () -> MithrasException.newException("计划起租日不能为空"));
        Assert.notNull(leasePrice.getApplyCreditAmount(), () -> MithrasException.newException("申报授信金额不能为空"));
        Assert.notNull(leasePrice.getEarnestMoney(), () -> MithrasException.newException("保证金不能为空"));
        Assert.notNull(leasePrice.getDownPayment(), () -> MithrasException.newException("首期租金不能为空"));
        Assert.notNull(leasePrice.getConsultingFee(), () -> MithrasException.newException("服务费/咨询费不能为空"));
        Assert.notNull(leasePrice.getNominalPrice(), () -> MithrasException.newException("名义价款不能为空"));
        Assert.notNull(leasePrice.getRepayTimesTotal(), () -> MithrasException.newException("还款期数不能为空"));
        Assert.notBlank(leasePrice.getRepayRate(), () -> MithrasException.newException("还款频率不能为空"));
        Assert.notNull(leasePrice.getLeaseRatePercent(), () -> MithrasException.newException("租赁利率不能为空"));
        err(!StrUtil.equalsAny(leasePrice.getRentalCalcType(), RepayCalcType.DEBX.name(), RepayCalcType.DEBJ.name()),
                "该功能暂时只支持租金计算方式为[等额本息、等额本金]的项目");
//        Assert.isTrue(StrUtil.equalsAny(leasePrice.getRentalCalcType(), EQUIVALENT_RENTAL.name()), () -> MithrasException.newException("该功能暂时只支持租金计算方式为等额租金的项目"));
//        Assert.isTrue(Objects.equals(leasePrice.getPayType(), PayType.AFTERWARD.name()), () -> MithrasException.newException("该功能暂时只支持支付方式为后付的项目"));
        Assert.isTrue(RepayRateEnum.isByRule(leasePrice.getRepayRate()), () -> MithrasException.newException("该功能暂时只支持还款频率为规则还款的项目"));
        return leasePrice;
    }
}
