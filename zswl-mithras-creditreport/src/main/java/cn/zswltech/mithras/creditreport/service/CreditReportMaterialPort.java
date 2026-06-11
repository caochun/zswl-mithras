package cn.zswltech.mithras.creditreport.service;

import cn.zswltech.mithras.document.mapper.model.MaterialsList;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

public interface CreditReportMaterialPort {

    List<MaterialsList> list(String businessType, List<String> materialsTypes, List<Long> belongIds);

    void removeByIds(Collection<Long> ids);

    Long add(InputStream inputStream, String fileName, Long belongId, String materialsType, String businessType);
}
