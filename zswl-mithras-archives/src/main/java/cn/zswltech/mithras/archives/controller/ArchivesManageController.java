package cn.zswltech.mithras.archives.controller;

import cn.zswltech.mithras.api.archives.ArchivesManageApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.archives.application.ArchivesManageService;
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
    private ArchivesManageService archivesManageService;

    @Override
    public R<List<ProjEstablishVagueListRSP>> vague(@Valid ProjEstablishVagueListREQ req) {
        return R.ok(archivesManageService.vague(req));
    }

    @Override
    public R<PageR<ArchivesListRSP>> list(@Valid ArchivesListREQ req) {
        return R.ok(archivesManageService.list(req));
    }

    @Override
    public R<Void> downloadEffect(@Valid ArchiveDownloadEffectREQ req) {
        return R.ok(archivesManageService.downloadEffect(req));
    }

    @Override
    public R<ArchivesRSP> addArchives(@Valid ArchivesAddREQ req) {
        return R.ok(archivesManageService.addArchives(req));
    }

    @Override
    public R<ArchivesInfoRSP> archivesInfo(@Valid ArchivesInfoREQ req) {
        return R.ok(archivesManageService.archivesInfo(req));
    }

    @Override
    public R<List<ArchivesUploadSelectRsp>> archivesUploadInfo(@Valid ArchivesInfoREQ req) {
        return R.ok(archivesManageService.archivesUploadInfo(req));
    }

    @Override
    public R<List<ArchivesSearchRSP>> archivesSearch(@Valid ArchivesSearchREQ req) {
        return R.ok(archivesManageService.archivesSearch(req));
    }

    @Override
    public R<Void> remind(@Valid ArchivesInfoREQ req) {
        return R.ok(archivesManageService.remind(req));
    }

    @Override
    public R<Void> archivesEffect(@Valid ArchivesInfoREQ req) {
        return R.ok(archivesManageService.archivesEffect(req));
    }

    @Override
    public R<ArchiveDownloadFlowRSP> downloadFlowInfo(ArchivesFlowInfoREQ req) {
        return R.ok(archivesManageService.downloadFlowInfo(req));
    }

    @Override
    public R<ArchivesFlowRSP> archivesFlow(@Valid ArchivesInfoREQ req) {
        return R.ok(archivesManageService.archivesFlow(req));
    }
}
