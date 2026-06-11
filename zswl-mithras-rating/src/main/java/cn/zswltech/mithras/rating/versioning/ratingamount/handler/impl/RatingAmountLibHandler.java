package cn.zswltech.mithras.rating.versioning.ratingamount.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.rating.ratingamount.RatingAmountDetailLibRSP;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientDetailLibRSP;
import cn.zswltech.mithras.rating.versioning.ratingamount.handler.RatingAmountAbstractHandler;
import cn.zswltech.mithras.rating.model.RatingAmount;
import cn.zswltech.mithras.rating.model.RatingAmountLib;
import cn.zswltech.mithras.rating.model.RatingClient;
import cn.zswltech.mithras.rating.model.RatingClientLib;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


@Service
public class RatingAmountLibHandler
        extends RatingAmountAbstractHandler<RatingAmountLib, RatingAmount,
                        RatingAmountDetailLibRSP> {

    @Override
    public Set<String> compareIgnoreFieldNames() {
        HashSet<String> fields = new HashSet<>();
        fields.add("ratingStatus");
        fields.add("createTime");
        fields.add("updateTime");
        return fields;
    }

    @Override
    protected RatingAmountLib entity2Lib(RatingAmount ratingAmount) {
        return BeanUtil.copyProperties(ratingAmount, RatingAmountLib.class);
    }

    @Override
    protected RatingAmount lib2Entity(RatingAmountLib ratingAmountLib) {
        return BeanUtil.copyProperties(ratingAmountLib, RatingAmount.class);
    }

    @Override
    protected RatingAmountDetailLibRSP lib2Rsp(RatingAmountLib ratingAmountLib) {
        RatingAmountDetailLibRSP rsp = BeanUtil.copyProperties(ratingAmountLib, RatingAmountDetailLibRSP.class);
        return rsp;
    }

    @Override
    public RatingAmountDetailLibRSP getSubModule() {
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
