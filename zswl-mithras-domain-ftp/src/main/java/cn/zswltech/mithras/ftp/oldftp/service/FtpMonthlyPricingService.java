package cn.zswltech.mithras.ftp.oldftp.service;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.ftp.FtpGuidanceIdReq;
import cn.zswltech.mithras.dto.ftp.FtpMonthlyPricingRsp;
import cn.zswltech.mithras.ftp.oldftp.convert.FtpMonthlyGuidanceConverter;
import cn.zswltech.mithras.ftp.oldftp.mapper.FtpMonthlyPricingMapper;
import cn.zswltech.mithras.ftp.oldftp.mapper.lib.FtpMonthlyPricingLibMapper;
import cn.zswltech.mithras.ftp.oldftp.model.FtpMonthlyPricing;
import cn.zswltech.mithras.ftp.oldftp.model.FtpMonthlyPricingLib;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 月度ftp定价指导
 * @date 2023-01-10
 */
@Service
public class FtpMonthlyPricingService extends ServiceImpl<FtpMonthlyPricingMapper, FtpMonthlyPricing> {
    @Resource
    private FtpMonthlyPricingLibMapper libMapper;
    @Resource
    private FtpMonthlyGuidanceConverter converter;

    public List<FtpMonthlyPricingRsp> detailPricing(FtpGuidanceIdReq req) {
        if(ObjectUtil.isNotEmpty(req.getVersion())){
            List<FtpMonthlyPricingLib> pricingLibs = libMapper.selectList(Wrappers.<FtpMonthlyPricingLib>lambdaQuery()
                    .eq(FtpMonthlyPricing::getGuidanceId, req.getId())
                    .eq(FtpMonthlyPricingLib::getVersion, req.getVersion()));
            List<FtpMonthlyPricingRsp> rspList = pricingLibs.stream().map(pricingLib -> {
                FtpMonthlyPricingRsp pricingRsp = converter.pricingLib2Rsp(pricingLib);
                pricingRsp.setId(pricingLib.getOriginId());
                return pricingRsp;
            }).collect(Collectors.toList());
            return rspList;
        }else {
            return converter.pricingEntity2Rsp(baseMapper.selectList(Wrappers.<FtpMonthlyPricing>lambdaQuery()
                    .eq(FtpMonthlyPricing::getGuidanceId, req.getId())));
        }
    }

    public List<FtpMonthlyPricing> selectByGuidance(Long id) {
        return baseMapper.selectList(Wrappers.<FtpMonthlyPricing>lambdaQuery()
                .eq(FtpMonthlyPricing::getGuidanceId, id));
    }
}
