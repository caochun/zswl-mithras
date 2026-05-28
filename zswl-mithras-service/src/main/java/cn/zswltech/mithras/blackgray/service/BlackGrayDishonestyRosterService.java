package cn.zswltech.mithras.blackgray.service;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayDishonestyRosterREQ;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayDishonestyRosterRSP;
import com.github.pagehelper.PageInfo;

public interface BlackGrayDishonestyRosterService {

    R<PageInfo<BlackGrayDishonestyRosterRSP>> query(BlackGrayDishonestyRosterREQ req);
}
