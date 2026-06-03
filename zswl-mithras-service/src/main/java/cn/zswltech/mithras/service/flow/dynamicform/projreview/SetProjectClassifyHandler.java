package cn.zswltech.mithras.service.flow.dynamicform.projreview;

import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.form.SetProjectClassifyREQ;
import cn.zswltech.mithras.dto.flow.form.SetProjectClassifyRSP;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.workflow.domain.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * 项目评审流程，评审会秘书汇票节点，设置项目分类
 *
 * @author wangchuanhao
 * @date 2022/9/8 2:36 PM
 */
@Component
public class SetProjectClassifyHandler implements DynamicFormHandler {

    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        if (!CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.ProjReviewCreateFlow.name(), ProcessModelTypeEnum.ProjReviewModifyFlow.name())) {
            return;
        }
        if (Objects.isNull(formMap.get(getType().name()))) {
            throw new MithrasException("必须补全项目分类信息");
        }
        SetProjectClassifyREQ projectClassifyREQ = JSONObject.parseObject(JSON.toJSONString(formMap.get(getType().name())), SetProjectClassifyREQ.class);
        if (Objects.isNull(projectClassifyREQ) || StringUtils.isBlank(projectClassifyREQ.getProjectClassify())) {
            throw new MithrasException("必须补全项目分类信息");
        }
    }

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        if (!CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.ProjReviewCreateFlow.name(), ProcessModelTypeEnum.ProjReviewModifyFlow.name())) {
            return;
        }
        SetProjectClassifyREQ projectClassifyREQ = JSONObject.parseObject(JSON.toJSONString(formMap.get(getType().name())), SetProjectClassifyREQ.class);
        ProjReviewBaseInfo baseInfo = projReviewBaseInfoMapper.selectById(Long.valueOf(taskResp.getBusinessKey()));
        baseInfo.setProjectClassify(projectClassifyREQ.getProjectClassify());
        projReviewBaseInfoMapper.updateById(baseInfo);
    }

    @Override
    public void collect(TaskDetailRSP rsp) {
        if (!CharSequenceUtil.equalsAny(rsp.getModelKey(), ProcessModelTypeEnum.ProjReviewCreateFlow.name(), ProcessModelTypeEnum.ProjReviewModifyFlow.name())) {
            return;
        }
        ProjReviewBaseInfo baseInfo = projReviewBaseInfoMapper.selectById(Long.valueOf(rsp.getBusinessKey()));
        SetProjectClassifyRSP setProjectClassifyRSP = new SetProjectClassifyRSP();
        setProjectClassifyRSP.setProjectClassify(baseInfo.getProjectClassify());
        rsp.getDynamicFormData().put(getType().name(), setProjectClassifyRSP);
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.projReview_setProjectClassify;
    }

}
