package cn.zswltech.mithras.afterlease.controller;

import cn.zswltech.mithras.afterlease.application.RentCollectionEmailApplicationService;
import cn.zswltech.mithras.api.afterlease.RentCollectionEmailApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.afterlease.RentCollectionEmailDetailREQ;
import cn.zswltech.mithras.dto.afterlease.RentCollectionEmailDetailRSP;
import cn.zswltech.mithras.dto.afterlease.RentCollectionEmailGenREQ;
import cn.zswltech.mithras.dto.afterlease.RentCollectionEmailSendREQ;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
public class RentCollectionEmailController implements RentCollectionEmailApi {

    @Resource
    private RentCollectionEmailApplicationService rentCollectionEmailApplicationService;

    @Override
    public R<Void> sendEmail(@Valid RentCollectionEmailSendREQ req) {
        return rentCollectionEmailApplicationService.sendEmail(req);
    }

    @Override
    public R<Long> genEmail(@Valid RentCollectionEmailGenREQ req) {
        return rentCollectionEmailApplicationService.genEmail(req);
    }

    @Override
    public R<String> genEmailHtml(@Valid RentCollectionEmailGenREQ req) {
        return rentCollectionEmailApplicationService.genEmailHtml(req);
    }

    @Override
    public R<RentCollectionEmailDetailRSP> detail(@Valid RentCollectionEmailDetailREQ req) {
        return rentCollectionEmailApplicationService.detail(req);
    }

    @Override
    public byte[] htmlPreview(String htmlKey) {
        return rentCollectionEmailApplicationService.htmlPreview(htmlKey);
    }

    @Override
    public R<Void> down(Long collectionId, String accountName, String accountBank, String accountNumber) {
        return rentCollectionEmailApplicationService.down(collectionId, accountName, accountBank, accountNumber);
    }
}
