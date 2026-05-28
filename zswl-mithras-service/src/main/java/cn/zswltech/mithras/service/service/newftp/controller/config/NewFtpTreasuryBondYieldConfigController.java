package cn.zswltech.mithras.service.service.newftp.controller.config;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.newftp.config.NewFtpTreasuryBondYieldConfigApi;
import cn.zswltech.mithras.dto.PageReq;
import cn.zswltech.mithras.dto.newftp.NewFtpTreasuryBondYieldListRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpTreasuryBondYieldPricingListRSP;
import cn.zswltech.mithras.service.service.newftp.convert.NewFtpTreasuryBondYieldConfigConverter;
import cn.zswltech.mithras.service.service.newftp.model.config.NewFtpTreasuryBondYieldConfig;
import cn.zswltech.mithras.service.service.newftp.model.config.NewFtpTreasuryBondYieldPricingConfig;
import cn.zswltech.mithras.service.service.newftp.service.config.NewFtpTreasuryBondYieldConfigService;
import cn.zswltech.mithras.service.service.newftp.service.config.NewFtpTreasuryBondYieldPricingConfigService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yangxiong
 * @date 2024/3/29/16:20
 * @description
 */
@RestController
public class NewFtpTreasuryBondYieldConfigController implements NewFtpTreasuryBondYieldConfigApi {
    @Resource
    private NewFtpTreasuryBondYieldConfigService yieldService;
    @Resource
    private NewFtpTreasuryBondYieldPricingConfigService yieldPricingService;
    @Resource
    private NewFtpTreasuryBondYieldConfigConverter baseConverter;

    @Override
    public R<PageR<NewFtpTreasuryBondYieldListRSP>> list(PageReq req) {
        Page<NewFtpTreasuryBondYieldConfig> page = yieldService.page(
                new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<NewFtpTreasuryBondYieldConfig>lambdaQuery()
                        .orderByDesc(NewFtpTreasuryBondYieldConfig::getDate));
        List<NewFtpTreasuryBondYieldListRSP> rspList = baseConverter.entity2rsp(page.getRecords());
        return R.ok(PageR.of(page, rspList));
    }

    @Override
    public R<PageR<NewFtpTreasuryBondYieldPricingListRSP>> listprincing(PageReq req) {
        Page<NewFtpTreasuryBondYieldPricingConfig> page = yieldPricingService.page(
                new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<NewFtpTreasuryBondYieldPricingConfig>lambdaQuery()
                        .orderByDesc(NewFtpTreasuryBondYieldPricingConfig::getMonth));
        List<NewFtpTreasuryBondYieldPricingListRSP> rspList = baseConverter.pricingEntity2rsp(page.getRecords());
        return R.ok(PageR.of(page, rspList));
    }
}
