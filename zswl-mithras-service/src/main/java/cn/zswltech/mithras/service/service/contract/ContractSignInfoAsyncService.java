package cn.zswltech.mithras.service.service.contract;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.ProcessHistoryReq;
import cn.zswltech.flow.core.domain.resp.ProcessHistoryResp;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.text.ContractTextStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.text.SigningWayEnum;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractSignInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTextManage;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.service.service.contract.text.ContractTextManageService;
import cn.zswltech.mithras.service.service.contract.text.ContractTextSignInfoService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2024/11/18
 * @description
 */
@Slf4j
@Service
public class ContractSignInfoAsyncService {

    @Resource
    protected MaterialsListService materialsListService;
    @Resource
    private ContractTextSignInfoService contractTextSignInfoService;
    @Resource
    private FlowProcessApiService flowProcessApiService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ContractSignInfoService contractSignInfoService;
    @Resource
    protected ContractBaseInfoService contractBaseInfoService;

    @Async
    public void asyncInvokeSettle(Long contractId, Long fileId, ContractTextManageService contractTextManageService, ContractTextManage textManage, String processInstanceId, String modelKey, String accountNo) {
        log.info("所有权转移证书、实际租金表准备开始签约........");
        //用印接口提交人
        String account = "";
        if (Objects.equals(modelKey, ProcessModelTypeEnum.ContractNormalSettleFlow.name())) {
            // 查最近一次运营经理操作的操作人
            account = getYunYingGuanLi(processInstanceId);
            // 将所有的签约状态改回未签约
            contractSignInfoService.lambdaUpdate()
                    .set(ContractSignInfo::getSignStatus, ContractTextStatusEnum.NO_SIGNED.name())
                    .set(ContractSignInfo::getSignFinishTime, null)
                    .set(ContractSignInfo::getSignWay, SigningWayEnum.LEASE_ONLINE_SIGN.name())
                    .eq(ContractSignInfo::getContractId, contractId)
                    .eq(ContractSignInfo::getFileId, fileId)
                    .update();
        } else {
            account = accountNo;
        }
        //转换当前文件
        Long signId = contractTextManageService.transformFile(contractId, textManage.getId(), fileId);
        log.info("合同结清合同文本管理模块开始转换文件结束........");
        //针对文件签约
        contractTextSignInfoService.fileSign(signId, account, contractId, modelKey);
    }

    //针对存量没有合同文本管理的数据进行签约
    @Async
    public void asyncInvokeSettle(Long contractId, Long fileId, String processInstanceId, String modelKey, String accountNo) {
        log.info("文件准备开始签约........");
        //用印接口提交人
        String account = "";
        if (Objects.equals(modelKey, ProcessModelTypeEnum.ContractNormalSettleFlow.name())) {
            // 查最近一次运营经理操作的操作人
            account = getYunYingGuanLi(processInstanceId);
        } else {
            account = accountNo;
        }
        //针对存量合同的文件签约
        contractTextSignInfoService.existFileSign(fileId, account, contractId, modelKey);
    }

    //查最近一次运营经理操作的操作人
    private String getYunYingGuanLi(String processInstanceId) {
        String accountId = "";
        ProcessHistoryReq historyReq = new ProcessHistoryReq();
        historyReq.setPageIndex(1);
        historyReq.setPageSize(Integer.MAX_VALUE);
        historyReq.setProcessInstanceId(processInstanceId);
        cn.zswltech.flow.core.util.Page<ProcessHistoryResp> history = flowProcessApiService.history(historyReq);
        if (ObjectUtil.isNotEmpty(history) && ObjectUtil.isNotEmpty(history.getContents())) {
            for (ProcessHistoryResp content : history.getContents()) {
                if ("userTask_yunYingGuanLi".equals(content.getTaskActivityId())) {
                    accountId = content.getOperatorId();
                }
            }
        }
        log.info("最近一次运营经理操作的操作人accountId:" + accountId);
        String account = "";
        if (StringUtils.isNotEmpty(accountId)) {
            UserDO user = sysUserService.getSpecificUser(Long.parseLong(accountId));
            account = user.getAccount();
        }
        log.info("最近一次运营经理操作的操作人:" + account);
        return account;
    }
}
