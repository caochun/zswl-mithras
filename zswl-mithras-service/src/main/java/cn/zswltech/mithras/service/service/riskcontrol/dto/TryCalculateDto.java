package cn.zswltech.mithras.service.service.riskcontrol.dto;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlScoreCardTargetListRSP;
import cn.zswltech.mithras.dto.riskcontrol.scorecard.RiskControlScoreCordAreaType;
import cn.zswltech.mithras.dto.riskcontrol.scorecard.RiskControlScoreCordTryCalculateRSP;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.riskcontrol.AreaStatusEnum;
import cn.zswltech.mithras.service.enums.riskcontrol.AreaTypeEnum;
import cn.zswltech.mithras.service.enums.riskcontrol.GradeEnum;
import cn.zswltech.mithras.service.mapper.model.riskcontrol.RiskControlScoreCardData;
import cn.zswltech.mithras.service.others.MithrasException;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * @ClassName TryCalculateDto
 * @Description 试计算
 * @Author jackerhe
 * @Date 2023/3/3 10:01 上午
 * @Version 1.0
 **/
@Data
public class TryCalculateDto {

    /**
     * targetMap<targetName,target>
     **/
    private Map<String, RiskControlScoreCardTargetListRSP> targetMap;

    /**
     * targetMap<targetName, data>
     **/
    private Map<String, RiskControlScoreCardData> targetDataMap;


    public TryCalculateDto(List<RiskControlScoreCardTargetListRSP>  riskControlScoreCardTargetListRSPS, List<RiskControlScoreCardData>  riskControlScoreCardDataList){
        setTargetMap(riskControlScoreCardTargetListRSPS);
        setTargetDataMap(riskControlScoreCardDataList);
    }

    public void setTargetMap(List<RiskControlScoreCardTargetListRSP>  riskControlScoreCardTargetListRSPS){
        if(ObjectUtil.isNotEmpty(riskControlScoreCardTargetListRSPS)){
            this.targetMap = riskControlScoreCardTargetListRSPS.stream().collect(Collectors.toMap(RiskControlScoreCardTargetListRSP::getTargetName, e->e));
        }
    }

    public void setTargetDataMap(List<RiskControlScoreCardData>  riskControlScoreCardDataList){
        if(ObjectUtil.isNotEmpty(riskControlScoreCardDataList)){
            this.targetDataMap = riskControlScoreCardDataList.stream().collect(Collectors.toMap(RiskControlScoreCardData::getTitleName, e->e));
        }
    }

    /**
     * 计算打分
     **/
    public void calculate(RiskControlScoreCordTryCalculateRSP calculateRSP){
        if(ObjectUtil.isEmpty(targetDataMap) || ObjectUtil.isEmpty(targetMap)){
            return;
        }
        targetDataMap.keySet().forEach(data -> {
            RiskControlScoreCardTargetListRSP target = targetMap.get(data);
            if(ObjectUtil.isNotEmpty(target)){
                //计算
                calculate(target, targetDataMap.get(data).getContent(), calculateRSP);
            }
        });

    }

    private void calculate(RiskControlScoreCardTargetListRSP target, String data, RiskControlScoreCordTryCalculateRSP calculateRSP){
        if(ObjectUtil.isEmpty(data)){
            data = String.valueOf(0);
        }
        BigDecimal dataLong;
        try {
            dataLong = new BigDecimal(data);
        } catch (NumberFormatException e){
            throw new MithrasException("暂时只支持数字类型打分");
        }
        RiskControlScoreCordTryCalculateRSP.TryCalculateBody tryCalculateBody;
        GradeEnum gradeEnum = Optional.ofNullable(GradeEnum.of(target.getGradeType())).orElseThrow(() -> new MithrasException(ResultMsg.UNSUPPORT_TYPE));
        tryCalculateBody = calculateRSP.new TryCalculateBody();
        tryCalculateBody.setTargetId(target.getId());
        tryCalculateBody.setTargetName(target.getTargetName());
        tryCalculateBody.setTargetWeight(target.getTargetWeight());
        tryCalculateBody.setGradeType(target.getGradeType());
        tryCalculateBody.setData(dataLong);
        switch (gradeEnum) {
            case LINEAR:
                List<RiskControlScoreCordAreaType> areaConfig = target.getAreaConfig();
                for (RiskControlScoreCordAreaType area : areaConfig) {
                    if (ObjectUtil.equals(target.getAreaStatus(), AreaStatusEnum.PARTITION.name())) {
                        if (ObjectUtil.equals(calculateRSP.getRegionalLevel(), area.getAreaType())) {
                            tryCalculateBody.setScore(linearCalculation(dataLong, area.getMin(), area.getMax(), target.getTargetWeight()));
                            break;
                        }
                    } else {
                        if (ObjectUtil.equals(AreaTypeEnum.NO_PARTITION.name(), area.getAreaType())) {
                            tryCalculateBody.setScore(linearCalculation(dataLong, area.getMin(), area.getMax(), target.getTargetWeight()));
                            break;
                        }
                    }
                }
                break;
            case OPTION:
                tryCalculateBody.setOptionGrade(target.getOptionGrade());
            default:
        }
        List<RiskControlScoreCordTryCalculateRSP.TryCalculateBody> tryCalculateBodies = calculateRSP.getTryCalculateBodies();
        if(ObjectUtil.isEmpty(tryCalculateBodies)){
            tryCalculateBodies = new ArrayList<>();
            calculateRSP.setTryCalculateBodies(tryCalculateBodies);
        }
        tryCalculateBodies.add(tryCalculateBody);
    }

    private BigDecimal linearCalculation(BigDecimal data, Long min, Long max, Long weight){
        BigDecimal score = new BigDecimal(0);
        BigDecimal dataB = new BigDecimal(data.toString());
        BigDecimal minB = new BigDecimal(min);
        BigDecimal maxB = new BigDecimal(max);
        if (data.compareTo(maxB) > 0) {
            score = new BigDecimal(1);
        } else if (data.compareTo(minB) > 0) {
            dataB = dataB.subtract(minB);//分子
            maxB = maxB.subtract(minB);//分母
            score = dataB.divide(maxB, 5, RoundingMode.HALF_UP);
        }
        score = score.multiply(new BigDecimal(weight)).setScale(2,BigDecimal.ROUND_HALF_UP);
        return score;
    }




}
