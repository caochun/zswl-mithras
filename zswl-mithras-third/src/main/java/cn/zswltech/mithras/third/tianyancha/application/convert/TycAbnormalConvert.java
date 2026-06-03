package cn.zswltech.mithras.third.tianyancha.application.convert;

import cn.zswltech.mithras.dto.client.external.tyc.TycAbnormalRSP;
import cn.zswltech.mithras.customer.externaldata.tianyancha.infrastructure.model.TycAbnormal;
import cn.zswltech.mithras.third.tianyancha.infrastructure.client.resp.TycAbnormalResp;

/**
 * 经营异常
 *
 * @author wangchuanhao
 * @date 2022/6/21 12:36 PM
 */
public class TycAbnormalConvert {

    public static TycAbnormal tycResp2Entity(TycAbnormalResp.ItemsDTO resp) {
        TycAbnormal tycAbnormal = new TycAbnormal();
        tycAbnormal.setRemoveDate(resp.getRemoveDate());
        tycAbnormal.setPutReason(resp.getPutReason());
        tycAbnormal.setPutDepartment(resp.getPutDepartment());
        tycAbnormal.setRemoveDepartment(resp.getRemoveDepartment());
        tycAbnormal.setRemoveReason(resp.getRemoveReason());
        tycAbnormal.setPutDate(resp.getPutDate());
        return tycAbnormal;

    }

    public static TycAbnormalRSP entity2RSP(TycAbnormal entity) {
        TycAbnormalRSP tycAbnormalRSP = new TycAbnormalRSP();
        tycAbnormalRSP.setId(entity.getId());
        tycAbnormalRSP.setRemoveDate(entity.getRemoveDate());
        tycAbnormalRSP.setPutReason(entity.getPutReason());
        tycAbnormalRSP.setPutDepartment(entity.getPutDepartment());
        tycAbnormalRSP.setRemoveDepartment(entity.getRemoveDepartment());
        tycAbnormalRSP.setRemoveReason(entity.getRemoveReason());
        tycAbnormalRSP.setPutDate(entity.getPutDate());
        return tycAbnormalRSP;
    }

}
