package cn.zswltech.mithras.service.controller.creditreport;
import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.creditreport.CreditReportRecordDetailsApi;
import cn.zswltech.mithras.dto.creditreport.*;
import cn.zswltech.mithras.service.mapper.model.creditreport.CreditReportRecordDetails;
import cn.zswltech.mithras.service.service.creditreport.CreditReportRecordDetailsService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
* @description 征信报告-信贷记录明细表
* @author vico
* @date 2025-11-28
*/
@RestController
public class CreditReportRecordDetailsController implements CreditReportRecordDetailsApi {

    @Resource
    private CreditReportRecordDetailsService creditReportRecordDetailsService;

    @Override
    public R<Void> add(CreditReportRecordDetailsAddREQ req) {
        creditReportRecordDetailsService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(CreditReportRecordDetailsModifyREQ req){
        creditReportRecordDetailsService.modify(req);
        return R.ok();
    }

    @Override
    public R<List<CreditReportRecordDetailsListRSP>> list(CreditReportRecordDetailsListREQ req){
        List<CreditReportRecordDetails> data = creditReportRecordDetailsService.list(req);
        List<CreditReportRecordDetailsListRSP> list = BeanUtil.copyToList(data, CreditReportRecordDetailsListRSP.class);
        return R.ok(list);
    }

    @Override
    public R<Void> remove(CreditReportRecordDetailsRemoveREQ req){
        creditReportRecordDetailsService.remove(req);
        return R.ok();
    }

}