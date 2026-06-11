package cn.zswltech.mithras.projectprocess.application.lib.projpricing.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingLeasePriceRSP;
import cn.zswltech.mithras.projectprocess.mapper.lib.projpricing.ProjPricingLeasePriceLibMapper;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingLeasePrice;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingLeasePriceLib;
import cn.zswltech.mithras.projectprocess.application.lib.projpricing.ProjPricingEditionAdvice;
import cn.zswltech.mithras.projectprocess.application.lib.projpricing.ProjPricingLeasePriceLibService;
import cn.zswltech.mithras.projectprocess.application.projpricing.dto.ProjPricingPriceDto;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @ClassName ProjPricingLeasePriceLibService
 * @Description
 * @Author jackerhe
 * @Date 2022/8/2 10:41 上午
 * @Version 1.0
 **/
@Service
public class ProjPricingLeasePriceLibServiceImpl extends ProjPricingEditionAdvice<ProjPricingLeasePriceLibMapper, ProjPricingLeasePriceLib> implements ProjPricingLeasePriceLibService {

    @Override
    public ProjPricingLeasePriceRSP getOldEdition(Long projId){
        CommonVersion commonVersion = oldVersion(projId);
        ProjPricingLeasePriceLib oldData = baseMapper.selectOne(Wrappers.<ProjPricingLeasePriceLib>lambdaQuery()
                .eq(ProjPricingLeasePriceLib::getProjectId, projId)
                .eq(ProjPricingLeasePriceLib::getVersion, commonVersion.getVersion())
                .last("LIMIT 1"));
        if(ObjectUtil.isEmpty(oldData)){
            return null;
        }
        oldData.setId(oldData.getOriginId());
        return toRsp(oldData);
    }

    @Override
    public ProjPricingLeasePriceLib getByProjPricingIdAndVersion(Long ProjPricingId, String version) {
        LambdaQueryWrapper<ProjPricingLeasePriceLib> query = Wrappers.lambdaQuery();
        query.eq(ProjPricingLeasePrice::getProjectId, ProjPricingId);
        query.eq(ProjPricingLeasePriceLib::getVersion, version);
        query.orderByDesc(ProjPricingLeasePrice::getId);
        query.last(StringUtil.mysqlLimit(0, 1));
        return this.getOne(query);
    }

    @Override
    public List<ProjPricingLeasePriceLib> listNewestByProjPricingIds(Set<Long> ProjPricingIds) {
        return baseMapper.listNewestPrice(new ProjPricingPriceDto().setProjPricingIds(ProjPricingIds));
    }

    private ProjPricingLeasePriceRSP toRsp(ProjPricingLeasePriceLib entity) {
        ProjPricingLeasePriceRSP rsp = new ProjPricingLeasePriceRSP();
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
