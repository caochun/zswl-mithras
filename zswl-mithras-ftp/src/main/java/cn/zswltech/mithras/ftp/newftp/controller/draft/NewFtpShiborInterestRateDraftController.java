package cn.zswltech.mithras.ftp.newftp.controller.draft;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.newftp.draft.NewFtpShiborInterestRateDraftApi;
import cn.zswltech.mithras.dto.newftp.NewFtpCommonDetailReq;
import cn.zswltech.mithras.dto.newftp.NewFtpDetailTreasuryBondYieldListRSP;
import cn.zswltech.mithras.ftp.newftp.service.config.NewFtpShiborInterestRateConfigService;
import cn.zswltech.mithras.ftp.newftp.service.draft.NewFtpShiborInterestRateDraftService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;

/**
 * @author yangxiong
 * @date 2024/3/29/16:16
 * @description
 */
@RestController
public class NewFtpShiborInterestRateDraftController implements NewFtpShiborInterestRateDraftApi {

    @Resource
    private NewFtpShiborInterestRateDraftService newFtpShiborInterestRateDraftService;
    @Resource
    private NewFtpShiborInterestRateConfigService newFtpShiborInterestRateConfigService;

    @Override
    public R<PageR<NewFtpDetailTreasuryBondYieldListRSP>> shiborInterest(@Valid NewFtpCommonDetailReq req) {
        return R.ok(newFtpShiborInterestRateDraftService.shiborInterestList(req));
    }

    @Override
    public R<Void> importFile(MultipartFile file, Long mainId) throws IOException {
        newFtpShiborInterestRateConfigService.importFile(file.getInputStream());
        if(ObjectUtil.isNotEmpty(mainId)){
            newFtpShiborInterestRateDraftService.addShiborInterestRate(mainId);
        }
        return R.ok();
    }
}
