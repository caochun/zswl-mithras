package cn.zswltech.mithras.riskcontrol.scorecard.application;

import cn.zswltech.mithras.riskcontrol.common.TitleNameEnum;
import cn.zswltech.mithras.riskcontrol.scorecard.model.RiskControlScoreCardData;
import cn.zswltech.mithras.riskcontrol.scorecard.model.RiskControlScoreCardTitle;
import cn.zswltech.mithras.riskcontrol.scorecard.mapper.RiskControlScoreCardTitleMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName RiskControlScoreCardTitleService
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/2/27 4:56 下午
 * @Version 1.0
 **/
@Service
public class RiskControlScoreCardTitleService extends ServiceImpl<RiskControlScoreCardTitleMapper, RiskControlScoreCardTitle> {

    @Resource
    private RiskControlScoreCardDataService riskControlScoreCardDataService;

    //取row行的 TitleNameEnum 数据
    public Map<String, String> getAreaDataByTitleNameEnum(Integer row, Integer year){
        List<String> columnList = new ArrayList<>();
        Map<String, String> rspMap = new HashMap<>();
        for (TitleNameEnum value : TitleNameEnum.values()) {
            columnList.add(value.display);
        }
        List<RiskControlScoreCardData> dataList = riskControlScoreCardDataService.list(Wrappers.<RiskControlScoreCardData>lambdaQuery()
                .eq(RiskControlScoreCardData::getRow, row)
                .in(RiskControlScoreCardData::getTitleName, columnList));
        dataList.forEach(data -> rspMap.put(data.getTitleName(), data.getContent()));
        return rspMap;
    }
}
