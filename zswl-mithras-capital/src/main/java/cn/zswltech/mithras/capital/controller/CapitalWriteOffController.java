package cn.zswltech.mithras.capital.controller;

import cn.zswltech.mithras.api.capital.CapitalWriteOffApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.capital.service.api.CapitalWriteOffApplicationService;
import cn.zswltech.mithras.dto.capital.write_off.AddBusinessFlowREQ;
import cn.zswltech.mithras.dto.capital.write_off.AddFlowREQ;
import cn.zswltech.mithras.dto.capital.write_off.CheckBeforeImportREQ;
import cn.zswltech.mithras.dto.capital.write_off.CheckBeforeImportRSP;
import cn.zswltech.mithras.dto.capital.write_off.DeleteBusinessFlowREQ;
import cn.zswltech.mithras.dto.capital.write_off.DeleteFlowREQ;
import cn.zswltech.mithras.dto.capital.write_off.DeleteTabREQ;
import cn.zswltech.mithras.dto.capital.write_off.FlowMatchResultRSP;
import cn.zswltech.mithras.dto.capital.write_off.ManualWriteOffREQ;
import cn.zswltech.mithras.dto.capital.write_off.ReleaseBankFlowREQ;
import cn.zswltech.mithras.dto.capital.write_off.RematchTabREQ;
import cn.zswltech.mithras.dto.capital.write_off.SingleTabREQ;
import cn.zswltech.mithras.dto.capital.write_off.UpdateBusinessFlowREQ;

import javax.annotation.Resource;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class CapitalWriteOffController implements CapitalWriteOffApi {
    @Resource
    private CapitalWriteOffApplicationService capitalWriteOffApplicationService;

    @Override
    public R<Void> manualWriteOff(ManualWriteOffREQ req) {
        return capitalWriteOffApplicationService.manualWriteOff(req);
    }

    @Override
    public R<CheckBeforeImportRSP> checkBeforeImport(CheckBeforeImportREQ req) {
        return capitalWriteOffApplicationService.checkBeforeImport(req);
    }

    @Override
    public R<List<FlowMatchResultRSP>> flowMatchResult(CheckBeforeImportREQ req) {
        return capitalWriteOffApplicationService.flowMatchResult(req);
    }

    @Override
    public R<Void> deleteBankFlow(DeleteFlowREQ req) {
        return capitalWriteOffApplicationService.deleteBankFlow(req);
    }

    @Override
    public R<Void> addBankFlow(AddFlowREQ req) {
        return capitalWriteOffApplicationService.addBankFlow(req);
    }

    @Override
    public R<Void> updateBusinessFlow(UpdateBusinessFlowREQ req) {
        return capitalWriteOffApplicationService.updateBusinessFlow(req);
    }

    @Override
    public R<Void> addBusinessFlow(AddBusinessFlowREQ req) {
        return capitalWriteOffApplicationService.addBusinessFlow(req);
    }

    @Override
    public R<Void> deleteBusinessFlow(DeleteBusinessFlowREQ req) {
        return capitalWriteOffApplicationService.deleteBusinessFlow(req);
    }

    @Override
    public R<Void> rematchTab(RematchTabREQ req) {
        return capitalWriteOffApplicationService.rematchTab(req);
    }

    @Override
    public R<Void> deleteTab(DeleteTabREQ req) {
        return capitalWriteOffApplicationService.deleteTab(req);
    }

    @Override
    public R<FlowMatchResultRSP> singleTab(SingleTabREQ req) {
        return capitalWriteOffApplicationService.singleTab(req);
    }

    @Override
    public R<Void> releaseBankFlow(ReleaseBankFlowREQ req) {
        return capitalWriteOffApplicationService.releaseBankFlow(req);
    }

}
