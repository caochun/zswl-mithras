package cn.zswltech.mithras.service.convert.tyc;

import cn.zswltech.mithras.dto.client.external.tyc.TycJudicialRSP;
import cn.zswltech.mithras.service.enums.TycJudicialTypeStateEnum;
import cn.zswltech.mithras.service.mapper.model.client.TycJudicial;
import cn.zswltech.mithras.service.repository.tyc.resp.TycJudicialResp;

/**
 * 司法协助
 *
 * @author wangchuanhao
 * @date 2022/6/21 12:22 PM
 */
public class TycJudicialConvert {

    public static TycJudicial tycResp2Entity(TycJudicialResp.ItemsDTO resp) {
        TycJudicial tycJudicial = new TycJudicial();
        tycJudicial.setExecuteNoticeNum(resp.getExecuteNoticeNum());
        tycJudicial.setExecutedPersonCid(resp.getExecutedPersonCid());
        tycJudicial.setPublicityDate(resp.getPublicityDate());
        tycJudicial.setStockExecutedCompany(resp.getStockExecutedCompany());
        tycJudicial.setExecutedPersonHid(resp.getExecutedPersonHid());
        tycJudicial.setStockExecutedCid(resp.getStockExecutedCid());
        tycJudicial.setExecutedPerson(resp.getExecutedPerson());
        tycJudicial.setAssId(resp.getAssId());
        tycJudicial.setEquityAmount(resp.getEquityAmount());
        tycJudicial.setTycId(resp.getId());
        tycJudicial.setTypeState(resp.getTypeState());
        tycJudicial.setExecutedPersonType(resp.getExecutedPersonType());
        tycJudicial.setExecutiveCourt(resp.getExecutiveCourt());
        return tycJudicial;
    }

    public static TycJudicialRSP entity2RSP(TycJudicial entity) {
        TycJudicialRSP tycJudicialRSP = new TycJudicialRSP();
        tycJudicialRSP.setId(entity.getId());
        tycJudicialRSP.setPublicityDate(entity.getPublicityDate());
        tycJudicialRSP.setExecuteNoticeNum(entity.getExecuteNoticeNum());
        tycJudicialRSP.setExecutedPerson(entity.getExecutedPerson());
        tycJudicialRSP.setStockExecutedCompany(entity.getStockExecutedCompany());
        tycJudicialRSP.setEquityAmount(entity.getEquityAmount());
        tycJudicialRSP.setExecutiveCourt(entity.getExecutiveCourt());
        TycJudicialTypeStateEnum typeStateEnum = TycJudicialTypeStateEnum.getByVal(entity.getTypeState());
        tycJudicialRSP.setTypeState(typeStateEnum.getType(entity.getTypeState()));
        tycJudicialRSP.setStatus(typeStateEnum.getState(entity.getTypeState()));
        return tycJudicialRSP;
    }

}
