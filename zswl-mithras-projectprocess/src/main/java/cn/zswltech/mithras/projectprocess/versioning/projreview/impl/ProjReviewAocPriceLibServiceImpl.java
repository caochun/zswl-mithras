package cn.zswltech.mithras.projectprocess.versioning.projreview.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewAocPriceRSP;
import cn.zswltech.mithras.projectprocess.mapper.lib.projreview.ProjReviewAocPriceLibMapper;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewAocPrice;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewAocPriceLib;
import cn.zswltech.mithras.projectprocess.versioning.projreview.ProjReviewAocPriceLibService;
import cn.zswltech.mithras.projectprocess.versioning.projreview.ProjReviewEditionAdvice;
import cn.zswltech.mithras.projectprocess.versioning.projreview.dto.ProjReviewPriceDto;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @ClassName ProjReviewAocPriceLibServiceImpl
 * @Description
 * @Author jackerhe
 * @Date 2022/8/2 10:38 上午
 * @Version 1.0
 **/
@Service
public class ProjReviewAocPriceLibServiceImpl extends ProjReviewEditionAdvice<ProjReviewAocPriceLibMapper, ProjReviewAocPriceLib> implements ProjReviewAocPriceLibService {

    @Override
    public ProjReviewAocPriceRSP getOldEdition(Long projId){
        CommonVersion commonVersion = oldVersion(projId);
        ProjReviewAocPriceLib oldData = baseMapper.selectOne(Wrappers.<ProjReviewAocPriceLib>lambdaQuery()
                .eq(ProjReviewAocPriceLib::getProjectId, projId)
                .eq(ProjReviewAocPriceLib::getVersion, commonVersion.getVersion())
                .last("LIMIT 1"));
        if(ObjectUtil.isEmpty(oldData)){
            return null;
        }
        oldData.setId(oldData.getOriginId());
        return toRsp(oldData);
    }

    @Override
    public ProjReviewAocPriceLib getByProjReviewIdAndVersion(Long projReviewId, String version) {
        LambdaQueryWrapper<ProjReviewAocPriceLib> query = Wrappers.lambdaQuery();
        query.eq(ProjReviewAocPrice::getProjectId, projReviewId);
        query.eq(ProjReviewAocPriceLib::getVersion, version);
        query.orderByDesc(ProjReviewAocPrice::getId);
        query.last(StringUtil.mysqlLimit(0, 1));
        return this.getOne(query);
    }

    @Override
    public List<ProjReviewAocPriceLib> listNewestByProjReviewIds(Set<Long> projReviewIds) {
        return baseMapper.listNewestPrice(new ProjReviewPriceDto().setProjReviewIds(projReviewIds));
    }

    private ProjReviewAocPriceRSP toRsp(ProjReviewAocPriceLib entity) {
        ProjReviewAocPriceRSP rsp = new ProjReviewAocPriceRSP();
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
