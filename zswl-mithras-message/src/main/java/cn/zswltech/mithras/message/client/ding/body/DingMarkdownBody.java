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
public class DingMarkdownBody extends DingBody {

    private AtBody at;
    private MarkdownBody markdown;

    public DingMarkdownBody() {
        this.setMsgType(DingType.MARKDOWN.name().toLowerCase());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MarkdownBody {
        private String title;
        private String text;
    }

}
