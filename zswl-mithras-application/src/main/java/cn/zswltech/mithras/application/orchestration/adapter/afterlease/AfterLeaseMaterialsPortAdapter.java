package cn.zswltech.mithras.application.orchestration.adapter.afterlease;

import cn.zswl.oss.core.OssClient;
import cn.zswltech.mithras.afterlease.application.AfterLeaseMaterialSnapshot;
import cn.zswltech.mithras.afterlease.application.AfterLeaseMaterialsPort;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AfterLeaseMaterialsPortAdapter implements AfterLeaseMaterialsPort {
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private OssClient ossClient;

    @Override
    public void add(MultipartFile file, Long belongId, String materialsType, String businessType) {
        materialsListService.add(file, belongId, materialsType, businessType);
    }

    @Override
    public Long add(InputStream inputStream, String fileName, Long belongId, String materialsType, String businessType) {
        return materialsListService.add(inputStream, fileName, belongId, materialsType, businessType);
    }

    @Override
    public void download(OutputStream outputStream, List<Long> materialsIds) {
        materialsListService.download(outputStream, materialsIds);
    }

    @Override
    public List<AfterLeaseMaterialSnapshot> list(String businessType, List<String> materialsTypes, List<Long> belongIds) {
        return materialsListService.list(businessType, materialsTypes, belongIds)
                .stream()
                .map(this::toSnapshot)
                .collect(Collectors.toList());
    }

    @Override
    public AfterLeaseMaterialSnapshot getById(Long materialsId) {
        return toSnapshot(materialsListService.getById(materialsId));
    }

    @Override
    public void remove(List<Long> materialsIds) {
        materialsListService.remove(materialsIds);
    }

    @Override
    public InputStream downloadFromOss(String ossFilename) {
        return ossClient.downLoad(ossFilename);
    }

    private AfterLeaseMaterialSnapshot toSnapshot(MaterialsList materialsList) {
        if (materialsList == null) {
            return null;
        }
        return AfterLeaseMaterialSnapshot.builder()
                .id(materialsList.getId())
                .belongId(materialsList.getBelongId())
                .createBy(materialsList.getCreateBy())
                .materialsType(materialsList.getMaterialsType())
                .filename(materialsList.getFilename())
                .ossFilename(materialsList.getOssFilename())
                .createTime(materialsList.getCreateTime())
                .build();
    }
}
