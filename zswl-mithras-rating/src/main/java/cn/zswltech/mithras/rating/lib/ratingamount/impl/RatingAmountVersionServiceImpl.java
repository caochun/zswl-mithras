package cn.zswltech.mithras.rating.lib.ratingamount.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.payment.version.PaymentVersionListRSP;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.rating.lib.ratingamount.RatingAmountLibService;
import cn.zswltech.mithras.rating.lib.ratingamount.handler.RatingAmountAbstractHandler;
import cn.zswltech.mithras.rating.model.RatingAmount;
import cn.zswltech.mithras.rating.model.RatingAmountLib;
import cn.zswltech.mithras.foundation.persistence.dto.ChangeDTO;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.foundation.version.CommonVersionService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class RatingAmountVersionServiceImpl extends CommonVersionService<RatingAmount> {
    @Resource
    private List<RatingAmountAbstractHandler> libHandlerList;
    @Resource
    private RatingAmountLibService baseInfoLibService;

    @Override
    public void customFlushData(RatingAmount ratingAmount, String version, boolean needClearLastFlag, Integer versionType) {
        // 处理抄表逻辑
        for (RatingAmountAbstractHandler libHandler : libHandlerList) {
            libHandler.flushData(version, ratingAmount.getId(), needClearLastFlag, versionType);
        }
    }

    @Override
    public ChangeDTO checkActualChange(Long mainId) {
        return null;
    }

    @Override
    public void customReset(RatingAmount ratingAmount, CommonVersion commonVersion) {
        // 处理抄表逻辑
        for (RatingAmountAbstractHandler libHandler : libHandlerList) {
            libHandler.reset(ratingAmount.getId(), commonVersion.getVersion());
        }
    }

    @Override
    public CommonVersionDiffRSP doCompare(CommonVersion newVersion, CommonVersion oldVersion) {
        return null;
    }

    @Override
    protected CommonVersionListRSP convertPageRsp(CommonVersion cv, RatingAmount ratingAmount, Map<Long, String> userNameMap) {
        PaymentVersionListRSP rsp = BeanUtil.copyProperties(cv, PaymentVersionListRSP.class);
        RatingAmountLib one = baseInfoLibService.getOne(Wrappers.<RatingAmountLib>lambdaQuery()
                .eq(RatingAmountLib::getVersion, rsp.getVersion())
                .eq(RatingAmountLib::getOriginId, rsp.getMainId()));
        CommonVersionListRSP commonVersionListRSP = BeanUtil.copyProperties(one, CommonVersionListRSP.class);
        commonVersionListRSP.setMainId(one.getOriginId());
        return commonVersionListRSP;
    }

    @Override
    protected String getBusinessModuleName() {
        return "RATING_AMOUNT";
    }
}
