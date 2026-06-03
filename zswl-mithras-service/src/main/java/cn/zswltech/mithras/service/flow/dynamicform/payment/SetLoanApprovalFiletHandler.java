package cn.zswltech.mithras.service.flow.dynamicform.payment;

import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.workflow.domain.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.service.enums.payment.LendingMaterialType;
import cn.zswltech.mithras.service.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.service.mapper.MaterialsListMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/22 20:15
 */
@Component
public class SetLoanApprovalFiletHandler implements DynamicFormHandler {
    @Resource
    private MaterialsListMapper materialsListMapper;

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        // 应运营要求去掉校验
//        Integer fileCount = materialsListMapper.selectCount(Wrappers.<MaterialsList>lambdaQuery()
//                .eq(MaterialsList::getBelongId, Long.valueOf(taskResp.getBusinessKey()))
//                .eq(MaterialsList::getMaterialsType, LendingMaterialType.LOAN_APPROVAL.name())
//                .eq(MaterialsList::getBusinessType, BusinessModuleEnum.PAYMENT.name())
//        );
//        if (fileCount == 0) {
//            throw new MithrasException("请上传" + LendingMaterialType.LOAN_APPROVAL.display);
//        }
    }

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {

    }

    @Override
    public void collect(TaskDetailRSP rsp) {

    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.payment_setLoanApprovalFile;
    }
}
