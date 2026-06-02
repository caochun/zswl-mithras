package cn.zswltech.mithras.third.providence.feign;

import com.zswltec.providence.api.PeerComparisonApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author Jim
 * @version 1.0.0
 * @descripition:
 * @date 2025/1/13 17:14
 */
@FeignClient(name = "providence", path = "/api")
public interface ProvidencePeerComparisonApiClient extends PeerComparisonApi {
}
