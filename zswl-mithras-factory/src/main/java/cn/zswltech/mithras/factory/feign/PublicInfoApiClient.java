package cn.zswltech.mithras.factory.feign;

import com.zswltec.providence.api.PublicInfoApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/12/18 16:49
 */
@FeignClient(name = "providence", path = "/api")
public interface PublicInfoApiClient extends PublicInfoApi {

}
