package cn.zswltech.mithras.system.controller.download;

import cn.zswltech.mithras.api.IndexDownloadApi;
import cn.zswltech.mithras.dto.IndexDownloadREQ;
import cn.zswltech.mithras.system.application.download.IndexDownloadApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
public class IndexDownloadController implements IndexDownloadApi {

    @Resource
    private IndexDownloadApplicationService indexDownloadApplicationService;

    @Override
    public void indexDownload(@Valid IndexDownloadREQ req) {
        indexDownloadApplicationService.indexDownload(req);
    }
}
