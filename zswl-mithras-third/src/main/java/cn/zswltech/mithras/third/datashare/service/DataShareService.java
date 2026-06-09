package cn.zswltech.mithras.third.datashare.service;


import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.client.share.DataShareRegisterCustomREQ;
import cn.zswltech.mithras.dto.client.share.DataShareUserREQ;
import cn.zswltech.mithras.dto.client.share.DataShareUserRSP;

import java.util.List;

public interface DataShareService {

    Boolean syncMerchants();

    //同步集团数据至租赁
    Boolean syscMainCode();

    //前端点击调用同步
    DataShareUserRSP getMainCode(DataShareUserREQ req);

    List<SelectRSP> getMainOrg();

    //去集团注册客户
    String registerCustom(DataShareRegisterCustomREQ req);

}
