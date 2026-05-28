package cn.zswltech.mithras.factory.feign;

import com.zswltec.providence.api.CustomerUnifiedViewApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/12/18 18:38
 */
@FeignClient(name = "providence", path = "/api")
public interface ProvidenceCustomerUnifiedViewApiClient extends CustomerUnifiedViewApi {
}
