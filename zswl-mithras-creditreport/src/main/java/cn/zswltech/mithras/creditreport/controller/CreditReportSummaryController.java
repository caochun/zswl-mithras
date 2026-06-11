package cn.zswltech.mithras.creditreport.controller;
import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.creditreport.CreditReportSummaryApi;
import cn.zswltech.mithras.dto.creditreport.*;
import cn.zswltech.mithras.creditreport.model.CreditReportSummary;
import cn.zswltech.mithras.creditreport.service.CreditReportSummaryService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
* @description 征信报告-信息概要表
* @author vico
* @date 2025-11-14
*/
@RestController
public class CreditReportSummaryController implements CreditReportSummaryApi {

    @Resource
    private CreditReportSummaryService creditReportSummaryService;

    @Override
    public R<Void> add(CreditReportSummaryAddREQ req) {
        creditReportSummaryService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(CreditReportSummaryModifyREQ req){
        creditReportSummaryService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<CreditReportSummaryListRSP>> list(CreditReportSummaryListREQ req){
        Page<CreditReportSummary> data = creditReportSummaryService.list(req);
        List<CreditReportSummaryListRSP> list = BeanUtil.copyToList(data.getRecords(), CreditReportSummaryListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(CreditReportSummaryRemoveREQ req){
        creditReportSummaryService.remove(req);
        return R.ok();
    }

    @Override
    public R<CreditReportSummaryDetailRSP> detail(@Valid CreditReportBaseDetailREQ req) {
        return R.ok(creditReportSummaryService.detail(req));
    }

}