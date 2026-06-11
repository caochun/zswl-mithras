package cn.zswltech.mithras.projectprocess.application.projestablish;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.projestablish.pricefactoring.ProjEstablishFactoringPriceModifyREQ;
import cn.zswltech.mithras.dto.projestablish.pricefactoring.ProjEstablishFactoringPriceRSP;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishFactoringPrice;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishFactoringPriceMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * @author zhaozhengkang
 * @description 保理报价方案表
 * @date 2022-07-19
 */
@Service
@Validated
public class ProjEstablishFactoringPriceService
        extends ServiceImpl<ProjEstablishFactoringPriceMapper, ProjEstablishFactoringPrice>
        implements ProjEstablishUpdateAdvice {

    /**
     * 单条明细
     * @param projEstablishId
     * @return rsp
     */
    public ProjEstablishFactoringPriceRSP detail(Long projEstablishId) {
        return BeanUtil.copyProperties(getByProjEstablishId(projEstablishId), ProjEstablishFactoringPriceRSP.class);
    }

    /**
     * 单条明细
     * @param projEstablishId
     * @return Entity
     */
    public ProjEstablishFactoringPrice getByProjEstablishId(Long projEstablishId) {
        return baseMapper.selectOne(Wrappers.<ProjEstablishFactoringPrice>lambdaQuery()
                .eq(ProjEstablishFactoringPrice::getProjEstablishId, projEstablishId));
    }

    /**
     * simple save or update
     *
     * @param req
     */
    @Validated
    @Transactional(rollbackFor = Throwable.class)
    public void modify(@Valid ProjEstablishFactoringPriceModifyREQ req) {
        saveCheck(req.getProjEstablishId());
        saveOrUpdate(BeanUtil.copyProperties(req, ProjEstablishFactoringPrice.class));
        recordStatus(req.getProjEstablishId());
    }
}
