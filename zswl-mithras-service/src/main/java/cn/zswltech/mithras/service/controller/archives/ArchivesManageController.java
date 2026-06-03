package cn.zswltech.mithras.service.controller.archives;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.archives.ArchivesManageApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.archives.*;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListRSP;
import cn.zswltech.mithras.archives.infrastructure.persistence.model.ArchivesManagement;
import cn.zswltech.mithras.service.service.archives.ArchivesManageService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @create: 2023-02-27
 **/
@RestController
@Slf4j
public class ArchivesManageController implements ArchivesManageApi {

    @Resource
    private ArchivesManageService archivesManageService;

    @Resource
    private ProjEstablishBaseInfoService establishBaseInfoService;

    @Override
    public R<List<ProjEstablishVagueListRSP>> vague(@Valid ProjEstablishVagueListREQ req) {
        Map<String, ProjEstablishVagueListRSP> projEstablishVagueMap = establishBaseInfoService.vagueQuery(req);
        if (ObjectUtil.isEmpty(projEstablishVagueMap)) {
            return R.ok();
        }
        return R.ok(new ArrayList<>(projEstablishVagueMap.values()));
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
