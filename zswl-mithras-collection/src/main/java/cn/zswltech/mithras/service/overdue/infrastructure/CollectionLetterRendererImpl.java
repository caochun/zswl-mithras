package cn.zswltech.mithras.service.overdue.infrastructure;

import cn.zswltech.mithras.contract.overdue.application.collection.CollectionLetterRenderer;
import cn.zswltech.mithras.contract.overdue.domain.acl.ContractGuarantorInfo;
import cn.zswltech.mithras.contract.overdue.domain.acl.ContractLesseeInfo;
import cn.zswltech.mithras.service.gendoc.render.overduecollect.CollectionLetterRender;
import cn.zswltech.mithras.service.gendoc.render.overduecollect.CreditNotificationLetterRender;
import cn.zswltech.mithras.service.gendoc.render.overduecollect.JointLiabilityNoticeRender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.OutputStream;

@Service
public class CollectionLetterRendererImpl implements CollectionLetterRenderer {

    @Resource
    private CollectionLetterRender collectionLetterRender;
    @Resource
    private CreditNotificationLetterRender creditNotificationLetterRender;
    @Resource
    private JointLiabilityNoticeRender jointLiabilityNoticeRender;

    @Override
    public String renderCollectionLetter(OutputStream outputStream, ContractLesseeInfo contractLesseeInfo) throws Exception {
        return collectionLetterRender.render(outputStream, contractLesseeInfo);
    }

    @Override
    public String renderCreditNotificationLetter(OutputStream outputStream, ContractLesseeInfo contractLesseeInfo) throws Exception {
        return creditNotificationLetterRender.render(outputStream, contractLesseeInfo);
    }

    @Override
    public String renderJointLiabilityNotice(OutputStream outputStream, ContractGuarantorInfo contractGuarantorInfo) throws Exception {
        return jointLiabilityNoticeRender.render(outputStream, contractGuarantorInfo);
    }
}
