package cn.zswltech.mithras.finance.service.profitcalculate;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.ProfitCalculateResultListREQ;
import cn.zswltech.mithras.dto.ProfitCalculateResultListRSP;
import cn.zswltech.mithras.dto.kpi.parameterconfig.TaxRateConfig;
import cn.zswltech.mithras.finance.application.profitcalculate.api.ProfitCalculateResultApplicationService;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.kpi.convert.KpiParameterConfigConvert;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.collection.enums.BillTypeEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.kpi.enums.KpiParameterConfigCodeEnum;
import cn.zswltech.mithras.kpi.enums.config.TaxRateEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.finance.excel.model.ProfitCalculateExcelModel;
import cn.zswltech.mithras.finance.mapper.ProfitCalculateResultMapper;
import cn.zswltech.mithras.finance.mapper.model.ProfitCalculateResult;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.model.AssetClassifyClientAuxiliaryLib;
import cn.zswltech.mithras.collection.mapper.model.BillManagement;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceiptLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActualLib;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceProjectProfitDetail;
import cn.zswltech.mithras.ftp.oldftp.model.FtpInterestBaseInfo;
import cn.zswltech.mithras.ftp.oldftp.model.FtpInterestDetailRecord;
import cn.zswltech.mithras.kpi.mapper.model.KpiParameterConfig;
import cn.zswltech.mithras.margin.mapper.model.MarginBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.finance.mapper.query.ProfitCalculateResultQuery;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.finance.bo.ProfitCalculateResultBO;
import cn.zswltech.mithras.kpi.service.KpiParameterConfigService;
import cn.zswltech.mithras.assetclassify.application.lib.AssetClassifyClientAuxiliaryLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractBaseInfoLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractReceiptLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractRentActualLibService;
import cn.zswltech.mithras.margin.service.MarginBaseInfoService;
import cn.zswltech.mithras.basedata.util.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/6/25
 * @description
 */
@Slf4j
@Service
public class ProfitCalculateResultService extends ServiceImpl<ProfitCalculateResultMapper, ProfitCalculateResult> implements ProfitCalculateResultApplicationService {
    @Resource
    private ContractBaseInfoLibService contractBaseInfoLibService;
    @Resource
    private ContractReceiptLibService contractReceiptLibService;
    @Resource
    private ContractRentActualLibService contractRentActualLibService;
    @Resource
    private MarginBaseInfoService marginBaseInfoService;
    @Resource
    private KpiParameterConfigService kpiParameterConfigService;
    @Resource
    private AssetClassifyClientAuxiliaryLibService assetClassifyClientAuxiliaryLibService;
    @Resource
    private cn.zswltech.mithras.finance.excel.exporter.ProfitCalculateExcelExporter profitCalculateExcelExporter;
    @Resource
    private HttpServletResponse httpServletResponse;
    @Resource
    private ProfitCalculateSupportPort profitCalculateSupportPort;

