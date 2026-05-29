package cn.zswltech.mithras.service.mapper.client;

import cn.zswltech.mithras.service.mapper.dto.ClientListParam;
import cn.zswltech.mithras.service.mapper.dto.client.DashboardClientBasicDTO;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.client.ClientAuthority;
import cn.zswltech.mithras.service.mapper.model.dashboard.DashboardClientOverviewAllQuery;
import cn.zswltech.mithras.common.plugin.CustomBaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author junke
 */
public interface ClientAuthorityMapper extends CustomBaseMapper<ClientAuthority> {

}