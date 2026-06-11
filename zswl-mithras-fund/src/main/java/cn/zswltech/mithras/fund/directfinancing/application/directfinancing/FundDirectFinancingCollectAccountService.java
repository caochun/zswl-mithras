package cn.zswltech.mithras.fund.directfinancing.application.directfinancing;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingCollectAccountAddREQ;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingCollectAccountListREQ;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingCollectAccountListRSP;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingCollectAccountModifyREQ;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.fund.directfinancing.application.directfinancing.convert.FundDirectFinancingCollectAccountConverter;
import cn.zswltech.mithras.fund.directfinancing.mapper.model.FundDirectFinancingCollectAccount;
import cn.zswltech.mithras.fund.directfinancing.mapper.FundDirectFinancingCollectAccountMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 直接融资-对方收款账户
 * @date 2023-06-17
 */
@Service
public class FundDirectFinancingCollectAccountService
        extends ServiceImpl<FundDirectFinancingCollectAccountMapper, FundDirectFinancingCollectAccount> {
    @Resource
    private FundDirectFinancingCollectAccountConverter baseConverter;

    @Transactional(rollbackFor = Throwable.class)
    public void add(FundDirectFinancingCollectAccountAddREQ req) {
        FundDirectFinancingCollectAccount info = baseConverter.addReq2Entity(req);
        baseMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(FundDirectFinancingCollectAccountModifyREQ req) {
        FundDirectFinancingCollectAccount originalInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        FundDirectFinancingCollectAccount info = baseConverter.modifyReq2Entity(req);
        baseMapper.updateById(info);
    }

    public List<FundDirectFinancingCollectAccountListRSP> list(FundDirectFinancingCollectAccountListREQ req) {
        List<FundDirectFinancingCollectAccount> CollectAccounts = list(Wrappers.<FundDirectFinancingCollectAccount>lambdaQuery()
                .eq(FundDirectFinancingCollectAccount::getFinancingId, req.getFinancingId()));
        if (ObjectUtil.isEmpty(CollectAccounts)) {
            return Collections.emptyList();
        }
        return baseConverter.entity2ListRsp(CollectAccounts);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(Long id) {
        FundDirectFinancingCollectAccount originalInfo = baseMapper.selectById(id);
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        baseMapper.deleteById(id);
    }


}