    public ProfitCalculateResult calculate(Long contractId) {
        ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibService.getLatest(contractId);
        if (Objects.isNull(contractBaseInfoLib)) {
            log.info("没有找到最新的合同生效版本数据[contractId:{}]", contractId);
            return null;
        }
        List<ContractReceiptLib> contractReceiptLibList = contractReceiptLibService.listByContractIdVersion(contractId, contractBaseInfoLib.getVersion());
        if (CollectionUtil.isEmpty(contractReceiptLibList)) {
            log.info("没有找到最新的借据生效版本数据[contractId:{}]", contractId);
            return null;
        }
        List<ContractRentActualLib> contractRentActualLibList = contractRentActualLibService.listLibByContractVersion(contractId, contractBaseInfoLib.getVersion());
        if (CollectionUtil.isEmpty(contractRentActualLibList)) {
            log.info("没有找到最新的实际租金表生效版本数据[contractId:{}]", contractId);
            return null;
        }
        Map<Long, List<ContractRentActualLib>> contractRentActualLibMap = contractRentActualLibList.stream().collect(Collectors.groupingBy(ContractRentActual::getReceiptId));
        if (Objects.equals(contractBaseInfoLib.getContractStatus(), ContractStatus.SETTLE.name())) {
            // 判断结清日期是否在当年，不是的话就不需要进行利润测算
            contractRentActualLibList.sort(Comparator.comparing(ContractRentActual::getCashFlowPhase));
            ContractRentActualLib last = contractRentActualLibList.get(contractReceiptLibList.size() - 1);
            if (last.getCashFlowDate().getYear() != LocalDate.now().getYear()) {
                log.info("合同已结清且结清日期不是当年，无需进行利润测算");
                return null;
            }
        } else {
            boolean hasEffectRent = false;
            for (ContractRentActualLib contractRentActualLib : contractRentActualLibList) {
                if (StrUtil.isNotBlank(contractRentActualLib.getCashFlowCode())) {
                    hasEffectRent = true;
                    break;
                }
            }
            if (!hasEffectRent) {
                log.info("合同不存在有效租金表，无需进行利润测算");
                return null;
            }
        }
        // 获取对应的增值税税率
        BigDecimal taxRate;
        try {
            taxRate = this.ensureTaxRate(contractBaseInfoLib);
        } catch (Exception e) {
            log.error("获取增值税税率发生异常[contractId:{}]", contractBaseInfoLib.getOriginId(), e);
            throw new MithrasException("获取增值税税率发生异常");
        }
        if (Objects.isNull(taxRate)) {
            throw new MithrasException("增值税配置不存在");
        }
        // 查询当天是否已存在测算结果
        LambdaQueryWrapper<ProfitCalculateResult> query = Wrappers.lambdaQuery();
        query.eq(ProfitCalculateResult::getContractId, contractId);
        query.eq(ProfitCalculateResult::getCalculateDate, LocalDate.now());
        ProfitCalculateResult exist = this.getOne(query);
        // 准备计算
        ProfitCalculateResult toSaveModel = new ProfitCalculateResult();
        if (Objects.nonNull(exist)) {
            toSaveModel.setId(exist.getId());
        }
        toSaveModel.setContractId(contractId);
        toSaveModel.setCalculateDate(LocalDate.now());
        // 找到投放时间
        PaymentActualDetail paymentActualDetail = profitCalculateSupportPort.getEarliestPayment(contractId);
        if (Objects.nonNull(paymentActualDetail)) {
            toSaveModel.setContractStartDate(paymentActualDetail.getPaidInDate());
        }
        // 当年已确认收入（税后）
        toSaveModel.setConfirmIncomeThisYear(Optional.ofNullable(this.ensureConfirmIncomeThisYear(contractBaseInfoLib)).orElse(0L));
        // 当年测算利息收入（税后）
        toSaveModel.setCalculateInterestThisYear(Optional.ofNullable(this.ensureCalculateInterestThisYear(contractBaseInfoLib, taxRate, contractRentActualLibList)).orElse(0L));
        // 营业收入 = 当年已确认收入（税后） + 当年测算利息收入（税后）
        long operatingIncome = toSaveModel.getConfirmIncomeThisYear() + toSaveModel.getCalculateInterestThisYear();
        toSaveModel.setOperatingIncome(operatingIncome);
        // FTP成本
        toSaveModel.setFtpInterest(Optional.ofNullable(this.ensureFtpInterest(contractReceiptLibList, contractRentActualLibMap)).orElse(0L));
        // 上期末风险金余额
        toSaveModel.setRiskBalanceEndOfLastYear(Optional.ofNullable(this.ensureRiskBalanceEndOfLastYear(contractBaseInfoLib)).orElse(0L));
        // 本期末风险金余额
        toSaveModel.setRiskBalanceEndOfThisYear(Optional.ofNullable(this.ensureRiskBalanceEndOfThisYear(contractBaseInfoLib, contractReceiptLibList, contractRentActualLibMap)).orElse(0L));
        // 本期风险金计提/转回 = 本期末风险金余额 - 上期末风险金余额
        long riskProvisionThisYear = toSaveModel.getRiskBalanceEndOfThisYear() - toSaveModel.getRiskBalanceEndOfLastYear();
        toSaveModel.setRiskProvisionThisYear(riskProvisionThisYear);
        // 附加税 = (营业收入 - FTP成本) * 增值税率 * 12%
        BigDecimal additionalTax = BigDecimal.valueOf(toSaveModel.getOperatingIncome() - toSaveModel.getFtpInterest()).multiply(taxRate).multiply(BigDecimal.valueOf(0.12)).setScale(2, BigDecimal.ROUND_UP);
        toSaveModel.setAdditionalTax(mithrasLongDecimalTwo(additionalTax.longValue()));
        // 利润总额 = 营业收入 - FTP成本 - 本期风险金计提/转回 - 附加税
        long profit = toSaveModel.getOperatingIncome() - toSaveModel.getFtpInterest() - toSaveModel.getRiskProvisionThisYear() - toSaveModel.getAdditionalTax();
        toSaveModel.setProfit(profit);
        // 利润总额（扣除费用） = 利润总额 * 0.8
        toSaveModel.setProfitExcludeFee(mithrasLongDecimalTwo(BigDecimal.valueOf(toSaveModel.getProfit()).multiply(BigDecimal.valueOf(0.8)).setScale(2, RoundingMode.HALF_UP).longValue()));
        // 年末剩余本金
        toSaveModel.setRemainingPrincipleEndOfThisYear(Optional.ofNullable(this.ensureRemainingPrincipleEndOfThisYear(contractReceiptLibList, contractRentActualLibMap)).orElse(0L));
        // 年末保证金余额
        toSaveModel.setRemainingEarnestEndOfThisYear(Optional.ofNullable(this.ensureRemainingEarnestEndOfThisYear(contractReceiptLibList)).orElse(0L));
        // 年末敞口 = 年末剩余本金 - 年末保证金余额
        toSaveModel.setExposureEndOfThisYear(toSaveModel.getRemainingPrincipleEndOfThisYear() - toSaveModel.getRemainingEarnestEndOfThisYear());
        // 保存
        this.saveOrUpdate(toSaveModel);
        return toSaveModel;
    }

