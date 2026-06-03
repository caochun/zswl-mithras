package cn.zswltech.mithras.afterlease.application.lib;

import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.lib.NewAfterLeaseCheckPlanClientLibMapper;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckPlanClientLib;
import cn.zswltech.mithras.afterlease.application.lib.handler.impl.AfterLeaseCheckPlanClientLibHandler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/11/15
 * @description
 */
@Service
public class AfterLeaseCheckPlanClientLibService extends ServiceImpl<NewAfterLeaseCheckPlanClientLibMapper, NewAfterLeaseCheckPlanClientLib> {

    @Resource
    private AfterLeaseCheckPlanClientLibHandler checkPlanProjectLibHandler;

    public List<NewAfterLeaseCheckPlanClient> listByPlanIdAndVersion(Long planId, String version) {
        LambdaQueryWrapper<NewAfterLeaseCheckPlanClientLib> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckPlanClient::getPlanId, planId);
        query.eq(NewAfterLeaseCheckPlanClientLib::getVersion, version);
        return this.list(query).stream().map(checkPlanProjectLibHandler::actualLib2Entity).collect(Collectors.toList());
    }
}
