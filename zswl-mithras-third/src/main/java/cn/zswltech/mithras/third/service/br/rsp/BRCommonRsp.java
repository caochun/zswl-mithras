package cn.zswltech.mithras.third.service.br.rsp;

import cn.zswltech.mithras.third.service.br.req.CwgsApiAppUser;
import cn.zswltech.mithras.third.service.br.req.CwgsHead;
import lombok.Data;

@Data
public class BRCommonRsp {
    private CwgsHead cwgsHead;
    private CwgsApiAppUser cwgsApiAppUser;
}