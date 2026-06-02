package cn.zswltech.mithras.contract.overdue.application.collection;

import cn.zswltech.mithras.contract.overdue.domain.acl.ContractGuarantorInfo;
import cn.zswltech.mithras.contract.overdue.domain.acl.ContractLesseeInfo;

import java.io.OutputStream;

public interface CollectionLetterRenderer {

    String renderCollectionLetter(OutputStream outputStream, ContractLesseeInfo contractLesseeInfo) throws Exception;

    String renderCreditNotificationLetter(OutputStream outputStream, ContractLesseeInfo contractLesseeInfo) throws Exception;

    String renderJointLiabilityNotice(OutputStream outputStream, ContractGuarantorInfo contractGuarantorInfo) throws Exception;
}
