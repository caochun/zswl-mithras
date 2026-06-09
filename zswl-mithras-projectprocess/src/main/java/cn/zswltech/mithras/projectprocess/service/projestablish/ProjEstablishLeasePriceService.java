package cn.zswltech.mithras.projectprocess.service.projestablish;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.projestablish.pricelease.ProjEstablishLeasePriceListREQ;
import cn.zswltech.mithras.dto.projestablish.pricelease.ProjEstablishLeasePriceModifyREQ;
import cn.zswltech.mithras.dto.projestablish.pricelease.ProjEstablishLeasePriceRSP;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishLeasePrice;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishLeasePriceMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author zhaozhengkang
 * @description 租赁报价方案表
 * @date 2022-07-19
 */
@Service
@Validated
public class ProjEstablishLeasePriceService extends ServiceImpl<ProjEstablishLeasePriceMapper, ProjEstablishLeasePrice>
        implements ProjEstablishUpdateAdvice {

    @Resource
    private ProjEstablishLeasePriceMapper projEstablishLeasePriceMapper;
    /**
     * 单条明细
     *
     * @param projEstablishId
     * @return rsp
     */
    public ProjEstablishLeasePriceRSP detail(Long projEstablishId) {
        return BeanUtil.copyProperties(getByProjEstablishId(projEstablishId), ProjEstablishLeasePriceRSP.class);
    }

    /**
     * 单条明细
     *
     * @param projEstablishId
     * @return entity
     */
    public ProjEstablishLeasePrice getByProjEstablishId(Long projEstablishId) {
        return projEstablishLeasePriceMapper.selectOne(Wrappers.<ProjEstablishLeasePrice>lambdaQuery()
                .eq(ProjEstablishLeasePrice::getProjEstablishId, projEstablishId));
    }

    /**
     * simple save or update
     *
     * @param req
     */
    @Validated
    @Transactional(rollbackFor = Throwable.class)
    public void modify(@Valid ProjEstablishLeasePriceModifyREQ req) {
        //  校验手续费和首期利息
        Long commission = req.getCommission();
        if (!ObjectUtils.isEmpty(commission) && commission < 0) {
            throw new MithrasException("手续费必须填写大于等于零的数");
        }
        Long firstInstallmentInterest = req.getFirstInstallmentInterest();
        if (!ObjectUtils.isEmpty(firstInstallmentInterest) && firstInstallmentInterest < 0) {
            throw new MithrasException("首期利息必须填写大于等于零的数");
        }
        saveCheck(req.getProjEstablishId());
        saveOrUpdate(BeanUtil.copyProperties(req, ProjEstablishLeasePrice.class));
        recordStatus(req.getProjEstablishId());
    }

    public Page<ProjEstablishLeasePrice> list(ProjEstablishLeasePriceListREQ req) {
        return projEstablishLeasePriceMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<ProjEstablishLeasePrice>lambdaQuery()
                        .eq(ProjEstablishLeasePrice::getProjEstablishId, req.getProjEstablishId()));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(Long projEstablishId) {
        ProjEstablishLeasePrice originalInfo = projEstablishLeasePriceMapper.selectById(projEstablishId);
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        projEstablishLeasePriceMapper.deleteById(projEstablishId);
    }

    public ProjEstablishLeasePrice getById(Long id) {
        return projEstablishLeasePriceMapper.selectById(id);
    }


}
