package cn.zswltech.mithras.service.service.newftp.controller.config;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.newftp.config.NewFtpLprPricingConfigApi;
import cn.zswltech.mithras.dto.newftp.NewFtpLprPricingAddREQ;
import cn.zswltech.mithras.dto.newftp.NewFtpLprPricingListRSP;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.basedata.BaseDataLprService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yangxiong
 * @date 2024/3/29/16:06
 * @description
 */
@RestController
public class NewFtpLprPricingConfigController implements NewFtpLprPricingConfigApi {

    @Resource
    private BaseDataLprService baseDataLprService;

    @Override
    public R<List<NewFtpLprPricingListRSP>> list() {
        return R.ok(baseDataLprService.listWithDiff());
    }

    @Override
    public R<Void> add(NewFtpLprPricingAddREQ req) {
        throw new MithrasException("不支持新增，请在财务管理-LPR设置中操作");
    }
}
