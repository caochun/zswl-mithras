package cn.zswltech.mithras.rating.lib.ratingclient.impl;

import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientDetailLibRSP;
import cn.zswltech.mithras.rating.lib.ratingclient.RatingClientLibService;
import cn.zswltech.mithras.rating.lib.ratingclient.handler.impl.RatingClientLibHandler;
import cn.zswltech.mithras.rating.mapper.lib.RatingClientLibMapper;
import cn.zswltech.mithras.rating.model.RatingClientLib;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;


@Service
public class RatingClientLibServiceImpl
        extends ServiceImpl<RatingClientLibMapper, RatingClientLib>
        implements RatingClientLibService {

    @Resource
    private RatingClientLibHandler baseInfoLibHandler;

    @Override
    public RatingClientDetailLibRSP detail(Long id) {
        RatingClientLib ratingClientLib = baseMapper.selectOne(Wrappers.<RatingClientLib>lambdaQuery()
                .eq(RatingClientLib::getOriginId, id)
                .orderByDesc(RatingClientLib::getCreateTime)
                .last("limit 1"));
        return Optional.ofNullable(ratingClientLib).map(baseInfoLibHandler::actualLib2Rsp).orElse(new RatingClientDetailLibRSP());
    }
}
