package cn.zswltech.mithras.afterlease.application;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.afterlease.*;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckClientDeptInfoRSP;
import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckPlanClient;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author dingqi
 * @date 2022/11/8
 * @description
 */
public interface AfterLeaseCheckPlanClientService extends IService<NewAfterLeaseCheckPlanClient> {
    AfterLeaseCheckClientInfoRSP getInfoById(Long checkPlanClientId, String version);

    List<AfterLeaseCheckClientListRSP> listBy(AfterLeaseCheckClientListREQ req);

    void save(AfterLeaseCheckClientSaveREQ req);

    List<NewAfterLeaseCheckPlanClient> listBy(Long planId);

    List<NewAfterLeaseCheckPlanClient> listBy(Long planId, Collection<Long> projectIds);

    List<NewAfterLeaseCheckPlanClient> listByClientIds(Collection<Long> clientIds);

    List<AfterLeaseCheckClientDeptInfoRSP> listAfterLeaseCanCheckGroupByDept(Long planId);

    List<NewAfterLeaseCheckPlanClient> listToCheck(Long planId);

    AfterLeaseCheckReportVersionRSP changeReportType(Long id, String reportType);

    List<AfterLeaseCheckClientSelectRSP> queryClient(AfterLeaseCheckClientSelectREQ req);

    void submitApproval(Long checkProjectId);

    void submitApproval(Long checkProjectId, boolean isCheck);

    Map<Long, List<NewAfterLeaseCheckPlanClient>> getClientMapByPlan(Collection<Long> planIds);

    void removeProjectById(Long id);

    List<AfterLeaseCheckClientListRSP> listByLoginUser(Long planId, String version);

    void submitApprovalCheck(Long checkProjectId);

    void submitApprovalCheck(NewAfterLeaseCheckPlanClient checkPlanProject);

    void updateAnnotationIncludeNullById(NewAfterLeaseCheckPlanClient checkPlanClient);

    PageR<AfterLeaseCheckLedgerListRSP> queryLedgerList(AfterLeaseCheckLedgerListREQ req);
}
