package cn.zswltech.mithras.service.service.lib.fund.financing;

import cn.zswltech.mithras.service.mapper.lib.fund.financing.FinancingQueryDto;
import cn.zswltech.mithras.service.mapper.lib.fund.financing.FundFinancingCreditRefLibMapper;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingCreditRefLib;
import cn.zswltech.mithras.common.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class FundFinancingCreditRefLibService extends ServiceImpl<FundFinancingCreditRefLibMapper, FundFinancingCreditRefLib> {
    public FundFinancingCreditRefLib getOneByOriginIdVersion(Long originId, String version) {
        LambdaQueryWrapper<FundFinancingCreditRefLib> query = Wrappers.lambdaQuery();
        query.eq(FundFinancingCreditRefLib::getOriginId, originId);
        query.eq(FundFinancingCreditRefLib::getVersion, version);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    public List<FundFinancingCreditRefLib> queryLastestVersionLibList(Set<Long> targetFinancingIds) {
//        return baseMapper.queryLastestVersionLibList(targetFinancingIds);
        return null;
    }

    public List<FundFinancingCreditRefLib> queryLastestVersionLibs(FinancingQueryDto dto) {
//        return baseMapper.queryLastestVersionLibs(dto);
        return null;
    }
}
