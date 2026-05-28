package cn.zswltech.mithras.service.service;

import cn.zswltech.mithras.dto.flow.search.ProcessTaskExtra;
import cn.zswltech.mithras.service.mapper.BizProcessDataMapper;
import cn.zswltech.mithras.service.mapper.model.BizProcessData;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;
import static cn.zswltech.mithras.service.service.flow.FlowQueryExtraService.format;

/**
 * 流程中的业务数据
 *
 * @author wangchuanhao
 * @date 2022/12/9 1:06 PM
 */
@Service
public class BizProcessDataService extends ServiceImpl<BizProcessDataMapper, BizProcessData> {

    @Resource
    private BizProcessDataMapper bizProcessDataMapper;

    public void recordAuthLevel(String processInstanceId, int level) {
        BizProcessData bizProcessData = bizProcessDataMapper.selectOne(Wrappers.<BizProcessData>lambdaQuery()
                .eq(BizProcessData::getProcessInstanceId, processInstanceId)
                .last("LIMIT 1")
        );
        if (Objects.nonNull(bizProcessData)) {
            bizProcessData.setAuthLevel(level);
            bizProcessDataMapper.updateById(bizProcessData);
            return;
        }
        bizProcessData = new BizProcessData();
        bizProcessData.setProcessInstanceId(processInstanceId);
        bizProcessData.setAuthLevel(level);
        bizProcessDataMapper.insert(bizProcessData);
    }

    public void recordBizData(String processInstanceId, Long clientId) {
        BizProcessData bizProcessData = bizProcessDataMapper.selectOne(Wrappers.<BizProcessData>lambdaQuery()
                .eq(BizProcessData::getProcessInstanceId, processInstanceId)
                .last("LIMIT 1")
        );
        if (Objects.nonNull(bizProcessData)) {
            bizProcessData.setClientId(clientId);
            bizProcessDataMapper.updateById(bizProcessData);
            return;
        }
        bizProcessData = new BizProcessData();
        bizProcessData.setProcessInstanceId(processInstanceId);
        bizProcessData.setClientId(clientId);
        bizProcessDataMapper.insert(bizProcessData);
    }

    /**
     * 任务列表 sql 客户查询
     *
     * @return
     */
    public String taskDynamicSqlClient() {
        return " AND t1.PROC_INST_ID_ IN (SELECT process_instance_id FROM biz_process_data b WHERE b.client_id = #{dynamicFilterParam.clientId})";
    }

    /**
     * 任务列表 sql 客户所属部门查询
     *
     * @return
     */
    public String taskDynamicSqlClientDept() {
        return " AND t1.PROC_INST_ID_ IN (SELECT process_instance_id FROM biz_process_data b WHERE b.client_id in (" +
                " SELECT id FROM client c WHERE c.belong_dept_id = #{dynamicFilterParam.belongDeptId}))";
    }

    /**
     * 任务列表 sql 客户所属部门与ID查询
     *
     * @return
     */
    public String taskDynamicSqlClientDeptAndId() {
        return " AND t1.PROC_INST_ID_ IN (SELECT process_instance_id FROM biz_process_data b WHERE b.client_id in (" +
                " SELECT id FROM client c WHERE c.belong_dept_id = #{dynamicFilterParam.belongDeptId} AND c.id = #{dynamicFilterParam.clientId}))";
    }

    /**
     * 流程列表 sql 客户查询
     *
     * @return
     */
    public String processDynamicSqlClient() {
        return " AND t1.ID_ IN (SELECT process_instance_id FROM biz_process_data b WHERE b.client_id = #{dynamicFilterParam.clientId})";
    }

    /**
     * 抄送流程列表 sql 客户查询
     *
     * @return
     */
    public String ccProcessDynamicSqlClient() {
        return " AND t2.ID_ IN (SELECT process_instance_id FROM biz_process_data b WHERE b.client_id = #{dynamicFilterParam.clientId})";
    }

    /**
     * 动态sql 参数 客户查询
     *
     * @param param
     * @param clientId
     */
    public void dynamicParamClient(Map<String, Object> param, Long clientId) {
        param.put("clientId", clientId);
    }

    /**
     * 动态sql 参数 客户所属部门查询
     *
     * @param param
     * @param belongDeptId
     */
    public void dynamicParamClientDept(Map<String, Object> param, Long belongDeptId) {
        param.put("belongDeptId", belongDeptId);
    }

    /**
     * 动态sql 参数 客户所属部门与ID查询
     *
     * @param param
     * @param belongDeptId
     */
    public void dynamicParamClientDeptAndId(Map<String, Object> param, Long belongDeptId, Long clientId) {
        param.put("belongDeptId", belongDeptId);
        param.put("clientId", clientId);
    }

    public String ccExtraQueryCondition(ProcessTaskExtra extra) {
        if (extra.allBlank()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(" AND t2.ID_ IN (SELECT instance_id FROM flow_query_extra WHERE 1=1 ");

        if (isNotBlank(extra.getProjName())) {
            builder.append("AND ").append("proj_name like '%").append(extra.getProjName()).append("%' ");
        }
        if (isNotBlank(extra.getProjCode())) {
            builder.append("AND ").append("proj_code like '%").append(extra.getProjCode()).append("%' ");
        }
        if (isNotBlank(extra.getContractCode())) {
            builder.append("AND ").append("contract_code like '%").append(format(extra.getContractCode())).append("%' ");
        }
        builder.append(")");
        return builder.toString();
    }

    public String extraQueryCondition(ProcessTaskExtra extra, String instanceTableAlias) {
        if (extra.allBlank()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(" AND ").append(instanceTableAlias).append(".ID_ IN (SELECT instance_id FROM flow_query_extra WHERE 1=1 ");

        if (isNotBlank(extra.getProjName())) {
            builder.append("AND ").append("(proj_name like '%").append(extra.getProjName().replace("'", "\\'").replace("\"", "\\\"")).append("%' ");
            builder.append("or json_contains(proj_name_info, '\"").append(extra.getProjName().replace("'", "\\'").replace("\"", "\\\"")).append("\"')) ");
        }
        if (isNotBlank(extra.getProjCode())) {
            builder.append("AND ").append("proj_code like '%").append(extra.getProjCode().replace("'", "\\'").replace("\"", "\\\"")).append("%' ");
        }
        if (isNotBlank(extra.getContractCode())) {
            builder.append("AND ").append("contract_code like '%").append(format(extra.getContractCode().replace("'", "\\'").replace("\"", "\\\""))).append("%' ");
        }
        builder.append(")");
        return builder.toString();
    }
}
