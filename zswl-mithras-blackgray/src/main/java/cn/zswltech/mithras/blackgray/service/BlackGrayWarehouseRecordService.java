package cn.zswltech.mithras.blackgray.service;


import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.blackgray.dto.req.*;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayBusinessTypeRsp;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayWarehouseRecordDetailRSP;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayWarehouseRecordListRSP;
import cn.zswltech.mithras.blackgray.mapper.model.BlackGrayWarehouseRecord;
import cn.zswltech.mithras.blackgray.mapper.model.BlackGrayWarehouseRuleConfig;
import cn.zswltech.mithras.blackgray.vo.BlackGrayApplyReasonVo;

import java.util.List;

/**
* @description 黑灰名单记录表
* @author
* @date 2023-11-28
*/
public interface BlackGrayWarehouseRecordService {

    Long add(BlackGrayWarehouseRecordAddREQ req);

    void batchAdd(List<BlackGrayWarehouseRecordAddREQ> reqs);

    void batchModify(BlackGrayWarehouseRecordSupplyREQ req);

    /**
     * 批量保存记录
     **/
    void saveBatch(List<BlackGrayWarehouseRecord> records);

    void modify(BlackGrayWarehouseRecordModifyREQ req);

    PageR<BlackGrayWarehouseRecordListRSP> list(BlackGrayWarehouseRecordListREQ req);

    BlackGrayWarehouseRecordDetailRSP detail(Long id);

    void remove(BlackGrayWarehouseRecordRemoveREQ req);

    List<BlackGrayBusinessTypeRsp> listBusinessType(BlackGrayBusinessTypeReq req);

    void updateStatue(Long id, Integer status);

    List<BlackGrayApplyReasonVo> getApplyReason();

    BlackGrayWarehouseRuleConfig getApplyReasonLeave(String applyReasonCode);

    String num2Name(String applyReasonCode);

}