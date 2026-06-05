package cn.zswltech.mithras.filingmaterials.interfaces.ledger;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fileledger.FundSideMaterialsManagementLedgerApi;
import cn.zswltech.mithras.dto.PageReq;
import cn.zswltech.mithras.dto.fileledger.FundSideArchivedMaterialsBatchDownloadREQ;
import cn.zswltech.mithras.dto.fileledger.FundSideArchivedMaterialsDownloadRecordsQueryRSP;
import cn.zswltech.mithras.dto.fileledger.FundSideArchivedMaterialsQueryREQ;
import cn.zswltech.mithras.dto.fileledger.FundSideArchivedMaterialsQueryRSP;
import javax.annotation.Resource;
import cn.zswltech.mithras.filingmaterials.application.ledger.api.FundSideMaterialsManagementLedgerApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FundSideMaterialsManagementLedgerController implements FundSideMaterialsManagementLedgerApi {
    @Resource
    private FundSideMaterialsManagementLedgerApplicationService fundSideMaterialsManagementLedgerApplicationService;

    @Override
    public R<PageR<FundSideArchivedMaterialsQueryRSP>> fundSideArchivedMaterialsQuery(FundSideArchivedMaterialsQueryREQ req) {
        return fundSideMaterialsManagementLedgerApplicationService.fundSideArchivedMaterialsQuery(req);
    }

    @Override
    public R<Void> fundSideArchivedMaterialsBatchDownload(FundSideArchivedMaterialsBatchDownloadREQ req) {
        return fundSideMaterialsManagementLedgerApplicationService.fundSideArchivedMaterialsBatchDownload(req);
    }

    @Override
    public R<PageR<FundSideArchivedMaterialsDownloadRecordsQueryRSP>> recordsQuery(PageReq req) {
        return fundSideMaterialsManagementLedgerApplicationService.recordsQuery(req);
    }
}
