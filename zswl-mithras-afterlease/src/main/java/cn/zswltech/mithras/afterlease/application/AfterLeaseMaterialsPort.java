package cn.zswltech.mithras.afterlease.application;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface AfterLeaseMaterialsPort {
    void add(MultipartFile file, Long belongId, String materialsType, String businessType);

    Long add(InputStream inputStream, String fileName, Long belongId, String materialsType, String businessType);

    void download(OutputStream outputStream, List<Long> materialsIds);

    List<AfterLeaseMaterialSnapshot> list(String businessType, List<String> materialsTypes, List<Long> belongIds);

    AfterLeaseMaterialSnapshot getById(Long materialsId);

    void remove(List<Long> materialsIds);

    InputStream downloadFromOss(String ossFilename);
}
