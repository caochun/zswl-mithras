package cn.zswltech.mithras.creditreport.controller;
import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.creditreport.CreditReportRepaymentResponsibilityApi;
import cn.zswltech.mithras.dto.creditreport.CreditReportRepaymentResponsibilityListREQ;
import cn.zswltech.mithras.dto.creditreport.CreditReportRepaymentResponsibilityListRSP;
import cn.zswltech.mithras.dto.creditreport.CreditReportRepaymentResponsibilityModifyREQ;
import cn.zswltech.mithras.dto.creditreport.CreditReportRepaymentResponsibilityRemoveREQ;
import cn.zswltech.mithras.creditreport.model.CreditReportRepaymentResponsibility;
import cn.zswltech.mithras.creditreport.service.CreditReportRepaymentResponsibilityService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
* @description 征信报告-相关还款责任信息概要表
* @author vico
* @date 2025-11-14
*/
@RestController
public class CreditReportRepaymentResponsibilityController implements CreditReportRepaymentResponsibilityApi {

    @Resource
    private CreditReportRepaymentResponsibilityService creditReportRepaymentResponsibilityService;

    @Override
    public R<Void> modify(CreditReportRepaymentResponsibilityModifyREQ req){
        creditReportRepaymentResponsibilityService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<CreditReportRepaymentResponsibilityListRSP>> list(CreditReportRepaymentResponsibilityListREQ req){
        Page<CreditReportRepaymentResponsibility> data = creditReportRepaymentResponsibilityService.list(req);
        List<CreditReportRepaymentResponsibilityListRSP> list = BeanUtil.copyToList(data.getRecords(), CreditReportRepaymentResponsibilityListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(CreditReportRepaymentResponsibilityRemoveREQ req){
        creditReportRepaymentResponsibilityService.remove(req);
        return R.ok();
    }

}