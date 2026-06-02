package cn.zswltech.mithras.service.service.newftp.controller.config;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.newftp.config.NewFtpShiborInterestRateConfigApi;
import cn.zswltech.mithras.dto.PageReq;
import cn.zswltech.mithras.dto.newftp.NewFtpShiborInterestRateListRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpShiborInterestRatePricingListRSP;
import cn.zswltech.mithras.service.service.newftp.convert.NewFtpShiborInterestRateConfigConverter;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpShiborInterestRateConfig;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpShiborInterestRatePricingConfig;
import cn.zswltech.mithras.service.service.newftp.service.config.NewFtpShiborInterestRateConfigService;
import cn.zswltech.mithras.ftp.newftp.service.config.NewFtpShiborInterestRatePricingConfigService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yangxiong
 * @date 2024/3/29/16:19
 * @description
 */
@RestController
public class NewFtpShiborInterestRateConfigController implements NewFtpShiborInterestRateConfigApi {

    @Resource
    private NewFtpShiborInterestRateConfigService newFtpShiborInterestRateConfigService;
    @Resource
    private NewFtpShiborInterestRatePricingConfigService newFtpShiborInterestRatePricingConfigService;
    @Resource
    private NewFtpShiborInterestRateConfigConverter baseConverter;

    @Override
    public R<PageR<NewFtpShiborInterestRateListRSP>> list(PageReq req) {
        Page<NewFtpShiborInterestRateConfig> page = newFtpShiborInterestRateConfigService.page(
                new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<NewFtpShiborInterestRateConfig>lambdaQuery()
                        .orderByDesc(NewFtpShiborInterestRateConfig::getDate));
        List<NewFtpShiborInterestRateListRSP> rspList = baseConverter.entity2ListRSP(page.getRecords());
        return R.ok(PageR.of(page, rspList));
    }

    @Override
    public R<PageR<NewFtpShiborInterestRatePricingListRSP>> listprincing(PageReq req) {
        Page<NewFtpShiborInterestRatePricingConfig> page = newFtpShiborInterestRatePricingConfigService.page(
                new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<NewFtpShiborInterestRatePricingConfig>lambdaQuery()
                        .orderByDesc(NewFtpShiborInterestRatePricingConfig::getMonth));
        List<NewFtpShiborInterestRatePricingListRSP> rspList =
                baseConverter.pricingEntity2ListRSP(page.getRecords());
        return R.ok(PageR.of(page, rspList));
    }
}