    @Override
    public R<ProfitCalculateResultListRSP> pageList(@Valid ProfitCalculateResultListREQ req) {
        return R.ok(queryPageList(req));
    }

    public ProfitCalculateResultListRSP queryPageList(ProfitCalculateResultListREQ req) {
        LocalDate calculateDate = LocalDate.now();
        ProfitCalculateResultListRSP rsp = new ProfitCalculateResultListRSP();
        rsp.setCalculateDate(LocalDateTimeUtil.format(calculateDate, DatePattern.NORM_DATE_PATTERN));
        ProfitCalculateResultQuery query = new ProfitCalculateResultQuery();
        query.setStart((req.getPage() - 1) * req.getPageSize());
        query.setSize(req.getPageSize());
        query.setCalculateDate(calculateDate);
        query.setContractCode(req.getContractCode());
        int count = this.getBaseMapper().myPageListCount(query);
        if (count == 0) {
            rsp.setPageResult(PageR.empty(req.getPage(), req.getPageSize()));
            return rsp;
        }
        List<ProfitCalculateResultBO> dbList = this.getBaseMapper().myPageList(query);
        List<ProfitCalculateResultListRSP.Data> rspList = dbList.stream().map(item -> {
            ProfitCalculateResultListRSP.Data rspData = BeanUtil.copyProperties(item, ProfitCalculateResultListRSP.Data.class);
            // 根据客户风控行业分类确定项目类型（逻辑同绩效考核）
            if (StrUtil.isNotBlank(item.getRiskControlIndustryClassify())) {
                rspData.setProjectClassify(profitCalculateSupportPort.ensureProjClassify(item.getRiskControlIndustryClassify()).display());
            }
            // 处理业务类型
            ProjectBizType projectBizType = ProjectBizType.of(item.getBizType());
            if (Objects.nonNull(projectBizType)) {
                switch (projectBizType) {
                    case ZL:
                    case ZZ: {
                        LeaseType leaseType = LeaseType.of(item.getLeaseType());
                        rspData.setBizType(projectBizType.display + Optional.ofNullable(leaseType).map(i -> "-" + i.display).orElse(""));
                        break;
                    }
                    case BL:
                    case ZR: {
                        rspData.setBizType(projectBizType.display);
                        break;
                    }
                }
            }
            if (Objects.nonNull(item.getContractStartDate())) {
                rspData.setContractStartDate(LocalDateTimeUtil.format(item.getContractStartDate(), DatePattern.NORM_DATE_PATTERN));
            }
            // 本期风险金计提/转回 = 本期末风险金余额 - 上期末风险金余额
            if (Objects.nonNull(item.getRiskBalanceEndOfLastYear()) && Objects.nonNull(item.getRiskBalanceEndOfThisYear())) {
                rspData.setRiskUsedThisYear(item.getRiskBalanceEndOfThisYear() - item.getRiskBalanceEndOfLastYear());
            }
            // 年末敞口 = 年末剩余本金 - 年末保证金余额
            if (Objects.nonNull(item.getRemainingPrincipleEndOfThisYear()) && Objects.nonNull(item.getRemainingEarnestEndOfThisYear())) {
                rspData.setRiskExposureEndOfThisYear(item.getRemainingPrincipleEndOfThisYear() - item.getRemainingEarnestEndOfThisYear());
            }
            return rspData;
        }).collect(Collectors.toList());
        rsp.setPageResult(PageR.of(rspList, count, req.getPage(), req.getPageSize()));
        return rsp;
    }

