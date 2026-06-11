package cn.zswltech.mithras.ftp.newftp.service.draft;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.ftp.newftp.mapper.NewFtpBaseInfoMapper;
import cn.zswltech.mithras.ftp.newftp.mapper.config.NewFtpShiborInterestRatePricingConfigMapper;
import cn.zswltech.mithras.ftp.newftp.mapper.draft.NewFtpShiborInterestRatePricingDraftMapper;
import cn.zswltech.mithras.ftp.newftp.model.NewFtpBaseInfo;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpShiborInterestRatePricingConfig;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpShiborInterestRatePricingDraft;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 一年期shibor利率定价
 * @date 2023-05-21
 */
@Service
public class NewFtpShiborInterestRatePricingDraftService
        extends ServiceImpl<NewFtpShiborInterestRatePricingDraftMapper, NewFtpShiborInterestRatePricingDraft> {

    @Resource
    private NewFtpBaseInfoMapper newFtpBaseInfoMapper;

    @Resource
    private NewFtpShiborInterestRatePricingConfigMapper newFtpShiborInterestRatePricingConfigMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void addTreasuryBondYield(Long ftpId){
        NewFtpBaseInfo newFtpBaseInfo = newFtpBaseInfoMapper.selectById(ftpId);
        if(ObjectUtil.isEmpty(newFtpBaseInfo)){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        //清理已有数据
        remove(Wrappers.<NewFtpShiborInterestRatePricingDraft>lambdaQuery()
                .eq(NewFtpShiborInterestRatePricingDraft::getFtpId, ftpId));

        List<NewFtpShiborInterestRatePricingConfig> newFtpConfigs = newFtpShiborInterestRatePricingConfigMapper.selectList(Wrappers.<NewFtpShiborInterestRatePricingConfig>lambdaQuery()
                .between(NewFtpShiborInterestRatePricingConfig::getMonth, newFtpBaseInfo.getMonth().minusMonths(6).atStartOfDay(), newFtpBaseInfo.getMonth().with(TemporalAdjusters.lastDayOfMonth())));
        if(ObjectUtil.isNotEmpty(newFtpConfigs)){
            List<NewFtpShiborInterestRatePricingDraft> newFtpTreasuryBondYieldPricingDrafts = new ArrayList<>();
            newFtpConfigs.forEach(config -> {
                NewFtpShiborInterestRatePricingDraft draft = BeanUtil.copyProperties(config, NewFtpShiborInterestRatePricingDraft.class, "id");
                draft.setFtpId(ftpId);
                newFtpTreasuryBondYieldPricingDrafts.add(draft);
            });
            if(ObjectUtil.isNotEmpty(newFtpTreasuryBondYieldPricingDrafts)){
                saveBatch(newFtpTreasuryBondYieldPricingDrafts);
            }
        }
    }
}
