package cn.zswltech.mithras.ftp.oldftp.service;

import cn.zswltech.mithras.dto.ftp.FtpQuarterlyEnterprisePricingRsp;
import cn.zswltech.mithras.ftp.oldftp.mapper.FtpQuarterlyEnterprisePricingMapper;
import cn.zswltech.mithras.ftp.oldftp.model.FtpQuarterlyEnterprisePricing;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/9 15:49
 */
@Service
public class FtpQuarterlyEnterprisePricingService
        extends ServiceImpl<FtpQuarterlyEnterprisePricingMapper, FtpQuarterlyEnterprisePricing> {
    public List<FtpQuarterlyEnterprisePricing> selectByGuidanceId(Long guidanceId) {
        return baseMapper.selectList(Wrappers.<FtpQuarterlyEnterprisePricing>lambdaQuery().eq(FtpQuarterlyEnterprisePricing::getGuidanceId, guidanceId));
    }
}
