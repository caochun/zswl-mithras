package cn.zswltech.mithras.projectprocess.application.projestablish;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.projestablish.priceaoc.ProjEstablishAocPriceModifyREQ;
import cn.zswltech.mithras.dto.projestablish.priceaoc.ProjEstablishAocPriceRSP;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishAocPrice;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishAocPriceMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author zhaozhengkang
 * @description 债权转让报价方案表
 * @date 2022-07-19
 */
@Service
@Validated
public class ProjEstablishAocPriceService extends ServiceImpl<ProjEstablishAocPriceMapper, ProjEstablishAocPrice>
        implements ProjEstablishUpdateAdvice {

    @Resource
    private ProjEstablishAocPriceMapper projEstablishAocPriceMapper;
    /**
     * 查询单条明细
     * @param projEstablishId
     * @return rsp
     */
    public ProjEstablishAocPriceRSP detail(Long projEstablishId){
        return BeanUtil.copyProperties(getByProjEstablishId(projEstablishId), ProjEstablishAocPriceRSP.class);
    }

    /**
     * 查询单条明细
     * @param projEstablishId
     * @return entity
     */
    public ProjEstablishAocPrice getByProjEstablishId(Long projEstablishId) {
        return projEstablishAocPriceMapper.selectOne(Wrappers.<ProjEstablishAocPrice>lambdaQuery()
                .eq(ProjEstablishAocPrice::getProjEstablishId, projEstablishId));
    }

    /**
     * simple save or update
     *
     * @param req
     */
    @Validated
    @Transactional(rollbackFor = Throwable.class)
    public void modify(@Valid ProjEstablishAocPriceModifyREQ req) {
        saveCheck(req.getProjEstablishId());
        saveOrUpdate(BeanUtil.copyProperties(req, ProjEstablishAocPrice.class));
        recordStatus(req.getProjEstablishId());
    }
}
