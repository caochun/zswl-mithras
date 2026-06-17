package cn.zswltech.mithras.collection.overdue.infrastructure;

import cn.zswltech.mithras.collection.gendoc.render.overduecollect.CollectionLetterRender;
import cn.zswltech.mithras.collection.gendoc.render.overduecollect.CreditNotificationLetterRender;
import cn.zswltech.mithras.collection.gendoc.render.overduecollect.JointLiabilityNoticeRender;
import cn.zswltech.mithras.collection.overdue.render.CollectionGuarantorLetterData;
import cn.zswltech.mithras.collection.overdue.render.CollectionLesseeLetterData;
import cn.zswltech.mithras.collection.overdue.render.CollectionOverdueLetterRenderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.OutputStream;

@Service
public class CollectionLetterRendererImpl implements CollectionOverdueLetterRenderService {

    @Resource
    private CollectionLetterRender collectionLetterRender;
    @Resource
    private CreditNotificationLetterRender creditNotificationLetterRender;
    @Resource
    private JointLiabilityNoticeRender jointLiabilityNoticeRender;

    @Override
    public String renderCollectionLetter(OutputStream outputStream, CollectionLesseeLetterData contractLesseeInfo) throws Exception {
        return collectionLetterRender.render(outputStream, contractLesseeInfo);
    }

    @Override
    public String renderCreditNotificationLetter(OutputStream outputStream, CollectionLesseeLetterData contractLesseeInfo) throws Exception {
        return creditNotificationLetterRender.render(outputStream, contractLesseeInfo);
    }

    @Override
    public String renderJointLiabilityNotice(OutputStream outputStream, CollectionGuarantorLetterData contractGuarantorInfo) throws Exception {
        return jointLiabilityNoticeRender.render(outputStream, contractGuarantorInfo);
    }
}
