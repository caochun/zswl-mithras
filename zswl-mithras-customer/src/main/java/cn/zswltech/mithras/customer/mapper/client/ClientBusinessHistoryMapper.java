package cn.zswltech.mithras.customer.mapper.client;

import cn.zswltech.mithras.customer.model.client.ClientBusinessHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @description 客户工商信息历史表
* @author vico
* @date 2023-09-07
*/
public interface ClientBusinessHistoryMapper extends BaseMapper<ClientBusinessHistory> {
    List<ClientBusinessHistory> getLastByClientIds(@Param("ids") List<Long> clientIds);
}