package cn.zswltech.mithras.document.file.template;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.document.materialsfile.MaterialsListQueryService;
import cn.zswltech.mithras.document.service.api.FileTemplateApplicationService;
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
import cn.zswltech.mithras.document.persistence.model.FileTemplate;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.foundation.port.UserNameResolver;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FileTemplateFacade implements FileTemplateApplicationService {

    private static final String BUSINESS_TYPE_FILE_TEMPLATE = "FILE_TEMPLATE";

    @Resource
    private FileTemplateService fileTemplateService;
    @Resource
    private MaterialsListQueryService materialsListQueryService;
    @Resource
    private UserNameResolver userNameResolver;

    @Override
    public R<Void> addTemplateType(FileTemplateTypeAddREQ req) {
        String name = req.getName().trim();
        fileTemplateService.addTemplateType(name);
        return R.ok();
    }

    @Override
    public R<Void> removeTemplateType(FileTemplateTypeRemoveREQ req) {
        String name = req.getName().trim();
        fileTemplateService.removeTemplateType(name);
        return R.ok();
    }

    @Override
    public R<FileTemplateTypeListRSP> listTemplateType() {
        return R.ok(new FileTemplateTypeListRSP(fileTemplateService.listTemplateTypes().stream().distinct().collect(Collectors.toList())));
    }

    @SneakyThrows
    @Override
    public R<Void> addTemplate(FileTemplateAddREQ req) {
        FileTemplate fileTemplate = new FileTemplate().setTemplateType(req.getTemplateType()).setFilename(req.getFile().getOriginalFilename());
        fileTemplateService.addTemplate(req.getFile().getInputStream(), fileTemplate);
        return R.ok();
    }

    @SneakyThrows
    @Override
    public R<Void> replaceTemplate(FileTemplateReplaceREQ req) {
        fileTemplateService.replaceTemplate(req.getId(), req.getFile().getInputStream());
        return R.ok();
    }

    @Override
    public R<PageR<FileTemplateListRSP>> listTemplate(FileTemplateListREQ req) {
        Page<FileTemplate> data = fileTemplateService.listTemplate(req);
        List<FileTemplateListRSP> list = BeanUtil.copyToList(data.getRecords(), FileTemplateListRSP.class);
        fillOtherInfo(list);
        return R.ok(PageR.of(list, data.getTotal(), data.getPages(), data.getCurrent(), data.getSize()));
    }

    private void fillOtherInfo(List<FileTemplateListRSP> rspList) {
        List<Long> idList = rspList.stream().map(FileTemplateListRSP::getId).collect(Collectors.toList());
        Map<Long, Long> fileIdMap = materialsListQueryService.list(BUSINESS_TYPE_FILE_TEMPLATE, null, idList).stream()
                .collect(Collectors.toMap(MaterialsList::getBelongId, MaterialsList::getId, (a, b) -> a));
        rspList.forEach(e -> e.setFileId(fileIdMap.get(e.getId())));

        List<Long> createByList = rspList.stream().map(FileTemplateListRSP::getCreateBy).collect(Collectors.toList());
        Map<Long, String> nameMap = userNameResolver.sysUserId2Name(createByList);
        rspList.forEach(e -> e.setCreateByName(nameMap.get(e.getCreateBy())));
    }

    private void fillHistoryOtherInfo(List<FileTemplateHistoryListRSP> rspList) {
        List<Long> idList = rspList.stream().map(FileTemplateHistoryListRSP::getId).collect(Collectors.toList());
        Map<Long, Long> fileIdMap = materialsListQueryService.list(BUSINESS_TYPE_FILE_TEMPLATE, null, idList).stream()
                .collect(Collectors.toMap(MaterialsList::getBelongId, MaterialsList::getId, (a, b) -> a));
        rspList.forEach(e -> e.setFileId(fileIdMap.get(e.getId())));

        List<Long> createByList = rspList.stream().map(FileTemplateHistoryListRSP::getCreateBy).collect(Collectors.toList());
        Map<Long, String> nameMap = userNameResolver.sysUserId2Name(createByList);
        rspList.forEach(e -> e.setCreateByName(nameMap.get(e.getCreateBy())));
    }

    @Override
    public R<PageR<FileTemplateHistoryListRSP>> listTemplateHistory(FileTemplateHistoryListREQ req) {
        Page<FileTemplate> data = fileTemplateService.listTemplateHistory(req);
        List<FileTemplateHistoryListRSP> list = BeanUtil.copyToList(data.getRecords(), FileTemplateHistoryListRSP.class);
        fillHistoryOtherInfo(list);
        return R.ok(PageR.of(list, data.getTotal(), data.getPages(), data.getCurrent(), data.getSize()));
    }

    @Override
    public R<Void> rollbackHistory(FileTemplateHistoryRollbackREQ req) {
        fileTemplateService.rollbackHistory(req.getId());
        return R.ok();
    }

    @Override
    public R<Void> updateTemplate(FileTemplateUpdateREQ req) {
        fileTemplateService.updateTemplate(req);
        return R.ok();
    }
}
