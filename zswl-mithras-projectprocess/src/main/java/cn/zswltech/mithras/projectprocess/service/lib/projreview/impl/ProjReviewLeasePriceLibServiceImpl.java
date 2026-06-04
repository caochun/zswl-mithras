package cn.zswltech.mithras.projectprocess.service.lib.projreview.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewLeasePriceRSP;
import cn.zswltech.mithras.projectprocess.mapper.lib.projreview.ProjReviewLeasePriceLibMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewLeasePriceLib;
import cn.zswltech.mithras.projectprocess.service.lib.projreview.ProjReviewEditionAdvice;
import cn.zswltech.mithras.projectprocess.service.lib.projreview.ProjReviewLeasePriceLibService;
import cn.zswltech.mithras.projectprocess.service.riskcontrol.dto.ProjReviewPriceDto;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @ClassName ProjReviewLeasePriceLibService
 * @Description
 * @Author jackerhe
 * @Date 2022/8/2 10:41 上午
 * @Version 1.0
 **/
@Service
public class ProjReviewLeasePriceLibServiceImpl extends ProjReviewEditionAdvice<ProjReviewLeasePriceLibMapper, ProjReviewLeasePriceLib> implements ProjReviewLeasePriceLibService {

    @Override
    public ProjReviewLeasePriceRSP getOldEdition(Long projId){
        CommonVersion commonVersion = oldVersion(projId);
        ProjReviewLeasePriceLib oldData = baseMapper.selectOne(Wrappers.<ProjReviewLeasePriceLib>lambdaQuery()
                .eq(ProjReviewLeasePriceLib::getProjectId, projId)
                .eq(ProjReviewLeasePriceLib::getVersion, commonVersion.getVersion())
                .last("LIMIT 1"));
        if(ObjectUtil.isEmpty(oldData)){
            return null;
        }
        oldData.setId(oldData.getOriginId());
        return toRsp(oldData);
    }

    @Override
    public ProjReviewLeasePriceLib getByProjReviewIdAndVersion(Long projReviewId, String version) {
        LambdaQueryWrapper<ProjReviewLeasePriceLib> query = Wrappers.lambdaQuery();
        query.eq(ProjReviewLeasePrice::getProjectId, projReviewId);
        query.eq(ProjReviewLeasePriceLib::getVersion, version);
        query.orderByDesc(ProjReviewLeasePrice::getId);
        query.last(StringUtil.mysqlLimit(0, 1));
        return this.getOne(query);
    }

    @Override
    public List<ProjReviewLeasePriceLib> listNewestByProjReviewIds(Set<Long> projReviewIds) {
        return baseMapper.listNewestPrice(new ProjReviewPriceDto().setProjReviewIds(projReviewIds));
    }

    private ProjReviewLeasePriceRSP toRsp(ProjReviewLeasePriceLib entity) {
        ProjReviewLeasePriceRSP rsp = new ProjReviewLeasePriceRSP();
        rsp.setId(entity.getId());
        rsp.setProjectId(entity.getProjectId());
        rsp.setApplyCreditAmount(entity.getApplyCreditAmount());
        rsp.setApprovedAmount(entity.getProjectApprovalAmount());
        rsp.setLeaseMonthCount(entity.getLeaseMonthCount());
        rsp.setRepayRate(entity.getRepayRate());
        rsp.setRepayTimesTotal(entity.getRepayTimesTotal());
        rsp.setPayType(entity.getPayType());
        rsp.setRentalCalcType(entity.getRentalCalcType());
        rsp.setInterestWay(entity.getInterestWay());
        rsp.setCreditAmountLoop(entity.getCreditAmountLoop());
        rsp.setEarnestMoney(entity.getEarnestMoney());
        rsp.setDownPayment(entity.getDownPayment());
        rsp.setConsultingFee(entity.getConsultingFee());
        rsp.setCommission(entity.getCommission());
        rsp.setFirstInstallmentInterest(entity.getFirstInstallmentInterest());
        rsp.setNominalPrice(entity.getNominalPrice());
        rsp.setRateType(entity.getRateType());
        rsp.setLeaseRatePercent(entity.getLeaseRatePercent());
        rsp.setIrrPercent(entity.getIrrPercent());
        rsp.setPlannedStartingDate(entity.getPlannedStartingDate());
        return rsp;
    }

}
