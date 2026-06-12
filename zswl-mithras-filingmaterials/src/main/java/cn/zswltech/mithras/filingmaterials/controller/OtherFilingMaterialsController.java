package cn.zswltech.mithras.filingmaterials.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.filingmaterials.OtherFilingMaterialsApi;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.filingmaterials.application.OtherFilingMaterialsApplicationService;
import cn.zswltech.mithras.dto.filingmaterials.FilingBaseREQ;
import cn.zswltech.mithras.dto.filingmaterials.FilingBasicRemoveREQ;
import cn.zswltech.mithras.dto.filingmaterials.FundFilingMaterialsBatchDownloadREQ;
import cn.zswltech.mithras.dto.filingmaterials.OtherPageListREQ;
import cn.zswltech.mithras.dto.filingmaterials.OtherPageListRSP;
import cn.zswltech.mithras.dto.filingmaterials.OtherProjectListRESP;
import cn.zswltech.mithras.dto.filingmaterials.OtherSaveMaterialsDescREQ;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class OtherFilingMaterialsController implements OtherFilingMaterialsApi {
    @Resource
    private OtherFilingMaterialsApplicationService otherFilingMaterialsApplicationService;

    @Override
    public R<PageR<OtherPageListRSP>> list(@Valid OtherPageListREQ req) {
        return otherFilingMaterialsApplicationService.list(req);
    }

    @Override
    public R<String> getMaterialsDesc(FilingBaseREQ filingBaseREQ) {
        return otherFilingMaterialsApplicationService.getMaterialsDesc(filingBaseREQ);
    }

    @Override
    public R<Void> saveMaterialsDesc(@Valid OtherSaveMaterialsDescREQ req) {
        return otherFilingMaterialsApplicationService.saveMaterialsDesc(req);
    }

    @Override
    public R<Boolean> checkMaterialsDesc(@Valid FilingBaseREQ req) {
        return otherFilingMaterialsApplicationService.checkMaterialsDesc(req);
    }

    @Override
    public R<List<OtherProjectListRESP>> getReviewProject() {
        return otherFilingMaterialsApplicationService.getReviewProject();
    }

    @Override
    public R<String> selectConfirm(String projReviewId) {
        return otherFilingMaterialsApplicationService.selectConfirm(projReviewId);
    }

    @Override
    public R<Void> cancel(String id) {
        return otherFilingMaterialsApplicationService.cancel(id);
    }

    @Override
    public R<Void> commit(@Valid FilingBaseREQ req) {
        return otherFilingMaterialsApplicationService.commit(req);
    }

    @Override
    public void batchDownload(FundFilingMaterialsBatchDownloadREQ req) throws IOException {
        otherFilingMaterialsApplicationService.batchDownload(req);
    }

    @Override
    public R<Map<String, List<SelectRSP>>> getOperationsDirDict(@Valid FilingBaseREQ req) {
        return otherFilingMaterialsApplicationService.getOperationsDirDict(req);
    }

    @Override
    public R<Void> remove(@Valid FilingBasicRemoveREQ filingBasicRemoveREQ) {
        return otherFilingMaterialsApplicationService.remove(filingBasicRemoveREQ);
    }

    @Override
    public void export(OtherPageListREQ param) {
        otherFilingMaterialsApplicationService.export(param);
    }

    @Override
    public void test() {
        otherFilingMaterialsApplicationService.test();
    }

}
