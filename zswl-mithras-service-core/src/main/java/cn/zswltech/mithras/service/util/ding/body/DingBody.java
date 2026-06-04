package cn.zswltech.mithras.service.util.ding.body;

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
