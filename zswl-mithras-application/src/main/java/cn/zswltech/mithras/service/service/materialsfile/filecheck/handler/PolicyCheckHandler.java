package cn.zswltech.mithras.service.service.materialsfile.filecheck.handler;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.auth.rule.DataAuthProcessRule;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.materialsfile.filecheck.FileModuleCheck;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @create: 2023-03-09
 **/

@Component
public class PolicyCheckHandler extends FileModuleCheck {

    @Resource
    private DataAuthProcessRule dataAuthProcessRule;


    @Override
    public void checkUpload(String moduleKey, Long mainId , String materialsType) {
        if (Objects.isNull(mainId)) {
            throw new AuthCheckException("id不能为空");
        }
        BusinessModuleEnum moduleEnum = Optional.ofNullable(BusinessModuleEnum.of(moduleKey)).orElseThrow(() -> new MithrasException(ResultMsg.UNSUPPORT_TYPE));
        dataAuthProcessRule.check(moduleEnum, mainId);
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
        return BusinessModuleEnum.POLICY.name();
    }
}
