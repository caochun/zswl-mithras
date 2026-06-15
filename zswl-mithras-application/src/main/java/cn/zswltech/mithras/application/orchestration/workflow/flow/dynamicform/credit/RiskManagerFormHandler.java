package cn.zswltech.mithras.application.orchestration.workflow.flow.dynamicform.credit;

import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.credit.groupcredit.establish.mapper.GroupCreditEstablishBaseInfoMapper;
import cn.zswltech.mithras.credit.groupcredit.establish.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.dto.flow.form.SelectUserREQ;
import cn.zswltech.mithras.dto.flow.form.SelectUserRSP;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class RiskManagerFormHandler implements DynamicFormHandler {

    @Resource
    private ProjEstablishBaseInfoMapper projEstablishBaseInfoMapper;
    @Resource
    private GroupCreditEstablishBaseInfoMapper groupCreditEstablishBaseInfoMapper;
    @Resource
    private Id2NameService id2NameService;

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        if (Objects.isNull(formMap.get(getType().name()))) {
            throw new MithrasException("必须补全风控经理信息");
        }
        List<SelectUserREQ> selectUserList = JSONArray.parseArray(JSON.toJSONString(formMap.get(getType().name())))
                .toJavaList(SelectUserREQ.class);
        if (CollectionUtils.isEmpty(selectUserList) || Objects.isNull(selectUserList.get(0).getUserId())) {
            throw new MithrasException("必须补全风控经理信息");
        }
    }

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        List<SelectUserREQ> selectUserList = JSONArray.parseArray(JSON.toJSONString(formMap.get(getType().name()))).toJavaList(SelectUserREQ.class);
        List<Long> userIds = selectUserList.stream().map(SelectUserREQ::getUserId).collect(Collectors.toList());
        if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.ProjEstablishCreateFlow.name(), ProcessModelTypeEnum.ProjEstablishModifyFlow.name())) {
            ProjEstablishBaseInfo baseInfo = projEstablishBaseInfoMapper.selectById(Long.valueOf(taskResp.getBusinessKey()));
            baseInfo.setRiskControlManagerId(JSON.toJSONString(userIds));
            projEstablishBaseInfoMapper.updateById(baseInfo);
        } else if (CharSequenceUtil.equalsAny(taskResp.getModelKey(), ProcessModelTypeEnum.GroupCreditEstablishCreateFlow.name(), ProcessModelTypeEnum.GroupCreditEstablishModifyFlow.name())) {
            GroupCreditEstablishBaseInfo groupCreditEstablishBaseInfo = groupCreditEstablishBaseInfoMapper.selectById(Long.valueOf(taskResp.getBusinessKey()));
            groupCreditEstablishBaseInfo.setRiskControlManagerId(JSON.toJSONString(userIds));
            groupCreditEstablishBaseInfoMapper.updateById(groupCreditEstablishBaseInfo);
        }
    }

    @Override
    public void collect(TaskDetailRSP rsp) {
        List<SelectUserRSP> formRSPList = new ArrayList<>();
        rsp.getDynamicFormData().put(getType().name(), formRSPList);
        if (CharSequenceUtil.equalsAny(rsp.getModelKey(), ProcessModelTypeEnum.ProjEstablishCreateFlow.name(), ProcessModelTypeEnum.ProjEstablishModifyFlow.name())) {
            ProjEstablishBaseInfo baseInfo = projEstablishBaseInfoMapper.selectById(Long.valueOf(rsp.getBusinessKey()));
            if (Objects.nonNull(baseInfo.getRiskControlManagerId())) {
                fillRiskManagerName(formRSPList, baseInfo.getRiskControlManagerId());
            }
        } else if (CharSequenceUtil.equalsAny(rsp.getModelKey(), ProcessModelTypeEnum.GroupCreditEstablishCreateFlow.name(), ProcessModelTypeEnum.GroupCreditEstablishModifyFlow.name())) {
            GroupCreditEstablishBaseInfo baseInfo = groupCreditEstablishBaseInfoMapper.selectById(Long.valueOf(rsp.getBusinessKey()));
            if (Objects.nonNull(baseInfo.getRiskControlManagerId())) {
                fillRiskManagerName(formRSPList, baseInfo.getRiskControlManagerId());
            }
        }
    }

    private void fillRiskManagerName(List<SelectUserRSP> formRSPList, String riskControlManagerId) {
        List<Long> longs = JSON.parseObject(riskControlManagerId, new TypeReference<List<Long>>() {
        });
        Map<Long, String> riskManagerName = id2NameService.sysUserId2Name(longs);
        riskManagerName.forEach((k, v) -> formRSPList.add(SelectUserRSP.builder()
                .userId(k)
                .value(k)
                .userName(v)
                .label(v)
                .build()));
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.projEstablish_setRiskManager;
    }
}
