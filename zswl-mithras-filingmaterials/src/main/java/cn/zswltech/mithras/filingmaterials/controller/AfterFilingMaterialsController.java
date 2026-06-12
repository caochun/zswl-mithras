package cn.zswltech.mithras.filingmaterials.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.filingmaterials.AfterFilingMaterialsApi;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.filingmaterials.FilingBaseREQ;
import cn.zswltech.mithras.dto.filingmaterials.FilingBasicRemoveREQ;
import cn.zswltech.mithras.dto.filingmaterials.FundFilingMaterialsBatchDownloadREQ;
import cn.zswltech.mithras.filingmaterials.application.AfterFilingMaterialsApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class AfterFilingMaterialsController implements AfterFilingMaterialsApi {
    @Resource
    private AfterFilingMaterialsApplicationService afterFilingMaterialsApplicationService;

    @Override
    public void batchDownload(FundFilingMaterialsBatchDownloadREQ req) throws IOException {
        afterFilingMaterialsApplicationService.batchDownload(req);
    }

    @Override
    public R<Map<String, List<SelectRSP>>> getOperationsDirDict(@Valid FilingBaseREQ req) {
        return afterFilingMaterialsApplicationService.getOperationsDirDict(req);
    }

    @Override
    public R<Void> remove(@Valid FilingBasicRemoveREQ filingBasicRemoveREQ) {
        return afterFilingMaterialsApplicationService.remove(filingBasicRemoveREQ);
    }

    @Override
    public void test() {
        afterFilingMaterialsApplicationService.test();
    }

}
