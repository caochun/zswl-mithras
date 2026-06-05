package cn.zswltech.mithras.filingmaterials.interfaces.ledger;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fileledger.ProjSideMaterialsManagementLedgerApi;
import cn.zswltech.mithras.dto.fileledger.ProjSideArchivedMaterialsQueryREQ;
import cn.zswltech.mithras.dto.fileledger.ProjSideArchivedMaterialsQueryRSP;
import javax.annotation.Resource;
import cn.zswltech.mithras.filingmaterials.application.ledger.api.ProjSideMaterialsManagementLedgerApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjSideMaterialsManagementLedgerController implements ProjSideMaterialsManagementLedgerApi {
    @Resource
    private ProjSideMaterialsManagementLedgerApplicationService projSideMaterialsManagementLedgerApplicationService;

    @Override
    public R<PageR<ProjSideArchivedMaterialsQueryRSP>> projSideArchivedMaterialsQuery(ProjSideArchivedMaterialsQueryREQ req) {
        return projSideMaterialsManagementLedgerApplicationService.projSideArchivedMaterialsQuery(req);
    }
}
