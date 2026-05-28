package cn.zswltech.mithras.report.flow.node;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowVariableApiService;
import cn.zswltech.flow.core.extension.event.NodeEndEvent;
import cn.zswltech.flow.core.extension.event.context.NodeCommonContext;
import cn.zswltech.mithras.report.enums.biz.TableTypeEnum;
import cn.zswltech.mithras.report.enums.common.ReportState;
import cn.zswltech.mithras.report.mapper.base.model.CrAccountBase;
import cn.zswltech.mithras.report.mapper.draft.model.CrAccountDraft;
import cn.zswltech.mithras.report.mapper.model.CrModifyDataSnap;
import cn.zswltech.mithras.report.service.BatchRecordService;
import cn.zswltech.mithras.report.service.CrModifyDataSnapService;
import cn.zswltech.mithras.report.service.draft.CrAccountDraftService;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @date 2024/4/23/17:02
 * @description
 */
@Component
public class ReportProcessNodeEndListener implements ApplicationListener<NodeEndEvent> {

    @Resource
    private BatchRecordService batchRecordService;
    @Resource
    private FlowVariableApiService flowVariableApiService;
    @Resource
    private CrModifyDataSnapService crModifyDataSnapService;
    @Resource
    private CrAccountDraftService crAccountDraftService;

    @Override
    public void onApplicationEvent(NodeEndEvent nodeEndEvent) {
        NodeCommonContext nodeCommonContext = nodeEndEvent.getNodeCommonContext();

        //征信报送首席风险官之前的流程变量设置
        if (CharSequenceUtil.equals(ProcessModelTypeEnum.CreditReportFlow.name(), nodeCommonContext.getModelKey())
                && ("userTask_riskDeptMaster".equals(nodeCommonContext.getActivityId()))) {
//            //找到当前审批流中的逾期和五级分类，不为空需要首席风险官进行审批
//            FiveClassListREQ fiveClassListReq = new FiveClassListREQ();
//            fiveClassListReq.setChannel(QueryChannel.PROC.name());
//            fiveClassListReq.setProcBusinessKey(nodeCommonContext.getBusinessKey());
//            List<Map<String, DiffValue>> fiveClassList = fiveClassAggService.list(fiveClassListReq);
//            Map<String, Object> map = new HashMap<>();
//            String isContainsFiveClassOrOverdue = "isContainsFiveClassOrOverdue";
//            if (CollUtil.isNotEmpty(fiveClassList)) {
//                map.put(isContainsFiveClassOrOverdue, Boolean.TRUE);
//                flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), map);
//                return;
//            }
//
//            //逾期
//            OverdueRecordListREQ overdueRecordListReq = new OverdueRecordListREQ();
//            overdueRecordListReq.setChannel(QueryChannel.PROC.name());
//            overdueRecordListReq.setProcBusinessKey(nodeCommonContext.getBusinessKey());
//            PageR<Map<String, DiffValue>> pageR = overdueRecordAggService.list(overdueRecordListReq);
//            if (CollUtil.isNotEmpty(pageR.getList())){
//                map.put(isContainsFiveClassOrOverdue, Boolean.TRUE);
//                flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), map);
//                return;
//            }
//
//            map.put(isContainsFiveClassOrOverdue, Boolean.FALSE);
//            flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), map);

            // 如果前端没有删除或者修改征信数据的，审批只要经过栋哥就行了。如果有删除或者修改征信数据的，审批要经过栋哥和李总。
            List<CrModifyDataSnap> modifyDataSnaps = crModifyDataSnapService.list(Wrappers.<CrModifyDataSnap>lambdaQuery()
                    .eq(CrModifyDataSnap::getProcBusinessKey, nodeCommonContext.getBusinessKey()));
            // 过滤掉账户表中河南骏化项目
            modifyDataSnaps = modifyDataSnaps.stream()
                    .map(e -> {
                        if (Objects.equals(e.getTableType(), TableTypeEnum.ACCOUNT.name())) {
                            // 序列化后，检查
                            CrAccountDraft bean = JSONUtil.toBean(e.getDataMap(), CrAccountDraft.class);
                            if (Objects.equals(bean.getClientName(), "河南骏化发展股份有限公司")) {
                                return null;
                            }
                        }
                        return e;
                    }).filter(Objects::nonNull).collect(Collectors.toList());
            Map<String, Object> map = new HashMap<>();
            if (CollUtil.isNotEmpty(modifyDataSnaps)) {
                map.put("isContainsFiveClassOrOverdue", Boolean.TRUE);
            } else {
                // 再看是否有修改是否报送开关的数据(暂时先排除骏化项目)
                List<CrAccountDraft> accountDrafts = crAccountDraftService.list(Wrappers.<CrAccountDraft>lambdaQuery()
                        .eq(CrAccountDraft::getProcBusinessKey, nodeCommonContext.getBusinessKey())
                        .eq(CrAccountBase::getRepayRate, ReportState.TO_BE_REPORT.name()));
                if (CollUtil.isEmpty(accountDrafts)) {
                    map.put("isContainsFiveClassOrOverdue", Boolean.FALSE);
                } else {
                    // 比较前后报送开关是否存在变化
                    boolean isChanged = accountDrafts.stream().filter(e -> Objects.nonNull(e.getReportFlagInit()))
                            // 后续如果需要正常报送，直接将下面这行代码拿掉即可
                            .filter(e -> !Objects.equals(e.getClientName(), "河南骏化发展股份有限公司"))
                            .anyMatch(e -> !e.getReportFlagInit().equals(e.getReportFlag()));
                    if (isChanged) {
                        map.put("isContainsFiveClassOrOverdue", Boolean.TRUE);
                    } else {
                        map.put("isContainsFiveClassOrOverdue", Boolean.FALSE);
                    }
                }
            }
            flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), map);
        }
    }
}
