package cn.zswltech.mithras.application.orchestration.adapter.associationreport;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.associationreport.application.AssociationReportTemplateFilePort;
import cn.zswltech.mithras.document.persistence.model.FileTemplate;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.document.file.template.FileTemplateService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Component
public class AssociationReportTemplateFilePortAdapter implements AssociationReportTemplateFilePort {

    private static final String BUSINESS_TYPE_FILE_TEMPLATE = "FILE_TEMPLATE";

    @Resource
    private FileTemplateService fileTemplateService;
    @Resource
    private MaterialsListService materialsListService;

    @Override
    public String getTemplateFileUrl(String templateType, String filename) {
        FileTemplate fileTemplate = fileTemplateService.getTemplateRecord(templateType, filename);
        if (Objects.isNull(fileTemplate)) {
            throw new MithrasException("模板不存在");
        }
        List<MaterialsList> materialsList = materialsListService.list(BUSINESS_TYPE_FILE_TEMPLATE, null, ListUtil.of(fileTemplate.getId()));
        MaterialsList materials = materialsList.get(0);
        return materialsListService.download(materials.getId()).getFileUrl();
    }
}
