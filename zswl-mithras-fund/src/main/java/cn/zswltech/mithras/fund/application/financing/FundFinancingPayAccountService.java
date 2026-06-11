package cn.zswltech.mithras.fund.application.financing;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingAccountTypeEnum;
import cn.zswltech.mithras.fund.mapper.financing.FundFinancingPayAccountMapper;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingPayAccount;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FundFinancingPayAccountService extends ServiceImpl<FundFinancingPayAccountMapper, FundFinancingPayAccount> {
    public void checkExist(Long bankAccountId, Long financingId, Long id) {
        LambdaQueryWrapper<FundFinancingPayAccount> query = Wrappers.lambdaQuery();
        query.eq(FundFinancingPayAccount::getBankAccountId, bankAccountId);
        query.eq(FundFinancingPayAccount::getFinancingId, financingId);
        query.ne(ObjectUtil.isNotEmpty(id), FundFinancingPayAccount::getId, id);
        int count = this.count(query);
        Assert.isTrue(count == 0, () -> MithrasException.newException("付款账户已添加，请勿重复添加"));
    }


    /**
     * 找融资下的还本账户,优先还本 其次还本付息
     */
    public Map<Long, String> queryRepayPrincipal(Collection<Long> financingIdList){
        List<FundFinancingPayAccount> payAccountList = this.list(Wrappers.<FundFinancingPayAccount>lambdaQuery()
                .in(FundFinancingPayAccount::getFinancingId, financingIdList)
                .ne(FundFinancingPayAccount::getAccountCategory, FundFinancingAccountTypeEnum.REPAY_INTEREST.name()));
        if(CollectionUtil.isEmpty(payAccountList)){
            return Collections.emptyMap();
        }
        payAccountList.sort(Comparator.comparing(item -> Optional.ofNullable(
                FundFinancingAccountTypeEnum.finaByName(item.getAccountCategory())).map(FundFinancingAccountTypeEnum::getSort).orElse(100)));
        return payAccountList.stream().collect(Collectors.toMap(FundFinancingPayAccount::getFinancingId, FundFinancingPayAccount::getAccountNumber, (m1, m2) -> m1));

    }


    public List<FundFinancingPayAccount> listByFinancingIds(Collection<Long> financingIdList){
        if(CollectionUtil.isNotEmpty(financingIdList)){
            return this.list(Wrappers.<FundFinancingPayAccount>lambdaQuery()
                    .in(FundFinancingPayAccount::getFinancingId, financingIdList));
        }
        return Collections.emptyList();
    }

}
