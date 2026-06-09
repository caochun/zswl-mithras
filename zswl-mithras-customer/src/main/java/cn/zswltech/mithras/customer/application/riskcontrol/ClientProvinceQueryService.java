package cn.zswltech.mithras.customer.application.riskcontrol;

import java.util.List;
import java.util.Set;

public interface ClientProvinceQueryService {

    Set<Long> getSpecifyProvinceClientIds(List<String> provinceCodes);

    Set<Long> getNotInSpecifyProvinceClientIds(List<String> provinceCodes);
}
