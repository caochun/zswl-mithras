package cn.zswltech.mithras.message.client.ding.body;

import lombok.Data;

import java.util.List;

/**
 * @author junke
 */
@Data
public class AtBody {
    private List<String> atMobiles;
    private List<String> atUserIds;
    private Boolean isAtAll;
}
