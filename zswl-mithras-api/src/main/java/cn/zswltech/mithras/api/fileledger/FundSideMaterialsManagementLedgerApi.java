package cn.zswltech.mithras.api.fileledger;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.PageReq;
import cn.zswltech.mithras.dto.fileledger.FundSideArchivedMaterialsBatchDownloadREQ;
import cn.zswltech.mithras.dto.fileledger.FundSideArchivedMaterialsQueryREQ;
import cn.zswltech.mithras.dto.fileledger.FundSideArchivedMaterialsQueryRSP;
import cn.zswltech.mithras.dto.fileledger.FundSideArchivedMaterialsDownloadRecordsQueryRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Api(tags = "档案管理台账-资金端资料")
@RequestMapping("/documentManagementLedger/fund")
public interface FundSideMaterialsManagementLedgerApi {

    @ApiOperation("资金端归档资料-列表查询")
    @PostMapping("/list/query")
    R<PageR<FundSideArchivedMaterialsQueryRSP>> fundSideArchivedMaterialsQuery(@RequestBody FundSideArchivedMaterialsQueryREQ req);

    @ApiOperation("资金端归档资料-批量下载")
    @PostMapping("/batch/download")
    R<Void> fundSideArchivedMaterialsBatchDownload(@RequestBody @Valid FundSideArchivedMaterialsBatchDownloadREQ req);

    @ApiOperation("资金端归档资料-下载记录查询")
    @PostMapping("/download/records/query")
    R<PageR<FundSideArchivedMaterialsDownloadRecordsQueryRSP>> recordsQuery(@RequestBody PageReq req);
}
