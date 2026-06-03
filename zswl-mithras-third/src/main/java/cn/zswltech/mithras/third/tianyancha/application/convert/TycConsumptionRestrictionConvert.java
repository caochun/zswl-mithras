package cn.zswltech.mithras.third.tianyancha.application.convert;

import cn.zswltech.mithras.dto.client.external.tyc.TycConsumptionRestrictionRSP;
import cn.zswltech.mithras.customer.externaldata.tianyancha.infrastructure.model.TycConsumptionRestriction;
import cn.zswltech.mithras.third.tianyancha.infrastructure.client.resp.TycConsumptionRestrictionResp;
import cn.hutool.core.date.LocalDateTimeUtil;

import java.util.Objects;

/**
 * 限制消费令
 *
 * @author wangchuanhao
 * @date 2022/6/21 12:37 PM
 */
public class TycConsumptionRestrictionConvert {

    public static TycConsumptionRestriction tycResp2Entity(TycConsumptionRestrictionResp.ItemsDTO resp) {
        TycConsumptionRestriction tycConsumptionRestriction = new TycConsumptionRestriction();
        tycConsumptionRestriction.setCaseCode(resp.getCaseCode());
        tycConsumptionRestriction.setFilePath(resp.getFilePath());
        if (Objects.nonNull(resp.getPublishDate())) {
            tycConsumptionRestriction.setPublishDate(LocalDateTimeUtil.of(resp.getPublishDate()));
        }
        tycConsumptionRestriction.setXname(resp.getXname());
        tycConsumptionRestriction.setHcgid(resp.getHcgid());
        tycConsumptionRestriction.setApplicant(resp.getApplicant());
        tycConsumptionRestriction.setApplicantCid(resp.getApplicantCid());
        tycConsumptionRestriction.setQyinfoAlias(resp.getQyinfoAlias());
        if (Objects.nonNull(resp.getCaseCreateTime())) {
            tycConsumptionRestriction.setCaseCreateTime(LocalDateTimeUtil.of(resp.getCaseCreateTime()));

        }
        tycConsumptionRestriction.setAlias(resp.getAlias());
        tycConsumptionRestriction.setTycId(resp.getId());
        tycConsumptionRestriction.setCid(resp.getCid());
        return tycConsumptionRestriction;

    }

    public static TycConsumptionRestrictionRSP entity2RSP(TycConsumptionRestriction entity) {
        TycConsumptionRestrictionRSP tycConsumptionRestrictionRSP = new TycConsumptionRestrictionRSP();
        tycConsumptionRestrictionRSP.setId(entity.getId());
        tycConsumptionRestrictionRSP.setCaseCreateTime(entity.getCaseCreateTime());
        tycConsumptionRestrictionRSP.setCaseCode(entity.getCaseCode());
        tycConsumptionRestrictionRSP.setXname(entity.getXname());
        tycConsumptionRestrictionRSP.setQyinfoAlias(entity.getQyinfoAlias());
        tycConsumptionRestrictionRSP.setApplicant(entity.getApplicant());
        tycConsumptionRestrictionRSP.setPublishDate(entity.getPublishDate());
        tycConsumptionRestrictionRSP.setDetailUrl(entity.getFilePath());
        return tycConsumptionRestrictionRSP;

    }

}
