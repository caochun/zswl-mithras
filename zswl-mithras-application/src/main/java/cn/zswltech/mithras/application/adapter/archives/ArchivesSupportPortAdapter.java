package cn.zswltech.mithras.application.adapter.archives;

import cn.zswltech.mithras.archives.application.ArchivesSupportPort;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListRSP;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    public List<MaterialsList> listMaterialsByBelongId(String businessType, Long belongId) {
        return materialsListService.listBy(businessType, belongId);
    }

    @Override
    public List<MaterialsList> getMaterialsByIds(Collection<Long> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) {
            return Collections.emptyList();
        }
        return materialsListService.getByIds(recordIds instanceof List ? (List<Long>) recordIds : new java.util.ArrayList<>(recordIds));
    }
}
