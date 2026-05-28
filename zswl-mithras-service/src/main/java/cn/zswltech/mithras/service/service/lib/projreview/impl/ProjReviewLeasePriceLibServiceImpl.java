package cn.zswltech.mithras.service.service.lib.projreview.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewLeasePriceRSP;
import cn.zswltech.mithras.service.convert.projreview.ProjReviewPriceConverter;
import cn.zswltech.mithras.service.mapper.lib.projreview.ProjReviewLeasePriceLibMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewLeasePriceLib;
import cn.zswltech.mithras.service.service.lib.projreview.ProjReviewEditionAdvice;
import cn.zswltech.mithras.service.service.lib.projreview.ProjReviewLeasePriceLibService;
import cn.zswltech.mithras.service.service.riskcontrol.dto.ProjReviewPriceDto;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @ClassName ProjReviewLeasePriceLibService
 * @Description
 * @Author jackerhe
 * @Date 2022/8/2 10:41 上午
 * @Version 1.0
 **/
@Service
public class ProjReviewLeasePriceLibServiceImpl extends ProjReviewEditionAdvice<ProjReviewLeasePriceLibMapper, ProjReviewLeasePriceLib> implements ProjReviewLeasePriceLibService {

    @Resource
    private ProjReviewPriceConverter priceConverter;

    @Override
    public ProjReviewLeasePriceRSP getOldEdition(Long projId){
        CommonVersion commonVersion = oldVersion(projId);
        ProjReviewLeasePriceLib oldData = baseMapper.selectOne(Wrappers.<ProjReviewLeasePriceLib>lambdaQuery()
                .eq(ProjReviewLeasePriceLib::getProjectId, projId)
                .eq(ProjReviewLeasePriceLib::getVersion, commonVersion.getVersion())
                .last("LIMIT 1"));
        if(ObjectUtil.isEmpty(oldData)){
            return null;
        }
        oldData.setId(oldData.getOriginId());
        return priceConverter.entityToLeaseRsp(oldData);
    }

    @Override
    public ProjReviewLeasePriceLib getByProjReviewIdAndVersion(Long projReviewId, String version) {
        LambdaQueryWrapper<ProjReviewLeasePriceLib> query = Wrappers.lambdaQuery();
        query.eq(ProjReviewLeasePrice::getProjectId, projReviewId);
        query.eq(ProjReviewLeasePriceLib::getVersion, version);
        query.orderByDesc(ProjReviewLeasePrice::getId);
        query.last(StringUtil.mysqlLimit(0, 1));
        return this.getOne(query);
    }

    @Override
    public List<ProjReviewLeasePriceLib> listNewestByProjReviewIds(Set<Long> projReviewIds) {
        return baseMapper.listNewestPrice(new ProjReviewPriceDto().setProjReviewIds(projReviewIds));
    }

}
