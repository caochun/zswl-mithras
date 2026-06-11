package cn.zswltech.mithras.projectprocess.flow.dynamicform.projpricing;

import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.projectprocess.enums.projpricing.ProjPricingMaterialsEnum;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.document.mapper.MaterialsListMapper;
import cn.zswltech.mithras.document.mapper.model.MaterialsList;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 项目收益率审批意见书
 * @author zhaozhengkang
 * @date 2022/8/30 15:44 AM
 */
@Component
public class SetPricingApprovalFormHandler implements DynamicFormHandler {
    private static final String PROJ_PRICING = "PROJ_PRICING";

    @Resource
    private MaterialsListMapper materialsListMapper;

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        Integer fileCount = materialsListMapper.selectCount(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBelongId, Long.valueOf(taskResp.getBusinessKey()))
                .eq(MaterialsList::getMaterialsType, ProjPricingMaterialsEnum.YIELD_REVIEW_REPORT.name())
                .eq(MaterialsList::getBusinessType, PROJ_PRICING)
        );
        if (fileCount == 0) {
            throw new MithrasException("请上传" + ProjPricingMaterialsEnum.YIELD_REVIEW_REPORT.getDisplay());
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
        return FlowDynamicFormEnum.projReview_setPricingApprovalForm;
    }

}