    @Override
    public void exportExcel(@Valid ProfitCalculateResultListREQ req) {
        req.setPage(1);
        req.setPageSize(5000);
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("会计利润测算表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            profitCalculateExcelExporter.exportExcel(listExcelModel(req), httpServletResponse.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出会计利润测算表发生未知异常[req:{}]", JSONUtil.toJsonStr(req), e);
            throw new MithrasException("导出会计利润测算表发生未知异常");
        }
    }

    public List<ProfitCalculateExcelModel> listExcelModel(ProfitCalculateResultListREQ req) {
        ProfitCalculateResultListRSP rsp = this.queryPageList(req);
        if (rsp.getPageResult().getTotal() == 0) {
            throw new MithrasException("暂无可导出的数据");
        }
        return rsp.getPageResult().getList().stream().map(item -> {
            ProfitCalculateExcelModel excelModel = BeanUtil.copyProperties(item, ProfitCalculateExcelModel.class);
            // 转类型
            if (StrUtil.isNotBlank(item.getContractStartDate())) {
                excelModel.setContractStartDate(LocalDateTimeUtil.parseDate(item.getContractStartDate(), DatePattern.NORM_DATE_PATTERN));
            }
            if (Objects.nonNull(item.getConfirmIncomeThisYear())) {
                excelModel.setConfirmIncomeThisYear(BigDecimal.valueOf(item.getConfirmIncomeThisYear()).divide(new BigDecimal(GlobalConstants.MONEY_MULTIPLE), 2, RoundingMode.HALF_UP));
            }
            if (Objects.nonNull(item.getCalculateInterestThisYear())) {
                excelModel.setCalculateInterestThisYear(BigDecimal.valueOf(item.getCalculateInterestThisYear()).divide(new BigDecimal(GlobalConstants.MONEY_MULTIPLE), 2, RoundingMode.HALF_UP));
            }
            if (Objects.nonNull(item.getOperatingIncome())) {
                excelModel.setOperatingIncome(BigDecimal.valueOf(item.getOperatingIncome()).divide(new BigDecimal(GlobalConstants.MONEY_MULTIPLE), 2, RoundingMode.HALF_UP));
            }
            if (Objects.nonNull(item.getFtpInterest())) {
                excelModel.setFtpInterest(BigDecimal.valueOf(item.getFtpInterest()).divide(new BigDecimal(GlobalConstants.MONEY_MULTIPLE), 2, RoundingMode.HALF_UP));
            }
            if (Objects.nonNull(item.getRiskBalanceEndOfLastYear())) {
                excelModel.setRiskBalanceEndOfLastYear(BigDecimal.valueOf(item.getRiskBalanceEndOfLastYear()).divide(new BigDecimal(GlobalConstants.MONEY_MULTIPLE), 2, RoundingMode.HALF_UP));
            }
            if (Objects.nonNull(item.getRiskBalanceEndOfThisYear())) {
                excelModel.setRiskBalanceEndOfThisYear(BigDecimal.valueOf(item.getRiskBalanceEndOfThisYear()).divide(new BigDecimal(GlobalConstants.MONEY_MULTIPLE), 2, RoundingMode.HALF_UP));
            }
            if (Objects.nonNull(item.getRiskUsedThisYear())) {
                excelModel.setRiskUsedThisYear(BigDecimal.valueOf(item.getRiskUsedThisYear()).divide(new BigDecimal(GlobalConstants.MONEY_MULTIPLE), 2, RoundingMode.HALF_UP));
            }
            if (Objects.nonNull(item.getAdditionalTax())) {
                excelModel.setAdditionalTax(BigDecimal.valueOf(item.getAdditionalTax()).divide(new BigDecimal(GlobalConstants.MONEY_MULTIPLE), 2, RoundingMode.HALF_UP));
            }
            if (Objects.nonNull(item.getProfit())) {
                excelModel.setProfit(BigDecimal.valueOf(item.getProfit()).divide(new BigDecimal(GlobalConstants.MONEY_MULTIPLE), 2, RoundingMode.HALF_UP));
            }
            if (Objects.nonNull(item.getProfitExcludeFee())) {
                excelModel.setProfitExcludeFee(BigDecimal.valueOf(item.getProfitExcludeFee()).divide(new BigDecimal(GlobalConstants.MONEY_MULTIPLE), 2, RoundingMode.HALF_UP));
            }
            if (Objects.nonNull(item.getRemainingPrincipleEndOfThisYear())) {
                excelModel.setRemainingPrincipleEndOfThisYear(BigDecimal.valueOf(item.getRemainingPrincipleEndOfThisYear()).divide(new BigDecimal(GlobalConstants.MONEY_MULTIPLE), 2, RoundingMode.HALF_UP));
            }
            if (Objects.nonNull(item.getRemainingEarnestEndOfThisYear())) {
                excelModel.setRemainingEarnestEndOfThisYear(BigDecimal.valueOf(item.getRemainingEarnestEndOfThisYear()).divide(new BigDecimal(GlobalConstants.MONEY_MULTIPLE), 2, RoundingMode.HALF_UP));
            }
            if (Objects.nonNull(item.getRiskExposureEndOfThisYear())) {
                excelModel.setRiskExposureEndOfThisYear(BigDecimal.valueOf(item.getRiskExposureEndOfThisYear()).divide(new BigDecimal(GlobalConstants.MONEY_MULTIPLE), 2, RoundingMode.HALF_UP));
            }
            return excelModel;
        }).collect(Collectors.toList());
    }

    private Long ensureConfirmIncomeThisYear(ContractBaseInfoLib contractBaseInfoLib) {
        FinanceProjectProfitDetail financeProjectProfitDetail = profitCalculateSupportPort.getLatestProfitDetailByContractId(contractBaseInfoLib.getOriginId());
        if (Objects.isNull(financeProjectProfitDetail)) {
            log.info("当年已确认收入 - 没有找到最近的项目利润信息，无法获取");
            return 0L;
        }
        return Optional.ofNullable(financeProjectProfitDetail.getTotalIncomeThisYear()).orElse(0L);
    }

    private Long ensureCalculateInterestThisYear(ContractBaseInfoLib contractBaseInfoLib, BigDecimal taxRate, List<ContractRentActualLib> contractRentActualLibList) {
        FinanceProjectProfitDetail financeProjectProfitDetail = profitCalculateSupportPort.getLatestProfitDetailByContractId(contractBaseInfoLib.getOriginId());
        if (Objects.isNull(financeProjectProfitDetail)) {
            log.info("当年测算利息收入 - 没有找到最近的项目利润信息，无法计算");
            return 0L;
        }
        // 确定计算利息的日期范围
        LocalDate startDate = DateUtil.endOfMonth(LocalDate.of(financeProjectProfitDetail.getYear(), financeProjectProfitDetail.getMonth(), 1));
        LocalDate endDate = LocalDate.of(financeProjectProfitDetail.getYear(), 12, 31);
        // 按照借据分组
        BigDecimal interest = BigDecimal.ZERO;
        Map<Long, List<ContractRentActualLib>> rentMap = contractRentActualLibList.stream().filter(item -> StrUtil.isNotBlank(item.getCashFlowCode())).collect(Collectors.groupingBy(ContractRentActual::getReceiptId));
        for (Map.Entry<Long, List<ContractRentActualLib>> entry : rentMap.entrySet()) {
            List<ContractRentActualLib> rentList = entry.getValue();
            // 根据期项排序
            rentList.sort(Comparator.comparing(ContractRentActual::getCashFlowPhase));
            // 计算利息
            LocalDate preCashFlowDate = contractBaseInfoLib.getActualLeaseDate();
            for (ContractRentActualLib contractRentActualLib : rentList) {
                LocalDate currentDate = contractRentActualLib.getCashFlowDate();
                long currentInterest = Optional.ofNullable(contractRentActualLib.getInterest()).orElse(0L);
                // 在开始日期之前的跳过不用管
                if (startDate.isAfter(currentDate) || startDate.isEqual(currentDate)) {
                    preCashFlowDate = currentDate;
                    continue;
                }
                if (Objects.isNull(preCashFlowDate)) {
                    log.warn("上一期日期不存在，跳过不计算");
                    continue;
                }
                // 在结束日期之前和之后的分开处理
                if (endDate.isAfter(currentDate)) {
                    if (startDate.isAfter(preCashFlowDate)) {
                        // 如果上一期在开始日期之前则需要进行折算
                        long i1 = LocalDateTimeUtil.between(startDate.atStartOfDay(), currentDate.atStartOfDay(), ChronoUnit.DAYS);
                        long i2 = LocalDateTimeUtil.between(preCashFlowDate.atStartOfDay(), currentDate.atStartOfDay(), ChronoUnit.DAYS);
                        BigDecimal b = BigDecimal.valueOf(currentInterest).multiply(BigDecimal.valueOf(i1).divide(BigDecimal.valueOf(i2), 10, RoundingMode.HALF_UP));
                        interest = interest.add(b);
                    } else {
                        interest = interest.add(BigDecimal.valueOf(currentInterest));
                    }
                } else {
                    long i1 = LocalDateTimeUtil.between(preCashFlowDate.atStartOfDay(), endDate.atStartOfDay(), ChronoUnit.DAYS);
                    long i2 = LocalDateTimeUtil.between(preCashFlowDate.atStartOfDay(), currentDate.atStartOfDay(), ChronoUnit.DAYS);
                    BigDecimal b = BigDecimal.valueOf(currentInterest).multiply(BigDecimal.valueOf(i1).divide(BigDecimal.valueOf(i2), 10, RoundingMode.HALF_UP));
                    interest = interest.add(b);
                    break;
                }
                preCashFlowDate = currentDate;
            }
        }
        return mithrasLongDecimalTwo(interest.divide(BigDecimal.ONE.add(taxRate), 2, RoundingMode.HALF_UP).longValue());
    }

    private Long ensureFtpInterest(List<ContractReceiptLib> contractReceiptLibList, Map<Long, List<ContractRentActualLib>> contractRentActualLibMap) {
        long result = 0L;
        for (ContractReceiptLib contractReceiptLib : contractReceiptLibList) {
            List<ContractRentActualLib> contractRentActualLibList = Optional.ofNullable(contractRentActualLibMap.get(contractReceiptLib.getOriginId())).orElse(new LinkedList<>());
            result = result + this.calculateFtpInterestSingleReceipt(contractReceiptLib, contractRentActualLibList);
        }
        return result;
    }

    private Long calculateFtpInterestSingleReceipt(ContractReceiptLib contractReceiptLib, List<ContractRentActualLib> contractRentActualLibList) {
        // 获取当前已有FTP计息数据
        FtpInterestBaseInfo ftpInterestBaseInfo = profitCalculateSupportPort.getFtpInterestByReceiptId(contractReceiptLib.getOriginId());
        if (Objects.isNull(ftpInterestBaseInfo)) {
            return 0L;
        }
        long currentReceiptResult = ftpInterestBaseInfo.getTotalInterestAmount();
        if (Objects.equals(ftpInterestBaseInfo.getFinish(), YesOrNoNumberEnum.YES.getCode())) {
            return 0L;
        }
        LocalDate now = LocalDate.now();
        FtpInterestDetailRecord latestDetailRecord = profitCalculateSupportPort.getLatestFtpInterestRecord(ftpInterestBaseInfo.getId(), now);
        if (Objects.isNull(latestDetailRecord)) {
            return 0L;
        }
        // 从计息记录的后一天开始预测
        LocalDate startDate = latestDetailRecord.getInterestDate().plusDays(1);
        LocalDate endDate = LocalDate.of(now.getYear(), 12, 31);
        contractRentActualLibList.sort(Comparator.comparing(ContractRentActual::getCashFlowPhase));
        ContractRentActualLib last = contractRentActualLibList.get(contractRentActualLibList.size() - 1);
        // 按日期存租金金额
        Map<String, Long> rentMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(contractRentActualLibList)) {
            for (ContractRentActualLib contractRentActualLib : contractRentActualLibList) {
                if (startDate.isAfter(contractRentActualLib.getCashFlowDate())) {
                    continue;
                }
                if (endDate.isBefore(contractRentActualLib.getCashFlowDate())) {
                    break;
                }
                if (Objects.isNull(contractRentActualLib.getRent())) {
                    continue;
                }
                String mapKey = LocalDateTimeUtil.format(contractRentActualLib.getCashFlowDate(), DatePattern.NORM_DATE_PATTERN);
                rentMap.putIfAbsent(mapKey, 0L);
                long sum = rentMap.get(mapKey) + contractRentActualLib.getRent();
                rentMap.put(mapKey, sum);
            }
            // help gc
            contractRentActualLibList.clear();
        }
        // 查询所有收到的票据
        List<BillManagement> billManagementList = profitCalculateSupportPort.listBillsByReceiptId(contractReceiptLib.getOriginId());
        // 按日期存到期票据金额
        Map<String, Long> paymentBillMap = new HashMap<>();
        Map<String, Long> collectBillMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(billManagementList)) {
            for (BillManagement billManagement : billManagementList) {
                if (Objects.isNull(billManagement.getBillExpireDate())) {
                    continue;
                }
                if (Objects.isNull(billManagement.getBillAmount())) {
                    continue;
                }
                String mapKey = LocalDateTimeUtil.format(billManagement.getBillExpireDate(), DatePattern.NORM_DATE_PATTERN);
                if (Objects.equals(billManagement.getBillType(), BillTypeEnum.PAYMENT.name())) {
                    paymentBillMap.putIfAbsent(mapKey, 0L);
                    long sum = paymentBillMap.get(mapKey) + billManagement.getBillAmount();
                    paymentBillMap.put(mapKey, sum);
                }
                if (Objects.equals(billManagement.getBillType(), BillTypeEnum.COLLECTION.name())) {
                    collectBillMap.putIfAbsent(mapKey, 0L);
                    long sum = collectBillMap.get(mapKey) + billManagement.getBillAmount();
                    collectBillMap.put(mapKey, sum);
                }
            }
            // help gc
            billManagementList.clear();
        }
        // FIXME 模拟计算FTP计息（可通过改造计算逻辑，不用每天计算，只需要在资金占用发生变化的时候重新计算利息，资金占用没变化则沿用即可）
        long cashOccupy = latestDetailRecord.getCashOccupy();
        LocalDate targetDate = startDate;
        while (!targetDate.isAfter(endDate)) {
            String key = LocalDateTimeUtil.format(targetDate, DatePattern.NORM_DATE_PATTERN);
            long rent = Optional.ofNullable(rentMap.get(key)).orElse(0L);
            long collectBill = Optional.ofNullable(collectBillMap.get(key)).orElse(0L);
            long paymentBill = Optional.ofNullable(paymentBillMap.get(key)).orElse(0L);
            long earnestRefund = 0L;
            if (Objects.nonNull(last.getCashFlowDate()) && targetDate.isEqual(last.getCashFlowDate())) {
                // 计算保证金退款
                earnestRefund = this.calculateRemainingEarnestEndOfThisYearSingleReceipt(contractReceiptLib);
            }
            // 预计FTP利息
            cashOccupy = cashOccupy + paymentBill + earnestRefund - rent - collectBill;
            long i = BigDecimal.valueOf(cashOccupy).multiply(BigDecimal.valueOf(latestDetailRecord.getCashFtp()).divide(BigDecimal.valueOf(1000000), 2, RoundingMode.HALF_UP)).longValue();
            if (i > 0) {
                currentReceiptResult = currentReceiptResult + mithrasLongDecimalTwo(i);
            }
            targetDate = targetDate.plusDays(1);
        }
        return currentReceiptResult;
    }

    private Long ensureRiskBalanceEndOfLastYear(ContractBaseInfoLib contractBaseInfoLib) {
        FinanceProjectProfitDetail projectProfitDetail = profitCalculateSupportPort.getLastYearProfitDetail(contractBaseInfoLib.getOriginId());
        if (Objects.isNull(projectProfitDetail)) {
            return 0L;
        }
        return projectProfitDetail.getTotalRiskThisYear();
    }

    private Long ensureRiskBalanceEndOfThisYear(ContractBaseInfoLib contractBaseInfoLib, List<ContractReceiptLib> contractReceiptLibList, Map<Long, List<ContractRentActualLib>> contractRentActualLibMap) {
        long result = 0L;
        for (ContractReceiptLib contractReceiptLib : contractReceiptLibList) {
            List<ContractRentActualLib> contractRentActualLibList = Optional.ofNullable(contractRentActualLibMap.get(contractReceiptLib.getOriginId())).orElse(new LinkedList<>());
            result = result + this.calculateRiskBalanceEndOfThisYearSingleReceipt(contractBaseInfoLib, contractReceiptLib, contractRentActualLibList);
        }
        return result;
    }

    private Long calculateRiskBalanceEndOfThisYearSingleReceipt(ContractBaseInfoLib contractBaseInfoLib, ContractReceiptLib contractReceiptLib, List<ContractRentActualLib> contractRentActualLibList) {
        // 获取最新的客户五级分类的拨备计提比例
        AssetClassifyClientAuxiliaryLib assetClassifyClientAuxiliaryLib = assetClassifyClientAuxiliaryLibService.getEffectLatestOneByClientId(contractBaseInfoLib.getClientId());
        if (Objects.isNull(assetClassifyClientAuxiliaryLib) || Objects.isNull(assetClassifyClientAuxiliaryLib.getAwardRatio())) {
            return 0L;
        }
        // 年末敞口
        Long remainingPrinciple = this.calculateRemainingPrincipleEndOfThisYearSingleReceipt(contractRentActualLibList);
        Long remainingEarnest = this.calculateRemainingEarnestEndOfThisYearSingleReceipt(contractReceiptLib);
        long exposure = Optional.ofNullable(remainingPrinciple).orElse(0L) - Optional.ofNullable(remainingEarnest).orElse(0L);
        // 风险金余额 = 年末敞口 * 计提比例
        BigDecimal b = BigDecimal.valueOf(exposure).multiply(BigDecimal.valueOf(assetClassifyClientAuxiliaryLib.getAwardRatio()).divide(BigDecimal.valueOf(1000000), 10, RoundingMode.HALF_UP));
        return mithrasLongDecimalTwo(b.longValue());
    }

    private Long ensureRemainingPrincipleEndOfThisYear(List<ContractReceiptLib> contractReceiptLibList, Map<Long, List<ContractRentActualLib>> contractRentActualLibMap) {
        long result = 0L;
        for (ContractReceiptLib contractReceiptLib : contractReceiptLibList) {
            List<ContractRentActualLib> contractRentActualLibList = Optional.ofNullable(contractRentActualLibMap.get(contractReceiptLib.getOriginId())).orElse(new LinkedList<>());
            result = result + this.calculateRemainingPrincipleEndOfThisYearSingleReceipt(contractRentActualLibList);
        }
        return result;
    }

    private Long calculateRemainingPrincipleEndOfThisYearSingleReceipt(List<ContractRentActualLib> contractRentActualLibList) {
        if (CollectionUtil.isEmpty(contractRentActualLibList)) {
            return 0L;
        }
        contractRentActualLibList.sort(Comparator.comparing(ContractRentActual::getCashFlowPhase));
        int currentYear = LocalDate.now().getYear();
        ContractRentActualLib last = contractRentActualLibList.get(contractRentActualLibList.size() - 1);
        if (last.getCashFlowDate().getYear() > currentYear) {
            // 说明年末未到期，需要取本年最后一期所对应的剩余本金
            // 从后往前找直到找到第一个归属当前年份的
            for (int i = contractRentActualLibList.size() - 1; i >= 0; i--) {
                ContractRentActualLib lib = contractRentActualLibList.get(i);
                if (lib.getCashFlowDate().getYear() == currentYear) {
                    return lib.getRemainingPrincipal();
                }
            }
        } else {
            // 说明年末已到期，返回0
            return 0L;
        }
        return 0L;
    }

    private Long ensureRemainingEarnestEndOfThisYear(List<ContractReceiptLib> contractReceiptLibList) {
        long result = 0L;
        for (ContractReceiptLib contractReceiptLib : contractReceiptLibList) {
            result = result + this.calculateRemainingEarnestEndOfThisYearSingleReceipt(contractReceiptLib);
        }
        return result;
    }

    private Long calculateRemainingEarnestEndOfThisYearSingleReceipt(ContractReceiptLib contractReceiptLib) {
        MarginBaseInfo marginBaseInfo = marginBaseInfoService.getMarginBaseInfoByContractId(contractReceiptLib.getContractId());
        if (Objects.nonNull(marginBaseInfo) && Objects.nonNull(marginBaseInfo.getCollectionAmount())) {
            // 按照付款比例进行拆分
            List<PaymentBaseInfo> allPayment = profitCalculateSupportPort.listEffectPaymentByContractId(contractReceiptLib.getContractId());
            double total = allPayment.stream().mapToDouble(item -> Optional.ofNullable(item.getEarnestMoney()).orElse(0L)).sum();
            double currentReceipt = allPayment.stream().filter(item -> Objects.equals(item.getReceiptId(), contractReceiptLib.getOriginId())).mapToDouble(item -> Optional.ofNullable(item.getEarnestMoney()).orElse(0L)).sum();
            return mithrasLongDecimalTwo((long) (marginBaseInfo.getCollectionAmount() * (currentReceipt / total)));
        }
        return 0L;
    }

    private BigDecimal ensureTaxRate(ContractBaseInfoLib contractBaseInfoLib) throws Exception {
        KpiParameterConfig kpiParameterConfig = kpiParameterConfigService.getOneByConfigCode(KpiParameterConfigCodeEnum.TAX_RATE);
        if (Objects.isNull(kpiParameterConfig)) {
            return null;
        }
        if (StrUtil.isBlank(kpiParameterConfig.getConfigValue())) {
            return null;
        }
        TaxRateConfig taxRateConfig = KpiParameterConfigConvert.toConfigBase(kpiParameterConfig, TaxRateConfig.class);
        taxRateConfig.setConfigValue(JSONUtil.toList(kpiParameterConfig.getConfigValue(), TaxRateConfig.Data.class));
        TaxRateEnum taxRateEnum;
        if (Objects.equals(contractBaseInfoLib.getBizType(), ProjectBizType.BL.name())) {
            taxRateEnum = TaxRateEnum.ZZS_BL;
        } else if (Objects.equals(contractBaseInfoLib.getBizType(), ProjectBizType.ZR.name())) {
            taxRateEnum = TaxRateEnum.ZZS_ZR;
        } else {
            if (Objects.equals(contractBaseInfoLib.getLeaseType(), LeaseType.jyx_zu.name())) {
                taxRateEnum = TaxRateEnum.ZZS_ZL_JYX;
            } else if (Objects.equals(contractBaseInfoLib.getLeaseType(), LeaseType.zhi_zu.name())) {
                taxRateEnum = TaxRateEnum.ZZS_ZL_ZZ;
            } else {
                taxRateEnum = TaxRateEnum.ZZS_ZL_HZ;
            }
        }
        for (TaxRateConfig.Data data : taxRateConfig.getConfigValue()) {
            if (Objects.equals(data.getTaxType(), taxRateEnum.getTaxType()) && Objects.equals(data.getBizType(), taxRateEnum.getBizType())) {
                return new BigDecimal(data.getTaxRate()).divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
            }
        }
        return null;
    }

    private static Long mithrasLongDecimalTwo(Long value) {
        if (Objects.isNull(value)) {
            return null;
        }
        return BigDecimal.valueOf(value)
                .divide(new BigDecimal(GlobalConstants.MONEY_MULTIPLE))
                .setScale(2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE))
                .longValue();
    }
}
