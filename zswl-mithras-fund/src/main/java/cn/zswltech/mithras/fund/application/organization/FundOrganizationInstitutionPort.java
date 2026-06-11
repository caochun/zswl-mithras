package cn.zswltech.mithras.fund.application.organization;

import java.util.Collection;
import java.util.Map;

public interface FundOrganizationInstitutionPort {

    Map<String, Long> clientIdMapByCreditCodes(Collection<String> creditCodes);

    Long findClientIdByCreditCode(String creditCode);
}
