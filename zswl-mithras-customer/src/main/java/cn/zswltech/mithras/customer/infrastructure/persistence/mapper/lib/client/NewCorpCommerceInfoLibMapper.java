package cn.zswltech.mithras.customer.infrastructure.persistence.mapper.lib.client;

import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.NewCorpCommerceInfoLib;
import cn.zswltech.mithras.customer.application.riskcontrol.dto.CorpCommerceInfoLibDto;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author junke
 */
public interface NewCorpCommerceInfoLibMapper extends BaseMapper<NewCorpCommerceInfoLib> {

    List<NewCorpCommerceInfoLib> listNewestCommerceInfo(@Param("dto") CorpCommerceInfoLibDto dto);

    /**
     * 查询客户注册地址为「浙江省」或风控行业分类为「集团协同业务」
     */
    List<NewCorpCommerceInfoLib> intraGroupClients();

}
