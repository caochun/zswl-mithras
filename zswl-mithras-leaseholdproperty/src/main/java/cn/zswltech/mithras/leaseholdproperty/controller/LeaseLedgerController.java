package cn.zswltech.mithras.leaseholdproperty.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.leaseholdproperty.LeaseLedgerApi;
import cn.zswltech.mithras.dto.MultiplePkREQ;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.file.FileUploadRSP;
import cn.zswltech.mithras.dto.file.template.FileTemplateListREQ;
import cn.zswltech.mithras.dto.file.template.FileTemplateListRSP;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseCheckRepeatREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseCheckRepeatRSP;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseFlowUploadREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseItemAmountREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseItemImportREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseItemListExportREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseItemListREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseItemListRSP;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseItemMetadataREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseItemMetadataRSP;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseItemRedupRSP;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseLedgerDetailREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseLedgerMainREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseLedgerMainRSP;
import cn.zswltech.mithras.dto.leaseholdproperty.LedgerContractDetailRSP;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseLedgerApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class LeaseLedgerController implements LeaseLedgerApi {
    @Resource
    private LeaseLedgerApplicationService leaseLedgerApplicationService;

    @Override
    public R<PageR<LeaseLedgerMainRSP>> getPage(LeaseLedgerMainREQ param) {
        return leaseLedgerApplicationService.getPage(param);
    }

    @Override
    public void download(LeaseLedgerMainREQ param) {
        leaseLedgerApplicationService.download(param);
    }

    @Override
    public R<LedgerContractDetailRSP> getContractInfoById(LeaseLedgerDetailREQ param) {
        return leaseLedgerApplicationService.getContractInfoById(param);
    }

    @Override
    public R<LeaseCheckRepeatRSP> getLeaseCheckRepeatById(LeaseLedgerDetailREQ param) {
        return leaseLedgerApplicationService.getLeaseCheckRepeatById(param);
    }

    @Override
    public R<Boolean> checkRepeatSave(LeaseCheckRepeatREQ param) {
        return leaseLedgerApplicationService.checkRepeatSave(param);
    }

    @Override
    public R<Void> saveLeaseItemTotalAmount(LeaseItemAmountREQ req) {
        return leaseLedgerApplicationService.saveLeaseItemTotalAmount(req);
    }

    @Override
    public R<Void> saveLeaseItemMetadata(LeaseItemMetadataREQ req) {
        return leaseLedgerApplicationService.saveLeaseItemMetadata(req);
    }

    @Override
    public R<Void> init() {
        return leaseLedgerApplicationService.init();
    }

    @Override
    public R<LeaseItemMetadataRSP> getLeaseItemMetadata(SinglePkREQ req) {
        return leaseLedgerApplicationService.getLeaseItemMetadata(req);
    }

    @Override
    public void downloadLeaseItemTemplate(SinglePkREQ req) {
        leaseLedgerApplicationService.downloadLeaseItemTemplate(req);
    }

    @Override
    public R<Void> importLeaseItemList(LeaseItemImportREQ req) {
        return leaseLedgerApplicationService.importLeaseItemList(req);
    }

    @Override
    public R<LeaseItemListRSP> listItemWithPage(LeaseItemListREQ req) {
        return leaseLedgerApplicationService.listItemWithPage(req);
    }

    @Override
    public void exportLeaseItemList(LeaseItemListExportREQ req) {
        leaseLedgerApplicationService.exportLeaseItemList(req);
    }

    @Override
    public R<Void> removeLeaseItemList(MultiplePkREQ req) {
        return leaseLedgerApplicationService.removeLeaseItemList(req);
    }

    @Override
    public R<List<FileUploadRSP>> flowUpdate(LeaseFlowUploadREQ param) {
        return leaseLedgerApplicationService.flowUpdate(param);
    }

    @Override
    public R<PageR<FileTemplateListRSP>> downloadCheckRepeatTemplate(FileTemplateListREQ req) {
        return leaseLedgerApplicationService.downloadCheckRepeatTemplate(req);
    }

    @Override
    public R<LeaseItemRedupRSP> dedup(LeaseItemListREQ req) {
        return leaseLedgerApplicationService.dedup(req);
    }
}
