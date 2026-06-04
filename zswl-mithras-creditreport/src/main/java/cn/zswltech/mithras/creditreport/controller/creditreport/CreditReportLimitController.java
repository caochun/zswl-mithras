package cn.zswltech.mithras.creditreport.controller;
import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.creditreport.CreditReportLimitApi;
import cn.zswltech.mithras.dto.creditreport.CreditReportLimitListREQ;
import cn.zswltech.mithras.dto.creditreport.CreditReportLimitListRSP;
import cn.zswltech.mithras.dto.creditreport.CreditReportLimitModifyREQ;
import cn.zswltech.mithras.dto.creditreport.CreditReportLimitRemoveREQ;
import cn.zswltech.mithras.creditreport.mapper.model.CreditReportLimit;
import cn.zswltech.mithras.creditreport.service.CreditReportLimitService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
* @description 征信报告-信用额度表
* @author vico
* @date 2025-11-14
*/
@RestController
public class CreditReportLimitController implements CreditReportLimitApi {

    @Resource
    private CreditReportLimitService creditReportLimitService;

    @Override
    public R<Void> modify(CreditReportLimitModifyREQ req){
        creditReportLimitService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<CreditReportLimitListRSP>> list(CreditReportLimitListREQ req){
        Page<CreditReportLimit> data = creditReportLimitService.list(req);
        List<CreditReportLimitListRSP> list = BeanUtil.copyToList(data.getRecords(), CreditReportLimitListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(CreditReportLimitRemoveREQ req){
        creditReportLimitService.remove(req);
        return R.ok();
    }

}