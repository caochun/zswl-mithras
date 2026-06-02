package cn.zswltech.mithras.service.controller.riskcontrol;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.riskcontrol.RiskControlScoreCardTargetApi;
import cn.zswltech.mithras.dto.riskcontrol.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.mapper.model.riskcontrol.RiskControlScoreCardTarget;
import cn.zswltech.mithras.service.mapper.model.riskcontrol.RiskControlScoreCardTitle;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.riskcontrol.RiskControlScoreCardBaseInfoService;
import cn.zswltech.mithras.service.service.riskcontrol.RiskControlScoreCardTargetService;
import cn.zswltech.mithras.service.service.riskcontrol.RiskControlScoreCardTitleService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
* @description 评分卡指标
* @author vico
* @date 2023-02-27
*/
@RestController
public class RiskControlScoreCardTargetController implements RiskControlScoreCardTargetApi {

    @Resource
    private RiskControlScoreCardTargetService riskControlScoreCardTargetService;
    @Resource
    private RiskControlScoreCardTitleService riskControlScoreCardTitleService;
    @Resource
    private RiskControlScoreCardBaseInfoService riskControlScoreCardBaseInfoService;

    @Override
    public R<String> add(RiskControlScoreCardTargetAddREQ req) {
        riskControlScoreCardTargetService.add(req);
        riskControlScoreCardBaseInfoService.checkStatus(req.getCardId());
        return R.ok();
    }

    @Override
    public R<String> modify(RiskControlScoreCardTargetModifyREQ req){
        riskControlScoreCardTargetService.modify(req);
        riskControlScoreCardBaseInfoService.checkStatus(req.getCardId());
        return R.ok();
    }

    @Override
    public R<PageR<RiskControlScoreCardTargetListRSP>> list(RiskControlScoreCardTargetListREQ req){
        return R.ok(riskControlScoreCardTargetService.list(req));
    }

    @Override
    public R<Void> remove(RiskControlScoreCardTargetRemoveREQ req){
        RiskControlScoreCardTarget byId = riskControlScoreCardTargetService.getById(req.getId());
        if(ObjectUtil.isEmpty(byId)){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        riskControlScoreCardTargetService.remove(req);
        riskControlScoreCardBaseInfoService.checkStatus(byId.getCardId());
        return R.ok();
    }

    @Override
    public R<List<RiskControlScoreCardTargetSearchRSP>> targetSearch(@Valid RiskControlScoreCardTargetSearchREQ req) {
        List<RiskControlScoreCardTitle> list = riskControlScoreCardTitleService.list(Wrappers.<RiskControlScoreCardTitle>lambdaQuery()
                .eq(RiskControlScoreCardTitle::getYear, req.getYear())
                .eq(RiskControlScoreCardTitle::getTargetType, YesOrNoNumberEnum.NO.getCode())
                .like(ObjectUtil.isNotEmpty(req.getTargetName()), RiskControlScoreCardTitle::getName, req.getTargetName()));
        if(ObjectUtil.isEmpty(list)){
            return R.ok();
        }
        return R.ok(BeanUtil.copyToList(list, RiskControlScoreCardTargetSearchRSP.class));
    }
}