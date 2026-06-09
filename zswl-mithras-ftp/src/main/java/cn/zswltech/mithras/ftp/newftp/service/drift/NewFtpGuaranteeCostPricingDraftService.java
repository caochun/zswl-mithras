package cn.zswltech.mithras.ftp.newftp.service.drift;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.spring.SpringContextUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.newftp.NewFtpCommonDetailReq;
import cn.zswltech.mithras.dto.newftp.NewFtpGuaranteeCostPricingListRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpGuaranteeCostPricingModifyREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.ftp.newftp.mapper.NewFtpBaseInfoMapper;
import cn.zswltech.mithras.ftp.newftp.mapper.draft.NewFtpGuaranteeCostPricingDraftMapper;
import cn.zswltech.mithras.ftp.newftp.model.NewFtpBaseInfo;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpGuaranteeCostPricingConfig;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpGuaranteeCostPricingDraft;
import cn.zswltech.mithras.ftp.newftp.service.config.NewFtpGuaranteeCostPricingConfigService;
import cn.zswltech.mithras.ftp.newftp.service.lib.NewFtpGuaranteeCostPricingLibService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * @description 担保成本定价
 * @date 2023-05-21
 */
@Service
public class NewFtpGuaranteeCostPricingDraftService
        extends ServiceImpl<NewFtpGuaranteeCostPricingDraftMapper, NewFtpGuaranteeCostPricingDraft> {

    @Resource
    private NewFtpGuaranteeCostPricingConfigService newFtpGuaranteeCostPricingConfigService;
    @Resource
    private NewFtpBaseInfoMapper newFtpBaseInfoMapper;
    @Resource
    private NewFtpGuaranteeCostPricingLibService newFtpGuaranteeCostPricingLibService;


    private final String RATE = "5000";


    @Transactional(rollbackFor = Throwable.class)
    public void modify(NewFtpGuaranteeCostPricingModifyREQ req) {
        NewFtpGuaranteeCostPricingDraft originalInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        originalInfo.setHandCurrentAverage(req.getCurrentAverage());
        baseMapper.updateById(originalInfo);
    }

    //sum((担保额度*（融-还）/融资金额))/sum(融资余额) = sum(剩余额度担保额度)/sum(剩余额度)*0.5%
    @Transactional(rollbackFor = Throwable.class)
    public void add(Long ftpId) {
        NewFtpBaseInfo newFtpBaseInfo = newFtpBaseInfoMapper.selectById(ftpId);
        if(ObjectUtil.isEmpty(newFtpBaseInfo)){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        SpringContextUtil.getBean(NewFtpGuaranteeCostPricingDraftService.class).remove(Wrappers.<NewFtpGuaranteeCostPricingDraft>lambdaQuery()
                .eq(NewFtpGuaranteeCostPricingDraft::getFtpId, ftpId));
        List<NewFtpGuaranteeCostPricingConfig> costPricingConfigs = newFtpGuaranteeCostPricingConfigService.list(Wrappers.<NewFtpGuaranteeCostPricingConfig>lambdaQuery()
                .between(NewFtpGuaranteeCostPricingConfig::getMonth, newFtpBaseInfo.getMonth().minusMonths(6).atStartOfDay(), newFtpBaseInfo.getMonth().with(TemporalAdjusters.lastDayOfMonth()))
                .orderByDesc(NewFtpGuaranteeCostPricingConfig::getMonth));
        List<NewFtpGuaranteeCostPricingDraft> addList = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(costPricingConfigs)){
            costPricingConfigs.forEach(config -> {
                NewFtpGuaranteeCostPricingDraft ftpGuaranteeCostPricing = BeanUtil.copyProperties(config, NewFtpGuaranteeCostPricingDraft.class, "id");
                ftpGuaranteeCostPricing.setFtpId(ftpId);
                addList.add(ftpGuaranteeCostPricing);
            });
            if(!costPricingConfigs.get(costPricingConfigs.size() - 1).getMonth().equals(newFtpBaseInfo.getMonth())){
                NewFtpGuaranteeCostPricingDraft ftpGuaranteeCostPricing = new NewFtpGuaranteeCostPricingDraft();
                ftpGuaranteeCostPricing.setFtpId(ftpId);
                ftpGuaranteeCostPricing.setMonth(newFtpBaseInfo.getMonth());
                ftpGuaranteeCostPricing.setCurrentAverage(newFtpGuaranteeCostPricingConfigService.calculate(newFtpBaseInfo.getMonth()).intValue());
            }
        } else {
            NewFtpGuaranteeCostPricingDraft ftpGuaranteeCostPricing = new NewFtpGuaranteeCostPricingDraft();
            ftpGuaranteeCostPricing.setFtpId(ftpId);
            ftpGuaranteeCostPricing.setMonth(newFtpBaseInfo.getMonth());
            ftpGuaranteeCostPricing.setCurrentAverage(newFtpGuaranteeCostPricingConfigService.calculate(newFtpBaseInfo.getMonth()).intValue());
        }
        SpringContextUtil.getBean(NewFtpGuaranteeCostPricingDraftService.class).saveBatch(addList);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void flash(LocalDate localDate) {
        NewFtpGuaranteeCostPricingDraft ftpGuaranteeCostPricing = new NewFtpGuaranteeCostPricingDraft();
        ftpGuaranteeCostPricing.setMonth(localDate);
        NewFtpGuaranteeCostPricingDraft oldFtpGuaranteeCostPricing = baseMapper.selectOne(Wrappers.<NewFtpGuaranteeCostPricingDraft>lambdaQuery()
                .eq(NewFtpGuaranteeCostPricingDraft::getMonth, localDate)
                .orderByDesc(NewFtpGuaranteeCostPricingDraft::getId)
                .last(StringUtil.mysqlLimitOne()));
        if(ObjectUtil.isEmpty(oldFtpGuaranteeCostPricing)){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        BigDecimal calculate = newFtpGuaranteeCostPricingConfigService.calculate(localDate);
        ftpGuaranteeCostPricing.setCurrentAverage(calculate.intValue());
        if(ObjectUtil.isEmpty(oldFtpGuaranteeCostPricing)){
            baseMapper.insert(ftpGuaranteeCostPricing);
        } else {
            ftpGuaranteeCostPricing.setId(oldFtpGuaranteeCostPricing.getId());
            baseMapper.updateById(ftpGuaranteeCostPricing);
        }
    }

    public PageR<NewFtpGuaranteeCostPricingListRSP> list(NewFtpCommonDetailReq req) {
        Page<NewFtpGuaranteeCostPricingDraft> newFtpGuaranteeCostPricingPage;
        if(ObjectUtil.isEmpty(req.getVersion())){
            newFtpGuaranteeCostPricingPage = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<NewFtpGuaranteeCostPricingDraft>lambdaQuery()
                    .eq(NewFtpGuaranteeCostPricingDraft::getFtpId, req.getMainId())
                    .orderByDesc(NewFtpGuaranteeCostPricingDraft::getMonth));
        } else {
            newFtpGuaranteeCostPricingPage = newFtpGuaranteeCostPricingLibService.getByVersion(req.getMainId(), req.getVersion(), req.getPage(), req.getPageSize());
        }
        List<NewFtpGuaranteeCostPricingListRSP> rspList = new ArrayList<>();
        newFtpGuaranteeCostPricingPage.getRecords().forEach(base -> {
            NewFtpGuaranteeCostPricingListRSP rsp= new NewFtpGuaranteeCostPricingListRSP();
            rsp.setId(base.getId());
            rsp.setMonth(base.getMonth());
            rsp.setCurrentAverage(ObjectUtil.isEmpty(base.getHandCurrentAverage()) ? base.getCurrentAverage() : base.getHandCurrentAverage());
            rspList.add(rsp);
        });
        return PageR.of(rspList, newFtpGuaranteeCostPricingPage.getTotal(), newFtpGuaranteeCostPricingPage.getCurrent(),
                newFtpGuaranteeCostPricingPage.getSize());
    }


}