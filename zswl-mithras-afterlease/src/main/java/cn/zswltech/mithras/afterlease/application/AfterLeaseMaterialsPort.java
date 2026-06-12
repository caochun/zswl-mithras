package cn.zswltech.mithras.afterlease.application;

import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface AfterLeaseMaterialsPort {
    void add(MultipartFile file, Long belongId, String materialsType, String businessType);

    Long add(InputStream inputStream, String fileName, Long belongId, String materialsType, String businessType);

    void download(OutputStream outputStream, List<Long> materialsIds);

    List<MaterialsList> list(String businessType, List<String> materialsTypes, List<Long> belongIds);

    MaterialsList getById(Long materialsId);

    void remove(List<Long> materialsIds);

    InputStream downloadFromOss(String ossFilename);
}
