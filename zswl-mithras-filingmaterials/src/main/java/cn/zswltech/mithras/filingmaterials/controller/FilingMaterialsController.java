package cn.zswltech.mithras.filingmaterials.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.filingmaterials.FilingMaterialsApi;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.file.FileDownLoadRSP;
import cn.zswltech.mithras.dto.filingmaterials.*;
import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.*;
import cn.zswltech.mithras.filingmaterials.application.FilingMaterialsApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.io.IOException;

@RestController
public class FilingMaterialsController implements FilingMaterialsApi {
    @Resource
    private FilingMaterialsApplicationService filingMaterialsApplicationService;

    @Override
    public void batchDownload(FilingMaterialsREQ req) throws IOException {
        filingMaterialsApplicationService.batchDownload(req);
    }

    @Override
    public R<List<FilingMaterialsTabRSP>> filingTab(@Valid FilingBaseREQ req) {
        return filingMaterialsApplicationService.filingTab(req);
    }

    @Override
    public R<FilingProjMaterialsListListRSP> getCustomerReferenceMaterials(@Valid FilingMaterialsQueryREQ req) {
        return filingMaterialsApplicationService.getCustomerReferenceMaterials(req);
    }

    @Override
    public R<Void> materialsSynchronization(@Valid FilingMaterialsSynchronizationREQ req) {
        return filingMaterialsApplicationService.materialsSynchronization(req);
    }

    @Override
    public R<Void> materialsImport(@Valid FilingMaterialsImportREQ req) {
        return filingMaterialsApplicationService.materialsImport(req);
    }

    @Override
    public R<String> effect(@Valid FilingBaseREQ req) {
        return filingMaterialsApplicationService.effect(req);
    }

    @Override
    public R<Boolean> synchronizationButtonFlag(@Valid FilingMaterialsSynchronizationREQ req) {
        return filingMaterialsApplicationService.synchronizationButtonFlag(req);
    }

    @Override
    public R<FilingMaterialsQueryRSP> getNonCustomerReferenceMaterials(FilingMaterialsQueryREQ req) {
        return filingMaterialsApplicationService.getNonCustomerReferenceMaterials(req);
    }

    @Override
    public R<Map<String, List<SelectRSP>>> getOperationsDirDict(FilingBaseREQ filingBaseREQ) {
        return filingMaterialsApplicationService.getOperationsDirDict(filingBaseREQ);
    }

    @Override
    public R<FileDownLoadRSP> download(@Valid FilingFileDownloadREQ filingFileDownloadREQ) throws IOException {
        return filingMaterialsApplicationService.download(filingFileDownloadREQ);
    }

    @Override
    public void downloadTemplate(@Valid FilingTemplateDownloasREQ filingTemplateDownloasREQ) {
        filingMaterialsApplicationService.downloadTemplate(filingTemplateDownloasREQ);
    }

    @Override
    public R<Void> remove(FilingBasicRemoveREQ filingBasicRemoveREQ) {
        return filingMaterialsApplicationService.remove(filingBasicRemoveREQ);
    }

    @Override
    public R<String> getCurTaskDefKey(FilingProcessREQ filingProcessREQ) {
        return filingMaterialsApplicationService.getCurTaskDefKey(filingProcessREQ);
    }

}
