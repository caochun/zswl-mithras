package cn.zswltech.mithras.application.orchestration.adapter.blackgray;

import cn.zswltech.mithras.blackgray.application.port.BlackGrayCustomerPort;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class BlackGrayCustomerPortAdapter implements BlackGrayCustomerPort {

    @Resource
    private ClientMapper clientMapper;

    @Override
    public CustomerInfo getById(Long clientId) {
        Client client = clientMapper.selectById(clientId);
        if (client == null) {
            return null;
        }
        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setId(client.getId());
        customerInfo.setName(client.getClientName());
        customerInfo.setUnifiedSocialCreditCode(client.getUscCode());
        return customerInfo;
    }
}
