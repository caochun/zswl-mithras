package cn.zswltech.mithras.service.service.materialsfile.export;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.dto.finance.accountage.FinanceAccountAgeItemListREQ;
import cn.zswltech.mithras.dto.finance.accountage.FinanceAccountAgeItemListRSP;
import cn.zswltech.mithras.service.controller.accountage.FinanceAccountAgeItemController;
import cn.zswltech.mithras.service.enums.FileExportEnum;
import cn.zswltech.mithras.finance.enums.third.FinancialAccountAgeSendStatusStatus;
import cn.zswltech.mithras.finance.enums.third.FinancialAccountNumberENUM;
import cn.zswltech.mithras.finance.enums.third.FinancialPaymentContentENUM;
import cn.zswltech.mithras.finance.excel.exporter.FinanceAccountAgeItemExcelExporter;
import cn.zswltech.mithras.finance.excel.model.FinanceAccountAgeItemCheckModel;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName DashboardClientOverviewAllExportHandle
 * @Description 统一工作台-客户视图-客户一览-租后检查
 * @Author jackerhe
 * @Date 2024/7/8 3:14 下午
 * @Version 1.0
 **/
@Component
public class FinanceAccountAgeItemExportHandle extends ExportHandle<FinanceAccountAgeItemCheckModel, FinanceAccountAgeItemExcelExporter> {

    @Resource
    private FinanceAccountAgeItemController financeAccountAgeItemController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.FINANCE_ACCOUNT_AGE_ITEM.name();
    }

    @Override
    public FinanceAccountAgeItemExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(FinanceAccountAgeItemExcelExporter.class);
    }

    @Override
    public List<FinanceAccountAgeItemCheckModel> req2ExportList(FileExportREQ req) {
        FinanceAccountAgeItemListREQ allREQ = BeanUtil.copyProperties(req.getExt(), FinanceAccountAgeItemListREQ.class);
        allREQ.setPage(1);
        allREQ.setIds(req.getIds());
        allREQ.setPageSize(50000);
        R<PageR<FinanceAccountAgeItemListRSP>> sumRSPR = financeAccountAgeItemController.list(allREQ);
        PageR<FinanceAccountAgeItemListRSP> data = sumRSPR.getData();
        List<FinanceAccountAgeItemListRSP> lists = data.getList();
        if (ObjectUtil.isEmpty(lists)) {
            return ListUtil.empty();
        }
        List<FinanceAccountAgeItemCheckModel> list = new ArrayList<>();
        lists.forEach(rsp -> {
            FinanceAccountAgeItemCheckModel model = BeanUtil.copyProperties(rsp, FinanceAccountAgeItemCheckModel.class);
            model.setSendStatus(Optional.ofNullable(FinancialAccountAgeSendStatusStatus.of(rsp.getSendStatus())).map(FinancialAccountAgeSendStatusStatus::getDisplay).orElse(null));
            model.setAccountNumber(Optional.ofNullable(FinancialAccountNumberENUM.findByName(rsp.getAccountNumber())).map(FinancialAccountNumberENUM::display).orElse(null));
            model.setPaymentContent(Optional.ofNullable(FinancialPaymentContentENUM.findByName(rsp.getPaymentContent())).map(FinancialPaymentContentENUM::display).orElse(null));
            model.setBusinessDate(this.dateFormat(rsp.getBusinessDate()));
            model.setAgingDeadline(this.dateFormat(rsp.getAgingDeadline()));
            model.setPlanCollectionDate(this.dateFormat(rsp.getPlanCollectionDate()));
            model.setCurrency(ObjectUtil.equals(rsp.getCurrency(), "CNY") ? "人民币" : rsp.getCurrency());
            list.add(model);
        });
        return list;
    }

    private String dateFormat(LocalDate date) {
        if(ObjectUtil.isEmpty(date)) {
            return null;
        }
        return date.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN));
    }

}
