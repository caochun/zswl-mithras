package cn.zswltech.mithras.application.orchestration.adapter.archives;

import cn.zswltech.mithras.archives.port.ArchivesSupportPort;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListRSP;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ArchivesSupportPortAdapter implements ArchivesSupportPort {
    @Resource
    private ProjEstablishBaseInfoService projEstablishBaseInfoService;
    @Resource
    private MaterialsListService materialsListService;

    @Override
    public Map<String, ProjEstablishVagueListRSP> vagueQuery(ProjEstablishVagueListREQ req) {
        return projEstablishBaseInfoService.vagueQuery(req);
    }

    @Override
    public ProjectInfo getProjectInfo(Long projectId) {
        return toProjectInfo(projEstablishBaseInfoService.getById(projectId));
    }

    @Override
    public Map<Long, ProjectInfo> getProjectInfoMap(Collection<Long> projectIds) {
        if (projectIds == null || projectIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return projEstablishBaseInfoService.listByIds(projectIds).stream()
                .map(this::toProjectInfo)
                .collect(Collectors.toMap(ProjectInfo::getId, info -> info, (left, right) -> left));
    }

    @Override
    public List<MaterialInfo> listMaterialsByBelongId(String businessType, Long belongId) {
        return materialsListService.listBy(businessType, belongId).stream()
                .map(this::toMaterialInfo)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaterialInfo> getMaterialsByIds(Collection<Long> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) {
            return Collections.emptyList();
        }
        return materialsListService.getByIds(recordIds instanceof List ? (List<Long>) recordIds : new java.util.ArrayList<>(recordIds))
                .stream()
                .map(this::toMaterialInfo)
                .collect(Collectors.toList());
    }

    private ProjectInfo toProjectInfo(ProjEstablishBaseInfo source) {
        ProjectInfo info = new ProjectInfo();
        info.setId(source.getId());
        info.setProjName(source.getProjName());
        info.setBizType(source.getBizType());
        info.setClientId(source.getClientId());
        info.setBizDeptId(source.getBizDeptId());
        info.setBizDeptLeaderId(source.getBizDeptLeaderId());
        info.setProjSponsorUserId(source.getProjSponsorUserId());
        return info;
    }

    private MaterialInfo toMaterialInfo(MaterialsList source) {
        MaterialInfo info = new MaterialInfo();
        info.setId(source.getId());
        info.setMaterialsType(source.getMaterialsType());
        info.setFilename(source.getFilename());
        info.setBelongId(source.getBelongId());
        return info;
    }
}
