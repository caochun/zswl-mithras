package cn.zswltech.mithras.afterlease.adapter;

import cn.zswltech.mithras.afterlease.application.RentCollectionEmailNoticePort;
import cn.zswltech.mithras.afterlease.genhtml.PaymentNoticeHtmlRender;
import cn.zswltech.mithras.message.util.EmailUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.OutputStream;

@Component
public class RentCollectionEmailNoticePortAdapter implements RentCollectionEmailNoticePort {
    @Resource
    private PaymentNoticeHtmlRender paymentNoticeHtmlRender;

    @Override
    public String renderHtml(OutputStream outputStream, Long collectionId, String comment, Long bankId) throws Exception {
        return paymentNoticeHtmlRender.render(outputStream, collectionId, comment, bankId);
    }

    @Override
    public void send(String receiverMail, String title, String content, boolean html) {
        EmailUtil.send(receiverMail, title, content, html);
    }
}
