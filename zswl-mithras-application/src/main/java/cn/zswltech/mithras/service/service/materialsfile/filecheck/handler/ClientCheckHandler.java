package cn.zswltech.mithras.service.service.materialsfile.filecheck.handler;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.materialsfile.filecheck.FileModuleCheck;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class ClientCheckHandler extends FileModuleCheck {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private ClientService clientService;

    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.CLIENT.name();
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {
        super.checkUpload(moduleKey, mainId, materialsType);
    }



    @Override
    public void checkRemove(String moduleKey, Long fileId) {
        super.checkRemove(moduleKey, fileId);
    }

    @Override
    public void checkRemove(String moduleKey, List<Long> fileIds) {
        if(ObjectUtil.isEmpty(fileIds)){
            return;
        }
//        Set<Long> belongSet = materialsListService.getByIds(fileIds).stream().map(MaterialsList::getBelongId).collect(Collectors.toSet());
//        belongSet.forEach(id -> clientService.checkClientOccupy(id));
        List<MaterialsList> materialsList = materialsListService.listByIds(fileIds);
        if (CollectionUtil.isEmpty(materialsList)) {
            return;
        }
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        materialsList.forEach(e -> {
            if (!Objects.equals(currentUserId, e.getCreateBy())) {
                throw new MithrasException("只能删除自己上传的文件");
            }
        });
    }

    @Override
    public void checkList(String moduleKey, Long fileId) {
        // 客户模块文件列表不做权限校验
    }

    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileId) {
        // 客户模块文件下载不做权限校验
    }
}
