package cn.zswltech.mithras.message.client.ding;


import cn.zswltech.mithras.message.client.ding.body.DingTextBody;

/**
 * @author junke
 */
public class DingService {
    public static void ding(String text, DingGroups.DingGroup dingGroup) {
        DingTextBody textBody = new DingTextBody();
        textBody.setText(new DingTextBody.TextCtn(text));
        DingUtil.ding(new DingObj(dingGroup.getWebhook(), dingGroup.getToken(), textBody));
    }

}
