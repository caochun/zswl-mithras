package cn.zswltech.mithras.blackgray.service;


import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayManualOutboundAddREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayManualOutboundListREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayManualOutboundModifyREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayManualOutboundRemoveREQ;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayManualOutboundDetailRSP;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayManualOutboundListRSP;

/**
* @description 黑灰名单人工出库表
* @author
* @date 2023-11-29
*/
public interface BlackGrayManualOutboundService {

    Long add(BlackGrayManualOutboundAddREQ req);

    void modify(BlackGrayManualOutboundModifyREQ req);

    PageR<BlackGrayManualOutboundListRSP> list(BlackGrayManualOutboundListREQ req);

    BlackGrayManualOutboundDetailRSP detail(Long id);

    void updateStatue(Long id, Integer status);

    void remove(BlackGrayManualOutboundRemoveREQ req);

}