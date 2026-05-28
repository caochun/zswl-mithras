package cn.zswltech.mithras.service.controller.materialsdger;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fileledger.ProjSideMaterialsManagementLedgerApi;
import cn.zswltech.mithras.dto.fileledger.ProjSideArchivedMaterialsQueryREQ;
import cn.zswltech.mithras.dto.fileledger.ProjSideArchivedMaterialsQueryRSP;
import cn.zswltech.mithras.service.service.materialsdger.ProjSideMaterialsManagementLedgerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
public class ProjSideMaterialsManagementLedgerController implements ProjSideMaterialsManagementLedgerApi {

    @Resource
    private ProjSideMaterialsManagementLedgerService projSideMaterialsManagementLedgerService;

    @Override
    public R<PageR<ProjSideArchivedMaterialsQueryRSP>> projSideArchivedMaterialsQuery(ProjSideArchivedMaterialsQueryREQ req) {
        return R.ok(projSideMaterialsManagementLedgerService.queryMaterialsLedger(req));
    }
}
