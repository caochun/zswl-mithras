package cn.zswltech.mithras.riskcontrol.application.port;

import java.util.Map;
import java.util.Set;

public interface RiskControlClientFactPort {

    Set<Long> relatedClientIds();

    Set<Long> intraGroupClientIds();

    Map<Long, Long> intraGroupClientIdToGroupId();

    Set<Long> nonZhejiangNonIntraGroupClientIds();

    Map<Long, Long> nonZhejiangNonIntraGroupClientIdToGroupId();

    Set<Long> zhejiangClientIds();

    Set<Long> nonZhejiangClientIds();

    Map<Long, Long> zhejiangClientIdToGroupId();

    Set<Long> clientIdsNotInRiskControlIndustryClassify(Set<String> classifications);

    Set<Long> clientIdsInRiskControlIndustryClassify(Set<String> classifications);

    Map<Long, String> clientIdToIndustryTypeInRiskControlIndustryClassify(Set<String> classifications);

    Map<String, String> twoLevelIndustryTypeNameMap();

    String riskControlIndustryClassify(Long clientId);

    Map<String, Long> activeClientIdsByCreditCodes(Set<String> creditCodes);

    Map<Long, RiskControlConcentrationClientFact> concentrationClientFacts();

    Set<Long> clientIdsInRangeNotInRiskControlIndustryClassify(Set<Long> clientIds, Set<String> classifications);
}
