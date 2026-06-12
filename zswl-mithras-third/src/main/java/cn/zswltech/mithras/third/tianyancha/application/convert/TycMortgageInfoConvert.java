package cn.zswltech.mithras.third.tianyancha.application.convert;

import cn.zswltech.mithras.dto.client.external.tyc.TycMortgageInfoRSP;
import cn.zswltech.mithras.third.externaldata.tianyancha.persistence.model.TycMortgageInfo;
import cn.zswltech.mithras.third.tianyancha.client.resp.TycMortgageInfoResp;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 法律诉讼
 *
 * @author wangchuanhao
 * @date 2022/6/21 12:52 PM
 */
public class TycMortgageInfoConvert {

    public static TycMortgageInfo tycResp2Entity(TycMortgageInfoResp.ItemsDTO resp) {
        TycMortgageInfo tycMortgageInfo = new TycMortgageInfo();
        TycMortgageInfoResp.BaseInfoDTO baseInfoDTO = resp.getBaseInfo();
        tycMortgageInfo.setAmount(baseInfoDTO.getAmount());
        tycMortgageInfo.setCancelDate(Optional.ofNullable(baseInfoDTO.getCancelDate()).map(LocalDateTimeUtil::of).orElse(null));
        tycMortgageInfo.setPublishDate(Optional.ofNullable(baseInfoDTO.getPublishDate()).map(LocalDateTimeUtil::of).orElse(null));
        tycMortgageInfo.setRegDate(baseInfoDTO.getRegDate());
        tycMortgageInfo.setRemark(baseInfoDTO.getRemark());
        tycMortgageInfo.setType(baseInfoDTO.getType());
        tycMortgageInfo.setRegDepartment(baseInfoDTO.getRegDepartment());
        tycMortgageInfo.setRegNum(baseInfoDTO.getRegNum());
        tycMortgageInfo.setScope(baseInfoDTO.getScope());
        tycMortgageInfo.setTerm(baseInfoDTO.getTerm());
        tycMortgageInfo.setTycId(baseInfoDTO.getId());
        tycMortgageInfo.setCancelReason(baseInfoDTO.getCancelReason());
        tycMortgageInfo.setStatus(baseInfoDTO.getStatus());
        tycMortgageInfo.setBase(baseInfoDTO.getBase());
        tycMortgageInfo.setPeopleInfoJson(Optional.ofNullable(resp.getPeopleInfo()).map(JSON::toJSONString).orElse(null));
        tycMortgageInfo.setPawnInfoJson(Optional.ofNullable(resp.getPawnInfoList()).map(JSON::toJSONString).orElse(null));
        tycMortgageInfo.setChangeInfoJson(Optional.ofNullable(resp.getChangeInfoList()).map(JSON::toJSONString).orElse(null));
        return tycMortgageInfo;

    }

    public static TycMortgageInfoRSP entity2RSP(TycMortgageInfo entity) {
        TycMortgageInfoRSP tycMortgageInfoRSP = new TycMortgageInfoRSP();
        tycMortgageInfoRSP.setId(entity.getId());
        tycMortgageInfoRSP.setRegDate(entity.getRegDate());
        tycMortgageInfoRSP.setRegNum(entity.getRegNum());
        tycMortgageInfoRSP.setType(entity.getType());
        if (StringUtils.isNotBlank(entity.getPeopleInfoJson())) {
            List<TycMortgageInfoResp.PeopleInfoDTO> peopleInfoDTOList = JSONArray.parseArray(entity.getPeopleInfoJson(), TycMortgageInfoResp.PeopleInfoDTO.class);
            tycMortgageInfoRSP.setPeopleInfo(peopleInfoDTOList.stream().map(TycMortgageInfoResp.PeopleInfoDTO::getPeopleName).filter(StringUtils::isNotBlank).collect(Collectors.joining(";")));
        }
        tycMortgageInfoRSP.setAmount(entity.getAmount());
        tycMortgageInfoRSP.setTerm(entity.getTerm());
        tycMortgageInfoRSP.setRegDepartment(entity.getRegDepartment());
        if (StringUtils.isNotBlank(entity.getPawnInfoJson())) {
            List<TycMortgageInfoResp.PawnInfoListDTO> pawnInfoListDTOList = JSONArray.parseArray(entity.getPawnInfoJson()).toJavaList(TycMortgageInfoResp.PawnInfoListDTO.class);
            tycMortgageInfoRSP.setBelongTo(pawnInfoListDTOList.stream().map(TycMortgageInfoResp.PawnInfoListDTO::getOwnership).distinct().collect(Collectors.joining(";")));
        }
        return tycMortgageInfoRSP;
    }

}
