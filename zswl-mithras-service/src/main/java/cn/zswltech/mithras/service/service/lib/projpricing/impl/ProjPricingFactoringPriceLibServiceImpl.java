package cn.zswltech.mithras.service.service.lib.projpricing.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingFactoringPriceRSP;
import cn.zswltech.mithras.service.convert.projpricing.ProjPricingPriceConverter;
import cn.zswltech.mithras.service.mapper.lib.projpricing.ProjPricingFactoringPriceLibMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingFactoringPriceLib;
import cn.zswltech.mithras.service.service.lib.projpricing.ProjPricingEditionAdvice;
import cn.zswltech.mithras.service.service.lib.projpricing.ProjPricingFactoringPriceLibService;
import cn.zswltech.mithras.service.service.projpricing.dto.ProjPricingPriceDto;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @ClassName ProjPricingFactoringPriceLibServiceImpl
 * @Description
 * @Author jackerhe
 * @Date 2022/8/2 10:41 上午
 * @Version 1.0
 **/
@Service
public class ProjPricingFactoringPriceLibServiceImpl extends ProjPricingEditionAdvice<ProjPricingFactoringPriceLibMapper, ProjPricingFactoringPriceLib> implements ProjPricingFactoringPriceLibService {

    @Resource
    private ProjPricingPriceConverter priceConverter;

    @Override
    public ProjPricingFactoringPriceRSP getOldEdition(Long projId){
        CommonVersion commonVersion = oldVersion(projId);
        ProjPricingFactoringPriceLib oldData = baseMapper.selectOne(Wrappers.<ProjPricingFactoringPriceLib>lambdaQuery()
                .eq(ProjPricingFactoringPriceLib::getProjectId, projId)
                .eq(ProjPricingFactoringPriceLib::getVersion, commonVersion.getVersion())
                .last("LIMIT 1"));
        if(ObjectUtil.isEmpty(oldData)){
            return null;
        }
        oldData.setId(oldData.getOriginId());
        return priceConverter.entityToFactoringRsp(oldData);
    }

    @Override
    public ProjPricingFactoringPriceLib getByProjPricingIdAndVersion(Long ProjPricingId, String version) {
        LambdaQueryWrapper<ProjPricingFactoringPriceLib> query = Wrappers.lambdaQuery();
        query.eq(ProjPricingFactoringPriceLib::getProjectId, ProjPricingId);
        query.eq(ProjPricingFactoringPriceLib::getVersion, version);
        query.orderByDesc(ProjPricingFactoringPriceLib::getId);
        query.last(StringUtil.mysqlLimit(0, 1));
        return this.getOne(query);
    }

    @Override
    public List<ProjPricingFactoringPriceLib> listNewestByProjPricingIds(Set<Long> ProjPricingIds) {
        return baseMapper.listNewestPrice(new ProjPricingPriceDto().setProjPricingIds(ProjPricingIds));
    }

}
