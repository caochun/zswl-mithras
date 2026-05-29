package cn.zswltech.mithras.service.mapper.normal;

import cn.zswltech.mithras.service.mapper.model.client.NormalBaseInfo;
import cn.zswltech.mithras.common.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author junke
 */
public interface NormalBaseInfoMapper extends CustomBaseMapper<NormalBaseInfo> {

    List<NormalBaseInfo> availableSpouseList(
            @Param("spouseCertNumberList") List<String> spouseCertNumberList,
            @Param("selfClientId") Long selfClientId,
            @Param("gender") String targetGender,
            @Param("fuzzyName") String fuzzyName);
}
