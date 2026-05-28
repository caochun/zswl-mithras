package cn.zswltech.mithras.service.convert.tyc;

import cn.zswltech.mithras.dto.client.external.tyc.TycEquityInfoRSP;
import cn.zswltech.mithras.service.mapper.model.client.TycEquityInfo;
import cn.zswltech.mithras.service.repository.tyc.resp.TycEquityInfoResp;
import cn.zswltech.mithras.service.util.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * 股权出质
 *
 * @author wangchuanhao
 * @date 2022/6/21 12:52 PM
 */
public class TycEquityInfoConvert {

    public static TycEquityInfo tycResp2Entity(TycEquityInfoResp.ItemsDTO resp) {
        TycEquityInfo tycEquityInfo = new TycEquityInfo();
        tycEquityInfo.setPledgeeJson(Optional.ofNullable(resp.getPledgeeList()).map(JSON::toJSONString).orElse(null));
        tycEquityInfo.setRegDate(Optional.ofNullable(resp.getRegDate()).map(DateUtil::timestamp2LDT).orElse(null));
        tycEquityInfo.setPledgor(resp.getPledgor());
        tycEquityInfo.setCertifNumberR(resp.getCertifNumberR());
        tycEquityInfo.setPledgee(resp.getPledgee());
        tycEquityInfo.setRegNumber(resp.getRegNumber());
        tycEquityInfo.setCertifNumber(resp.getCertifNumber());
        tycEquityInfo.setCompanyJson(Optional.ofNullable(resp.getCompanyList()).map(JSON::toJSONString).orElse(null));
        tycEquityInfo.setTargetCompanyJson(Optional.ofNullable(resp.getTargetCompany()).map(JSON::toJSONString).orElse(null));
        tycEquityInfo.setPledgorJson(Optional.ofNullable(resp.getPledgorList()).map(JSON::toJSONString).orElse(null));
        tycEquityInfo.setEquityAmount(resp.getEquityAmount());
        tycEquityInfo.setTycId(resp.getId());
        tycEquityInfo.setState(resp.getState());
        tycEquityInfo.setPutDate(Optional.ofNullable(resp.getPutDate()).map(DateUtil::timestamp2LDT).orElse(null));
        return tycEquityInfo;
    }

    public static TycEquityInfoRSP entity2RSP(TycEquityInfo entity) {
        TycEquityInfoRSP tycEquityInfoRSP = new TycEquityInfoRSP();
        tycEquityInfoRSP.setId(entity.getId());
        tycEquityInfoRSP.setRegDate(entity.getRegDate());
        tycEquityInfoRSP.setRegNumber(entity.getRegNumber());
        tycEquityInfoRSP.setPledgor(entity.getPledgor());
        if (StringUtils.isNotBlank(entity.getTargetCompanyJson())) {
            TycEquityInfoResp.TargetCompanyDTO targetCompanyDTO = JSONObject.parseObject(entity.getTargetCompanyJson(), TycEquityInfoResp.TargetCompanyDTO.class);
            tycEquityInfoRSP.setTargetCompany(targetCompanyDTO.getName());
        }
        tycEquityInfoRSP.setPledgee(entity.getPledgee());
        tycEquityInfoRSP.setEquityAmount(entity.getEquityAmount());
        tycEquityInfoRSP.setState(entity.getState());
        return tycEquityInfoRSP;

    }

}
