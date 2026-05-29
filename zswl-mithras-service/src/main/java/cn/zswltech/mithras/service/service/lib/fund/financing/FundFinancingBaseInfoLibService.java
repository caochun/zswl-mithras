package cn.zswltech.mithras.service.service.lib.fund.financing;

import cn.zswltech.mithras.service.mapper.lib.fund.financing.FinancingQueryDto;
import cn.zswltech.mithras.service.mapper.lib.fund.financing.FundFinancingBaseInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfoLib;
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
public class FundFinancingBaseInfoLibService extends ServiceImpl<FundFinancingBaseInfoLibMapper, FundFinancingBaseInfoLib> {
    public FundFinancingBaseInfoLib getOneByOriginIdVersion(Long originId, String version) {
        LambdaQueryWrapper<FundFinancingBaseInfoLib> query = Wrappers.lambdaQuery();
        query.eq(FundFinancingBaseInfoLib::getOriginId, originId);
        query.eq(FundFinancingBaseInfoLib::getVersion, version);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    public List<FundFinancingBaseInfoLib> queryLastestVersionLibList(Set<Long> targetFinancingIds) {
        return baseMapper.queryLastestVersionLibList(targetFinancingIds);
    }

    public List<FundFinancingBaseInfoLib> queryLastestVersionLibs(FinancingQueryDto dto) {
        return baseMapper.queryLastestVersionLibs(dto);
    }
}
