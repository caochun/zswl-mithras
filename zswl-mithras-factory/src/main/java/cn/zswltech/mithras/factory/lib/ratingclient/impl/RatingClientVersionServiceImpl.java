package cn.zswltech.mithras.factory.lib.ratingclient.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.payment.version.PaymentVersionListRSP;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.factory.lib.ratingclient.RatingClientLibService;
import cn.zswltech.mithras.factory.lib.ratingclient.handler.RatingClientAbstractHandler;
import cn.zswltech.mithras.factory.model.RatingClient;
import cn.zswltech.mithras.factory.model.RatingClientLib;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.service.lib.CommonVersionService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class RatingClientVersionServiceImpl extends CommonVersionService<RatingClient> {
    @Resource
    private List<RatingClientAbstractHandler> libHandlerList;
    @Resource
    private RatingClientLibService baseInfoLibService;

    @Override
    public void customFlushData(RatingClient ratingClient, String version, boolean needClearLastFlag, Integer versionType) {
        // 处理抄表逻辑
        for (RatingClientAbstractHandler libHandler : libHandlerList) {
            libHandler.flushData(version, ratingClient.getId(), needClearLastFlag, versionType);
        }
    }

    @Override
    public ChangeDTO checkActualChange(Long mainId) {
        return null;
    }

    @Override
    public void customReset(RatingClient ratingClient, CommonVersion commonVersion) {
        // 处理抄表逻辑
        for (RatingClientAbstractHandler libHandler : libHandlerList) {
            libHandler.reset(ratingClient.getId(), commonVersion.getVersion());
        }
    }

    @Override
    public CommonVersionDiffRSP doCompare(CommonVersion newVersion, CommonVersion oldVersion) {
        return null;
    }

    @Override
    protected CommonVersionListRSP convertPageRsp(CommonVersion cv, RatingClient ratingClient, Map<Long, String> userNameMap) {
        PaymentVersionListRSP rsp = BeanUtil.copyProperties(cv, PaymentVersionListRSP.class);
        RatingClientLib one = baseInfoLibService.getOne(Wrappers.<RatingClientLib>lambdaQuery()
                .eq(RatingClientLib::getVersion, rsp.getVersion())
                .eq(RatingClientLib::getOriginId, rsp.getMainId()));
        CommonVersionListRSP commonVersionListRSP = BeanUtil.copyProperties(one, CommonVersionListRSP.class);
        commonVersionListRSP.setMainId(one.getOriginId());
        return commonVersionListRSP;
    }

    @Override
    protected String getBusinessModuleName() {
        return "RATING_CLIENT";
    }
}
