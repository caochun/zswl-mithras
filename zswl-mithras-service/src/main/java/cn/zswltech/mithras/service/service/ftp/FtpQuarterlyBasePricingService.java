package cn.zswltech.mithras.service.service.ftp;

import cn.zswltech.mithras.service.mapper.ftp.FtpQuarterlyBasePricingMapper;
import cn.zswltech.mithras.service.mapper.model.ftp.FtpQuarterlyBasePricing;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/9 14:49
 */
@Service
public class FtpQuarterlyBasePricingService
        extends ServiceImpl<FtpQuarterlyBasePricingMapper, FtpQuarterlyBasePricing> {

    public List<FtpQuarterlyBasePricing> selectByGuidanceId(Long guidanceId){
        return baseMapper.selectList(Wrappers.<FtpQuarterlyBasePricing>lambdaQuery()
                .eq(FtpQuarterlyBasePricing::getGuidanceId, guidanceId));
    }
}
