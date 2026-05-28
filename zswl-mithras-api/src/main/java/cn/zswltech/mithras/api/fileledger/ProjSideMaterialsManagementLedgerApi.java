package cn.zswltech.mithras.api.fileledger;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fileledger.ProjSideArchivedMaterialsQueryREQ;
import cn.zswltech.mithras.dto.fileledger.ProjSideArchivedMaterialsQueryRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Api(tags = "档案管理台账-项目资料")
@RequestMapping("/documentManagementLedger/proj")
public interface ProjSideMaterialsManagementLedgerApi {

    @ApiOperation("项目端归档资料-列表查询")
    @PostMapping("/list/query")
    R<PageR<ProjSideArchivedMaterialsQueryRSP>> projSideArchivedMaterialsQuery(@RequestBody ProjSideArchivedMaterialsQueryREQ req);
}
