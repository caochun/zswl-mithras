package cn.zswltech.mithras.archives.application;

import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListRSP;
import cn.zswltech.mithras.document.model.MaterialsList;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ArchivesSupportPort {
    String BUSINESS_TYPE_ARCHIVES = "ARCHIVES";

    List<String> ARCHIVES_MODEL_KEYS = java.util.Arrays.asList("ArchivesFlow");

    Map<String, ProjEstablishVagueListRSP> vagueQuery(ProjEstablishVagueListREQ req);

    List<MaterialsList> listMaterialsByBelongId(String businessType, Long belongId);

    List<MaterialsList> getMaterialsByIds(Collection<Long> recordIds);
}
