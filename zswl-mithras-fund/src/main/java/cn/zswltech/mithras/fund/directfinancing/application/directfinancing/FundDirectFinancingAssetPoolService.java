package cn.zswltech.mithras.fund.directfinancing.application.directfinancing;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingAssetPoolDetailRSP;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingAssetPoolModifyREQ;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.fund.directfinancing.application.directfinancing.convert.FundDirectFinancingAssetPoolConverter;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingAssetPool;
import cn.zswltech.mithras.fund.directfinancing.mapper.FundDirectFinancingAssetPoolMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author zhaozhengkang
 * @description 直接融资-资产池信息
 * @date 2023-06-17
 */
@Service
public class FundDirectFinancingAssetPoolService
        extends ServiceImpl<FundDirectFinancingAssetPoolMapper, FundDirectFinancingAssetPool> {

    @Resource
    private FundDirectFinancingAssetPoolConverter assetPoolConverter;

    @Transactional(rollbackFor = Throwable.class)
    public void modify(FundDirectFinancingAssetPoolModifyREQ req) {
//        FundDirectFinancingAssetPool originalInfo = baseMapper.selectById(req.getId());
        FundDirectFinancingAssetPool originalInfo = baseMapper.selectOne(Wrappers.<FundDirectFinancingAssetPool>lambdaQuery()
                .eq(FundDirectFinancingAssetPool::getFinancingId, req.getId())
                .last("limit 1"));
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        FundDirectFinancingAssetPool info = assetPoolConverter.modifyReq2Entity(req);
        info.setId(originalInfo.getId());
        baseMapper.updateById(info);
    }

    public FundDirectFinancingAssetPoolDetailRSP detail(Long financingId) {
        FundDirectFinancingAssetPool assetPool = baseMapper.selectOne(Wrappers.<FundDirectFinancingAssetPool>lambdaQuery()
                .eq(FundDirectFinancingAssetPool::getFinancingId, financingId)
                .last("limit 1"));
        return assetPoolConverter.entity2DetailRsp(assetPool);
    }
}