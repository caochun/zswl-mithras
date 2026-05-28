package cn.zswltech.mithras.service.service.riskcontrol;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.riskcontrol.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.riskcontrol.RiskControlAssertEnum;
import cn.zswltech.mithras.service.mapper.model.riskcontrol.RiskControlScoreCardBaseInfo;
import cn.zswltech.mithras.service.mapper.riskcontrol.RiskControlScoreCardBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
* @description 评分卡基本信息表
* @author vico
* @date 2023-02-27
*/
@Service
public class RiskControlScoreCardBaseInfoService extends ServiceImpl<RiskControlScoreCardBaseInfoMapper, RiskControlScoreCardBaseInfo>  {

    @Resource
    private RiskControlScoreCardBaseInfoMapper riskControlScoreCardBaseInfoMapper;

    @Resource
    private RiskControlScoreCardService riskControlScoreCardService;


    @Transactional(rollbackFor = Throwable.class)
    public RiskControlScoreCardBaseInfoDetailRSP add(RiskControlScoreCardBaseInfoAddREQ req) {
        RiskControlScoreCardBaseInfo info = BeanUtil.copyProperties(req, RiskControlScoreCardBaseInfo.class);
        if(ObjectUtil.isNotEmpty(info.getStatus())){
            info.setStatus(RiskControlAssertEnum.NOT_EFFECT.name());
        }
        checkModify(req.getYear(), req.getSuitTrade(), req.getProvinceSeat(), null);
        riskControlScoreCardBaseInfoMapper.insert(info);
        if(RiskControlAssertEnum.EFFECT.name().equals(info.getStatus()) && !riskControlScoreCardService.effectAuth(info.getId())){
            throw new MithrasException("指标权重相加不等于100");
        }
        return BeanUtil.copyProperties(info, RiskControlScoreCardBaseInfoDetailRSP.class);
    }

    public RiskControlScoreCardBaseInfoDetailRSP detail(RiskControlScoreCardBaseInfoCommREQ req){
        return BeanUtil.copyProperties(riskControlScoreCardBaseInfoMapper.selectById(req.getId()), RiskControlScoreCardBaseInfoDetailRSP.class);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(RiskControlScoreCardBaseInfoModifyREQ req) {
        RiskControlScoreCardBaseInfo originalInfo = riskControlScoreCardBaseInfoMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        checkModify(req.getYear(), req.getSuitTrade(), req.getProvinceSeat(), req.getId());
        RiskControlScoreCardBaseInfo info = BeanUtil.copyProperties(req, RiskControlScoreCardBaseInfo.class);
        riskControlScoreCardBaseInfoMapper.updateById(info);
        if(RiskControlAssertEnum.EFFECT.name().equals(info.getStatus()) && !riskControlScoreCardService.effectAuth(info.getId())){
            throw new MithrasException("指标权重相加不等于100");
        }
    }

    private void checkModify(Integer year, String suitTrade, String provinceSeat, Long ignoreId){
        if(baseMapper.selectCount(Wrappers.<RiskControlScoreCardBaseInfo>lambdaQuery()
        .eq(RiskControlScoreCardBaseInfo::getYear, year)
        .eq(RiskControlScoreCardBaseInfo::getSuitTrade, suitTrade)
        .eq(RiskControlScoreCardBaseInfo::getProvinceSeat, provinceSeat)
        .ne(ObjectUtil.isNotEmpty(ignoreId), RiskControlScoreCardBaseInfo::getId, ignoreId)) > 0){
            throw new MithrasException("同一年同一行业分类同省内/外已存在评分卡");
        }
    }

    public Page<RiskControlScoreCardBaseInfo> list(RiskControlScoreCardBaseInfoListREQ req) {
        LocalDateTime startOfDay = ObjectUtil.isNotEmpty(req.getUpdateTimeFrom()) ? req.getUpdateTimeFrom().atStartOfDay() : null;
        LocalDateTime endOfDay = ObjectUtil.isNotEmpty(req.getUpdateTimeTo()) ? req.getUpdateTimeTo().atTime(23, 59, 59) : null;
        return riskControlScoreCardBaseInfoMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<RiskControlScoreCardBaseInfo>lambdaQuery()
                .like(ObjectUtil.isNotEmpty(req.getScorecardName()), RiskControlScoreCardBaseInfo::getScorecardName, req.getScorecardName())
                .eq(ObjectUtil.isNotEmpty(req.getSuitTrade()), RiskControlScoreCardBaseInfo::getSuitTrade, req.getSuitTrade())
                .eq(ObjectUtil.isNotEmpty(req.getStatus()), RiskControlScoreCardBaseInfo::getStatus, req.getStatus())
                .between(ObjectUtil.isNotEmpty(req.getUpdateTimeFrom()), RiskControlScoreCardBaseInfo::getUpdateTime, startOfDay, endOfDay));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(RiskControlScoreCardBaseInfoRemoveREQ req) {
        riskControlScoreCardBaseInfoMapper.deleteBatchIds(req.getIds());
    }

    /**
     *生效，但权重不是100，修改状态
     **/
    @Transactional(rollbackFor = Throwable.class)
    public void checkStatus(Long id){
        RiskControlScoreCardBaseInfo cardBaseInfo = this.baseMapper.selectById(id);
        if(ObjectUtil.isEmpty(cardBaseInfo)){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if(RiskControlAssertEnum.EFFECT.name().equals(cardBaseInfo.getStatus()) && !riskControlScoreCardService.effectAuth(cardBaseInfo.getId())) {
            cardBaseInfo.setStatus(RiskControlAssertEnum.NOT_EFFECT.name());
            this.updateById(cardBaseInfo);
        }
    }

}