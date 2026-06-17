package cn.zswltech.mithras.application.orchestration.adapter.collection;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.collection.overdue.render.CollectionGuarantorLetterData;
import cn.zswltech.mithras.collection.overdue.render.CollectionLesseeLetterData;
import cn.zswltech.mithras.collection.overdue.render.CollectionOverdueLetterRenderService;
import cn.zswltech.mithras.contract.overdue.application.collection.CollectionLetterRenderer;
import cn.zswltech.mithras.contract.overdue.domain.acl.ContractGuarantorInfo;
import cn.zswltech.mithras.contract.overdue.domain.acl.ContractLesseeInfo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.OutputStream;

@Component
public class ContractCollectionLetterRendererAdapter implements CollectionLetterRenderer {

    @Resource
    private CollectionOverdueLetterRenderService collectionOverdueLetterRenderService;

    @Override
    public String renderCollectionLetter(OutputStream outputStream, ContractLesseeInfo contractLesseeInfo) throws Exception {
        CollectionLesseeLetterData letterData = BeanUtil.copyProperties(contractLesseeInfo, CollectionLesseeLetterData.class);
        return collectionOverdueLetterRenderService.renderCollectionLetter(outputStream, letterData);
    }

    @Override
    public String renderCreditNotificationLetter(OutputStream outputStream, ContractLesseeInfo contractLesseeInfo) throws Exception {
        CollectionLesseeLetterData letterData = BeanUtil.copyProperties(contractLesseeInfo, CollectionLesseeLetterData.class);
        return collectionOverdueLetterRenderService.renderCreditNotificationLetter(outputStream, letterData);
    }

    @Override
    public String renderJointLiabilityNotice(OutputStream outputStream, ContractGuarantorInfo contractGuarantorInfo) throws Exception {
        CollectionGuarantorLetterData letterData = BeanUtil.copyProperties(contractGuarantorInfo, CollectionGuarantorLetterData.class);
        return collectionOverdueLetterRenderService.renderJointLiabilityNotice(outputStream, letterData);
    }
}
