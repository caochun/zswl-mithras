package cn.zswltech.mithras.service.service.newftp.service.drift;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.spring.SpringContextUtil;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.newftp.mapper.NewFtpBaseInfoMapper;
import cn.zswltech.mithras.service.service.newftp.mapper.config.NewFtpTreasuryBondYieldPricingConfigMapper;
import cn.zswltech.mithras.service.service.newftp.mapper.draft.NewFtpTreasuryBondYieldPricingDraftMapper;
import cn.zswltech.mithras.service.service.newftp.model.NewFtpBaseInfo;
import cn.zswltech.mithras.service.service.newftp.model.config.NewFtpTreasuryBondYieldPricingConfig;
import cn.zswltech.mithras.service.service.newftp.model.draft.NewFtpTreasuryBondYieldPricingDraft;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * @description 10年期国债收益率定价
 * @date 2023-05-21
 */
@Service
public class NewFtpTreasuryBondYieldPricingDraftService
        extends ServiceImpl<NewFtpTreasuryBondYieldPricingDraftMapper, NewFtpTreasuryBondYieldPricingDraft> {

    @Resource
    private NewFtpBaseInfoMapper newFtpBaseInfoMapper;
    @Resource
    private NewFtpTreasuryBondYieldPricingConfigMapper newFtpTreasuryBondYieldPricingConfigMapper;

    //从配置区拉取数据
    @Transactional(rollbackFor = Throwable.class)
    public void addTreasuryBondYieldPricing(Long ftpId){
        NewFtpBaseInfo newFtpBaseInfo = newFtpBaseInfoMapper.selectById(ftpId);
        if(ObjectUtil.isEmpty(newFtpBaseInfo)){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        //清理已有数据
        SpringContextUtil.getBean(NewFtpTreasuryBondYieldPricingDraftService.class).remove(Wrappers.<NewFtpTreasuryBondYieldPricingDraft>lambdaQuery()
                .eq(NewFtpTreasuryBondYieldPricingDraft::getFtpId, ftpId));
        List<NewFtpTreasuryBondYieldPricingConfig> newFtpTreasuryBondYieldConfigs = newFtpTreasuryBondYieldPricingConfigMapper.selectList(Wrappers.<NewFtpTreasuryBondYieldPricingConfig>lambdaQuery()
                .between(NewFtpTreasuryBondYieldPricingConfig::getMonth, newFtpBaseInfo.getMonth().minusMonths(6).atStartOfDay(), newFtpBaseInfo.getMonth().with(TemporalAdjusters.lastDayOfMonth())));
        if(ObjectUtil.isNotEmpty(newFtpTreasuryBondYieldConfigs)){
            List<NewFtpTreasuryBondYieldPricingDraft> newFtpTreasuryBondYieldPricingDrafts = new ArrayList<>();
            newFtpTreasuryBondYieldConfigs.forEach(config -> {
                NewFtpTreasuryBondYieldPricingDraft draft = BeanUtil.copyProperties(config, NewFtpTreasuryBondYieldPricingDraft.class, "id");
                draft.setFtpId(ftpId);
                newFtpTreasuryBondYieldPricingDrafts.add(draft);
            });
            if(ObjectUtil.isNotEmpty(newFtpTreasuryBondYieldPricingDrafts)){
                SpringContextUtil.getBean(NewFtpTreasuryBondYieldPricingDraftService.class).saveBatch(newFtpTreasuryBondYieldPricingDrafts);
            }
        }
    }

}