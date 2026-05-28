package cn.zswltech.mithras.service.service.afterlese;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.afterlease.*;
import cn.zswltech.mithras.service.enums.afterlease.AfterLeaseCheckReportTypeEnum;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckPlanBase;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/8
 * @description
 */
public interface AfterLeaseCheckPlanBaseService extends IService<NewAfterLeaseCheckPlanBase> {
    void close(Long id);

    Long add(AfterLeaseCheckPlanBaseAddREQ req);

    Long modify(AfterLeaseCheckPlanBaseModifyREQ req);

    void publish(AfterLeaseCheckPlanPublishREQ req);

    void finish(Long planId);

    AfterLeaseCheckPlanDetailRSP detail(Long planId, String version);

    AfterLeaseCheckPlanCommonlyDetailRSP commonlyDetail(AfterLeaseCheckPlanCommonlyDetailREQ req);

    PageR<AfterLeaseCheckPlanListRSP> pageList(AfterLeaseCheckPlanListREQ req);

    void startChecking(Long id, AfterLeaseCheckReportTypeEnum reportType);

    void generateCheckReportMetaList(Long id, AfterLeaseCheckReportTypeEnum reportType);

    void changeModifyStatus(Long planId);

    String formatPlanTime(NewAfterLeaseCheckPlanBase newAfterLeaseCheckPlanBase);

    void publishCheck(Long id);

    void publishCheck(NewAfterLeaseCheckPlanBase plan);

    /**
     * 租后检查审批快照前置信息查询接口
     *
     * @param request 必要的请求参数
     * @return AfterLeaseAuditFlowPreRSP 结果
     */
    AfterLeaseAuditFlowPreRSP afterLeaseAuditPreSelect(AfterLeaseAuditFlowPreREQ request);

    /**
     * 实时计算客户检查时间间隔，检查形式及
     **/
    List<AfterLeaseClientPlanRSP> getClientPlan(Long clientId, LocalDate riskExposureDate);

    //资产管理策略查询
    PageR<AfterLeaseAssetStrategyRSP> afterLeaseAssetStrategy(AfterLeaseAssetStrategyREQ request);

    void afterLeaseAssetStrategyModify(AfterLeaseAssetStrategyModifyREQ req);

    void clientFirstCheck(Long clientId);

    PageR<AfterLeaseAssetStrategyRSP> afterLeaseAssetStrategys(AfterLeaseAssetStrategyREQ request);

    List<AfterLeaseClientPlanRSP> getNextCheckPlan(Long clientId, Long todoId);
}
