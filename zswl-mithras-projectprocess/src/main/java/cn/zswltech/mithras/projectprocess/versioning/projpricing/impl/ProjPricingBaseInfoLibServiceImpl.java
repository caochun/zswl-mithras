package cn.zswltech.mithras.projectprocess.versioning.projpricing.impl;


import cn.zswltech.mithras.dto.projpricing.baseinfo.ProjPricingBaseInfoDetailRSP;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.projectprocess.mapper.lib.projpricing.ProjPricingBaseInfoLibMapper;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingBaseInfoLib;
import cn.zswltech.mithras.projectprocess.versioning.projpricing.ProjPricingBaseInfoLibService;
import cn.zswltech.mithras.projectprocess.versioning.projpricing.ProjPricingEditionAdvice;
import cn.zswltech.mithras.projectprocess.versioning.projpricing.handler.impl.ProjPricingBaseInfoLibHandler;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;


/**
 * @ClassName ProjPricingBaseInfoLibServiceImpl
 * @Description
 * @Author jackerhe
 * @Date 2022/8/2 10:41 上午
 * @Version 1.0
 **/
@Service
public class ProjPricingBaseInfoLibServiceImpl extends ProjPricingEditionAdvice<ProjPricingBaseInfoLibMapper, ProjPricingBaseInfoLib> implements ProjPricingBaseInfoLibService {

    @Resource
    private ProjPricingBaseInfoLibHandler baseInfoLibHandler;

    @Override
    public ProjPricingBaseInfo getOldEdition(Long projId){
        CommonVersion commonVersion = oldVersion(projId);
        ProjPricingBaseInfoLib oldData = baseMapper.selectOne(Wrappers.<ProjPricingBaseInfoLib>lambdaQuery()
                .eq(ProjPricingBaseInfoLib::getOriginId, projId)
                .eq(ProjPricingBaseInfoLib::getVersion, commonVersion.getVersion())
                .last("LIMIT 1"));
        oldData.setId(oldData.getOriginId());
        return oldData;
    }

    @Override
    public ProjPricingBaseInfoDetailRSP detail(Long id, String version) {
        ProjPricingBaseInfoLib versionLib = baseMapper.selectOne(Wrappers.<ProjPricingBaseInfoLib>lambdaQuery().eq(ProjPricingBaseInfoLib::getOriginId, id)
                .eq(ProjPricingBaseInfoLib::getVersion, version)
                .last("LIMIT 1")
        );
        return Optional.ofNullable(versionLib).map(baseInfoLibHandler::actualLib2Rsp).orElse(new ProjPricingBaseInfoDetailRSP());
    }

    @Override
    public ProjPricingBaseInfoLib getByOriginIdAndVersion(Long originId, String version) {
        LambdaQueryWrapper<ProjPricingBaseInfoLib> query = Wrappers.lambdaQuery();
        query.eq(ProjPricingBaseInfoLib::getOriginId, originId);
        query.eq(ProjPricingBaseInfoLib::getVersion, version);
        query.orderByDesc(ProjPricingBaseInfo::getId);
        query.last(StringUtil.mysqlLimit(0, 1));
        return this.getOne(query);
    }

    @Override
    public List<ProjPricingBaseInfoLib> listNewestReviews() {
        return baseMapper.listNewestPreviewByClientIds(null, null);
    }

    @Override
    public ProjPricingBaseInfoLib getEffectLatestOne(Long originId) {
        LambdaQueryWrapper<ProjPricingBaseInfoLib> query = Wrappers.lambdaQuery();
        query.eq(ProjPricingBaseInfoLib::getOriginId, originId);
        query.eq(ProjPricingBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL);
        query.orderByDesc(ProjPricingBaseInfoLib::getVersion);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }


}
