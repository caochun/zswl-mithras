package cn.zswltech.mithras.service.fund.direct.service;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingPayAccountAddREQ;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingPayAccountListREQ;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingPayAccountListRSP;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingPayAccountModifyREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.fund.direct.converter.FundDirectFinancingPayAccountConverter;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingPayAccount;
import cn.zswltech.mithras.service.fund.direct.mapper.FundDirectFinancingPayAccountMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 直接融资-还款账户
 * @date 2023-06-17
 */
@Service
public class FundDirectFinancingPayAccountService
        extends ServiceImpl<FundDirectFinancingPayAccountMapper, FundDirectFinancingPayAccount> {
    @Resource
    private FundDirectFinancingPayAccountConverter baseConverter;

    @Transactional(rollbackFor = Throwable.class)
    public void add(FundDirectFinancingPayAccountAddREQ req) {
        checkExist(req.getBankAccountId(), req.getFinancingId(), null);
        FundDirectFinancingPayAccount info = baseConverter.addReq2Entity(req);
        baseMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(FundDirectFinancingPayAccountModifyREQ req) {
        checkExist(req.getBankAccountId(), req.getFinancingId(), req.getId());
        FundDirectFinancingPayAccount originalInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        FundDirectFinancingPayAccount info = baseConverter.modifyReq2Entity(req);
        baseMapper.updateById(info);
    }

    public List<FundDirectFinancingPayAccountListRSP> list(FundDirectFinancingPayAccountListREQ req) {
        List<FundDirectFinancingPayAccount> payAccounts = list(Wrappers.<FundDirectFinancingPayAccount>lambdaQuery()
                .eq(FundDirectFinancingPayAccount::getFinancingId, req.getFinancingId()));
        if (ObjectUtil.isEmpty(payAccounts)) {
            return Collections.emptyList();
        }
        return baseConverter.entity2ListRsp(payAccounts);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(Long id) {
        FundDirectFinancingPayAccount originalInfo = baseMapper.selectById(id);
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        baseMapper.deleteById(id);
    }


    public void checkExist(Long bankAccountId, Long financingId, Long id) {
        LambdaQueryWrapper<FundDirectFinancingPayAccount> query = Wrappers.lambdaQuery();
        query.eq(FundDirectFinancingPayAccount::getBankAccountId, bankAccountId);
        query.eq(FundDirectFinancingPayAccount::getFinancingId, financingId);
        query.ne(ObjectUtil.isNotEmpty(id), FundDirectFinancingPayAccount::getId, id);
        int count = this.count(query);
        Assert.isTrue(count == 0, () -> MithrasException.newException("付款账户已添加，请勿重复添加"));
    }

}