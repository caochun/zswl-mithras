package cn.zswltech.mithras.service.flow.dynamicform.ftp;

import cn.hutool.core.lang.Assert;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.groupcreditreview.GroupCreditReviewMaterialsEnum;
import cn.zswltech.mithras.service.enums.projreview.ProjReviewMaterialsEnum;
import cn.zswltech.mithras.service.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;



@Component
public class SetFtpSupplementHandler implements DynamicFormHandler {
    @Resource
    private MaterialsListService materialsListService;

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        BusinessModuleEnum businessModuleEnum = Optional.ofNullable(ProcessModelTypeEnum.getByName(taskResp.getModelKey()))
                .map(ProcessModelTypeEnum::getBusinessModuleName)
                .map(BusinessModuleEnum::of)
                .orElseThrow(() -> new MithrasException("modelKey未登记，请联系管理员进行处理"));
        if (BusinessModuleEnum.FTP_QUARTERLY_GUIDANCE.equals(businessModuleEnum)) {
            LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
            query.eq(MaterialsList::getBusinessType, BusinessModuleEnum.FTP_QUARTERLY_GUIDANCE.name());
            query.eq(MaterialsList::getBelongId, Long.valueOf(taskResp.getBusinessKey()));
            query.eq(MaterialsList::getMaterialsType, "SUPPLEMENT");
            int count = materialsListService.count(query);
            Assert.isTrue(count > 0, () -> MithrasException.newException("请先上传补充资料"));
        }else if(BusinessModuleEnum.FTP_MONTHLY_GUIDANCE.equals(businessModuleEnum)){
            LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
            query.eq(MaterialsList::getBusinessType, BusinessModuleEnum.FTP_MONTHLY_GUIDANCE.name());
            query.eq(MaterialsList::getBelongId, Long.valueOf(taskResp.getBusinessKey()));
            query.eq(MaterialsList::getMaterialsType, "SUPPLEMENT");
            int count = materialsListService.count(query);
            Assert.isTrue(count > 0, () -> MithrasException.newException("请先上传补充资料"));
        }
    }

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {

    }

    @Override
    public void collect(TaskDetailRSP rsp) {

    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.ftp_setSupplement;
    }
}
