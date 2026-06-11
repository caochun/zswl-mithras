package cn.zswltech.mithras.fund.excel.exporter;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.NumberUtil;
import cn.zswltech.mithras.fund.excel.model.FundReceiptRepayBatchPlanExcelModel;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptRepayPlan;
import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 收款核销列表
 *
 * @author wangchuanhao
 * @date 2022/8/23 3:31 PM
 */
@Component
public class FundReceiptRepayBatchPlanExcelExporter extends AbstractSimpleExcelExporter<FundReceiptRepayBatchPlanExcelModel> {
    public FundReceiptRepayBatchPlanExcelModel entity2Model(FundReceiptRepayPlan entity) {
        FundReceiptRepayBatchPlanExcelModel fundReceiptRepayBatchPlanExcelModel = new FundReceiptRepayBatchPlanExcelModel();
        fundReceiptRepayBatchPlanExcelModel.setReceiptRepayCode(entity.getReceiptRepayCode());
//        fundReceiptRepayBatchPlanExcelModel.setFinancingOrgName(entity.getFinancingOrgName());
        fundReceiptRepayBatchPlanExcelModel.setFinancingAmount(toYuan(entity.getFinancingAmount()));
        fundReceiptRepayBatchPlanExcelModel.setPaidPrincipal(toYuan(entity.getPaidPrincipal()));
        fundReceiptRepayBatchPlanExcelModel.setPaidInterest(toYuan(entity.getPaidInterest()));
        fundReceiptRepayBatchPlanExcelModel.setProjNameList(StringUtils.isBlank(entity.getSupportingProjectJson()) ? "" : JSONArray.parseArray(entity.getSupportingProjectJson(), String.class).stream().collect(Collectors.joining("\n")));
        fundReceiptRepayBatchPlanExcelModel.setPledgeContractCodeList(StringUtils.isBlank(entity.getPledgeContracCodeJson()) ? "" : JSONArray.parseArray(entity.getPledgeContracCodeJson(), String.class).stream().collect(Collectors.joining("\n")));
        fundReceiptRepayBatchPlanExcelModel.setBorrowingDate(Optional.ofNullable(entity.getBorrowingDate()).map(d -> LocalDateTimeUtil.format(d, "yyyy-MM-dd")).orElse(""));
        fundReceiptRepayBatchPlanExcelModel.setExpirationDate(Optional.ofNullable(entity.getExpirationDate()).map(d -> LocalDateTimeUtil.format(d, "yyyy-MM-dd")).orElse(""));
        fundReceiptRepayBatchPlanExcelModel.setPlanedRepayAmount(toYuan(entity.getPlanedRepayAmount()));
        fundReceiptRepayBatchPlanExcelModel.setPlanedRepayPrincipal(toYuan(entity.getPlanedRepayPrincipal()));
        fundReceiptRepayBatchPlanExcelModel.setPlanedRepayInterest(toYuan(entity.getPlanedRepayInterest()));
        fundReceiptRepayBatchPlanExcelModel.setPlanedRepayPrincipleDate(Optional.ofNullable(entity.getPlanedRepayPrincipleDate()).map(d -> LocalDateTimeUtil.format(d, "yyyy-MM-dd")).orElse(""));
        fundReceiptRepayBatchPlanExcelModel.setPlanedRepayInterestDate(Optional.ofNullable(entity.getPlanedRepayInterestDate()).map(d -> LocalDateTimeUtil.format(d, "yyyy-MM-dd")).orElse(""));
        fundReceiptRepayBatchPlanExcelModel.setReceiptRepayId(entity.getReceiptRepayId());
        return fundReceiptRepayBatchPlanExcelModel;

    }

    private String toYuan(Long dbNumber) {
        if (Objects.isNull(dbNumber)) {
            return null;
        }
        return NumberUtil.decimalFormat(",##0.####", BigDecimal.valueOf(dbNumber).divide(BigDecimal.valueOf(10000L)));
    }

    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<FundReceiptRepayBatchPlanExcelModel> modelClz() {
        return FundReceiptRepayBatchPlanExcelModel.class;
    }
}
