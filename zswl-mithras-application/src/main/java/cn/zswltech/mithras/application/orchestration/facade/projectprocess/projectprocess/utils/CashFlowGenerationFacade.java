package cn.zswltech.mithras.application.orchestration.facade.projectprocess.projectprocess.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.utils.*;
import cn.zswltech.mithras.projectprocess.application.cashflow.CashFlowGenerationApplicationService;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.RepayRateEnum;
import cn.zswltech.mithras.projectprocess.enums.projestablish.PayType;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.projectprocess.excel.exporter.AbstractCashFlowExcelExporter;
import cn.zswltech.mithras.projectprocess.excel.exporter.IRRCalculateExcelExporter;
import cn.zswltech.mithras.projectprocess.excel.importer.CashFlowExcelImporter;
import cn.zswltech.mithras.projectprocess.excel.model.CashFlowExcelModel;
import cn.zswltech.mithras.projectprocess.excel.model.CashFlowRichExcelModel;
import cn.zswltech.mithras.projectprocess.excel.model.IRRCalculateExcelModel;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.projectprocess.application.bo.CashFlowBO;
import cn.zswltech.mithras.projectprocess.application.bo.CashFlowCalculateBO;
import cn.zswltech.mithras.projectprocess.application.bo.CashFlowIRRBO;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.core.bean.BeanUtil.copyToList;
import static cn.hutool.core.io.IoUtil.toStream;
import static cn.hutool.core.util.NumberUtil.div;
import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.foundation.constant.GlobalConstants.MONEY_MULTIPLE;
import static cn.zswltech.mithras.contract.enums.contract.RepayRateEnum.of;
import static cn.zswltech.mithras.foundation.exception.MithrasException.err;
import static cn.zswltech.mithras.application.orchestration.util.FinancialUtil.*;

/**
 * @author yibin
 */
@Slf4j
@Service
public class CashFlowGenerationFacade implements CashFlowGenerationApplicationService {
    @Autowired
    private HttpServletResponse response;

    @Override
    public R<List<CashFlowGenerationExecRSP>> generate(CashFlowGenerationExecREQ req) {
        if(ObjectUtil.isEmpty(req.getPayType())){
            req.setPayType(PayType.AFTERWARD.name());
        }
//        err(!StrUtil.equalsAny(req.getRentalCalcType(), RepayCalcType.DEBX.name(), RepayCalcType.DEBJ.name()),
//                "该功能暂时只支持租金计算方式为[等额本息、等额本金]的项目");
        CashFlowCalculateBO cashFlowCalculateBO = copyProperties(req, CashFlowCalculateBO.class);
        cashFlowCalculateBO.setTotalMonth(req.getLeaseMonthCount());
        List<CashFlowBO> cashFlowList = calcCashFlow(cashFlowCalculateBO);
        return R.ok(copyToList(cashFlowList, CashFlowGenerationExecRSP.class));
    }

