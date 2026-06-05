package cn.zswltech.mithras.policy.application.lib.service;

import cn.zswltech.mithras.dto.policy.PolicyInfoDetailRSP;
import cn.zswltech.mithras.policy.infrastructure.persistence.model.PolicyInfoLib;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @create: 2023-06-18
 **/

public interface PolicyInfoLibService extends IService<PolicyInfoLib> {

    PolicyInfoDetailRSP detail(Long id, String version);
}
