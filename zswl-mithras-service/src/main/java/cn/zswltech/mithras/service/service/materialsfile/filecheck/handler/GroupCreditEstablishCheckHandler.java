package cn.zswltech.mithras.service.service.materialsfile.filecheck.handler;


import cn.hutool.core.lang.Pair;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.mapper.GroupCreditEstablishBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.groupcreditestablish.GroupCreditEstablishService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.materialsfile.filecheck.FileModuleCheck;
import cn.zswltech.mithras.service.util.FlowUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.service.constant.ResultMsg.ONLY_BIZ_DEPT_DO;


/**
 * @ClassName AfterLeaseCollectionCheck
 * 集团授信立项文件检查
 * @Author jackerhe
 * @Date 2022/11/20 11:51 上午
 * @Version 1.0
 **/
@Component
public class GroupCreditEstablishCheckHandler extends FileModuleCheck {


    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private GroupCreditEstablishBaseInfoMapper groupCreditEstablishBaseInfoMapper;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private GroupCreditEstablishService groupCreditEstablishService;

    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.GROUP_CREDIT_ESTABLISH.name();
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {
        GroupCreditEstablishBaseInfo baseInfo = groupCreditEstablishBaseInfoMapper.selectById(mainId);
        authCheck(baseInfo);
    }



    @Override
    public void checkRemove(String moduleKey, Long fileId) {
        MaterialsList materialsList = materialsListService.getById(fileId);
        if (Objects.isNull(materialsList)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!BusinessModuleEnum.GROUP_CREDIT_ESTABLISH.name().equals(materialsList.getBusinessType())) {
            throw new MithrasException("不可操作其他模块业务数据");
        }
        GroupCreditEstablishBaseInfo baseInfo = groupCreditEstablishBaseInfoMapper.selectById(materialsList.getBelongId());
        authCheck(baseInfo);
    }

    @Override
    public void checkList(String moduleKey, Long fileId) {
        // 不做权限校验
    }

    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileId) {
        // 不做权限校验
    }

    private void authCheck(GroupCreditEstablishBaseInfo baseInfo) {
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        OrgDO bizOrgDO = sysUserService.currentUserBizDept();
        if (isNull(bizOrgDO)) {
            throw new MithrasException(ONLY_BIZ_DEPT_DO);
        }
        // 判断是否在流程中 且 是否在发起人节点
        ProcessResp processResp = groupCreditEstablishService.findRelatedProcess(baseInfo.getId());
        if (Objects.nonNull(processResp)) {
            boolean isStartUserNode = FlowUtil.isStartUserNode(processResp);
            if (!isStartUserNode) {
                throw new AuthCheckException("该数据处于流程中，且流程不在发起人节点，不允许修改数据");
            }
        }
    }
}
