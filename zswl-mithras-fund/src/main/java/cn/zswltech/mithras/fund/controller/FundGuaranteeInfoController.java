package cn.zswltech.mithras.fund.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.FundGuaranteeInfoApi;
import cn.zswltech.mithras.dto.fund.*;
import cn.zswltech.mithras.fund.application.FundGuaranteeInfoApplicationService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
public class FundGuaranteeInfoController implements FundGuaranteeInfoApi {
    @Resource
    private FundGuaranteeInfoApplicationService fundGuaranteeInfoApplicationService;

    @Override
    public R<Void> add(MultipartFile[] files, FundGuaranteeInfoAddREQ req) {
        return fundGuaranteeInfoApplicationService.add(files, req);
    }

    @Override
    public R<Void> modify(MultipartFile[] addFiles, FundGuaranteeInfoModifyREQ req) {
        return fundGuaranteeInfoApplicationService.modify(addFiles, req);
    }

    @Override
    public R<PageR<FundGuaranteeInfoListRSP>> list(FundGuaranteeInfoListREQ req) {
        return fundGuaranteeInfoApplicationService.list(req);
    }

    @Override
    public R<FundGuaranteeInfoDetailRSP> detail(FundGuaranteeInfoDetailREQ req) {
        return fundGuaranteeInfoApplicationService.detail(req);
    }

    @Override
    public R<Void> remove(FundGuaranteeInfoRemoveREQ req) {
        return fundGuaranteeInfoApplicationService.remove(req);
    }
}
