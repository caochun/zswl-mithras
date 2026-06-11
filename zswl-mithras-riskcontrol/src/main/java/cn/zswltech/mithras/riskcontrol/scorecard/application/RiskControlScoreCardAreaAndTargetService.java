package cn.zswltech.mithras.riskcontrol.scorecard.application;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlScoreCardTargetListRSP;
import cn.zswltech.mithras.dto.riskcontrol.scorecard.RiskControlScoreCordCalculateDetailREQ;
import cn.zswltech.mithras.dto.riskcontrol.scorecard.RiskControlScoreCordCalculateDetailRSP;
import cn.zswltech.mithras.dto.riskcontrol.scorecard.RiskControlScoreCordCalculateSaveREQ;
import cn.zswltech.mithras.riskcontrol.scorecard.application.assembler.RiskControlCardTargetConverter;
import cn.zswltech.mithras.riskcontrol.scorecard.model.RiskControlScoreCardAreaAndTarget;
import cn.zswltech.mithras.riskcontrol.scorecard.model.RiskControlScoreCardTarget;
import cn.zswltech.mithras.riskcontrol.scorecard.mapper.RiskControlScoreCardAreaAndTargetMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
* @description 某省市县具体得分
* @author vico
* @date 2023-03-02
*/
@Service
public class RiskControlScoreCardAreaAndTargetService extends ServiceImpl<RiskControlScoreCardAreaAndTargetMapper, RiskControlScoreCardAreaAndTarget> {

    @Resource
    private RiskControlScoreCardTargetService riskControlScoreCardTargetService;
    @Resource
    private RiskControlCardTargetConverter riskControlCardTargetConverter;
    @Resource
    private ScoreCardClientAddressResolver scoreCardClientAddressResolver;

    @Transactional(rollbackFor = Throwable.class)
    public void save(RiskControlScoreCordCalculateSaveREQ req){
        List<RiskControlScoreCardAreaAndTarget> riskControlScoreCardAreaAndTargets = new ArrayList<>();
        if(ObjectUtil.isEmpty(req.getYear())){
            req.setYear(LocalDate.now().getYear());
        }
        req.getTargetScoreBodies().forEach(body -> {
            RiskControlScoreCardAreaAndTarget riskControlScoreCardAreaAndTarget = new RiskControlScoreCardAreaAndTarget();
            //基本信息
            riskControlScoreCardAreaAndTarget.setArea(req.getArea());
            riskControlScoreCardAreaAndTarget.setProvince(req.getProvince());
            riskControlScoreCardAreaAndTarget.setCity(req.getCity());
            riskControlScoreCardAreaAndTarget.setExecutiveLevel(req.getExecutiveLevel());
            riskControlScoreCardAreaAndTarget.setRegionalLevel(req.getRegionalLevel());
            riskControlScoreCardAreaAndTarget.setYear(req.getYear());
            //评分信息
            riskControlScoreCardAreaAndTarget.setTargetId(body.getTargetId());
            riskControlScoreCardAreaAndTarget.setData(body.getData());
            riskControlScoreCardAreaAndTarget.setScore(body.getScore().toString());
            riskControlScoreCardAreaAndTargets.add(riskControlScoreCardAreaAndTarget);
        });
        this.remove(Wrappers.<RiskControlScoreCardAreaAndTarget>lambdaQuery()
        .eq(RiskControlScoreCardAreaAndTarget::getYear, req.getYear())
        .eq(RiskControlScoreCardAreaAndTarget::getProvince, req.getProvince())
        .eq(RiskControlScoreCardAreaAndTarget::getCity, req.getCity())
        .eq(RiskControlScoreCardAreaAndTarget::getArea, req.getArea()));
        this.saveBatch(riskControlScoreCardAreaAndTargets);
    }

