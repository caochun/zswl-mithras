package cn.zswltech.mithras.archives.interfaces;

import cn.zswltech.mithras.api.archives.ArchivesManageApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.archives.application.ArchivesManageApplicationService;
import cn.zswltech.mithras.dto.archives.ArchiveDownloadEffectREQ;
import cn.zswltech.mithras.dto.archives.ArchiveDownloadFlowRSP;
import cn.zswltech.mithras.dto.archives.ArchivesAddREQ;
import cn.zswltech.mithras.dto.archives.ArchivesFlowInfoREQ;
import cn.zswltech.mithras.dto.archives.ArchivesFlowRSP;
import cn.zswltech.mithras.dto.archives.ArchivesInfoREQ;
import cn.zswltech.mithras.dto.archives.ArchivesInfoRSP;
import cn.zswltech.mithras.dto.archives.ArchivesListREQ;
import cn.zswltech.mithras.dto.archives.ArchivesListRSP;
import cn.zswltech.mithras.dto.archives.ArchivesRSP;
import cn.zswltech.mithras.dto.archives.ArchivesSearchREQ;
import cn.zswltech.mithras.dto.archives.ArchivesSearchRSP;
import cn.zswltech.mithras.dto.archives.ArchivesUploadSelectRsp;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListRSP;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
public class ArchivesManageController implements ArchivesManageApi {

    @Resource
    private ArchivesManageApplicationService archivesManageApplicationService;

    @Override
    public R<List<ProjEstablishVagueListRSP>> vague(@Valid ProjEstablishVagueListREQ req) {
        return archivesManageApplicationService.vague(req);
    }

    @Override
    public R<PageR<ArchivesListRSP>> list(@Valid ArchivesListREQ req) {
        return archivesManageApplicationService.list(req);
    }

    @Override
    public R<Void> downloadEffect(@Valid ArchiveDownloadEffectREQ req) {
        return archivesManageApplicationService.downloadEffect(req);
    }

    @Override
    public R<ArchivesRSP> addArchives(@Valid ArchivesAddREQ req) {
        return archivesManageApplicationService.addArchives(req);
    }

    @Override
    public R<ArchivesInfoRSP> archivesInfo(@Valid ArchivesInfoREQ req) {
        return archivesManageApplicationService.archivesInfo(req);
    }

    @Override
    public R<List<ArchivesUploadSelectRsp>> archivesUploadInfo(@Valid ArchivesInfoREQ req) {
        return archivesManageApplicationService.archivesUploadInfo(req);
    }

    @Override
    public R<List<ArchivesSearchRSP>> archivesSearch(@Valid ArchivesSearchREQ req) {
        return archivesManageApplicationService.archivesSearch(req);
    }

    @Override
    public R<Void> remind(@Valid ArchivesInfoREQ req) {
        return archivesManageApplicationService.remind(req);
    }

    @Override
    public R<Void> archivesEffect(@Valid ArchivesInfoREQ req) {
        return archivesManageApplicationService.archivesEffect(req);
    }

    @Override
    public R<ArchiveDownloadFlowRSP> downloadFlowInfo(ArchivesFlowInfoREQ req) {
        return archivesManageApplicationService.downloadFlowInfo(req);
    }

    @Override
    public R<ArchivesFlowRSP> archivesFlow(@Valid ArchivesInfoREQ req) {
        return archivesManageApplicationService.archivesFlow(req);
    }
}
