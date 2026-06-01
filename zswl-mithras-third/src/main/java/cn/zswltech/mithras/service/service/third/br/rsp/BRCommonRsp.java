package cn.zswltech.mithras.service.service.third.br.rsp;

import cn.zswltech.mithras.service.service.third.br.req.CwgsApiAppUser;
import cn.zswltech.mithras.service.service.third.br.req.CwgsHead;
import lombok.Data;

@Data
public class BRCommonRsp {
    private CwgsHead cwgsHead;
    private CwgsApiAppUser cwgsApiAppUser;
}