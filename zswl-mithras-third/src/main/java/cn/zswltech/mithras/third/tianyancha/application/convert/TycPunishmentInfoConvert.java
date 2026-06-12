package cn.zswltech.mithras.third.tianyancha.application.convert;

import cn.zswltech.mithras.dto.client.external.tyc.TycPunishmentInfoRSP;
import cn.zswltech.mithras.third.externaldata.tianyancha.model.TycPunishmentInfo;
import cn.zswltech.mithras.third.tianyancha.client.resp.TycPunishmentInfoResp;


/**
 * 行政处罚
 *
 * @author wangchuanhao
 * @date 2022/6/21 1:06 PM
 */
public class TycPunishmentInfoConvert {

    public static TycPunishmentInfo tycResp2Entity(TycPunishmentInfoResp.ItemsDTO resp) {
        TycPunishmentInfo tycPunishmentInfo = new TycPunishmentInfo();
        tycPunishmentInfo.setDepartmentName(resp.getDepartmentName());
        tycPunishmentInfo.setReason(resp.getReason());
        tycPunishmentInfo.setEvidence(resp.getEvidence());
        tycPunishmentInfo.setPunishStatus(resp.getPunishStatus());
        tycPunishmentInfo.setRemark(resp.getRemark());
        tycPunishmentInfo.setSource(resp.getSource());
        tycPunishmentInfo.setType(resp.getType());
        tycPunishmentInfo.setContent(resp.getContent());
        tycPunishmentInfo.setDecisionDate(resp.getDecisionDate());
        tycPunishmentInfo.setLegalPersonName(resp.getLegalPersonName());
        tycPunishmentInfo.setPunishName(resp.getPunishName());
        tycPunishmentInfo.setPunishNumber(resp.getPunishNumber());
        tycPunishmentInfo.setTypeSecond(resp.getTypeSecond());
        return tycPunishmentInfo;

    }

    public static TycPunishmentInfoRSP entity2RSP(TycPunishmentInfo entity) {
        TycPunishmentInfoRSP tycPunishmentInfoRSP = new TycPunishmentInfoRSP();
        tycPunishmentInfoRSP.setId(entity.getId());
        tycPunishmentInfoRSP.setDecisionDate(entity.getDecisionDate());
        tycPunishmentInfoRSP.setPunishNumber(entity.getPunishNumber());
        tycPunishmentInfoRSP.setReason(entity.getReason());
        tycPunishmentInfoRSP.setContent(entity.getContent());
        tycPunishmentInfoRSP.setDepartmentName(entity.getDepartmentName());
        tycPunishmentInfoRSP.setSource(entity.getSource());
        return tycPunishmentInfoRSP;

    }

}
