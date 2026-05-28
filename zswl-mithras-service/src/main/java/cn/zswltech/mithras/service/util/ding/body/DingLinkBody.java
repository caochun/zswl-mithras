package cn.zswltech.mithras.service.util.ding.body;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author junke
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class DingLinkBody extends DingBody {
    private LinkBody link;

    @Data
    public static class LinkBody {
        private String text;
        private String title;
        private String picUrl;
        private String messageUrl;
    }


}
