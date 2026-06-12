package cn.zswltech.mithras.third.tianyancha.application.port;

import cn.zswltech.mithras.third.tianyancha.client.resp.TycBaseInfo;

import java.util.List;

public interface TycIndustryTypePort {

    String toIndustryCode(TycBaseInfo.IndustryAll industryAll);

    List<String> findAllParentCodes(String industryType);

    String findIndustryCodeByDisplay(String display);
}
