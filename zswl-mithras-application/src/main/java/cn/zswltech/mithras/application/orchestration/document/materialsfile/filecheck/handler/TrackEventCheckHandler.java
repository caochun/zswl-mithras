package cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.handler;

import cn.hutool.core.lang.Pair;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.application.orchestration.auth.rule.DataAuthProcessRule;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.FileModuleCheck;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Component
public class TrackEventCheckHandler extends FileModuleCheck {


    @Override
    public void checkUpload(String moduleKey, Long mainId , String materialsType) {
        // 该业务流程中可上传文件
    }



    @Override
    public void checkRemove(String moduleKey, List<Long> fileIds){
        fileIds.forEach(fileId ->{
            MaterialsList byId = materialsListService.getById(fileId);
            if(byId == null){
                return;
            }
            if (!Objects.equals(AccountUtil.getLoginInfo().getId(), byId.getCreateBy())) {
                throw new AuthCheckException("权限校验失败: 只有文件创建人可以操作删除");
            }
        });

    }

    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileIds) {

    }

    @Override
    public void checkList(String moduleKey, Long mainId) {
        BusinessModuleEnum businessModuleEnum = Optional.ofNullable(BusinessModuleEnum.of(moduleKey))
                .orElseThrow(() -> new MithrasException(ResultMsg.UNSUPPORT_TYPE));
    }

    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.TRACK_EVENT.name();
    }
}
