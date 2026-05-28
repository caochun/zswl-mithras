package cn.zswltech.mithras.api.contract;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.text.*;
import cn.zswltech.mithras.dto.file.FileListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * @author bigbear
 * @date 2024/11/18 09:51
 * @description 合同文本管理API
 */
@Api(tags = "合同文本管理API")
@RequestMapping(path = "/contract/text/manage")
public interface ContractTextManageApi {

    @ApiOperation(value = "合同文本管理-台账列表")
    @PostMapping(path = "/list")
    R<PageR<ContractTextManageListRSP>> list(@RequestBody @Valid ContractTextManageListREQ req);

    @ApiOperation(value = "合同文本管理-未签约详情")
    @PostMapping(path = "/unSigned/detail")
    R<List<ContractTextManageUnSignDetailRSP>> unSignedDetail(@RequestBody @Valid ContractTextManageUnSignDetailREQ req);

    @ApiOperation(value = "合同文本管理-已签约详情")
    @PostMapping(path = "/signed/detail")
    R<List<FileListRSP>> signedDetail(@RequestBody @Valid ContractTextManageSignedDetailREQ req);

    @ApiOperation(value = "合同文本管理-合同文本下载")
    @PostMapping(path = "/download/all")
    R<Void> downloadAll(@RequestBody @Valid ContractTextManageDownloadAllREQ req);

    @ApiOperation(value = "合同文本管理-修改默认的签约方式")
    @PostMapping(path = "/update/signing/way/default")
    R<Void> updateDefaultSigningWay(@RequestBody @Valid ContractTextManageUpdateDefaultSigningWayREQ req);

    @ApiOperation(value = "合同文本管理-修改单个文件的签约方式")
    @PostMapping(path = "/update/signing/way/single")
    R<Void> updateSingleSigningWay(@RequestBody @Valid ContractTextManageUpdateSingleSigningWayREQ req);

    @ApiOperation(value = "合同文本管理-批量用印")
    @PostMapping(path = "/batch/sign")
    R<String> batchSign(@RequestBody @Valid ContractTextManageBatchSignREQ req);

    @ApiOperation(value = "合同文本管理-单个客户用印")
    @PostMapping(path = "/single/sign")
    R<Void> singleSign(@RequestBody @Valid ContractTextManageSingleSignREQ req);

    @ApiOperation(value = "合同文本管理-待签约批量下载")
    @PostMapping(path = "/download/wait/sign")
    R<Void> downloadWaitSign(@RequestBody @Valid ContractTextManageDownloadWaitSignREQ req);

    @ApiOperation(value = "合同文本管理-合同签署照片和视频")
    @PostMapping(path = "/sign/photos/and/videos")
    R<List<ContractTextSignInfoSignPhotosAndVideosRSP>> signPhotosAndVideos(@RequestBody @Valid ContractTextSignInfoSignPhotosAndVideosREQ req);
}
