package cn.zswltech.mithras.rating.versioning.ratingclient.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientDetailLibRSP;
import cn.zswltech.mithras.rating.versioning.ratingclient.handler.RatingClientAbstractHandler;
import cn.zswltech.mithras.rating.model.RatingClient;
import cn.zswltech.mithras.rating.model.RatingClientLib;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import static cn.hutool.json.JSONUtil.toBean;


@Service
public class RatingClientLibHandler
        extends RatingClientAbstractHandler<RatingClientLib, RatingClient,
        RatingClientDetailLibRSP> {

    @Override
    public Set<String> compareIgnoreFieldNames() {
        HashSet<String> fields = new HashSet<>();
        fields.add("ratingStatus");
        fields.add("createTime");
        fields.add("updateTime");
        return fields;
    }

    @Override
    protected RatingClientLib entity2Lib(RatingClient ratingClient) {
        return BeanUtil.copyProperties(ratingClient, RatingClientLib.class);
    }

    @Override
    protected RatingClient lib2Entity(RatingClientLib ratingClientLib) {
        return BeanUtil.copyProperties(ratingClientLib, RatingClient.class);
    }

    @Override
    protected RatingClientDetailLibRSP lib2Rsp(RatingClientLib ratingClientLib) {
        RatingClientDetailLibRSP rsp = BeanUtil.copyProperties(ratingClientLib, RatingClientDetailLibRSP.class);
        return rsp;
    }

    @Override
    public RatingClientDetailLibRSP getSubModule() {
//        return PolicyInfoModule.BASE_INFO;
        return null;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }

    @Override
    public String libMainIdFieldName() {
        return "origin_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "id";
    }

    @Override
    public boolean isMainTable() {
        return true;
    }
}
