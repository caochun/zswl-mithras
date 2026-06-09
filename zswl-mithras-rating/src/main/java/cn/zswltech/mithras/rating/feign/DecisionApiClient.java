package cn.zswltech.mithras.rating.feign;

import cn.zswltech.decision.engine.api.decision.DecisionApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "decision-engine", path = "/api")
public interface DecisionApiClient extends DecisionApi {
}
