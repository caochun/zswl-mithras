package cn.zswltech.mithras.capital.controller;

import cn.zswltech.mithras.api.capital.BusinessFlowApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.capital.service.api.BusinessFlowApplicationService;
import cn.zswltech.mithras.dto.capital.BusinessFlowFinanceDetailListREQ;
import cn.zswltech.mithras.dto.capital.BusinessFlowFinanceDetailListRSP;
import cn.zswltech.mithras.dto.capital.BusinessFlowFinanceDetailSaveREQ;
import cn.zswltech.mithras.dto.capital.BusinessFlowFinanceListREQ;
import cn.zswltech.mithras.dto.capital.BusinessFlowFinanceListRSP;
import cn.zswltech.mithras.dto.capital.BusinessFlowFinanceManualPushREQ;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class BusinessFlowController implements BusinessFlowApi {
    @Resource
    private BusinessFlowApplicationService businessFlowApplicationService;

    @Override
    public R<PageR<BusinessFlowFinanceListRSP>> selectList(BusinessFlowFinanceListREQ req) {
        return businessFlowApplicationService.selectList(req);
    }

    @Override
    public void exportExcel(BusinessFlowFinanceListREQ req) {
        businessFlowApplicationService.exportExcel(req);
    }

    @Override
    public R<List<BusinessFlowFinanceDetailListRSP>> detailList(@Valid BusinessFlowFinanceDetailListREQ req) {
        return businessFlowApplicationService.detailList(req);
    }

    @Override
    public R<Void> saveDetail(@Valid BusinessFlowFinanceDetailSaveREQ req) {
        return businessFlowApplicationService.saveDetail(req);
    }

    @Override
    public R<Void> manualPush(@Valid BusinessFlowFinanceManualPushREQ req) {
        return businessFlowApplicationService.manualPush(req);
    }

}
