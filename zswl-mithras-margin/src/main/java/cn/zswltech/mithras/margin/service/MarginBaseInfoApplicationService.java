package cn.zswltech.mithras.margin.service;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.margin.MarginBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.margin.MarginBaseInfoListREQ;
import cn.zswltech.mithras.dto.margin.MarginBaseInfoListRSP;
import cn.zswltech.mithras.dto.margin.MarginBaseInfoRSP;

import javax.servlet.ServletOutputStream;

public interface MarginBaseInfoApplicationService {

    PageR<MarginBaseInfoListRSP> list(MarginBaseInfoListREQ req);

    MarginBaseInfoRSP detail(MarginBaseInfoDetailREQ req);

    void exportList(MarginBaseInfoListREQ req, ServletOutputStream outputStream);
}
