package cn.zswltech.mithras.filingmaterials.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.filingmaterials.FundFilingMaterialsApi;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.filingmaterials.FilingBaseREQ;
import cn.zswltech.mithras.dto.filingmaterials.FilingFileDownloadREQ;
import cn.zswltech.mithras.dto.filingmaterials.FundFilingMaterialsBatchDownloadREQ;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.*;
import cn.zswltech.mithras.filingmaterials.application.FundFilingMaterialsApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class FundFilingMaterialsController implements FundFilingMaterialsApi {
    @Resource
    private FundFilingMaterialsApplicationService fundFilingMaterialsApplicationService;

    @Override
    public void batchDownload(FundFilingMaterialsBatchDownloadREQ req) throws IOException {
        fundFilingMaterialsApplicationService.batchDownload(req);
    }

    @Override
    public R<Map<String, List<SelectRSP>>> getOperationsDirDict(@Valid FilingBaseREQ req) {
        return fundFilingMaterialsApplicationService.getOperationsDirDict(req);
    }

    @Override
    public void download(@Valid FilingFileDownloadREQ filingFileDownloadREQ) throws IOException {
        fundFilingMaterialsApplicationService.download(filingFileDownloadREQ);
    }

    @Override
    public R<Boolean> checkFile(FilingBaseREQ req) {
        return fundFilingMaterialsApplicationService.checkFile(req);
    }

    @Override
    public void test() {
        fundFilingMaterialsApplicationService.test();
    }

}
