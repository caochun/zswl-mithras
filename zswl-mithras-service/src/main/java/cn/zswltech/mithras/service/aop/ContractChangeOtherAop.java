package cn.zswltech.mithras.service.aop;

import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.contract.ContractCanChangeRSP;
import cn.zswltech.mithras.service.annotation.ContractChangeOther;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.contract.ContractChangeTypeEnum;
import cn.zswltech.mithras.service.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractService;
import cn.zswltech.mithras.service.service.materialsfile.FileService;
import cn.zswltech.mithras.service.util.FlowUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @ClassName ContractChangeOtherAop
 * @Description
 * @Author jackerhe
 * @Date 2022/8/26 3:04 下午
 * @Version 1.0
 **/
@Component
@Aspect
@Slf4j
public class ContractChangeOtherAop {

    @Autowired
    private ContractBaseInfoService baseInfoService;

    @Resource
    private ContractService contractService;

    @Resource
    private FileService fileService;

    @Pointcut("@annotation(cn.zswltech.mithras.service.annotation.ContractChangeOther)")
    public void authCheck() {

    }

    @Around("authCheck() && @annotation(contractChangeOther)")
    public Object saveCheck(ProceedingJoinPoint pjp, ContractChangeOther contractChangeOther) throws Throwable{
        AccountVO loginUser = AccountUtil.getLoginInfo();
        if (Objects.isNull(loginUser)) {
            throw new MithrasException(ResultMsg.USER_NOT_LOGIN);
        }
        String fild = contractChangeOther.contractId();
        Long contractId = null;

        Object[] args = pjp.getArgs();
        Object arg = args[contractChangeOther.paramIndex()];
        if (Objects.isNull(arg)) {
            throw new AuthCheckException("指定参数为NULL");
        }
        if (arg instanceof Long) {
            contractId = Long.valueOf(arg.toString());
        }else if(arg instanceof List){
            for (Object o : (List<?>) arg) {
                contractId = getFieldValueByName(o, fild);
                break;
            }
        } else {
            contractId = getFieldValueByName(arg, fild);
        }
        // 如果当前审批人是运营管理经办是可以修改的
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        Map<Long, List<String>> allOperateUserJob = fileService.getAllOperateUserJob(JobEnum.yunYingGuanLi);
        ContractCanChangeRSP rsp = new ContractCanChangeRSP();
        if (!allOperateUserJob.containsKey(loginInfo.getId())) {
           rsp = baseInfoService.canUpdateContractProcessStatus(Collections.singletonList(ContractProcessStatusEnum.CHANGE_UNCOMMIT.name()), contractId);
        }
        // 判断是否有流程，有的话流程在发起人状态也可以删除
        ProcessResp processResp = contractService.findRelatedProcess(contractId);
        boolean b = false;
        if (Objects.nonNull(processResp)) {
             b = FlowUtil.isStartUserNode(processResp);
        }
        //新建未提交放过
        if(ContractProcessStatusEnum.NEW_UNCOMMIT.name().equals(rsp.getStatus()) || ContractProcessStatusEnum.NEW_COMMIT.name().equals(rsp.getStatus()) ||
                ContractProcessStatusEnum.NEW_CANCEL.name().equals(rsp.getStatus()) ||
                ContractProcessStatusEnum.NEW_PASS.name().equals(rsp.getStatus()) ||
                b){
            // 放过新建
        } else if (Boolean.FALSE.equals(rsp.getCanProcess())) {
            if (contractChangeOther.twoStatus() == ContractChangeTypeEnum.EARLY_REPAYMENT
                    && Objects.nonNull(processResp)
                    && Objects.equals(loginUser.getId().toString(), processResp.getStartUserId())
                    && Objects.equals(loginUser.getId().toString(), processResp.getCurAssigneeIds())) {
                // 特殊处理
                b = true;
            } else {
                throw new MithrasException(rsp.getMessage());
            }
        } else if  (ObjectUtils.isNotEmpty(rsp.getTwoStatus()) && !contractChangeOther.twoStatus().name().equals(rsp.getTwoStatus())) {
            throw new MithrasException("合同变更处于" + ContractChangeTypeEnum.of(rsp.getTwoStatus()).display);
        }
        Object proceed = pjp.proceed();
        if(ContractProcessStatusEnum.NEW_UNCOMMIT.name().equals(rsp.getStatus()) ||
                ContractProcessStatusEnum.NEW_COMMIT.name().equals(rsp.getStatus()) ||
                ContractProcessStatusEnum.NEW_CANCEL.name().equals(rsp.getStatus()) ||
                //ContractProcessStatusEnum.NEW_PASS.name().equals(rsp.getStatus()) ||
                b){
        }else {
            // 如果当前审批人是运营管理经办是可以修改的
            if (allOperateUserJob.containsKey(loginInfo.getId())) {
                rsp.setCanProcess(Boolean.TRUE);
                return proceed;
            }
            baseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.CHANGE_UNCOMMIT.name(), contractChangeOther.twoStatus().name(), contractId);
        }
        //后置处理
        return proceed;
    }

    private Long getFieldValueByName(Object o,String fieldName) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[] {});
            Long value = (Long) method.invoke(o, new Object[] {});
            return value;
        } catch (Exception e) {
            throw new MithrasException("获取合同id失败");
        }
    }
}