    @SneakyThrows
    @Override
    public R<CashFlowGenerationIrrRSP> irr(CashFlowGenerationIrrREQ req) {
        CashFlowGenerationIrrRSP rsp = new CashFlowGenerationIrrRSP();
        RepayRateEnum repayRateEnum = of(req.getRepayRate());
        List<CashFlowGenerationIrrREQ.CashFlowGenerationIrrItem> itemList = req.getItemList();
        for (CashFlowGenerationIrrREQ.CashFlowGenerationIrrItem item : itemList) {
            if (ObjectUtil.isNull(item.getRent())) {
                item.setRent(0L);
            }
            if (ObjectUtil.isNull(item.getPrincipal())) {
                item.setPrincipal(0L);
            }
            if (ObjectUtil.isNull(item.getInterest())) {
                item.setInterest(0L);
            }
        }
        CashFlowIRRBO bo = calculateIRR(req.getMonthCount(), repayRateEnum, copyToList(itemList, CashFlowBO.class));
        // 生成计算详情文件
        List<IRRCalculateExcelModel.CashFlowAdjustExcelModel> excelDataList = bo.getCashFlowAdjustList().stream().map(item -> {
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
        excelModel.setIrrPerPhase(bo.getIrrPerPhase());
        excelModel.setIrr(bo.getIrr());
        excelModel.setCashFlowAdjustExcelModelList(excelDataList);
        Long fileId;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            getBean(IRRCalculateExcelExporter.class).export(os, excelModel);
            try (ByteArrayInputStream is = toStream(os)) {
                fileId = getBean(MaterialsListService.class)
                        .add(is, UUID.randomUUID() + GlobalConstants.OFFICE_EXCEL_SUFFIX, System.currentTimeMillis(), "IRR_DETAIL", "TMP");
                rsp.setFileId(fileId);
            }
        } catch (IOException e) {
            log.error("", e);
            err(e.getMessage());
        }
        // 拼装返回结果
        rsp.setIrr(excelModel.getIrr().setScale(4, RoundingMode.HALF_UP).toPlainString());
        return R.ok(rsp);
    }

    @SneakyThrows
    @Override
    public R<List<CashFlowGenerationExecRSP>> importCashFlow(MultipartFile file) {
        List<CashFlowExcelModel> list = getBean(CashFlowExcelImporter.class).parse(file.getInputStream());
        BigDecimal mul = new BigDecimal(GlobalConstants.MONEY_MULTIPLE);
        list.forEach(base -> {
            base.setCashFlowAmount(base.getCashFlowAmount().multiply(mul).setScale(2, RoundingMode.HALF_UP));
            base.setRent(mul(base.getRent()));
            base.setPrincipal(mul(base.getPrincipal()));
            base.setInterest(mul(base.getInterest()));
            base.setRemainingPrincipal(mul(base.getRemainingPrincipal()));
        });
        return R.ok(copyToList(list, CashFlowGenerationExecRSP.class));
    }
    private BigDecimal mul(BigDecimal num) {
        BigDecimal mul = new BigDecimal(GlobalConstants.MONEY_MULTIPLE);
        if(ObjectUtil.isEmpty(num)){
            return BigDecimal.ZERO;
        }
        return num.multiply(mul).setScale(2, RoundingMode.HALF_UP);
    }

    @SneakyThrows
    @Override
    public R<Void> exportCashFlow(CashFlowGenerationExportREQ req) {
        try {
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("现金流量表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            getBean(CashFlowUtilsZLExcelExporter.class).export(response.getOutputStream(), req);

            // 执行导出
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出现金流表发生未知异常", e);
            throw new MithrasException("导出现金流表发生未知异常");
        }
        return R.ok();
    }

    @Component
    public static class CashFlowUtilsZLExcelExporter extends AbstractCashFlowExcelExporter<CashFlowGenerationExportREQ> {
        @Override
        protected CashFlowRichExcelModel prepare(CashFlowGenerationExportREQ req) {
            if(ObjectUtil.isEmpty(req.getGenerationParams().getPayType())){
                req.getGenerationParams().setPayType(PayType.AFTERWARD.name());
            }
            CashFlowRichExcelModel model = new CashFlowRichExcelModel();
            model.setConsultingFee(div(req.getGenerationParams().getConsultingFee().toString(), MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
            model.setEarnest(div(req.getGenerationParams().getEarnestMoney().toString(), MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
            LinkedList<CashFlowExcelModel> flowList = new LinkedList<>();
            for (CashFlowGenerationIrrREQ.CashFlowGenerationIrrItem item : req.getItems()) {
                CashFlowExcelModel cashFlowExcelModel = copyProperties(item, CashFlowExcelModel.class);
                if (null != cashFlowExcelModel.getCashFlowAmount()) {
                    cashFlowExcelModel.setCashFlowAmount(div(cashFlowExcelModel.getCashFlowAmount().toString(), MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
                }
                if (null != cashFlowExcelModel.getInterest()) {
                    cashFlowExcelModel.setInterest(div(cashFlowExcelModel.getInterest().toString(), MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
                }
                if (null != cashFlowExcelModel.getPrincipal()) {
                    cashFlowExcelModel.setPrincipal(div(cashFlowExcelModel.getPrincipal().toString(), MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
                }
                if (null != cashFlowExcelModel.getRemainingPrincipal()) {
                    cashFlowExcelModel.setRemainingPrincipal(div(cashFlowExcelModel.getRemainingPrincipal().toString(), MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
                }
                if (null != cashFlowExcelModel.getRent()) {
                    cashFlowExcelModel.setRent(div(cashFlowExcelModel.getRent().toString(), MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
                }
                flowList.add(cashFlowExcelModel);
            }
            model.setCashFlowList(flowList);
            model.setProjectAmount(div(req.getGenerationParams().getCreditAmount().toString(), MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP));
            model.setInterestRate(div(req.getGenerationParams().getInterestRate().toString(), "1000000").setScale(4, RoundingMode.HALF_UP));
            model.setMonthCount(req.getGenerationParams().getLeaseMonthCount());
            model.setDownPayment(div(LongUtil.null2zero(req.getGenerationParams().getDownPayment()).toString(), MONEY_MULTIPLE).setScale(2,
                    RoundingMode.HALF_UP));
            if (YesOrNoNumberEnum.YES.getCode().equals(req.getDataSource())) {
                model.setMoneyCellName("应收保理款（元）");
            } else {
                model.setMoneyCellName("租金（元）");
            }
            model.setRepayTimesTotal(req.getGenerationParams().getRepayTimes());
            model.setStartRentDate(req.getGenerationParams().getStartDate());
            model.setNominalPrice(div(LongUtil.null2zero(req.getGenerationParams().getNominalPrice()).toString(), MONEY_MULTIPLE).setScale(2,
                    RoundingMode.HALF_UP));
            model.setPayWay(ObjectUtil.isNull(req.getGenerationParams().getPayType()) ? PayType.AFTERWARD.display :
                    PayType.of(req.getGenerationParams().getPayType()).display);
            model.setRepayTimesInYear(getRepayTimesInYear(req.getGenerationParams().getRepayRate()));
//            model.setRentCalculateWay(RentalCalcType.of(req.getGenerationParams().getRentalCalcType()).display);
            model.setRentCalculateWay(Optional.ofNullable(RepayCalcType.find(req.getGenerationParams().getRentalCalcType())).map(RepayCalcType::display).orElse(""));
            //
            RepayRateEnum repayRate = RepayRateEnum.valueOf(req.getGenerationParams().getRepayRate());
//            BigDecimal periodIrr = calculateIRR(repayRate, copyToList(req.getItems(), CashFlowBO.class)).getIrrPerPhase();
            BigDecimal yearIrr = calculateIRR(req.getGenerationParams().getLeaseMonthCount(), repayRate, copyToList(req.getItems(), CashFlowBO.class)).getIrr();
            model.setIrr(yearIrr);
            return model;
        }
    }


}
