package cn.zswltech.mithras.service.service.lib.projreview.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewAocPriceRSP;
import cn.zswltech.mithras.service.convert.projreview.ProjReviewPriceConverter;
import cn.zswltech.mithras.service.mapper.lib.projreview.ProjReviewAocPriceLibMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewAocPrice;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewAocPriceLib;
import cn.zswltech.mithras.service.service.lib.projreview.ProjReviewAocPriceLibService;
import cn.zswltech.mithras.service.service.lib.projreview.ProjReviewEditionAdvice;
import cn.zswltech.mithras.service.service.riskcontrol.dto.ProjReviewPriceDto;
import cn.zswltech.mithras.common.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @ClassName ProjReviewAocPriceLibServiceImpl
 * @Description
 * @Author jackerhe
 * @Date 2022/8/2 10:38 上午
 * @Version 1.0
 **/
@Service
public class ProjReviewAocPriceLibServiceImpl extends ProjReviewEditionAdvice<ProjReviewAocPriceLibMapper, ProjReviewAocPriceLib> implements ProjReviewAocPriceLibService {

    @Resource
    private ProjReviewPriceConverter priceConverter;

    @Override
    public ProjReviewAocPriceRSP getOldEdition(Long projId){
        CommonVersion commonVersion = oldVersion(projId);
        ProjReviewAocPriceLib oldData = baseMapper.selectOne(Wrappers.<ProjReviewAocPriceLib>lambdaQuery()
                .eq(ProjReviewAocPriceLib::getProjectId, projId)
                .eq(ProjReviewAocPriceLib::getVersion, commonVersion.getVersion())
                .last("LIMIT 1"));
        if(ObjectUtil.isEmpty(oldData)){
            return null;
        }
        oldData.setId(oldData.getOriginId());
        return priceConverter.entityToAocRsp(oldData);
    }

    @Override
    public ProjReviewAocPriceLib getByProjReviewIdAndVersion(Long projReviewId, String version) {
        LambdaQueryWrapper<ProjReviewAocPriceLib> query = Wrappers.lambdaQuery();
        query.eq(ProjReviewAocPrice::getProjectId, projReviewId);
        query.eq(ProjReviewAocPriceLib::getVersion, version);
        query.orderByDesc(ProjReviewAocPrice::getId);
        query.last(StringUtil.mysqlLimit(0, 1));
        return this.getOne(query);
    }

    @Override
    public List<ProjReviewAocPriceLib> listNewestByProjReviewIds(Set<Long> projReviewIds) {
        return baseMapper.listNewestPrice(new ProjReviewPriceDto().setProjReviewIds(projReviewIds));
    }

}
