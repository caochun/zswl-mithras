package cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.handler;


import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.projectprocess.enums.projpricing.ProjPricingMaterialsEnum;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projpricing.ProjPricingBaseInfoMapper;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.FileModuleCheck;
import cn.zswltech.mithras.application.orchestration.projectprocess.projpricing.ProjPricingService;
import cn.zswltech.mithras.workflow.flow.util.FlowUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static cn.zswltech.mithras.foundation.enums.common.RecordStatus.CLOSED;


/**
 * @ClassName AfterLeaseCollectionCheck
 * 项目评审文件检查
 * @Author jackerhe
 * @Date 2022/11/20 11:51 上午
 * @Version 1.0
 **/
@Component
public class ProjPricingCheckHandler extends FileModuleCheck {

    private static final Set<String> SPONSOR_COULD_REMOVE_TYPE_SET = new HashSet<>();

    static {
        SPONSOR_COULD_REMOVE_TYPE_SET.add(ProjPricingMaterialsEnum.BUSINESS_PRICING_APPROVAL_FORM.name());
        SPONSOR_COULD_REMOVE_TYPE_SET.add(ProjPricingMaterialsEnum.PRICING_OTHER.name());
    }

    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private ProjPricingBaseInfoMapper projPricingBaseInfoMapper;
    @Resource
    private ProjPricingService projPricingService;


    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.PROJ_PRICING.name();
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {
        ProjPricingBaseInfo projPricingBaseInfo = projPricingBaseInfoMapper.selectById(mainId);
        authCheck(projPricingBaseInfo);
        // 确定是否为发起人
        if (!Objects.equals(AccountUtil.getLoginInfo().getId(), projPricingBaseInfo.getProjSponsorUserId())) {
            // 上传尽调报告 或 其他文件
            throw new AuthCheckException("详情页只允许发起人上传，其他岗位请在审批流中上传相应文件报告");
        }
    }



    @Override
    public void checkRemove(String moduleKey, Long fileId) {
        // 先查询文档信息
        MaterialsList materialsList = materialsListService.getById(fileId);
        if (Objects.isNull(materialsList)) {
            throw new MithrasException("没有找到对应报告文件");
        }
        // 查询项目评审主数据
        ProjPricingBaseInfo projPricingBaseInfo = projPricingBaseInfoMapper.selectById(materialsList.getBelongId());
        authCheck(projPricingBaseInfo);
        if (!SPONSOR_COULD_REMOVE_TYPE_SET.contains(materialsList.getMaterialsType())) {
            throw new MithrasException("只能删除尽调报告/业务定价审批表/其他");
        }
    }

    @Override
    public void checkList(String moduleKey, Long fileId) {
        super.checkList(moduleKey, fileId);
    }

    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileId) {
        //下载权限同查看暂不限制
        super.checkDownload(moduleKey, mainId, fileId);
    }

    private void authCheck(ProjPricingBaseInfo projPricingBaseInfo) {
        if (Objects.isNull(projPricingBaseInfo)) {
            throw new MithrasException("项目定价信息不存在");
        }
        if (CLOSED.name().equals(projPricingBaseInfo.getProjPricingStatus())) {
            throw new MithrasException("该定价已关闭，不允许再修改有关信息");
        }
        // 判断是否在流程中 且 是否在发起人节点
        ProcessResp processResp = projPricingService.findRelatedProcess(projPricingBaseInfo.getId());
        if (Objects.nonNull(processResp)) {
            boolean isStartUserNode = FlowUtil.isStartUserNode(processResp);
            if (!isStartUserNode) {
                throw new AuthCheckException("该数据处于流程中，且流程不在发起人节点，不允许修改数据");
            }
        }
    }


}
