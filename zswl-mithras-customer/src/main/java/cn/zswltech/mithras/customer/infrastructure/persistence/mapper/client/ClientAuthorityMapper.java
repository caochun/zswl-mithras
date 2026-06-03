package cn.zswltech.mithras.customer.infrastructure.persistence.mapper.client;

import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.ClientAuthority;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.dashboard.DashboardClientOverviewAllQuery;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author junke
 */
public interface ClientAuthorityMapper extends CustomBaseMapper<ClientAuthority> {

}