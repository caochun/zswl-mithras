package cn.zswltech.mithras.third.tianyancha.application.convert;

import cn.zswltech.mithras.dto.client.external.tyc.TycZhixingInfoRSP;
import cn.zswltech.mithras.customer.externaldata.tianyancha.mapper.model.TycZhixingInfo;
import cn.zswltech.mithras.third.tianyancha.client.resp.TycZhixingInfoResp;
import cn.hutool.core.date.LocalDateTimeUtil;

import java.util.Optional;


/**
 * 被执行人
 *
 * @author wangchuanhao
 * @date 2022/6/21 1:06 PM
 */
public class TycZhixingInfoConvert {

    public static TycZhixingInfo tycResp2Entity(TycZhixingInfoResp.ItemsDTO resp) {
        TycZhixingInfo tycZhixingInfo = new TycZhixingInfo();
        tycZhixingInfo.setCaseCode(resp.getCaseCode());
        tycZhixingInfo.setPartyCardNum(resp.getPartyCardNum());
        tycZhixingInfo.setPname(resp.getPname());
        tycZhixingInfo.setExecCourtName(resp.getExecCourtName());
        tycZhixingInfo.setCaseCreateTime(Optional.ofNullable(resp.getCaseCreateTime()).map(LocalDateTimeUtil::of).orElse(null));
        tycZhixingInfo.setExecMoney(resp.getExecMoney());
        return tycZhixingInfo;
    }

    public static TycZhixingInfoRSP entity2RSP(TycZhixingInfo entity) {
        TycZhixingInfoRSP tycZhixingInfoRSP = new TycZhixingInfoRSP();
        tycZhixingInfoRSP.setId(entity.getId());
        tycZhixingInfoRSP.setCaseCreateTime(entity.getCaseCreateTime());
        tycZhixingInfoRSP.setCaseCode(entity.getCaseCode());
        tycZhixingInfoRSP.setExecMoney(entity.getExecMoney());
        tycZhixingInfoRSP.setExecCourtName(entity.getExecCourtName());
        return tycZhixingInfoRSP;

    }

}
