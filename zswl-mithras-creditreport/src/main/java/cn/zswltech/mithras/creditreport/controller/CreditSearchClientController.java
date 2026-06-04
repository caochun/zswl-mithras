package cn.zswltech.mithras.creditreport.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.creditreport.CredReportClientApi;
import cn.zswltech.mithras.dto.creditreport.CreditReportListDTO;
import cn.zswltech.mithras.dto.creditreport.CreditSearchClientQuery;
import cn.zswltech.mithras.creditreport.service.CreditSearchClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
public class CreditSearchClientController implements CredReportClientApi {

    @Resource
    private CreditSearchClientService creditSearchClientService;

    @Override
    public R<PageR<CreditReportListDTO>> list(CreditSearchClientQuery query) {
        return R.ok(creditSearchClientService.list(query));
    }

    @Override
    public R<Void> delete(Long id) {
        creditSearchClientService.delete(id);
        return R.ok();
    }
}
