package cn.zswltech.mithras.service.service.ftp;

import cn.zswltech.mithras.service.mapper.ftp.FtpQuarterlyMonthPricingMapper;
import cn.zswltech.mithras.service.mapper.model.ftp.FtpQuarterlyMonthPricing;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/9 15:48
 */
@Service
public class FtpQuarterlyMonthPricingService
        extends ServiceImpl<FtpQuarterlyMonthPricingMapper, FtpQuarterlyMonthPricing> {
    public List<FtpQuarterlyMonthPricing> selectByGuidanceId(Long guidanceId) {
        return baseMapper.selectList(Wrappers.<FtpQuarterlyMonthPricing>lambdaQuery()
                .eq(FtpQuarterlyMonthPricing::getGuidanceId, guidanceId));
    }
}
