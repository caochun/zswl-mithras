package cn.zswltech.mithras.projectprocess.application.lib.projreview.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewFactoringPriceRSP;
import cn.zswltech.mithras.projectprocess.mapper.lib.projreview.ProjReviewFactoringPriceLibMapper;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewFactoringPriceLib;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.ProjReviewEditionAdvice;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.ProjReviewFactoringPriceLibService;
import cn.zswltech.mithras.projectprocess.application.riskcontrol.dto.ProjReviewPriceDto;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @ClassName ProjReviewFactoringPriceLibServiceImpl
 * @Description
 * @Author jackerhe
 * @Date 2022/8/2 10:41 上午
 * @Version 1.0
 **/
@Service
public class ProjReviewFactoringPriceLibServiceImpl extends ProjReviewEditionAdvice<ProjReviewFactoringPriceLibMapper, ProjReviewFactoringPriceLib> implements ProjReviewFactoringPriceLibService {

    @Override
    public ProjReviewFactoringPriceRSP getOldEdition(Long projId){
        CommonVersion commonVersion = oldVersion(projId);
        ProjReviewFactoringPriceLib oldData = baseMapper.selectOne(Wrappers.<ProjReviewFactoringPriceLib>lambdaQuery()
                .eq(ProjReviewFactoringPriceLib::getProjectId, projId)
                .eq(ProjReviewFactoringPriceLib::getVersion, commonVersion.getVersion())
                .last("LIMIT 1"));
        if(ObjectUtil.isEmpty(oldData)){
            return null;
        }
        oldData.setId(oldData.getOriginId());
        return toRsp(oldData);
    }

    @Override
    public ProjReviewFactoringPriceLib getByProjReviewIdAndVersion(Long projReviewId, String version) {
        LambdaQueryWrapper<ProjReviewFactoringPriceLib> query = Wrappers.lambdaQuery();
        query.eq(ProjReviewFactoringPriceLib::getProjectId, projReviewId);
        query.eq(ProjReviewFactoringPriceLib::getVersion, version);
        query.orderByDesc(ProjReviewFactoringPriceLib::getId);
        query.last(StringUtil.mysqlLimit(0, 1));
        return this.getOne(query);
    }

    @Override
    public List<ProjReviewFactoringPriceLib> listNewestByProjReviewIds(Set<Long> projReviewIds) {
        return baseMapper.listNewestPrice(new ProjReviewPriceDto().setProjReviewIds(projReviewIds));
    }

    private ProjReviewFactoringPriceRSP toRsp(ProjReviewFactoringPriceLib entity) {
        ProjReviewFactoringPriceRSP rsp = new ProjReviewFactoringPriceRSP();
        rsp.setId(entity.getId());
        rsp.setProjectId(entity.getProjectId());
        rsp.setApplyCreditAmount(entity.getApplyCreditAmount());
        rsp.setApprovedAmount(entity.getProjectApprovalAmount());
        rsp.setFactoringCreditTerm(entity.getFactoringCreditTerm());
        rsp.setCreditAmountLoop(entity.getCreditAmountLoop());
        rsp.setEarnestMoney(entity.getEarnestMoney());
        rsp.setFactoringFinancingProportion(entity.getFactoringFinancingProportion());
        rsp.setConsultingFee(entity.getConsultingFee());
        rsp.setRateType(entity.getRateType());
        rsp.setFactoringRatePercent(entity.getFactoringRatePercent());
        rsp.setIrrPercent(entity.getIrrPercent());
        rsp.setRentalCalcType(entity.getRentalCalcType());
        rsp.setInterestWay(entity.getInterestWay());
        rsp.setRepayRate(entity.getRepayRate());
        return rsp;
    }

}
