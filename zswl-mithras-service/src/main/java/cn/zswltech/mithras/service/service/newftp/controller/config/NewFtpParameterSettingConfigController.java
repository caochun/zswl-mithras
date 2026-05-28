package cn.zswltech.mithras.service.service.newftp.controller.config;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.newftp.config.NewFtpParameterSettingConfigApi;
import cn.zswltech.mithras.dto.newftp.NewFtpParameterSettingConfigListRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpParameterSettingListREQ;
import cn.zswltech.mithras.dto.newftp.NewFtpParameterSettingModifyREQ;
import cn.zswltech.mithras.service.enums.newftp.ParamCategory;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.newftp.model.config.NewFtpParameterSettingConfig;
import cn.zswltech.mithras.service.service.newftp.service.config.NewFtpParameterSettingConfigService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @date 2024/3/29/16:13
 * @description
 */
@RestController
public class NewFtpParameterSettingConfigController implements NewFtpParameterSettingConfigApi {

    private static final long FINANCIAL_MARKET_VOLATILITY_TOTAL = 1000000L;
    @Resource
    private NewFtpParameterSettingConfigService newFtpParameterSettingConfigService;

    @Override
    public R<Void> modify(List<NewFtpParameterSettingModifyREQ> reqs) {
        List<NewFtpParameterSettingConfig> info = newFtpParameterSettingConfigService.req2Domain(reqs);
        if (reqs.get(0).getCategory().equals(ParamCategory.FINANCIAL_MARKET_VOLATILITY.name())) {
            Integer total = reqs.stream().map(NewFtpParameterSettingModifyREQ::getValue).reduce(0, Integer::sum);
            if (total != FINANCIAL_MARKET_VOLATILITY_TOTAL) {
                throw new MithrasException("权重总和必须为100");
            }
        }
        if (ObjectUtil.isNotEmpty(info)) {
            newFtpParameterSettingConfigService.updateBatchById(info);
        }
        return R.ok();
    }

    @Override
    public R<Map<String, List<NewFtpParameterSettingConfigListRSP>>> list(NewFtpParameterSettingListREQ req) {
        List<NewFtpParameterSettingConfig> data = newFtpParameterSettingConfigService.list(req);
        return R.ok(newFtpParameterSettingConfigService.domain2Rsp(data)
                .stream().collect(Collectors.groupingBy(NewFtpParameterSettingConfigListRSP::getCategoryDisplay)));
    }
}
