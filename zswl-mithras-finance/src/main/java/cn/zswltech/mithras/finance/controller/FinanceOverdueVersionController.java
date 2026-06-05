package cn.zswltech.mithras.finance.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.finance.FinanceOverdueVersionApi;
import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueIntegrationPushRSP;
import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueVersionSubmitREQ;
import cn.zswltech.mithras.finance.service.FinanceOverdueVersionApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @ClassName FinanceOverdueVersionController
 * @Description TODO
 * @Author jackerhe
 * @Date 2025/9/19 10:06
 * @Version 1.0
 **/
@RestController
public class FinanceOverdueVersionController implements FinanceOverdueVersionApi {

    @Resource
    private FinanceOverdueVersionApplicationService financeOverdueVersionService;

    @Override
    public R<Void> submit(@Valid FinanceOverdueVersionSubmitREQ req) {
        financeOverdueVersionService.submit(req);
        return R.ok();
    }

    @Override
    public R<FinanceOverdueIntegrationPushRSP> push(@Valid FinanceOverdueVersionSubmitREQ req) {
        return R.ok(financeOverdueVersionService.push(req));
    }

}
