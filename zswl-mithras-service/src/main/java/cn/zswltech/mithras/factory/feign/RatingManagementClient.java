package cn.zswltech.mithras.factory.feign;

import cn.zswltech.fuxi.common.api.RatingManagementApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "fuxi-rzy")
public interface RatingManagementClient extends RatingManagementApi {
}
