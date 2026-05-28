package cn.zswltech.mithras.service.convert.tyc;

import cn.zswltech.mithras.dto.client.external.tyc.TycDishonestRSP;
import cn.zswltech.mithras.service.mapper.model.client.TycDishonest;
import cn.zswltech.mithras.service.repository.tyc.resp.TycDishonestResp;
import cn.zswltech.mithras.service.util.DateUtil;
import com.alibaba.fastjson.JSON;

import java.util.Objects;
import java.util.Optional;

/**
 * 失信人
 *
 * @author wangchuanhao
 * @date 2022/6/21 12:48 PM
 */
public class TycDishonestConvert {

    public static TycDishonest tycResp2Entity(TycDishonestResp.ItemsDTO resp) {
        TycDishonest tycDishonest = new TycDishonest();
        tycDishonest.setBusinessEntity(resp.getBusinessentity());
        tycDishonest.setAreaName(resp.getAreaname());
        tycDishonest.setCourtName(resp.getCourtname());
        tycDishonest.setUnperformPart(resp.getUnperformPart());
        if (Objects.nonNull(resp.getStaff())) {
            tycDishonest.setStaffJson(JSON.toJSONString(resp.getStaff()));
        }
        tycDishonest.setType(resp.getType());
        tycDishonest.setPerformedPart(resp.getPerformedPart());
        tycDishonest.setIname(resp.getIname());
        tycDishonest.setDisruptTypeName(resp.getDisrupttypename());
        tycDishonest.setCaseCode(resp.getCasecode());
        tycDishonest.setCardNum(resp.getCardnum());
        tycDishonest.setPerformance(resp.getPerformance());
        tycDishonest.setRegDate(Optional.ofNullable(resp.getRegdate()).map(DateUtil::timestamp2LDT).orElse(null));
        tycDishonest.setPublishDate(Optional.ofNullable(resp.getPublishdate()).map(DateUtil::timestamp2LDT).orElse(null));
        tycDishonest.setGistUnit(resp.getGistunit());
        tycDishonest.setDuty(resp.getDuty());
        tycDishonest.setGistId(resp.getGistid());
        return tycDishonest;
    }

    public static TycDishonestRSP entity2RSP(TycDishonest entity) {
        TycDishonestRSP tycDishonestRSP = new TycDishonestRSP();
        tycDishonestRSP.setId(entity.getId());
        tycDishonestRSP.setRegDate(entity.getRegDate());
        tycDishonestRSP.setCaseCode(entity.getCaseCode());
        tycDishonestRSP.setGistId(entity.getGistId());
        tycDishonestRSP.setCourtName(entity.getCourtName());
        tycDishonestRSP.setDisruptTypeName(entity.getDisruptTypeName());
        tycDishonestRSP.setPerformance(entity.getPerformance());
        tycDishonestRSP.setPublishDate(entity.getPublishDate());
        return tycDishonestRSP;

    }

}