    public RiskControlScoreCordCalculateDetailRSP calculateDetail(RiskControlScoreCordCalculateDetailREQ req){

        if(ObjectUtil.isNotEmpty(req.getClientId())){
            ScoreCardClientRegistryAddress registryAddress = scoreCardClientAddressResolver.registryAddress(req.getClientId())
                    .orElseThrow(() -> new MithrasException("该用户无注册地址信息"));
            req.setProvince(registryAddress.getProvinceName());
            req.setCity(registryAddress.getCityName());
            if("市辖区".equals(registryAddress.getCityName())){
                req.setCity(registryAddress.getProvinceName());
            }
            if("市辖区".equals(registryAddress.getDistrictName())){
                req.setArea(registryAddress.getCityName());
            } else {
                req.setArea(registryAddress.getDistrictName());
            }
        }
        List<RiskControlScoreCardAreaAndTarget> riskControlScoreCardAreaAndTargets = this.baseMapper.selectList(Wrappers.<RiskControlScoreCardAreaAndTarget>lambdaQuery()
                .eq(RiskControlScoreCardAreaAndTarget::getArea, req.getArea())
                .eq(RiskControlScoreCardAreaAndTarget::getProvince, req.getProvince())
                .eq(RiskControlScoreCardAreaAndTarget::getCity, req.getCity())
                .eq(RiskControlScoreCardAreaAndTarget::getYear, req.getYear()));
        if(ObjectUtil.isEmpty(riskControlScoreCardAreaAndTargets)){
            return null;
        }
        Set<Long> targetIdSet = riskControlScoreCardAreaAndTargets.stream().map(RiskControlScoreCardAreaAndTarget::getTargetId).collect(Collectors.toSet());
        Map<Long, RiskControlScoreCardTargetListRSP> targetMap = riskControlScoreCardTargetService.list(Wrappers.<RiskControlScoreCardTarget>lambdaQuery()
                .in(RiskControlScoreCardTarget::getId, targetIdSet)).stream().map(riskControlCardTargetConverter::entity2Rsp).collect(Collectors.toMap(RiskControlScoreCardTargetListRSP::getId, e -> e));
        RiskControlScoreCordCalculateDetailRSP detailRSP = new RiskControlScoreCordCalculateDetailRSP();
        detailRSP.setArea(req.getArea());
        detailRSP.setProvince(req.getProvince());
        detailRSP.setCity(req.getCity());
        detailRSP.setExecutiveLevel(riskControlScoreCardAreaAndTargets.get(0).getExecutiveLevel());
        detailRSP.setRegionalLevel(riskControlScoreCardAreaAndTargets.get(0).getRegionalLevel());
        detailRSP.setCreateTime(LocalDate.from(riskControlScoreCardAreaAndTargets.get(0).getCreateTime()));
        detailRSP.setUpdateTime(LocalDate.from(riskControlScoreCardAreaAndTargets.get(0).getUpdateTime()));
        List<RiskControlScoreCordCalculateDetailRSP.CalculateDetailBody> calculateDetailBodies = new ArrayList<>();
        riskControlScoreCardAreaAndTargets.forEach(base -> {
            RiskControlScoreCordCalculateDetailRSP.CalculateDetailBody detailBody = detailRSP.new CalculateDetailBody();
            RiskControlScoreCardTargetListRSP target = targetMap.get(base.getTargetId());
            detailBody.setTargetId(base.getTargetId());
            detailBody.setData(base.getData());
            detailBody.setScore(base.getScore());
            if(ObjectUtil.isNotEmpty(target)){
                detailBody.setTargetName(target.getTargetName());
                detailBody.setTargetWeight(target.getTargetWeight());
                detailBody.setGradeType(target.getGradeType());
                detailBody.setOptionGrade(target.getOptionGrade());
            }
            calculateDetailBodies.add(detailBody);
        });
        detailRSP.setCalculateDetailBodies(calculateDetailBodies);
        if(ObjectUtil.isNotEmpty(detailRSP.getCalculateDetailBodies())){
            detailRSP.setTotalPoints(new BigDecimal(0));
            detailRSP.getCalculateDetailBodies().forEach(base -> detailRSP.setTotalPoints(detailRSP.getTotalPoints().add(Optional.of(new BigDecimal(base.getScore())).orElse(new BigDecimal(0)))));
        }
        return detailRSP;
    }


}
