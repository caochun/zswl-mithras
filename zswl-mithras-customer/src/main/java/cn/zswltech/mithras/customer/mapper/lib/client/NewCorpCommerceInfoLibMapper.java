package cn.zswltech.mithras.customer.mapper.lib.client;

import cn.zswltech.mithras.customer.model.client.NewCorpCommerceInfoLib;
import cn.zswltech.mithras.customer.application.lib.client.dto.CorpCommerceInfoLibDto;
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
