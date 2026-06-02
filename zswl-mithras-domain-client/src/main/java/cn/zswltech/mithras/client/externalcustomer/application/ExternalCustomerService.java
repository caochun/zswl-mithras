package cn.zswltech.mithras.client.externalcustomer.application;

import cn.zswltech.mithras.client.externalcustomer.infrastructure.mapper.ExternalCustomerMapper;
import cn.zswltech.mithras.client.externalcustomer.infrastructure.model.ExternalCustomer;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author shaokang
 * @description 外部客户名单service类
 * @date 2026-01-29
 */
@Slf4j
@Service
public class ExternalCustomerService extends ServiceImpl<ExternalCustomerMapper, ExternalCustomer> {

}
