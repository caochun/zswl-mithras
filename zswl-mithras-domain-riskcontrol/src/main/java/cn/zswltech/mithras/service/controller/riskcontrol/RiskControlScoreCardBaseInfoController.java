package cn.zswltech.mithras.service.controller.riskcontrol;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.riskcontrol.RiskControlScoreCardBaseInfoApi;
import cn.zswltech.mithras.dto.riskcontrol.*;
import cn.zswltech.mithras.service.mapper.model.riskcontrol.RiskControlScoreCardBaseInfo;
import cn.zswltech.mithras.service.service.riskcontrol.RiskControlScoreCardBaseInfoService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
* @description 评分卡指标基本信息
* @author vico
* @date 2023-02-27
*/
@RestController
public class RiskControlScoreCardBaseInfoController implements RiskControlScoreCardBaseInfoApi {

    @Resource
    private RiskControlScoreCardBaseInfoService riskControlScoreCardBaseInfoService;

    @Override
    public R<RiskControlScoreCardBaseInfoDetailRSP> add(RiskControlScoreCardBaseInfoAddREQ req) {
        return R.ok(riskControlScoreCardBaseInfoService.add(req));
    }

    @Override
    public R<RiskControlScoreCardBaseInfoDetailRSP> detail(@Valid RiskControlScoreCardBaseInfoCommREQ req) {
        return R.ok(riskControlScoreCardBaseInfoService.detail(req));
    }

    @Override
    public R<Void> modify(RiskControlScoreCardBaseInfoModifyREQ req){
        riskControlScoreCardBaseInfoService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<RiskControlScoreCardBaseInfoListRSP>> list(RiskControlScoreCardBaseInfoListREQ req){
        Page<RiskControlScoreCardBaseInfo> data = riskControlScoreCardBaseInfoService.list(req);
        List<RiskControlScoreCardBaseInfoListRSP> list = BeanUtil.copyToList(data.getRecords(), RiskControlScoreCardBaseInfoListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(RiskControlScoreCardBaseInfoRemoveREQ req){
        riskControlScoreCardBaseInfoService.remove(req);
        return R.ok();
    }

}