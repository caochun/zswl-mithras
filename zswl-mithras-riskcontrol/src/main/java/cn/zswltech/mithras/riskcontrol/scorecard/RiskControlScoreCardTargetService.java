package cn.zswltech.mithras.riskcontrol.scorecard;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.riskcontrol.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.riskcontrol.scorecard.RiskControlCardTargetConverter;
import cn.zswltech.mithras.riskcontrol.scorecard.RiskControlScoreCardTarget;
import cn.zswltech.mithras.riskcontrol.scorecard.RiskControlScoreCardTargetMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
* @description risk_control_score_card_target
* @author vico
* @date 2023-02-27
*/
@Service
public class RiskControlScoreCardTargetService extends ServiceImpl<RiskControlScoreCardTargetMapper, RiskControlScoreCardTarget> {

    @Resource
    private RiskControlScoreCardTargetMapper riskControlScoreCardTargetMapper;
    @Resource
    private RiskControlCardTargetConverter riskControlCardTargetConverter;

    @Transactional(rollbackFor = Throwable.class)
    public void add(RiskControlScoreCardTargetAddREQ req) {
        if(riskControlScoreCardTargetMapper.selectCount(Wrappers.<RiskControlScoreCardTarget>lambdaQuery()
        .eq(RiskControlScoreCardTarget::getCardId, req.getCardId())
        .eq(RiskControlScoreCardTarget::getTargetName, req.getTargetName())) > 0){
            throw new MithrasException(req.getTargetName() + "指标已存在");
        }
        RiskControlScoreCardTarget info = riskControlCardTargetConverter.addReq2Entity(req);
        riskControlScoreCardTargetMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(RiskControlScoreCardTargetModifyREQ req) {
        if(riskControlScoreCardTargetMapper.selectCount(Wrappers.<RiskControlScoreCardTarget>lambdaQuery()
                .eq(RiskControlScoreCardTarget::getCardId, req.getCardId())
                .ne(RiskControlScoreCardTarget::getId, req.getId())
                .eq(RiskControlScoreCardTarget::getTargetName, req.getTargetName())) > 0){
            throw new MithrasException(req.getTargetName() + "指标已存在");
        }
        RiskControlScoreCardTarget originalInfo = riskControlScoreCardTargetMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        RiskControlScoreCardTarget info = riskControlCardTargetConverter.modifyReq2Entity(req);
        riskControlScoreCardTargetMapper.updateById(info);
    }

    public PageR<RiskControlScoreCardTargetListRSP> list(RiskControlScoreCardTargetListREQ req) {
        Page<RiskControlScoreCardTarget> riskControlScoreCardTargetPage = riskControlScoreCardTargetMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<RiskControlScoreCardTarget>lambdaQuery()
                .eq(RiskControlScoreCardTarget::getCardId, req.getCardId()));
        if(ObjectUtil.isEmpty(riskControlScoreCardTargetPage.getRecords())){
            return null;
        }
        List<RiskControlScoreCardTargetListRSP> list = riskControlScoreCardTargetPage.getRecords().stream().map(riskControlCardTargetConverter::entity2Rsp).collect(Collectors.toList());
        return PageR.of(list, riskControlScoreCardTargetPage.getTotal(),
                riskControlScoreCardTargetPage.getPages(),
                riskControlScoreCardTargetPage.getCurrent(),
                riskControlScoreCardTargetPage.getSize());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(RiskControlScoreCardTargetRemoveREQ req) {
        RiskControlScoreCardTarget originalInfo = riskControlScoreCardTargetMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        riskControlScoreCardTargetMapper.deleteById(req.getId());
    }

}