package cn.zswltech.mithras.projectprocess.service.lib.projreview.impl;


import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.projectprocess.mapper.lib.projreview.ProjReviewBaseInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfoLib;
import cn.zswltech.mithras.projectprocess.service.lib.projreview.ProjReviewBaseInfoLibService;
import cn.zswltech.mithras.projectprocess.service.lib.projreview.ProjReviewEditionAdvice;
import cn.zswltech.mithras.projectprocess.service.lib.projreview.handler.impl.ProjReviewBaseInfoLibHandler;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;


/**
 * @ClassName ProjreviewBaseInfoLibServiceImpl
 * @Description
 * @Author jackerhe
 * @Date 2022/8/2 10:41 上午
 * @Version 1.0
 **/
@Service
public class ProjReviewBaseInfoLibServiceImpl extends ProjReviewEditionAdvice<ProjReviewBaseInfoLibMapper, ProjReviewBaseInfoLib> implements ProjReviewBaseInfoLibService {

    @Resource
    private ProjReviewBaseInfoLibHandler baseInfoLibHandler;

    @Override
    public ProjReviewBaseInfo getOldEdition(Long projId){
        CommonVersion commonVersion = oldVersion(projId);
        ProjReviewBaseInfoLib oldData = baseMapper.selectOne(Wrappers.<ProjReviewBaseInfoLib>lambdaQuery()
                .eq(ProjReviewBaseInfoLib::getOriginId, projId)
                .eq(ProjReviewBaseInfoLib::getVersion, commonVersion.getVersion())
                .last("LIMIT 1"));
        oldData.setId(oldData.getOriginId());
        return oldData;
    }

    @Override
    public ProjReviewBaseInfoDetailRSP detail(Long id, String version) {
        ProjReviewBaseInfoLib versionLib = baseMapper.selectOne(Wrappers.<ProjReviewBaseInfoLib>lambdaQuery().eq(ProjReviewBaseInfoLib::getOriginId, id)
                .eq(ProjReviewBaseInfoLib::getVersion, version)
                .last("LIMIT 1")
        );
        return Optional.ofNullable(versionLib).map(baseInfoLibHandler::actualLib2Rsp).orElse(new ProjReviewBaseInfoDetailRSP());
    }

    @Override
    public ProjReviewBaseInfoLib getByOriginIdAndVersion(Long originId, String version) {
        LambdaQueryWrapper<ProjReviewBaseInfoLib> query = Wrappers.lambdaQuery();
        query.eq(ProjReviewBaseInfoLib::getOriginId, originId);
        query.eq(ProjReviewBaseInfoLib::getVersion, version);
        query.orderByDesc(ProjReviewBaseInfo::getId);
        query.last(StringUtil.mysqlLimit(0, 1));
        return this.getOne(query);
    }

    @Override
    public List<ProjReviewBaseInfoLib> listNewestReviews() {
        return baseMapper.listNewestPreviewByClientIds(null, null);
    }

    @Override
    public ProjReviewBaseInfoLib getEffectLatestOne(Long originId) {
        LambdaQueryWrapper<ProjReviewBaseInfoLib> query = Wrappers.lambdaQuery();
        query.eq(ProjReviewBaseInfoLib::getOriginId, originId);
        query.eq(ProjReviewBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL);
        query.orderByDesc(ProjReviewBaseInfoLib::getVersion);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }


}
