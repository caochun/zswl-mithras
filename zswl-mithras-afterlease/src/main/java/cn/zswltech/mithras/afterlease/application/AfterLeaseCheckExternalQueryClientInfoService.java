package cn.zswltech.mithras.afterlease.application;

import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckExternalQueryClientInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/11/24 15:59
 */
public interface AfterLeaseCheckExternalQueryClientInfoService
        extends IService<NewAfterLeaseCheckExternalQueryClientInfo> {

    /**
     * 根据查询任务id获取相关客户信息
     */
    List<NewAfterLeaseCheckExternalQueryClientInfo> list(Long queryId);
}
