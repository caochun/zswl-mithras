package cn.zswltech.mithras.message.client.ding;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

/**
 * @author junke
 * {"errcode":310000,"errmsg":"sign not match"}
 */
@Data
public class DingResp {
    @Alias("errcode")
    private Integer errCode;
    @Alias("errmsg")
    private String errMsg;
}
