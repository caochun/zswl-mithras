package cn.zswltech.mithras.service.convert.afterlease;

import cn.zswltech.mithras.dto.afterlease.*;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.afterlease.AfterLeaseCheckWayEnum;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;

import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/11/10
 * @description
 */
public class AfterLeaseCheckPlanProjectConvert {
    public static AfterLeaseCheckClientInfoRSP toAfterLeaseCheckClientInfoRSP(NewAfterLeaseCheckPlanClient checkPlanClient) {
        AfterLeaseCheckClientInfoRSP rsp = new AfterLeaseCheckClientInfoRSP();
        rsp.setId(checkPlanClient.getId());
        rsp.setPlanId(checkPlanClient.getPlanId());
        rsp.setClientId(checkPlanClient.getClientId());
        rsp.setBizDeptId(checkPlanClient.getBelongDeptId());
        rsp.setSponsorId(checkPlanClient.getBelongSponsorId());
        rsp.setApprovalStatus(checkPlanClient.getApprovalStatus());
        rsp.setCheckWay(checkPlanClient.getCheckWay());
        return rsp;
    }

    public static AfterLeaseCheckClientSelectRSP toAfterLeaseCheckClientSelectRSP(Client client, NewAfterLeaseCheckPlanClient newAfterLeaseCheckPlanClient) {
        AfterLeaseCheckClientSelectRSP rsp = new AfterLeaseCheckClientSelectRSP();
        rsp.setClientId(client.getId());
        rsp.setClientName(client.getClientName());
        rsp.setClientType(client.getClientType());
        rsp.setClientCode(client.getClientCode());
        rsp.setSponsorId(client.getBelongSponsorId());
        rsp.setBizDeptId(client.getBelongDeptId());
        rsp.setIsSelected(Objects.nonNull(newAfterLeaseCheckPlanClient));
        return rsp;
    }

    public static NewAfterLeaseCheckPlanClient toAfterLeaseCheckPlanClient(Long planId, Client client) {
        NewAfterLeaseCheckPlanClient dbModel = new NewAfterLeaseCheckPlanClient();
        dbModel.setPlanId(planId);
        dbModel.setClientId(client.getId());
        dbModel.setClientName(client.getClientName());
        dbModel.setBelongSponsorId(client.getBelongSponsorId());
        dbModel.setBelongDeptId(client.getBelongDeptId());
        dbModel.setIsCheck(YesOrNoNumberEnum.YES.getCode());
        dbModel.setCheckWay(AfterLeaseCheckWayEnum.SITE.name());
        dbModel.setApprovalStatus(ProcessStatus.UN_SUBMIT.name());
        return dbModel;
    }

    public static NewAfterLeaseCheckPlanClient toAfterLeaseCheckPlanClient(AfterLeaseCheckClientSaveREQ req, Client client) {
        NewAfterLeaseCheckPlanClient dbModel = new NewAfterLeaseCheckPlanClient();
        dbModel.setPlanId(req.getPlanId());
        dbModel.setId(req.getId());
        dbModel.setClientId(client.getId());
        dbModel.setClientName(client.getClientName());
        dbModel.setBelongSponsorId(req.getSponsorUserId());
//        dbModel.setBelongDeptId(client.getBelongDeptId());
        dbModel.setIsCheck(req.getCheck() ? YesOrNoNumberEnum.YES.getCode() : YesOrNoNumberEnum.NO.getCode());
        dbModel.setCheckWay(req.getCheckWay());
        dbModel.setRiskManagerId(req.getRiskManagerId());
        dbModel.setRiskManagerName(req.getRiskManagerName());
        return dbModel;
    }

    public static AfterLeaseCheckClientListRSP toAfterLeaseCheckPlanClientListRSP(Client client, NewAfterLeaseCheckPlanClient newAfterLeaseCheckPlanClient) {
        AfterLeaseCheckClientListRSP rsp = new AfterLeaseCheckClientListRSP();
        rsp.setId(newAfterLeaseCheckPlanClient.getId());
        rsp.setClientId(client.getId());
        rsp.setClientName(client.getClientName());
        rsp.setClientType(client.getClientType());
        rsp.setClientCode(client.getClientCode());
        rsp.setSponsorId(newAfterLeaseCheckPlanClient.getBelongSponsorId());
        rsp.setBizDeptId(newAfterLeaseCheckPlanClient.getBelongDeptId());
        rsp.setCheck(Objects.equals(newAfterLeaseCheckPlanClient.getIsCheck(), YesOrNoNumberEnum.YES.getCode()));
        rsp.setCheckWay(newAfterLeaseCheckPlanClient.getCheckWay());
        rsp.setRiskManagerId(newAfterLeaseCheckPlanClient.getRiskManagerId());
        rsp.setRiskManagerName(newAfterLeaseCheckPlanClient.getRiskManagerName());
        rsp.setApprovalStatus(newAfterLeaseCheckPlanClient.getApprovalStatus());
        rsp.setCheckTime(newAfterLeaseCheckPlanClient.getCheckTime());
        return rsp;
    }

    public static AfterLeaseCheckClientListGroupRSP toAfterLeaseCheckProjectListGroupRSP(List<AfterLeaseCheckClientListRSP> checkClientListRSPList) {
        AfterLeaseCheckClientListGroupRSP rsp = new AfterLeaseCheckClientListGroupRSP();
        // 上层只会传入同部门的list  取第一个即可
        rsp.setBizDeptId(checkClientListRSPList.get(0).getBizDeptId());
        rsp.setBizDeptName(checkClientListRSPList.get(0).getBizDeptName());
        int checkCount = 0;
        int finishCount = 0;
        for (AfterLeaseCheckClientListRSP clientListRSP : checkClientListRSPList) {
            if (clientListRSP.getCheck()) {
                checkCount++;
            }
            if (Objects.equals(clientListRSP.getApprovalStatus(), ProcessStatus.APPROVAL_PASS.name())) {
                finishCount++;
            }
        }
        rsp.setToCheckCount(checkCount);
        rsp.setFinishCount(finishCount);
        rsp.setClientList(checkClientListRSPList);
        return rsp;
    }
}
