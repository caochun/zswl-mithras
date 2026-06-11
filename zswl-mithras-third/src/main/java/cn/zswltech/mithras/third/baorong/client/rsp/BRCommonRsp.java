package cn.zswltech.mithras.third.baorong.client.rsp;

import cn.zswltech.mithras.third.baorong.client.req.CwgsApiAppUser;
import cn.zswltech.mithras.third.baorong.client.req.CwgsHead;
import lombok.Data;

@Data
public class BRCommonRsp {
    private CwgsHead cwgsHead;
    private CwgsApiAppUser cwgsApiAppUser;
}