package cn.zswltech.mithras.projectprocess.service.lib.projpricing.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingAocPriceRSP;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingAocPrice;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingAocPriceLib;
import cn.zswltech.mithras.projectprocess.mapper.lib.projpricing.ProjPricingAocPriceLibMapper;
import cn.zswltech.mithras.projectprocess.service.lib.projpricing.ProjPricingAocPriceLibService;
import cn.zswltech.mithras.projectprocess.service.lib.projpricing.ProjPricingEditionAdvice;
import cn.zswltech.mithras.projectprocess.service.projpricing.dto.ProjPricingPriceDto;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @ClassName ProjPricingAocPriceLibServiceImpl
 * @Description
 * @Author jackerhe
 * @Date 2022/8/2 10:38 上午
 * @Version 1.0
 **/
@Service
public class ProjPricingAocPriceLibServiceImpl extends ProjPricingEditionAdvice<ProjPricingAocPriceLibMapper, ProjPricingAocPriceLib> implements ProjPricingAocPriceLibService {

    @Override
    public ProjPricingAocPriceRSP getOldEdition(Long projId){
        CommonVersion commonVersion = oldVersion(projId);
        ProjPricingAocPriceLib oldData = baseMapper.selectOne(Wrappers.<ProjPricingAocPriceLib>lambdaQuery()
                .eq(ProjPricingAocPriceLib::getProjectId, projId)
                .eq(ProjPricingAocPriceLib::getVersion, commonVersion.getVersion())
                .last("LIMIT 1"));
        if(ObjectUtil.isEmpty(oldData)){
            return null;
        }
        oldData.setId(oldData.getOriginId());
        return toRsp(oldData);
    }

    @Override
    public ProjPricingAocPriceLib getByProjPricingIdAndVersion(Long ProjPricingId, String version) {
        LambdaQueryWrapper<ProjPricingAocPriceLib> query = Wrappers.lambdaQuery();
        query.eq(ProjPricingAocPrice::getProjectId, ProjPricingId);
        query.eq(ProjPricingAocPriceLib::getVersion, version);
        query.orderByDesc(ProjPricingAocPrice::getId);
        query.last(StringUtil.mysqlLimit(0, 1));
        return this.getOne(query);
    }

    @Override
    public List<ProjPricingAocPriceLib> listNewestByProjPricingIds(Set<Long> ProjPricingIds) {
        return baseMapper.listNewestPrice(new ProjPricingPriceDto().setProjPricingIds(ProjPricingIds));
    }

    private ProjPricingAocPriceRSP toRsp(ProjPricingAocPriceLib entity) {
        ProjPricingAocPriceRSP rsp = new ProjPricingAocPriceRSP();
        rsp.setId(entity.getId());
        rsp.setProjectId(entity.getProjectId());
        rsp.setApplyCreditAmount(entity.getApplyCreditAmount());
        rsp.setApprovedAmount(entity.getProjectApprovalAmount());
        rsp.setAocCreditTerm(entity.getAocCreditTerm());
        rsp.setCreditAmountLoop(entity.getCreditAmountLoop());
        rsp.setEarnestMoney(entity.getEarnestMoney());
        rsp.setConsultingFee(entity.getConsultingFee());
        rsp.setRateType(entity.getRateType());
        rsp.setAocRatePercent(entity.getAocRatePercent());
        rsp.setIrrPercent(entity.getIrrPercent());
        rsp.setPlannedStartingDate(entity.getPlannedStartingDate());
        rsp.setRentalCalcType(entity.getRentalCalcType());
        rsp.setInterestWay(entity.getInterestWay());
        rsp.setRepayRate(entity.getRepayRate());
        return rsp;
    }

}
