package cn.zswltech.mithras.afterlease.application.impl;

import cn.zswltech.mithras.afterlease.enums.ClientRole;
import cn.zswltech.mithras.afterlease.mapper.NewAfterLeaseCheckExternalQueryClientInfoMapper;
import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckExternalQueryClientInfo;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckExternalQueryClientInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhaozhengkang
 * @description 租后检查外部查询承租人/担保人信息
 * @date 2022-11-17
 */
@Service
public class AfterLeaseCheckExternalQueryClientInfoServiceImpl
        extends ServiceImpl<NewAfterLeaseCheckExternalQueryClientInfoMapper, NewAfterLeaseCheckExternalQueryClientInfo>
        implements AfterLeaseCheckExternalQueryClientInfoService {

    @Override
    public List<NewAfterLeaseCheckExternalQueryClientInfo> list(Long queryId) {
        String lastSql = String.format("ORDER BY field(client_role,'%s','%s','%s','%s','%s','%s','%s')", ClientRole.MAIN_LESSEE.name(), ClientRole.LESSEE.name(), ClientRole.CREDITOR.name(), ClientRole.DEBTOR.name(), ClientRole.GUARANTEE.name(), ClientRole.MORTGAGE.name(), ClientRole.PLEDGE.name());
        return baseMapper.selectList(Wrappers.<NewAfterLeaseCheckExternalQueryClientInfo>lambdaQuery()
                .eq(NewAfterLeaseCheckExternalQueryClientInfo::getQueryId, queryId)
                .last(lastSql));
    }
}