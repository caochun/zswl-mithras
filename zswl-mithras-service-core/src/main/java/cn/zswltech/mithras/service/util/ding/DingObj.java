package cn.zswltech.mithras.service.util.ding;

import cn.zswltech.mithras.service.util.ding.body.DingBody;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author junke
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DingObj {
    private String webHook;
    private String token;
    private DingBody body;
}


