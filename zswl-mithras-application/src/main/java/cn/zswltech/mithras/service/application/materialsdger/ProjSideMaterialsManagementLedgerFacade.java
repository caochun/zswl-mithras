package cn.zswltech.mithras.service.application.materialsdger;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.filingmaterials.application.ledger.api.ProjSideMaterialsManagementLedgerApplicationService;
import cn.zswltech.mithras.dto.fileledger.ProjSideArchivedMaterialsQueryREQ;
import cn.zswltech.mithras.dto.fileledger.ProjSideArchivedMaterialsQueryRSP;
import cn.zswltech.mithras.service.service.materialsdger.ProjSideMaterialsManagementLedgerService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
@Slf4j
public class ProjSideMaterialsManagementLedgerFacade implements ProjSideMaterialsManagementLedgerApplicationService {

    @Resource
    private ProjSideMaterialsManagementLedgerService projSideMaterialsManagementLedgerService;

    @Override
    public R<PageR<ProjSideArchivedMaterialsQueryRSP>> projSideArchivedMaterialsQuery(ProjSideArchivedMaterialsQueryREQ req) {
        return R.ok(projSideMaterialsManagementLedgerService.queryMaterialsLedger(req));
    }
}
