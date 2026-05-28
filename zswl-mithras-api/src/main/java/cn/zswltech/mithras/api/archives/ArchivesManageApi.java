package cn.zswltech.mithras.api.archives;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.archives.*;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * @create: 2023-02-23
 **/
@Api(tags = "归档管理-接口")
public interface ArchivesManageApi {

    @ApiOperation("立项模糊查询")
    @PostMapping("/archives/establish/query")
    R<List<ProjEstablishVagueListRSP>> vague(@RequestBody @Valid ProjEstablishVagueListREQ req);

    @ApiOperation("归档列表")
    @PostMapping("/archives/list")
    R<PageR<ArchivesListRSP>> list(@RequestBody @Valid ArchivesListREQ req);

    @ApiOperation("申请下载")
    @PostMapping("/archives/download/effect")
    R<Void> downloadEffect(@RequestBody @Valid ArchiveDownloadEffectREQ req);

    @ApiOperation("发起归档")
    @PostMapping("/archives/add")
    R<ArchivesRSP> addArchives(@RequestBody @Valid ArchivesAddREQ req);

    @ApiOperation("归档详情")
    @PostMapping("/archives/info")
    R<ArchivesInfoRSP> archivesInfo(@RequestBody @Valid ArchivesInfoREQ req);

    @ApiOperation("文件上传展示详情")
    @PostMapping("/archives/file/upload/info")
    R<List<ArchivesUploadSelectRsp>> archivesUploadInfo(@RequestBody @Valid ArchivesInfoREQ req);

    @ApiOperation("归档文件搜索")
    @PostMapping("/archives/search")
    R<List<ArchivesSearchRSP>> archivesSearch(@RequestBody @Valid ArchivesSearchREQ req);

    @ApiOperation("提醒催办")
    @PostMapping("/archives/remind")
    R<Void> remind(@RequestBody @Valid ArchivesInfoREQ req);

    @ApiOperation("提交审批")
    @PostMapping("/archives/effect")
    R<Void> archivesEffect(@RequestBody @Valid ArchivesInfoREQ req);

    @ApiOperation("申请下载基本信息")
    @PostMapping("/archives/flow/download")
    R<ArchiveDownloadFlowRSP> downloadFlowInfo(@RequestBody @Valid ArchivesFlowInfoREQ req);

    @ApiOperation("归档审批基本信息")
    @PostMapping("/archives/flow")
    R<ArchivesFlowRSP> archivesFlow(@RequestBody @Valid ArchivesInfoREQ req);

}
