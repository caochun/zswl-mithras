package cn.zswltech.mithras.service.mapper.lib.client;

import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.service.service.riskcontrol.dto.CorpCommerceInfoLibDto;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author junke
 */
public interface CorpCommerceInfoLibMapper extends BaseMapper<CorpCommerceInfoLib> {

    List<CorpCommerceInfoLib> listNewestCommerceInfo(@Param("dto") CorpCommerceInfoLibDto dto);

    /**
     * 查询客户注册地址为「浙江省」或风控行业分类为「集团协同业务」
     */
    List<CorpCommerceInfoLib> intraGroupClients();

}
