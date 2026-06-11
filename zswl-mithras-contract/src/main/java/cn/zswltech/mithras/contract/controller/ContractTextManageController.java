package cn.zswltech.mithras.contract.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.text.*;
import cn.zswltech.mithras.dto.file.FileListRSP;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.api.contract.ContractTextManageApi;
import cn.zswltech.mithras.contract.application.ContractTextManageApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ContractTextManageController implements ContractTextManageApi {
    @Resource
    private ContractTextManageApplicationService contractTextManageApplicationService;

    @Override
    public R<PageR<ContractTextManageListRSP>> list(@RequestBody @Valid ContractTextManageListREQ req) {
        return contractTextManageApplicationService.list(req);
    }

    @Override
    public R<List<ContractTextManageUnSignDetailRSP>> unSignedDetail(@RequestBody @Valid ContractTextManageUnSignDetailREQ req) {
        return contractTextManageApplicationService.unSignedDetail(req);
    }

    @Override
    public R<List<FileListRSP>> signedDetail(@RequestBody @Valid ContractTextManageSignedDetailREQ req) {
        return contractTextManageApplicationService.signedDetail(req);
    }

    @Override
    public R<Void> downloadAll(@RequestBody @Valid ContractTextManageDownloadAllREQ req) {
        return contractTextManageApplicationService.downloadAll(req);
    }

    @Override
    public R<Void> updateDefaultSigningWay(@RequestBody @Valid ContractTextManageUpdateDefaultSigningWayREQ req) {
        return contractTextManageApplicationService.updateDefaultSigningWay(req);
    }

    @Override
    public R<Void> updateSingleSigningWay(@RequestBody @Valid ContractTextManageUpdateSingleSigningWayREQ req) {
        return contractTextManageApplicationService.updateSingleSigningWay(req);
    }

    @Override
    public R<String> batchSign(@RequestBody @Valid ContractTextManageBatchSignREQ req) {
        return contractTextManageApplicationService.batchSign(req);
    }

    @Override
    public R<Void> singleSign(@RequestBody @Valid ContractTextManageSingleSignREQ req) {
        return contractTextManageApplicationService.singleSign(req);
    }

    @Override
    public R<Void> downloadWaitSign(@RequestBody @Valid ContractTextManageDownloadWaitSignREQ req) {
        return contractTextManageApplicationService.downloadWaitSign(req);
    }

    @Override
    public R<List<ContractTextSignInfoSignPhotosAndVideosRSP>> signPhotosAndVideos(@RequestBody @Valid ContractTextSignInfoSignPhotosAndVideosREQ req) {
        return contractTextManageApplicationService.signPhotosAndVideos(req);
    }
}
