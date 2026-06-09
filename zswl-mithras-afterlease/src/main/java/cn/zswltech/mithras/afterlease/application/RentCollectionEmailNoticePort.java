package cn.zswltech.mithras.afterlease.application;

import java.io.OutputStream;

public interface RentCollectionEmailNoticePort {
    String renderHtml(OutputStream outputStream, Long collectionId, String comment, Long bankId) throws Exception;

    void send(String receiverMail, String title, String content, boolean html);
}
