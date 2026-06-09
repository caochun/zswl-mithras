package cn.zswltech.mithras.service.service.materialsfile.filecheck.handler;


import cn.hutool.core.lang.Pair;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.auth.rule.special.ProjEstablishAuthViewRule;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.materialsfile.filecheck.FileModuleCheck;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishService;
import cn.zswltech.mithras.workflow.application.flow.util.FlowUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author zhaozhengkang
 */
@Component
public class LitigationCheckHandler extends FileModuleCheck {

    @Resource
    private ProjEstablishBaseInfoMapper projEstablishBaseInfoMapper;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private ProjEstablishService projEstablishService;
    @Resource
    private ProjEstablishAuthViewRule projEstablishAuthViewRule;


    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.LITIGATION_REGISTRATION.name();
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {
//        ProjEstablishBaseInfo projEstablishBaseInfo = projEstablishBaseInfoMapper.selectById(mainId);
//        authCheck(projEstablishBaseInfo);
    }



    @Override
    public void checkRemove(String moduleKey, Long fileId) {
//        MaterialsList materialsList = materialsListService.getById(fileId);
//        if (Objects.isNull(materialsList)) {
//            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
//        }
//        ProjEstablishBaseInfo projEstablishBaseInfo = projEstablishBaseInfoMapper.selectById(materialsList.getBelongId());
//        authCheck(projEstablishBaseInfo);
    }

    @Override
    public void checkList(String moduleKey, Long mainId) {
        // 立项模块特殊权限校验
//        projEstablishAuthViewRule.checkEstablish(mainId);
    }

    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileId) {
        // 立项模块特殊权限校验
//        projEstablishAuthViewRule.checkEstablish(mainId);
    }

    private void authCheck(ProjEstablishBaseInfo establishBaseInfo) {
//        if (Objects.isNull(establishBaseInfo)) {
//            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
//        }
//        if (!AccountUtil.getLoginInfo().getId().equals(establishBaseInfo.getProjSponsorUserId())) {
//            throw new AuthCheckException("非数据主办，不支持该种操作");
//        }
//        if (RecordStatus.CLOSED.name().equals(establishBaseInfo.getProjEstablishStatus())) {
//            throw new MithrasException("该立项已关闭，不允许再修改有关信息");
//        }
//        // 判断是否在流程中 且 是否在发起人节点
//        ProcessResp processResp = projEstablishService.findRelatedProcess(establishBaseInfo.getId());
//        if (Objects.nonNull(processResp)) {
//            boolean isStartUserNode = FlowUtil.isStartUserNode(processResp);
//            if (!isStartUserNode) {
//                throw new AuthCheckException("该数据处于流程中，且流程不在发起人节点，不允许修改数据");
//            }
//        }
    }
}
