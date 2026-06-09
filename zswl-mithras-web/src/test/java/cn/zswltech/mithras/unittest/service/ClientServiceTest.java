package cn.zswltech.mithras.unittest.service;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.client.client.ClientListREQ;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.customer.domain.enums.client.ClientType;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.client.ClientMapper;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.service.service.client.ClientService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

/**
 * @author luyi
 */
@PrepareForTest(AccountUtil.class)
@RunWith(PowerMockRunner.class)
public class ClientServiceTest {

    @InjectMocks
    ClientService clientService;
    @Mock
    SysUserService sysUserService;
    @Mock
    ClientMapper clientMapper;


    @Test
    public void list() {
        ClientListREQ req = new ClientListREQ();
        req.setClientCode("1");
        req.setClientName("abc");
        req.setClientType(ClientType.CORPORATION.name());

        when(sysUserService.currentUserIsSpecificJob(JobEnum.projmanager.name())).thenReturn(true);
        when(sysUserService.currentUserIsSpecificJob(JobEnum.businesshead.name())).thenReturn(true);
        when(sysUserService.currentUserIsSpecificJob(JobEnum.leaderincharge.name())).thenReturn(false);
        when(sysUserService.canViewDeptIds()).thenReturn(ListUtil.of(1L));
        PowerMockito.mockStatic(AccountUtil.class);
        PowerMockito.when(AccountUtil.getLoginInfo()).thenReturn(new AccountVO());
        assertDoesNotThrow(() -> clientService.list(req));
    }
}