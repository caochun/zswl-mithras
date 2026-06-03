package cn.zswltech.mithras.third.baorong.infrastructure.client.rsp;

import cn.zswltech.mithras.third.baorong.infrastructure.client.req.CwgsApiAppUser;
import cn.zswltech.mithras.third.baorong.infrastructure.client.req.CwgsHead;
import lombok.Data;

@Data
public class BRCommonRsp {
    private CwgsHead cwgsHead;
    private CwgsApiAppUser cwgsApiAppUser;
}