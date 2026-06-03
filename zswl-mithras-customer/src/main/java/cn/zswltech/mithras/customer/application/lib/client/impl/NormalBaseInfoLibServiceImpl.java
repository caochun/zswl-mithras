package cn.zswltech.mithras.customer.application.lib.client.impl;

import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.lib.client.NormalBaseInfoLibMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.NormalBaseInfoLib;
import cn.zswltech.mithras.customer.application.lib.client.NormalBaseInfoLibService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 *
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
@Service
public class NormalBaseInfoLibServiceImpl extends ServiceImpl<NormalBaseInfoLibMapper, NormalBaseInfoLib> implements NormalBaseInfoLibService {
}
