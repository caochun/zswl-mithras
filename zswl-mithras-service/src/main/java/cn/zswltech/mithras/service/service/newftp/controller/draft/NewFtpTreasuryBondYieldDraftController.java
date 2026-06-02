package cn.zswltech.mithras.service.service.newftp.controller.draft;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.newftp.draft.NewFtpTreasuryBondYieldDraftApi;
import cn.zswltech.mithras.dto.newftp.NewFtpCommonDetailReq;
import cn.zswltech.mithras.dto.newftp.NewFtpDetailTreasuryBondYieldListRSP;
import cn.zswltech.mithras.ftp.newftp.service.config.NewFtpTreasuryBondYieldConfigService;
import cn.zswltech.mithras.ftp.newftp.service.drift.NewFtpTreasuryBondYieldDraftService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;

/**
 * @author yangxiong
 * @date 2024/3/29/16:23
 * @description
 */
@RestController
public class NewFtpTreasuryBondYieldDraftController implements NewFtpTreasuryBondYieldDraftApi {

    @Resource
    private NewFtpTreasuryBondYieldConfigService yieldService;
    @Resource
    private NewFtpTreasuryBondYieldDraftService newFtpTreasuryBondYieldDraftService;

    @Override
    public R<Void> importFile(MultipartFile file, Long mainId) throws IOException {
        yieldService.importFile(file.getInputStream());
        if (ObjectUtil.isNotEmpty(mainId)) {
            newFtpTreasuryBondYieldDraftService.addTreasuryBondYield(mainId);
        }
        return R.ok();
    }

    /**
     * 查询十年期国债
     * 直接查询编辑区即可
     **/
    @Override
    public R<PageR<NewFtpDetailTreasuryBondYieldListRSP>> treasuryBondYield(@Valid NewFtpCommonDetailReq req) {
        return R.ok(newFtpTreasuryBondYieldDraftService.treasuryBondYieldList(req));
    }

}
