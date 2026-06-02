package cn.zswltech.mithras.service.service.projpricing;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.projpricing.cashflowplan.ProjPricingCashFlowPlanExportREQ;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingPriceDetailRSP;
import cn.zswltech.mithras.dto.projreview.cashflowplan.IRRCalculateResultRSP;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.convert.projpricing.ProjPricingCashFlowPlanConverter;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.RepayRateEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.service.excel.ExcelExporterFactory;
import cn.zswltech.mithras.service.excel.exporter.IRRCalculateExcelExporter;
import cn.zswltech.mithras.service.excel.importer.CashFlowExcelImporter;
import cn.zswltech.mithras.service.excel.model.CashFlowExcelModel;
import cn.zswltech.mithras.service.excel.model.IRRCalculateExcelModel;
import cn.zswltech.mithras.service.mapper.model.projpricing.*;
import cn.zswltech.mithras.service.mapper.projpricing.ProjPricingCashFlowPlanMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.bo.CashFlowBO;
import cn.zswltech.mithras.service.service.bo.CashFlowIRRBO;
import cn.zswltech.mithras.service.service.bo.ProjPricingCashFlowExporterBO;
import cn.zswltech.mithras.service.service.lib.projpricing.ProjPricingBaseInfoLibService;
import cn.zswltech.mithras.service.service.lib.projpricing.ProjPricingCashFlowPlanLibService;
import cn.zswltech.mithras.service.service.lib.projpricing.handler.impl.ProjPricingBaseInfoLibHandler;
import cn.zswltech.mithras.service.service.lib.projpricing.handler.impl.ProjPricingCashFlowPlanLibHandler;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.util.FinancialUtil;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.others.MithrasException.err;

/**
 * <p>
 * 项目定价-现金流计划表 服务实现类
 * </p>
 *
 * @author chenyifei
 * @since 2024-08-16
 */
@Service
public class ProjPricingCashFlowPlanService extends ServiceImpl<ProjPricingCashFlowPlanMapper, ProjPricingCashFlowPlan> implements ProjPricingUpdateAdvice{

    @Resource
    private CashFlowExcelImporter cashFlowExcelImporter;
    @Resource
    private ProjPricingBaseInfoService projPricingBaseInfoService;
    @Resource
    private ProjPricingPriceService projPricingPriceService;
    @Resource
    private ProjPricingBaseInfoLibService projPricingBaseInfoLibService;
    @Resource
    private ProjPricingBaseInfoLibHandler projPricingBaseInfoLibHandler;
    @Resource
    private ProjPricingLeasePriceService projPricingLeasePriceService;
    @Resource
    private IRRCalculateExcelExporter irrCalculateExcelExporter;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private ProjPricingCashFlowPlanLibService projPricingCashFlowPlanLibService;
    @Resource
    private ProjPricingCashFlowPlanLibHandler projPricingCashFlowPlanLibHandler;



