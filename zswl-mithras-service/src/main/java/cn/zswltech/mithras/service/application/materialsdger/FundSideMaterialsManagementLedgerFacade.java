package cn.zswltech.mithras.service.application.materialsdger;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.filingmaterials.application.ledger.api.FundSideMaterialsManagementLedgerApplicationService;
import cn.zswltech.mithras.dto.PageReq;
import cn.zswltech.mithras.dto.fileledger.FundSideArchivedMaterialsBatchDownloadREQ;
import cn.zswltech.mithras.dto.fileledger.FundSideArchivedMaterialsDownloadRecordsQueryRSP;
import cn.zswltech.mithras.dto.fileledger.FundSideArchivedMaterialsQueryREQ;
import cn.zswltech.mithras.dto.fileledger.FundSideArchivedMaterialsQueryRSP;
import cn.zswltech.mithras.service.service.materialsdger.FundSideMaterialsManagementLedgerService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
@Slf4j
public class FundSideMaterialsManagementLedgerFacade implements FundSideMaterialsManagementLedgerApplicationService {


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
