package cn.zswltech.mithras.ftp.newftp.controller.draft;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.newftp.draft.NewFtpLprPricingDraftApi;
import cn.zswltech.mithras.dto.newftp.NewFtpCommonDetailReq;
import cn.zswltech.mithras.dto.newftp.NewFtpDetailLprPricingListRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpLprPricingModifyREQ;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.ftp.newftp.service.draft.NewFtpLprPricingDraftService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author yangxiong
 * @date 2024/3/29/16:08
 * @description
 */
@RestController
public class NewFtpLprPricingDraftController implements NewFtpLprPricingDraftApi {

    @Resource
    private NewFtpLprPricingDraftService newFtpLprPricingDraftService;

    @Override
    public R<Void> modify(NewFtpLprPricingModifyREQ req) {
        throw new MithrasException("不支持修改，请在财务管理-LPR设置中操作");
    }

    @Override
    public R<PageR<NewFtpDetailLprPricingListRSP>> lprPricingList(@Valid NewFtpCommonDetailReq req) {
        return R.ok(newFtpLprPricingDraftService.lprPricingList(req));
    }
}
