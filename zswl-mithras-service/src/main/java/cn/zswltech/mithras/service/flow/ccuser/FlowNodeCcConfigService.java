package cn.zswltech.mithras.service.flow.ccuser;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.flow.core.api.FlowVariableApiService;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.mapper.flow.FlowNodeCcConfigMapper;
import cn.zswltech.mithras.service.mapper.flow.model.FlowNodeCcConfig;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.service.enums.ProcessModelTypeEnum.ContractCreateFlow;
import static cn.zswltech.mithras.service.enums.ProcessModelTypeEnum.ContractModifyFlow;
import static cn.zswltech.mithras.service.enums.ProcessModelTypeEnum.ContractNormalSettleFlow;

/**
 * @description: 流程默认节点 抄送
 * @author: huangping
 * @date: 2025/11/20  11:39
 * @version: 1.0
 */
@Slf4j
@Service
public class FlowNodeCcConfigService extends ServiceImpl<FlowNodeCcConfigMapper, FlowNodeCcConfig> {

    private static final String JOB = "job";
    private static final String USER = "user";
    private static final String PROJ_REVIW_NEED_BORADAPPROVE = "projReviewNeedBoradApprove";
    private static final String USERTASK_GENERALMANAGER = "userTask_generalManager";

    @Resource(name = "userServiceAPI")
    private UserService userServiceAPI;

    @Resource
    private FlowVariableApiService variableApiService;


    /**
     * 有特殊处理逻辑的获取抄送用户
     *
     * @param processInstanceId 流程id
     * @param flowKey           流程Key
     * @param nodeKey           流程节点key
     * @return 抄送用户id
     */
    public List<Long> getCcUesrList(String processInstanceId, String flowKey, String nodeKey,String businessKey) {
        log.info("getCcUesrList procId:{},flowKey:{},nodeKey:{},busKey",processInstanceId,flowKey,nodeKey,businessKey);
        Long deptId = null;
        //合同创建+合同变更 总经理需要判断 是否过董事会  取流程参数进行判断
        if (Arrays.asList(ContractCreateFlow.name(), ContractModifyFlow.name()).contains(flowKey) && USERTASK_GENERALMANAGER.equals(nodeKey)) {
            //获取是否项目评审需要董事会审批
            Map<String, Object> paramMap = variableApiService.getVariables(processInstanceId, Collections.singletonList(PROJ_REVIW_NEED_BORADAPPROVE));
            if (CollectionUtil.isEmpty(paramMap)) {
                return Collections.emptyList();
            }
            Object obj = paramMap.get(PROJ_REVIW_NEED_BORADAPPROVE);
            // 是否过董事会 为是，无需赋值
            if (Objects.equals(Boolean.TRUE, obj)) {
                return Collections.emptyList();
            }
        }
        //合同正常结清
        if (ContractNormalSettleFlow.name().equals(flowKey)){
            ContractBaseInfo baseInfo = getBean(ContractBaseInfoService.class).getById(businessKey);
            deptId=baseInfo.getBizDeptId();
        }
        //假如流程有改动 需要兼容新旧流程，使用简单方案，就是新流程节点id修改一下，不要和之前的重复即可
        List<Long> ccUesrList = getCcUesrListWithConfig(flowKey, nodeKey, deptId);
        return ccUesrList;
    }


    /**
     * @param flowKey 流程Key
     * @param nodeKey 流程节点key
     * @return 抄送用户id
     */
    public List<Long> getCcUesrListWithConfig(String flowKey, String nodeKey, Long deptId) {

        log.info("getCcUesrList  flowKey={},nodeKey={}", flowKey, nodeKey);
        List<Long> resultUserIds = new ArrayList<>();
        if (StringUtils.isBlank(flowKey) || StringUtils.isBlank(nodeKey)) {
            return resultUserIds;
        }
        LambdaQueryWrapper<FlowNodeCcConfig> query = Wrappers.lambdaQuery();
        query.eq(FlowNodeCcConfig::getFlowKey, flowKey);
        query.eq(FlowNodeCcConfig::getNodeKey, nodeKey);
        List<FlowNodeCcConfig> flowNodeCcConfigs = this.getBaseMapper().selectList(query);
        boolean emptyFlag = CollectionUtils.isEmpty(flowNodeCcConfigs);
        log.info("flowNodeCcConfigs 是否为空判断结果:{}", emptyFlag);
        if (emptyFlag) {
            return resultUserIds;
        }
        //20251210新增排序规则  配置需要按规则去排序
        flowNodeCcConfigs.sort(Comparator.comparingInt(FlowNodeCcConfig::getSort));
        for (FlowNodeCcConfig flowNodeCcConfig : flowNodeCcConfigs) {
            getUserByConfig(flowNodeCcConfig, resultUserIds, deptId);
        }
        return resultUserIds;
    }


    private void getUserByConfig(FlowNodeCcConfig flowNodeCcConfig, List<Long> resultUserIds, Long deptId) {
        String objectType = flowNodeCcConfig.getObjectType();
        //这里配置的是accountNo
        String objectIds = flowNodeCcConfig.getObjectIds();
        if (StringUtils.isBlank(objectType) || StringUtils.isBlank(objectIds)) {
            return;
        }
        //用户配置
        if (USER.equals(objectType)) {
            String[] accountNoArray = objectIds.replaceAll("，", ",").split("\\s*,\\s*");
            for (String accountNo : accountNoArray) {
                UserDO userDO = userServiceAPI.queryByAccount(accountNo);
                if (Objects.nonNull(userDO) && !resultUserIds.contains(userDO.getId())) {
                    resultUserIds.add(userDO.getId());
                }
            }
            return;
        }
        //按顺序查询
        String[] jobArray = objectIds.replaceAll("，", ",").split("\\s*,\\s*");
        for (String job : jobArray) {
            List<UserDO> userDOList;
            //如果是分管领导需要加部门去查找
            if (Objects.equals(job, JobEnum.leaderincharge.name())) {
                userDOList = userServiceAPI.jobUsers(deptId, JobEnum.leaderincharge.name());
            }else {
                userDOList = userServiceAPI.getUsersByjobcod(job);
            }
            if (CollectionUtils.isEmpty(userDOList)) {
                continue;
            }
            for (UserDO userDO : userDOList) {
                if (Objects.nonNull(userDO) && !resultUserIds.contains(userDO.getId())) {
                    resultUserIds.add(userDO.getId());
                }
            }
        }
    }


    /**
     *
     * @param str 待转换字符串
     * @return 转换成功返回Long，失败返回null
     */
    private Long parseLongSafe(String str) {
        try {
            return Long.parseLong(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
