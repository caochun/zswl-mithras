package cn.zswltech.mithras.service.service.tyc;

import cn.zswltech.mithras.dto.client.external.ExternalPageREQ;
import cn.zswltech.mithras.dto.client.external.environment.EnvironmentPenaltyAddREQ;
import cn.zswltech.mithras.dto.client.external.environment.EnvironmentPenaltyModifyREQ;
import cn.zswltech.mithras.dto.client.external.environment.EnvironmentPenaltyRemoveREQ;
import cn.zswltech.mithras.service.mapper.model.client.EnvironmentPenalty;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 环保处罚
 *
 * @author wangchuanhao
 * @date 2022/6/21 3:00 PM
 */
public interface EnvironmentPenaltyService extends IService<EnvironmentPenalty> {

    void add(EnvironmentPenaltyAddREQ req);

    void modify(EnvironmentPenaltyModifyREQ req);

    Page<EnvironmentPenalty> list(ExternalPageREQ req);

    void remove(EnvironmentPenaltyRemoveREQ req);
}
