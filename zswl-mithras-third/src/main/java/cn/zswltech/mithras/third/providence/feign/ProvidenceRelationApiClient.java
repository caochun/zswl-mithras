package cn.zswltech.mithras.third.providence.feign;

import com.zswltec.providence.api.RelationApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/12/18 16:49
 */
@FeignClient(name = "providence", path = "/api")
public interface ProvidenceRelationApiClient extends RelationApi {

}
