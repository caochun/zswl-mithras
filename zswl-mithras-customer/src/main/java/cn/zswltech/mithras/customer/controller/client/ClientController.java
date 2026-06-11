package cn.zswltech.mithras.customer.controller.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.client.*;
import cn.zswltech.mithras.dto.client.commerceinfo.CorpCommerceInfoAddREQ;
import cn.zswltech.mithras.dto.client.commerceinfo.CorpCommerceInfoDetailRSP;
import cn.zswltech.mithras.dto.file.*;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.api.client.ClientApi;
import cn.zswltech.mithras.customer.application.client.ClientApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ClientController implements ClientApi {
    @Resource
    private ClientApplicationService clientApplicationService;

    @Override
    public R<PageR<ClientListRSP>> listNoAuthrotiy(@RequestBody @Valid ClientListREQ req) {
        return clientApplicationService.listNoAuthrotiy(req);
    }

    @Override
    public R<PageR<ClientListRSP>> list(@RequestBody @Valid ClientListREQ req) {
        return clientApplicationService.list(req);
    }

    @Override
    public R<PageR<ClientListRSP>> newList(@RequestBody @Valid ClientListREQ req) {
        return clientApplicationService.newList(req);
    }

    @Override
    public R<CorpCommerceInfoAddREQ> addCorporation(@RequestBody @Valid ClientCorpAddREQ req) {
        return clientApplicationService.addCorporation(req);
    }

    @Override
    public R<Long> addNormal(@RequestBody @Valid ClientNormalAddREQ req) {
        return clientApplicationService.addNormal(req);
    }

    @Override
    public R<Boolean> userNameExist(@RequestBody @Valid ClientNameExistREQ req) {
        return clientApplicationService.userNameExist(req);
    }

    @Override
    public R<Boolean> removeClient(@RequestBody @Valid ClientRemoveREQ req) {
        return clientApplicationService.removeClient(req);
    }

    @Override
    public R<ClientSyncRSP> sync(@RequestBody @Valid ClientSyncREQ req) {
        return clientApplicationService.sync(req);
    }

    @Override
    public R<CorpCommerceInfoDetailRSP> tycCommerceInfo(@RequestBody @Valid ClientSyncREQ req) {
        return clientApplicationService.tycCommerceInfo(req);
    }

    @Override
    public R<Void> effect(@RequestBody @Valid ClientEffectREQ req) {
        return clientApplicationService.effect(req);
    }

    @Override
    public R<ClientButtonStatusRSP> buttonStatus(@RequestBody @Valid ClientButtonStatusREQ req) {
        return clientApplicationService.buttonStatus(req);
    }

    @Override
    public R<ClientTransferApplyRSP> createTransferApply() {
        return clientApplicationService.createTransferApply();
    }

    @Override
    public R<ClientTransferApplyRSP> findTransferApplyByBatchNo(@RequestBody @Valid ClientTransferApplyQueryREQ req) {
        return clientApplicationService.findTransferApplyByBatchNo(req);
    }

    @Override
    public R<List<SponsorClientListRSP>> sponsorClientList(@RequestBody @Valid SponsorClientListREQ req) {
        return clientApplicationService.sponsorClientList(req);
    }

    @Override
    public R<List<SponsorClientListNewRSP>> sponsorClientNewList(@RequestBody @Valid SponsorClientListNewREQ req) {
        return clientApplicationService.sponsorClientNewList(req);
    }

    @Override
    public R<Void> sponsorClientSubmit(@RequestBody @Valid SponsorClientSubmitREQ req) {
        return clientApplicationService.sponsorClientSubmit(req);
    }

    @Override
    public R<Void> sponsorClientNewSubmit(@RequestBody @Valid SponsorClientSubmitNewREQ req) {
        return clientApplicationService.sponsorClientNewSubmit(req);
    }

    @Override
    public R<Void> sponsorClientNewModify(@RequestBody @Valid SponsorClientModifyNewREQ req) {
        return clientApplicationService.sponsorClientNewModify(req);
    }

    @Override
    public R<Void> sponsorClientNewModifyBatch(@RequestBody @Valid SponsorClientModifyNewBatchREQ req) {
        return clientApplicationService.sponsorClientNewModifyBatch(req);
    }

    @Override
    public R<Void> sponsorClientNewRemove(@RequestBody @Valid SponsorClientRemoveNewREQ req) {
        return clientApplicationService.sponsorClientNewRemove(req);
    }

    @Override
    public R<SponsorClientDetailRSP> transferDetail(@RequestBody @Valid SponsorClientDetailREQ req) {
        return clientApplicationService.transferDetail(req);
    }

    @Override
    public R<SponsorClientDetailNewRSP> newTransferDetail(@RequestBody @Valid SponsorClientDetailNewREQ req) {
        return clientApplicationService.newTransferDetail(req);
    }

    @Override
    public R<Void> newTransferDetailModify(@RequestBody @Valid SponsorClientDetailNewModifyREQ req) {
        return clientApplicationService.newTransferDetailModify(req);
    }

    @Override
    public R<UserButtonStatusRSP> buttonStatus() {
        return clientApplicationService.buttonStatus();
    }

    @Override
    public R<Void> authorityEffect(@RequestBody @Valid ClientAuthorityEffectREQ req) {
        return clientApplicationService.authorityEffect(req);
    }

    @Override
    public R<Void> applyEffect(@RequestBody @Valid ClientApplyEffectREQ req) {
        return clientApplicationService.applyEffect(req);
    }

    @Override
    public R<Void> applyModify(@RequestBody @Valid ClientApplyModifyREQ req) {
        return clientApplicationService.applyModify(req);
    }

    @Override
    public R<ClientApplyDetailRSP> applyDetail(@RequestBody @Valid ClientApplyDetailREQ req) {
        return clientApplicationService.applyDetail(req);
    }

    @Override
    public R<Void> applyValidate(@RequestBody @Valid ClientApplyDetailREQ req) {
        return clientApplicationService.applyValidate(req);
    }

    @Override
    public R<Void> exportClientTransfer(@RequestBody @Valid ClientTransferExportREQ clientTransferExportREQ) {
        return clientApplicationService.exportClientTransfer(clientTransferExportREQ);
    }

    @Override
    public R<ClientOwnApplyDetailRSP> applyOwn(@RequestBody @Valid ClientOwnApplyDetailREQ req) {
        return clientApplicationService.applyOwn(req);
    }

    @Override
    public R<ClientApplyStatusRSP> status(@RequestBody @Valid ClientApplyStatusREQ req) {
        return clientApplicationService.status(req);
    }

    @Override
    public R<ClientApplyOccupyRSP> checkOccupy(@RequestBody @Valid ClientApplyOccupyREQ req) {
        return clientApplicationService.checkOccupy(req);
    }

    @Override
    public R<Void> release(@RequestBody @Valid ClientReleaseREQ req) {
        return clientApplicationService.release(req);
    }

    @Override
    public R<FileUploadRSP> upload(@Valid FileUploadREQ fileUploadREQ) {
        return clientApplicationService.upload(fileUploadREQ);
    }

    @Override
    public R<PageR<FileListRSP>> list(@RequestBody @Valid ClientAuthorityFileListREQ req) {
        return clientApplicationService.list(req);
    }

    @Override
    public R<FileDownLoadRSP> download(@Valid FileDownLoadREQ req) {
        return clientApplicationService.download(req);
    }

    @Override
    public R<Void> batchRemove(@RequestBody @Valid FileBatchRemoveREQ req) {
        return clientApplicationService.batchRemove(req);
    }

    @Override
    public void batchDownload(@Valid FileBatchDownLoadREQ req) {
        clientApplicationService.batchDownload(req);
    }

    @Override
    public R<String> getBatchNumber() {
        return clientApplicationService.getBatchNumber();
    }

    @Override
    public R<PageR<ClientListRSP>> groupList(@RequestBody @Valid ClientListREQ req) {
        return clientApplicationService.groupList(req);
    }

    @Override
    public void test(String timePoint) {
        clientApplicationService.test(timePoint);
    }

    @Override
    public R<List<ClientAppQueryRSP>> queryCompany(@RequestBody @Valid ClientAppQueryREQ req) {
        return clientApplicationService.queryCompany(req);
    }
}
