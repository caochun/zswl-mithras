package cn.zswltech.mithras.customer.mapper.lib.client;

import cn.zswltech.mithras.customer.model.client.NewCorpAddressInfoLib;
import cn.zswltech.mithras.customer.versioning.dto.CorpAddressInfoLibDto;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author luyi
 */
public interface NewCorpAddressInfoLibMapper extends BaseMapper<NewCorpAddressInfoLib> {

    /**
     * 获取满足地址条件的客户列表
     * 优先办公地址、无则取注册地址
     *
     * @param dto
     * @return
     */
    List<Long> listNewestAddress(@Param("dto") CorpAddressInfoLibDto dto);

    /**
     * 获取指定客户的地址
     * 优先办公地址、无则取注册地址
     *
     * @param clientIds
     * @return
     */
    List<NewCorpAddressInfoLib> listNewestAddressByClientId(@Param("clientIds") Set<Long> clientIds);

    /**
     * 获取指定客户的地址
     */
    List<NewCorpAddressInfoLib> listAddressByClientIdAndType(@Param("clientIds") Set<Long> clientIds, @Param("corpAddressType") String corpAddressType);

    List<NewCorpAddressInfoLib> listNewestCorpAddress(@Param("dto") CorpAddressInfoLibDto dto);
}
