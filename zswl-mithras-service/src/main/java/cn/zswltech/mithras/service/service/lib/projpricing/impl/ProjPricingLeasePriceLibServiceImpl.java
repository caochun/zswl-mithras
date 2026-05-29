package cn.zswltech.mithras.service.service.lib.projpricing.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingLeasePriceRSP;
import cn.zswltech.mithras.service.convert.projpricing.ProjPricingPriceConverter;
import cn.zswltech.mithras.service.mapper.lib.projpricing.ProjPricingLeasePriceLibMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingLeasePrice;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingLeasePriceLib;
import cn.zswltech.mithras.service.service.lib.projpricing.ProjPricingEditionAdvice;
import cn.zswltech.mithras.service.service.lib.projpricing.ProjPricingLeasePriceLibService;
import cn.zswltech.mithras.service.service.projpricing.dto.ProjPricingPriceDto;
import cn.zswltech.mithras.common.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @ClassName ProjPricingLeasePriceLibService
 * @Description
 * @Author jackerhe
 * @Date 2022/8/2 10:41 上午
 * @Version 1.0
 **/
@Service
public class ProjPricingLeasePriceLibServiceImpl extends ProjPricingEditionAdvice<ProjPricingLeasePriceLibMapper, ProjPricingLeasePriceLib> implements ProjPricingLeasePriceLibService {

    @Resource
    private ProjPricingPriceConverter priceConverter;

    @Override
    public ProjPricingLeasePriceRSP getOldEdition(Long projId){
        CommonVersion commonVersion = oldVersion(projId);
        ProjPricingLeasePriceLib oldData = baseMapper.selectOne(Wrappers.<ProjPricingLeasePriceLib>lambdaQuery()
                .eq(ProjPricingLeasePriceLib::getProjectId, projId)
                .eq(ProjPricingLeasePriceLib::getVersion, commonVersion.getVersion())
                .last("LIMIT 1"));
        if(ObjectUtil.isEmpty(oldData)){
            return null;
        }
        oldData.setId(oldData.getOriginId());
        return priceConverter.entityToLeaseRsp(oldData);
    }

    @Override
    public ProjPricingLeasePriceLib getByProjPricingIdAndVersion(Long ProjPricingId, String version) {
        LambdaQueryWrapper<ProjPricingLeasePriceLib> query = Wrappers.lambdaQuery();
        query.eq(ProjPricingLeasePrice::getProjectId, ProjPricingId);
        query.eq(ProjPricingLeasePriceLib::getVersion, version);
        query.orderByDesc(ProjPricingLeasePrice::getId);
        query.last(StringUtil.mysqlLimit(0, 1));
        return this.getOne(query);
    }

    @Override
    public List<ProjPricingLeasePriceLib> listNewestByProjPricingIds(Set<Long> ProjPricingIds) {
        return baseMapper.listNewestPrice(new ProjPricingPriceDto().setProjPricingIds(ProjPricingIds));
    }

}
