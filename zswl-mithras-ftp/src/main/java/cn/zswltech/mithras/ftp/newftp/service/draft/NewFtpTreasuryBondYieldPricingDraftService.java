package cn.zswltech.mithras.ftp.newftp.service.draft;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.ftp.newftp.mapper.NewFtpBaseInfoMapper;
import cn.zswltech.mithras.ftp.newftp.mapper.config.NewFtpTreasuryBondYieldPricingConfigMapper;
import cn.zswltech.mithras.ftp.newftp.mapper.draft.NewFtpTreasuryBondYieldPricingDraftMapper;
import cn.zswltech.mithras.ftp.newftp.model.NewFtpBaseInfo;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpTreasuryBondYieldPricingConfig;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpTreasuryBondYieldPricingDraft;
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
        remove(Wrappers.<NewFtpTreasuryBondYieldPricingDraft>lambdaQuery()
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
                saveBatch(newFtpTreasuryBondYieldPricingDrafts);
            }
        }
    }

}
