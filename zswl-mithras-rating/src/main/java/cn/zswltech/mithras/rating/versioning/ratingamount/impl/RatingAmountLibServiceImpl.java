package cn.zswltech.mithras.rating.versioning.ratingamount.impl;

import cn.zswltech.mithras.dto.rating.ratingamount.RatingAmountDetailLibRSP;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientDetailLibRSP;
import cn.zswltech.mithras.rating.versioning.ratingamount.RatingAmountLibService;
import cn.zswltech.mithras.rating.versioning.ratingamount.handler.impl.RatingAmountLibHandler;
import cn.zswltech.mithras.rating.mapper.lib.RatingAmountLibMapper;
import cn.zswltech.mithras.rating.mapper.lib.RatingClientLibMapper;
import cn.zswltech.mithras.rating.model.RatingAmount;
import cn.zswltech.mithras.rating.model.RatingAmountLib;
import cn.zswltech.mithras.rating.model.RatingClientLib;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;


@Service
public class RatingAmountLibServiceImpl
        extends ServiceImpl<RatingAmountLibMapper, RatingAmountLib>
        implements RatingAmountLibService {

    @Resource
    private RatingAmountLibHandler baseInfoLibHandler;

    @Override
    public RatingAmountDetailLibRSP detail(Long id) {
        RatingAmountLib ratingAmountLib = baseMapper.selectOne(Wrappers.<RatingAmountLib>lambdaQuery()
                .eq(RatingAmountLib::getOriginId, id)
                .orderByDesc(RatingAmountLib::getDataCreateTime)
                .last("limit 1"));
        return Optional.ofNullable(ratingAmountLib).map(baseInfoLibHandler::actualLib2Rsp).orElse(new RatingAmountDetailLibRSP());
    }
}
