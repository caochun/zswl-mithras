package cn.zswltech.mithras.fund.application.credit;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.fund.persistence.mapper.FundCreditGuaranteeDetailMapper;
import cn.zswltech.mithras.fund.persistence.model.FundCreditGuaranteeDetail;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 授信担保详情
 * @date 2022-12-22
 */
@Service
public class FundCreditGuaranteeDetailService
        extends ServiceImpl<FundCreditGuaranteeDetailMapper, FundCreditGuaranteeDetail>{

    public List<FundCreditGuaranteeDetail> getByCreditId(Long CreditId) {
        return baseMapper.selectList(Wrappers.<FundCreditGuaranteeDetail>lambdaQuery().eq(FundCreditGuaranteeDetail::getCreditId, CreditId));
    }

    public Map<Long, List<FundCreditGuaranteeDetail>> getByCreditIdList(Collection<Long> creditIdList) {
        if(CollectionUtil.isEmpty(creditIdList)){
            return Collections.emptyMap();
        }
        List<FundCreditGuaranteeDetail> guaranteeDetails = baseMapper.selectList(Wrappers.<FundCreditGuaranteeDetail>lambdaQuery()
                .in(FundCreditGuaranteeDetail::getCreditId, creditIdList));
        return Optional.ofNullable(guaranteeDetails).map(m -> m.stream()
                .collect(Collectors.groupingBy(FundCreditGuaranteeDetail::getCreditId))
        ).orElse(Collections.emptyMap());
    }

    public Long getUsedGuaranteeLimit(Long agencyId){
        List<FundCreditGuaranteeDetail> details = baseMapper.selectList(
                Wrappers.<FundCreditGuaranteeDetail>lambdaQuery()
                        .eq(FundCreditGuaranteeDetail::getGuaranteeAgencyId, agencyId));
        return details.stream().mapToLong(FundCreditGuaranteeDetail::getGuaranteeAmount).sum();
    }

    public Integer selectCountByAgencyId(Long agencyId){
        return baseMapper.selectCount(Wrappers.<FundCreditGuaranteeDetail>lambdaQuery().eq(FundCreditGuaranteeDetail::getGuaranteeAgencyId, agencyId));
    }

    public List<Long> getCreditIdByAgencyIds(Collection<Long> agencyIdList){
        if(CollectionUtil.isEmpty(agencyIdList)){
            return Collections.emptyList();
        }
        List<FundCreditGuaranteeDetail> details = baseMapper.selectList(
                Wrappers.<FundCreditGuaranteeDetail>lambdaQuery().in(FundCreditGuaranteeDetail::getGuaranteeAgencyId, agencyIdList));
        return details.stream().map(FundCreditGuaranteeDetail::getCreditId).distinct().collect(Collectors.toList());
    }

}