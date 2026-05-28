package cn.zswltech.mithras.service.controller.finance;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.finance.FinanceOverdueIntegrationApi;
import cn.zswltech.mithras.dto.finance.overdue.*;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceOverdueIntegration;
import cn.zswltech.mithras.service.service.finance.FinanceOverdueIntegrationService;
import cn.zswltech.mithras.service.service.finance.FinanceOverdueReportBaseService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
* @description 应收逾期集成表
* @author vico
* @date 2025-09-15
*/
@RestController
public class FinanceOverdueIntegrationController implements FinanceOverdueIntegrationApi {

    @Resource
    private FinanceOverdueIntegrationService financeOverdueIntegrationService;
    @Resource
    private FinanceOverdueReportBaseService financeOverdueReportBaseService;


    @Override
    public R<Void> modify(FinanceOverdueIntegrationModifyREQ req){
        financeOverdueIntegrationService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<FinanceOverdueIntegrationListRSP>> list(FinanceOverdueIntegrationListREQ req){
        Page<FinanceOverdueIntegration> data = financeOverdueIntegrationService.list(req);
        List<FinanceOverdueIntegrationListRSP> list = BeanUtil.copyToList(data.getRecords(), FinanceOverdueIntegrationListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(FinanceOverdueIntegrationRemoveREQ req){
        financeOverdueIntegrationService.remove(req);
        return R.ok();
    }

}