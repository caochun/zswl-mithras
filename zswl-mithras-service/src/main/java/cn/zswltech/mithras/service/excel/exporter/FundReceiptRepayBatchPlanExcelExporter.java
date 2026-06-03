package cn.zswltech.mithras.service.excel.exporter;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.FundReceiptRepayBatchPlanExcelModel;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptRepayPlan;
import cn.zswltech.mithras.service.others.Util;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

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
        fundReceiptRepayBatchPlanExcelModel.setFinancingAmount(Util.toYuan(entity.getFinancingAmount()));
        fundReceiptRepayBatchPlanExcelModel.setPaidPrincipal(Util.toYuan(entity.getPaidPrincipal()));
        fundReceiptRepayBatchPlanExcelModel.setPaidInterest(Util.toYuan(entity.getPaidInterest()));
        fundReceiptRepayBatchPlanExcelModel.setProjNameList(StringUtils.isBlank(entity.getSupportingProjectJson()) ? "" : JSONArray.parseArray(entity.getSupportingProjectJson(), String.class).stream().collect(Collectors.joining("\n")));
        fundReceiptRepayBatchPlanExcelModel.setPledgeContractCodeList(StringUtils.isBlank(entity.getPledgeContracCodeJson()) ? "" : JSONArray.parseArray(entity.getPledgeContracCodeJson(), String.class).stream().collect(Collectors.joining("\n")));
        fundReceiptRepayBatchPlanExcelModel.setBorrowingDate(Optional.ofNullable(entity.getBorrowingDate()).map(d -> LocalDateTimeUtil.format(d, "yyyy-MM-dd")).orElse(""));
        fundReceiptRepayBatchPlanExcelModel.setExpirationDate(Optional.ofNullable(entity.getExpirationDate()).map(d -> LocalDateTimeUtil.format(d, "yyyy-MM-dd")).orElse(""));
        fundReceiptRepayBatchPlanExcelModel.setPlanedRepayAmount(Util.toYuan(entity.getPlanedRepayAmount()));
        fundReceiptRepayBatchPlanExcelModel.setPlanedRepayPrincipal(Util.toYuan(entity.getPlanedRepayPrincipal()));
        fundReceiptRepayBatchPlanExcelModel.setPlanedRepayInterest(Util.toYuan(entity.getPlanedRepayInterest()));
        fundReceiptRepayBatchPlanExcelModel.setPlanedRepayPrincipleDate(Optional.ofNullable(entity.getPlanedRepayPrincipleDate()).map(d -> LocalDateTimeUtil.format(d, "yyyy-MM-dd")).orElse(""));
        fundReceiptRepayBatchPlanExcelModel.setPlanedRepayInterestDate(Optional.ofNullable(entity.getPlanedRepayInterestDate()).map(d -> LocalDateTimeUtil.format(d, "yyyy-MM-dd")).orElse(""));
        fundReceiptRepayBatchPlanExcelModel.setReceiptRepayId(entity.getReceiptRepayId());
        return fundReceiptRepayBatchPlanExcelModel;

    }

    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<FundReceiptRepayBatchPlanExcelModel> modelClz() {
        return FundReceiptRepayBatchPlanExcelModel.class;
    }
}
