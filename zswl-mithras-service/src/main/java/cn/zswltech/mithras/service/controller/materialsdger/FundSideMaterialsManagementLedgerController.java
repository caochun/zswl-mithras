package cn.zswltech.mithras.service.controller.materialsdger;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fileledger.FundSideMaterialsManagementLedgerApi;
import cn.zswltech.mithras.dto.PageReq;
import cn.zswltech.mithras.dto.fileledger.FundSideArchivedMaterialsBatchDownloadREQ;
import cn.zswltech.mithras.dto.fileledger.FundSideArchivedMaterialsDownloadRecordsQueryRSP;
import cn.zswltech.mithras.dto.fileledger.FundSideArchivedMaterialsQueryREQ;
import cn.zswltech.mithras.dto.fileledger.FundSideArchivedMaterialsQueryRSP;
import cn.zswltech.mithras.service.service.materialsdger.FundSideMaterialsManagementLedgerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
public class FundSideMaterialsManagementLedgerController implements FundSideMaterialsManagementLedgerApi {


    @Resource
    private FundSideMaterialsManagementLedgerService fundSideMaterialsManagementLedgerService;

    @Override
    public R<PageR<FundSideArchivedMaterialsQueryRSP>> fundSideArchivedMaterialsQuery(FundSideArchivedMaterialsQueryREQ req) {
        return R.ok(fundSideMaterialsManagementLedgerService.queryMaterialsLedger(req));
    }

    @Override
    public R<Void> fundSideArchivedMaterialsBatchDownload(FundSideArchivedMaterialsBatchDownloadREQ req) {
        fundSideMaterialsManagementLedgerService.archivedMaterialsBatchDownload(req);
        return R.ok();
    }

    @Override
    public R<PageR<FundSideArchivedMaterialsDownloadRecordsQueryRSP>> recordsQuery(PageReq req) {
        return R.ok(fundSideMaterialsManagementLedgerService.recordsQuery(req));
    }
}
