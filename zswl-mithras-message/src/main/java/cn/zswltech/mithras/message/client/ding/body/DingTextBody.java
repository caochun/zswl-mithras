package cn.zswltech.mithras.message.client.ding.body;

import cn.zswltech.mithras.message.client.ding.DingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author junke
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class DingTextBody extends DingBody {
    private TextCtn text;
    private AtBody at;

    public DingTextBody() {
        this.setMsgType(DingType.TEXT.name().toLowerCase());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TextCtn {
        private String content;
    }
}
