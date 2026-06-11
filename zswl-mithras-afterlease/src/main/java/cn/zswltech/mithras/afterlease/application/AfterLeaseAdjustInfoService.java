package cn.zswltech.mithras.afterlease.application;

import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.dto.afterlease.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.afterlease.model.AfterLeaseAdjustInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author vico
 * 租后调整信息表
 * @date 2022-11-08
 */
public interface AfterLeaseAdjustInfoService extends IService<AfterLeaseAdjustInfo> {

    AfterLeaseAdjustInfoAddRSP add(AfterLeaseAdjustInfoAddREQ req);

    void modify(AfterLeaseAdjustInfoModifyREQ req);

    Page<AfterLeaseAdjustInfoListRSP> list(AfterLeaseAdjustInfoListREQ req);

    AfterLeaseAdjustDetailRSP detail(Long adjustId);

    AfterLeaseAdjustInfo adjustBaseLastByprojId(Long projId, String afterLeaseAdjustEnum);

    void effect(Long adjustId);

    void adjustCancel(AfterLeaseCancelREQ req);

    void processEnd(Long adjustId, Integer endType, Long startUserId, String processInstanceId);

    ProcessResp findRelatedProcess(Long projReviewId);

    List<ProcessResp> findRelatedProcesses(Long adjustId);

    void checkDetail(Long id);

    void checkDetail(AfterLeaseAdjustInfo baseInfo);
}