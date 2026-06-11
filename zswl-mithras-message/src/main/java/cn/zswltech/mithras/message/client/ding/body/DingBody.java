package cn.zswltech.mithras.message.client.ding.body;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

/**
 * @author junke
 */
@Data
public class DingBody {
    @Alias("msgtype")
    protected String msgType;
}
