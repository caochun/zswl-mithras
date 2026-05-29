package cn.zswltech.mithras.service.service.materialsfile.filecheck.handler;


import cn.hutool.core.lang.Pair;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.auth.rule.special.ProjEstablishAuthViewRule;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.common.enums.RecordStatus;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.service.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.materialsfile.filecheck.FileModuleCheck;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishService;
import cn.zswltech.mithras.service.util.FlowUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;


@Component
public class RatingClientCheckHandler extends FileModuleCheck {

    @Resource
    private MaterialsListService materialsListService;


    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.RATING_CLIENT.name();
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {

    }



    @Override
    public void checkRemove(String moduleKey, List<Long> fileIds){
        fileIds.forEach(fileId ->{
            MaterialsList byId = materialsListService.getById(fileId);
            if (Objects.isNull(byId)) {
                throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
            }
            if (!Objects.equals(AccountUtil.getLoginInfo().getId(), byId.getCreateBy())) {
                throw new AuthCheckException("权限校验失败: 只有文件创建人可以操作删除");
            }
        });
    }

    @Override
    public void checkList(String moduleKey, Long mainId) {
    }

    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileId) {
    }

}
