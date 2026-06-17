package cn.zswltech.mithras.collection.overdue.render;

import java.io.OutputStream;

public interface CollectionOverdueLetterRenderService {

    String renderCollectionLetter(OutputStream outputStream, CollectionLesseeLetterData lesseeLetterData) throws Exception;

    String renderCreditNotificationLetter(OutputStream outputStream, CollectionLesseeLetterData lesseeLetterData) throws Exception;

    String renderJointLiabilityNotice(OutputStream outputStream, CollectionGuarantorLetterData guarantorLetterData) throws Exception;
}
