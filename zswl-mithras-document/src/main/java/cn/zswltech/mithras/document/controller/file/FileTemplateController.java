package cn.zswltech.mithras.document.controller.file;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.file.template.FileTemplateApi;
import cn.zswltech.mithras.document.application.FileTemplateApplicationService;
import cn.zswltech.mithras.dto.file.template.FileTemplateAddREQ;
import cn.zswltech.mithras.dto.file.template.FileTemplateHistoryListREQ;
import cn.zswltech.mithras.dto.file.template.FileTemplateHistoryListRSP;
import cn.zswltech.mithras.dto.file.template.FileTemplateHistoryRollbackREQ;
import cn.zswltech.mithras.dto.file.template.FileTemplateListREQ;
import cn.zswltech.mithras.dto.file.template.FileTemplateListRSP;
import cn.zswltech.mithras.dto.file.template.FileTemplateReplaceREQ;
import cn.zswltech.mithras.dto.file.template.FileTemplateTypeAddREQ;
import cn.zswltech.mithras.dto.file.template.FileTemplateTypeListRSP;
import cn.zswltech.mithras.dto.file.template.FileTemplateTypeRemoveREQ;
import cn.zswltech.mithras.dto.file.template.FileTemplateUpdateREQ;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
public class FileTemplateController implements FileTemplateApi {

    @Resource
    private FileTemplateApplicationService fileTemplateApplicationService;

    @Override
    public R<Void> addTemplateType(@Valid FileTemplateTypeAddREQ req) {
        return fileTemplateApplicationService.addTemplateType(req);
    }

    @Override
    public R<Void> removeTemplateType(@Valid FileTemplateTypeRemoveREQ req) {
        return fileTemplateApplicationService.removeTemplateType(req);
    }

    @Override
    public R<FileTemplateTypeListRSP> listTemplateType() {
        return fileTemplateApplicationService.listTemplateType();
    }

    @Override
    public R<Void> addTemplate(@Valid FileTemplateAddREQ req) {
        return fileTemplateApplicationService.addTemplate(req);
    }

    @Override
    public R<Void> replaceTemplate(@Valid FileTemplateReplaceREQ req) {
        return fileTemplateApplicationService.replaceTemplate(req);
    }

    @Override
    public R<PageR<FileTemplateListRSP>> listTemplate(@Valid FileTemplateListREQ req) {
        return fileTemplateApplicationService.listTemplate(req);
    }

    @Override
    public R<PageR<FileTemplateHistoryListRSP>> listTemplateHistory(@Valid FileTemplateHistoryListREQ req) {
        return fileTemplateApplicationService.listTemplateHistory(req);
    }

    @Override
    public R<Void> rollbackHistory(@Valid FileTemplateHistoryRollbackREQ req) {
        return fileTemplateApplicationService.rollbackHistory(req);
    }

    @Override
    public R<Void> updateTemplate(@Valid FileTemplateUpdateREQ req) {
        return fileTemplateApplicationService.updateTemplate(req);
    }
}
