package cn.zswltech.mithras.riskcontrol.scorecard.application;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.riskcontrol.common.TitleNameEnum;
import cn.zswltech.mithras.riskcontrol.scorecard.infrastructure.model.RiskControlScoreCardData;
import cn.zswltech.mithras.riskcontrol.scorecard.infrastructure.mapper.RiskControlScoreCardDataMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName RiskControlScoreCardTitleService
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/2/27 4:56 下午
 * @Version 1.0
 **/
@Service
public class RiskControlScoreCardDataService extends ServiceImpl<RiskControlScoreCardDataMapper, RiskControlScoreCardData> {

    public Map<Integer, RiskControlScoreCardData> getAreaMap(TitleNameEnum titleNameEnum, Integer year){
        return baseMapper.selectList(Wrappers.<RiskControlScoreCardData>lambdaQuery()
                .eq(ObjectUtil.isNotEmpty(titleNameEnum), RiskControlScoreCardData::getTitleName, titleNameEnum.display())
                .eq(RiskControlScoreCardData::getYear, year)).stream().collect(Collectors.toMap(RiskControlScoreCardData::getRow, e -> e));
    }
}
