package cn.zswltech.mithras.blackgray.service;


import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayBreakBusinessAddREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayBreakBusinessListREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayBreakBusinessModifyREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayBreakBusinessRemoveREQ;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayBreakBusinessDetailRSP;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayBreakBusinessListRSP;
import cn.zswltech.mithras.blackgray.mapper.model.BlackGrayBreakBusiness;

import java.util.List;

/**
* @description black_gray_break_business
* @author
* @date 2023-11-28
*/
public interface BlackGrayBreakBusinessService {

    Long add(BlackGrayBreakBusinessAddREQ req);

    void modify(BlackGrayBreakBusinessModifyREQ req);

    PageR<BlackGrayBreakBusinessListRSP> list(BlackGrayBreakBusinessListREQ req);

    BlackGrayBreakBusinessDetailRSP detail(Long id);

    void remove(BlackGrayBreakBusinessRemoveREQ req);

    void updateStatue(Long id, Integer status);

    void saveBatch(List<BlackGrayBreakBusiness> records);

}