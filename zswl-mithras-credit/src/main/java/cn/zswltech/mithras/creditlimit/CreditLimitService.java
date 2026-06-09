package cn.zswltech.mithras.creditlimit.service;

import cn.zswltech.mithras.creditlimit.enums.CreditLimitStatusEnum;
import cn.zswltech.mithras.creditlimit.mapper.CreditLimitMapper;
import cn.zswltech.mithras.creditlimit.mapper.model.CreditLimit;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2024/9/3
 * @description
 */
@Slf4j
@Service
public class CreditLimitService extends ServiceImpl<CreditLimitMapper, CreditLimit> {
    public CreditLimit findByThreeKeys(String bizType, String grantingSubjectKey, String bizSourceKey) {
        LambdaQueryWrapper<CreditLimit> query = Wrappers.lambdaQuery();
        query.eq(CreditLimit::getBizType, bizType);
        query.eq(CreditLimit::getGrantingSubjectKey, grantingSubjectKey);
        query.eq(CreditLimit::getBizSourceKey, bizSourceKey);
        return this.getOne(query);
    }

    public List<CreditLimit> listByTwoKeys(String bizType, String grantingSubjectKey) {
        LambdaQueryWrapper<CreditLimit> query = Wrappers.lambdaQuery();
        query.eq(CreditLimit::getBizType, bizType);
        query.eq(CreditLimit::getGrantingSubjectKey, grantingSubjectKey);
        return this.list(query);
    }

    public void expire(String bizType, String grantingSubjectKey, String bizSourceKey) {
        CreditLimit creditLimit = this.findByThreeKeys(bizType, grantingSubjectKey, bizSourceKey);
        if (Objects.isNull(creditLimit)) {
            throw new MithrasException("授信主数据不存在");
        }
        if (Objects.equals(creditLimit.getStatus(), CreditLimitStatusEnum.INVALID.name())) {
            return;
        }
        creditLimit.setStatus(CreditLimitStatusEnum.INVALID.name());
        this.updateById(creditLimit);
    }

    public List<CreditLimit> listByQueryKeys(Collection<String> queryKeys) {
        LambdaQueryWrapper<CreditLimit> query = Wrappers.lambdaQuery();
        query.in(CreditLimit::getQueryKey, queryKeys);
        return this.list(query);
    }
}
