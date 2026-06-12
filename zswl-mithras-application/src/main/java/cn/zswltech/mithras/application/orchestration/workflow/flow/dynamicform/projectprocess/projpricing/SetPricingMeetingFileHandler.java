package cn.zswltech.mithras.application.orchestration.workflow.flow.dynamicform.projectprocess.projpricing;

import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.projectprocess.enums.projpricing.ProjPricingMaterialsEnum;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.document.persistence.mapper.MaterialsListMapper;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author dingqi
 * @date 2023/5/18
 * @description
 */
@Component
public class SetPricingMeetingFileHandler implements DynamicFormHandler {
    private static final String PROJ_PRICING = "PROJ_PRICING";

    @Resource
    private MaterialsListMapper materialsListMapper;

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        int fileCount = materialsListMapper.selectCount(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBelongId, Long.valueOf(taskResp.getBusinessKey()))
                .eq(MaterialsList::getMaterialsType, ProjPricingMaterialsEnum.BUSINESS_PRICING_APPROVAL_MEETING_REPORT.name())
                .eq(MaterialsList::getBusinessType, PROJ_PRICING)
        );
        if (fileCount == 0) {
            throw new MithrasException("请上传" + ProjPricingMaterialsEnum.BUSINESS_PRICING_APPROVAL_MEETING_REPORT.getDisplay());
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
        return FlowDynamicFormEnum.projReview_setPricingMeetingFile;
    }
}