    @Transactional(rollbackFor = Throwable.class)
    public void importExcel(InputStream inputStream, Long projPricingId) {
        // 找到指定的租金偿还方案对应的数据
        List<CashFlowExcelModel> dataList = cashFlowExcelImporter.parse(inputStream);
        if (CollectionUtils.isEmpty(dataList)) {
            throw new MithrasException("没有在导入的文件中找到指定格式的租金偿还方案表，请检查后重新上传");
        }
        // 过滤无期项的和第0期
        List<CashFlowExcelModel> filterList = dataList.stream().filter(item -> Objects.nonNull(item.getCashFlowPhase()) && item.getCashFlowPhase() > 0).collect(Collectors.toList());
        // 校验
        this.check(projPricingId, filterList);
        Map<Integer, ProjPricingCashFlowPlan> importMap = filterList.stream()
                .filter(item -> Objects.nonNull(item.getCashFlowPhase()) && item.getCashFlowPhase() > 0)
                .map(item -> {
                    ProjPricingCashFlowPlan projPricingCashFlowPlan = this.toProjPricingCashFlowPlan(item);
                    projPricingCashFlowPlan.setProjectId(projPricingId);
                    return projPricingCashFlowPlan;
                }).collect(Collectors.toMap(ProjPricingCashFlowPlan::getCashFlowPhase, item -> item));
        // 覆盖现金流计划表
        SpringUtil.getBean(ProjPricingCashFlowPlanService.class).doOverwriteCashFlowPlan(importMap, projPricingId);
        recordStatus(projPricingId);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void doOverwriteCashFlowPlan(Map<Integer, ProjPricingCashFlowPlan> importMap, Long projPricingId) {
        List<ProjPricingCashFlowPlan> existDataList = this.listByProjPricingId(projPricingId, null);
        Map<Integer, ProjPricingCashFlowPlan> dbMap = existDataList.stream().collect(Collectors.toMap(ProjPricingCashFlowPlan::getCashFlowPhase, item -> item));
        // dbMap和importMap同时存在的数据，用importMap的数据更新
        // dbMap有但是importMap没有的，删除dbMap的数据
        // dbMap没有但是importMap有的，则新增
        List<ProjPricingCashFlowPlan> toSaveOrUpdateList = new LinkedList<>();
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
            ProjPricingCashFlowPlan dbData = dbMap.get(i);
            ProjPricingCashFlowPlan importData = importMap.get(i);
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


    public List<ProjPricingCashFlowPlan> listByProjPricingId(Long projPricingId, String version) {
        if (StrUtil.isBlank(version)) {
            LambdaQueryWrapper<ProjPricingCashFlowPlan> query = Wrappers.lambdaQuery();
            query.eq(ProjPricingCashFlowPlan::getProjectId, projPricingId);
            query.orderByAsc(ProjPricingCashFlowPlan::getCashFlowPhase);
            return this.list(query);
        } else {
            List<ProjPricingCashFlowPlanLib> projPricingCashFlowPlanLibList = projPricingCashFlowPlanLibService.listByProjPricingIdAndVersion(projPricingId, version);
            return projPricingCashFlowPlanLibList.stream().map(projPricingCashFlowPlanLibHandler::actualLib2Entity).collect(Collectors.toList());
        }
    }


    private void check(Long projPricingId, List<CashFlowExcelModel> cashFlowExcelModelList) {
        // 查询基本信息
        ProjPricingBaseInfo projPricingBaseInfo = projPricingBaseInfoService.getById(projPricingId);
        // 查询报价方案
        Long applyCreditAmountL = null;
        Long downPaymentL = null;
        ProjPricingPriceDetailRSP rsp = projPricingPriceService.detail(projPricingId);
        Assert.notNull(rsp, () -> MithrasException.newException("请先维护报价方案"));
        if (Objects.nonNull(rsp.getLeasePriceDetailRSP())) {
            applyCreditAmountL = LongUtil.null2zero(rsp.getLeasePriceDetailRSP().getApprovedAmount());
            downPaymentL = rsp.getLeasePriceDetailRSP().getDownPayment();
        } else if (Objects.nonNull(rsp.getFactoringPriceDetailRSP())) {
            applyCreditAmountL = LongUtil.null2zero(rsp.getFactoringPriceDetailRSP().getApprovedAmount());
            downPaymentL = 0L;
        } else if (Objects.nonNull(rsp.getAocPriceDetailRSP())) {
            applyCreditAmountL = LongUtil.null2zero(rsp.getAocPriceDetailRSP().getApprovedAmount());
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
            if (cashFlowExcelModel.getCashFlowPhase() == 1 && StrUtil.isNotBlank(projPricingBaseInfo.getLeaseTypes()) && projPricingBaseInfo.getLeaseTypes().contains(LeaseType.zhi_zu.name())) {
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


    private ProjPricingCashFlowPlan toProjPricingCashFlowPlan(CashFlowExcelModel excelModel) {
        ProjPricingCashFlowPlan projPricingCashFlowPlan = new ProjPricingCashFlowPlan();
        projPricingCashFlowPlan.setCashFlowDate(excelModel.getCashFlowDate());
        projPricingCashFlowPlan.setCashFlowPhase(excelModel.getCashFlowPhase());
        if (Objects.nonNull(excelModel.getCashFlowAmount())) {
            projPricingCashFlowPlan.setCashFlowAmount(excelModel.getCashFlowAmount().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue());
        }
        if (Objects.nonNull(excelModel.getRent())) {
            projPricingCashFlowPlan.setRent(excelModel.getRent().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue());
        }
        if (Objects.nonNull(excelModel.getPrincipal())) {
            projPricingCashFlowPlan.setPrincipal(excelModel.getPrincipal().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue());
        }
        if (Objects.nonNull(excelModel.getInterest())) {
            projPricingCashFlowPlan.setInterest(excelModel.getInterest().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue());
        }
        if (Objects.nonNull(excelModel.getRemainingPrincipal())) {
            projPricingCashFlowPlan.setRemainingPrincipal(excelModel.getRemainingPrincipal().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue());
        }
        return projPricingCashFlowPlan;
    }

    public void exportRent(ProjPricingCashFlowPlanExportREQ req, ServletOutputStream outputStream) throws Exception {
        ProjPricingCashFlowExporterBO bo = this.build(req);
        // 模型转换
        List<CashFlowExcelModel> cashFlowExcelModelList = bo.getProjPricingCashFlowPlanList().stream().map(ProjPricingCashFlowPlanConverter::toCashFlowExcelModel).collect(Collectors.toList());
        // 执行导出
        ProjectBizType projectBizType = ProjectBizType.of(bo.getProjPricingBaseInfo().getBizType());
        ExcelExporterFactory.getProjPricingRentExcelExporter(projectBizType).exportExcel(cashFlowExcelModelList, outputStream);
    }


    public void exportCashFlow(ProjPricingCashFlowPlanExportREQ req, OutputStream outputStream) {
        ProjPricingCashFlowExporterBO bo = this.build(req);
        ProjectBizType projectBizType = ProjectBizType.of(bo.getProjPricingBaseInfo().getBizType());
        // 执行导出
        ExcelExporterFactory.getProjPricingCashFlowExcelExporter(projectBizType).export(outputStream, bo);
    }


    private ProjPricingCashFlowExporterBO build(ProjPricingCashFlowPlanExportREQ req) {
        List<ProjPricingCashFlowPlan> projPricingCashFlowPlanList;
        ProjPricingBaseInfo projPricingBaseInfo;
        if (StrUtil.isBlank(req.getVersion())) {
            projPricingBaseInfo = projPricingBaseInfoService.getById(req.getProjPricingId());
            Assert.notNull(projPricingBaseInfo, () -> MithrasException.newException("项目定价信息不存在"));
        } else {
            ProjPricingBaseInfoLib projPricingBaseInfoLib = projPricingBaseInfoLibService.getByOriginIdAndVersion(req.getProjPricingId(), req.getVersion());
            Assert.notNull(projPricingBaseInfoLib, () -> MithrasException.newException("对应版本的项目定价数据不存在"));
            projPricingBaseInfo = projPricingBaseInfoLibHandler.actualLib2Entity(projPricingBaseInfoLib);
        }
        projPricingCashFlowPlanList = this.listByProjPricingId(req.getProjPricingId(), req.getVersion());
        List<ProjPricingCashFlowPlan> list = new ArrayList<>();
        ProjPricingLeasePrice projPricingLeasePrice = projPricingLeasePriceService.lambdaQuery().eq(ProjPricingLeasePrice::getProjectId, req.getProjPricingId()).one();
        if (!ObjectUtils.isEmpty(projPricingCashFlowPlanList) && !ObjectUtils.isEmpty(projPricingLeasePrice) && !ObjectUtils.isEmpty(projPricingLeasePrice.getFirstInstallmentInterest())
                && projPricingLeasePrice.getFirstInstallmentInterest()>0) {
            //  增加零期现金流
            ProjPricingCashFlowPlan projPricingCashFlowPlan = new ProjPricingCashFlowPlan();
            projPricingCashFlowPlan.setProjectId(req.getProjPricingId());
            projPricingCashFlowPlan.setCashFlowDate(projPricingLeasePrice.getPlannedStartingDate());
            projPricingCashFlowPlan.setCashFlowPhase(0);
            projPricingCashFlowPlan.setRent(projPricingLeasePrice.getFirstInstallmentInterest());
            projPricingCashFlowPlan.setPrincipal(0L);
            projPricingCashFlowPlan.setInterest(projPricingLeasePrice.getFirstInstallmentInterest());
            projPricingCashFlowPlan.setRemainingPrincipal(0L);
            list.add(projPricingCashFlowPlan);
            list.addAll(projPricingCashFlowPlanList);
            projPricingCashFlowPlanList = list;
        }
        ProjPricingCashFlowExporterBO projPricingCashFlowExporterBO = new ProjPricingCashFlowExporterBO();
        projPricingCashFlowExporterBO.setProjPricingBaseInfo(projPricingBaseInfo);
        projPricingCashFlowExporterBO.setProjPricingCashFlowPlanList(projPricingCashFlowPlanList);
        projPricingCashFlowExporterBO.setVersion(req.getVersion());
        return projPricingCashFlowExporterBO;
    }


    private ProjPricingLeasePrice basicPreCheck(Long projPricingId) {
        ProjPricingBaseInfo projPricingBaseInfo = projPricingBaseInfoService.getById(projPricingId);
        Assert.notNull(projPricingBaseInfo, () -> MithrasException.newException("项目评审信息不存在"));
        boolean hitBizType = Objects.equals(projPricingBaseInfo.getBizType(), ProjectBizType.ZL.name()) || Objects.equals(projPricingBaseInfo.getBizType(), ProjectBizType.ZZ.name());
        Assert.isTrue(hitBizType, () -> MithrasException.newException("该功能暂时只支持租赁和转租赁类型"));
        ProjPricingLeasePrice leasePrice = projPricingLeasePriceService.getByProjectId(projPricingId);
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

    @Transactional(rollbackFor = Throwable.class)
    public void generate(Long projPricingId) {
        ProjPricingLeasePrice leasePrice = this.basicPreCheck(projPricingId);
        LocalDate plannedStartingDate = leasePrice.getPlannedStartingDate();
        // 生成现金流
        List<CashFlowBO> cashFlowBOList = FinancialUtil.calcCashFlow(ProjPricingCashFlowPlanConverter.toCashFlowCalculateBO(leasePrice));
        Assert.notEmpty(cashFlowBOList, () -> MithrasException.newException("没有成功生成现金流"));
        List<ProjPricingCashFlowPlan> cashFlowPlanList = cashFlowBOList.stream()
                .filter(item -> item.getCashFlowPhase() > 0)
                .map(item -> ProjPricingCashFlowPlanConverter.toProjPricingCashFlowPlan(projPricingId, item))
                .collect(Collectors.toList());
        Map<Integer, ProjPricingCashFlowPlan> map = cashFlowPlanList.stream().collect(Collectors.toMap(ProjPricingCashFlowPlan::getCashFlowPhase, item -> item));
        // 覆盖现金流计划表
        SpringUtil.getBean(ProjPricingCashFlowPlanService.class).doOverwriteCashFlowPlan(map, projPricingId);
    }

    public IRRCalculateResultRSP calculateIRR(Long projPricingId) {
//        ProjReviewLeasePrice leasePrice = this.basicPreCheck(projReviewId);
        List<ProjPricingCashFlowPlan> cashFlowPlanList = this.listByProjPricingId(projPricingId, null);
        Assert.notEmpty(cashFlowPlanList, () -> MithrasException.newException("现金流为空，无法计算"));
        ProjPricingPriceDetailRSP projPricingPriceDetailRSP = projPricingPriceService.detail(projPricingId);
        if (Objects.isNull(projPricingPriceDetailRSP.getPlanStartDate())) {
            throw new MithrasException("计划起租日为空，无法计算");
        }
        if (Objects.isNull(projPricingPriceDetailRSP.getApplyCreditAmount())) {
            throw new MithrasException("授信金额为空，无法计算");
        }
        if (Objects.isNull(projPricingPriceDetailRSP.getEarnestMoney())) {
            throw new MithrasException("保证金为空，无法计算");
        }
        if (Objects.isNull(projPricingPriceDetailRSP.getConsultingFee())) {
            throw new MithrasException("咨询费/服务费/手续费为空，无法计算");
        }
        if (Objects.isNull(projPricingPriceDetailRSP.getDownPayment())) {
            throw new MithrasException("首期租金为空，无法计算");
        }
        if (StrUtil.isBlank(projPricingPriceDetailRSP.getRepayRate())) {
            throw new MithrasException("还款频率为空，无法计算");
        }
        List<CashFlowBO> cashFlowBOList = new ArrayList<>(cashFlowPlanList.size() + 1);
        // 补全第0期
        CashFlowBO zeroCashFlowBO = new CashFlowBO();
        zeroCashFlowBO.setCashFlowDate(projPricingPriceDetailRSP.getPlanStartDate());
        zeroCashFlowBO.setCashFlowPhase(0);
        zeroCashFlowBO.setCashFlowAmount(projPricingPriceDetailRSP.getApplyCreditAmount() * -1 + projPricingPriceDetailRSP.getEarnestMoney() + projPricingPriceDetailRSP.getConsultingFee() + projPricingPriceDetailRSP.getDownPayment() + projPricingPriceDetailRSP.getCommission() + projPricingPriceDetailRSP.getFirstInstallmentInterest());
        cashFlowBOList.add(zeroCashFlowBO);
        // 填充完整现金流
        for (ProjPricingCashFlowPlan projPricingCashFlowPlan : cashFlowPlanList) {
            cashFlowBOList.add(ProjPricingCashFlowPlanConverter.toCashFlowBO(projPricingCashFlowPlan));
        }
        RepayRateEnum repayRateEnum = RepayRateEnum.of(projPricingPriceDetailRSP.getRepayRate());
        if (Objects.isNull(repayRateEnum)) {
            throw new MithrasException("没有找到对应的还款频率类型");
        }
        // 计算IRR
        CashFlowIRRBO cashFlowIRRBO = FinancialUtil.calculateIRR(projPricingPriceDetailRSP.getMonthCount(), repayRateEnum, cashFlowBOList);
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
            fileId = materialsListService.add(bais, UUID.randomUUID().toString() + GlobalConstants.OFFICE_EXCEL_SUFFIX, projPricingId, "IRR_DETAIL", "TMP");
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
}
