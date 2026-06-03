package cn.zswltech.mithras.service.service.afterlese;

import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckExternalQueryListReq;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckExternalQueryListStatisticsRsp;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckExternalQuery;
import cn.zswltech.mithras.service.service.flow.FlowEndEventProcessor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/11/24 15:59
 */
public interface AfterLeaseCheckExternalQueryService
        extends IService<NewAfterLeaseCheckExternalQuery>, FlowEndEventProcessor {

    /**
     * 提交审批
     *
     * @param id 查询任务id
     */
    void submit(Long id);

    /**
     * 插入
     *
     * @param req 请求体
     * @return 查询任务id
     */
    Long add(NewAfterLeaseCheckExternalQuery req);

    /**
     * 查询任务列表
     *
     * @param req 请求体
     * @return 分页列表
     */
    Page<NewAfterLeaseCheckExternalQuery> list(AfterLeaseCheckExternalQueryListReq req);

    /**
     * 查询统计信息
     *
     * @param req 请求体
     * @return 统计响应体
     */
    AfterLeaseCheckExternalQueryListStatisticsRsp listStatistics(AfterLeaseCheckExternalQueryListReq req);

    /**
     * 通过项目评审id查询外部查询信息
     *
     * @param clientId 客户id
     * @return 外部查询信息列表
     */
    List<NewAfterLeaseCheckExternalQuery> listByClientId(Long clientId);

    void submitCheck(Long id);

    void submitCheck(NewAfterLeaseCheckExternalQuery query);
}
