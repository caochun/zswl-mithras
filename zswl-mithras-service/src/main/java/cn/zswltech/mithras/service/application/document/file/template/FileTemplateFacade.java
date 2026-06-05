package cn.zswltech.mithras.service.application.document.file.template;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.document.application.file.api.FileTemplateApplicationService;
import cn.zswltech.mithras.dto.file.template.*;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.service.enums.BusinessModuleEnum.FILE_TEMPLATE;

/**
 * @author yibin
 */
@Service
public class FileTemplateFacade implements FileTemplateApplicationService {
    @Resource
    private FileTemplateService fileTemplateService;

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
        return R.ok(
                new FileTemplateTypeListRSP(fileTemplateService.listTemplateTypes().stream().distinct().collect(Collectors.toList()))
        );
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
        List<FileTemplate> records = data.getRecords();
        List<FileTemplateListRSP> list = BeanUtil.copyToList(records, FileTemplateListRSP.class);
        fillOtherInfo(list);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    private void fillOtherInfo(List<FileTemplateListRSP> rspList) {
        //填充fileId
        List<Long> idList = rspList.stream().map(FileTemplateListRSP::getId).collect(Collectors.toList());
        Map<Long, Long> map = getBean(MaterialsListService.class).list(FILE_TEMPLATE.name(), null, idList).stream()
                .collect(Collectors.toMap(MaterialsList::getBelongId, MaterialsList::getId));
        rspList.forEach(e -> e.setFileId(map.get(e.getId())));
        //
        List<Long> createByList = rspList.stream().map(FileTemplateListRSP::getCreateBy).collect(Collectors.toList());
        Map<Long, String> nameMap = getBean(Id2NameService.class).sysUserId2Name(createByList);
        rspList.forEach(e -> e.setCreateByName(nameMap.get(e.getCreateBy())));
    }

    private void fillHistoryOtherInfo(List<FileTemplateHistoryListRSP> rspList) {
        //填充fileId
        List<Long> idList = rspList.stream().map(FileTemplateHistoryListRSP::getId).collect(Collectors.toList());
        Map<Long, Long> map = getBean(MaterialsListService.class).list(FILE_TEMPLATE.name(), null, idList).stream()
                .collect(Collectors.toMap(MaterialsList::getBelongId, MaterialsList::getId));
        rspList.forEach(e -> e.setFileId(map.get(e.getId())));
        //
        List<Long> createByList = rspList.stream().map(FileTemplateHistoryListRSP::getCreateBy).collect(Collectors.toList());
        Map<Long, String> nameMap = getBean(Id2NameService.class).sysUserId2Name(createByList);
        rspList.forEach(e -> e.setCreateByName(nameMap.get(e.getCreateBy())));
    }

    @Override
    public R<PageR<FileTemplateHistoryListRSP>> listTemplateHistory(FileTemplateHistoryListREQ req) {
        Page<FileTemplate> data = fileTemplateService.listTemplateHistory(req);
        List<FileTemplate> records = data.getRecords();
        List<FileTemplateHistoryListRSP> list = BeanUtil.copyToList(records, FileTemplateHistoryListRSP.class);
        fillHistoryOtherInfo(list);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
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
