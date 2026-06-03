package cn.zswltech.mithras.afterlease.application.lib;

import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.NewAfterLeaseCheckExternalQueryClientInfoLibMapper;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckExternalQueryClientInfoLib;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/12/15
 * @description
 */
@Service
public class AfterLeaseCheckExternalQueryClientInfoLibService extends ServiceImpl<NewAfterLeaseCheckExternalQueryClientInfoLibMapper, NewAfterLeaseCheckExternalQueryClientInfoLib> {
    public List<NewAfterLeaseCheckExternalQueryClientInfoLib> listByQueryIdAndVersion(Long queryId, String version) {
        LambdaQueryWrapper<NewAfterLeaseCheckExternalQueryClientInfoLib> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckExternalQueryClientInfoLib::getQueryId, queryId);
        query.eq(NewAfterLeaseCheckExternalQueryClientInfoLib::getVersion, version);
        return this.list(query);
    }
}
