package cn.zswltech.mithras.unittest.mapper.client;

import cn.zswltech.mithras.customer.enums.client.ClientStatus;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.dto.ClientListParam;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.unittest.mapper.MithrasMapperTest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static cn.hutool.core.util.IdUtil.fastSimpleUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author luyi
 */

public class ClientMapperTest extends MithrasMapperTest {

    @Autowired
    private ClientMapper clientMapper;

    @Test
    public void test() {
        //构造数据
        String clientCode = fastSimpleUUID();
        String clientName = fastSimpleUUID();
        String clientStatus = ClientStatus.NEW.name();
        long belongDeptId = Integer.MAX_VALUE;
        long belongSponsorId = Integer.MAX_VALUE;
        long createBy = Integer.MAX_VALUE;
        LocalDateTime createAt = LocalDateTime.of(2028, 11, 1, 8, 0, 0, 0);
        long createByDeptId = Integer.MAX_VALUE;
        Client client = new Client();
        client.setClientCode(clientCode);
        client.setClientName(clientName);
        client.setClientStatus(clientStatus);
        client.setBelongDeptId(belongDeptId);
        client.setBelongSponsorId(belongSponsorId);
        client.setCreateBy(createBy);
        client.setCreateTime(createAt);
        client.setCreateByDept(createByDeptId);
        clientMapper.insert(client);
        //名称查询测试
        Page<Client> testPage = clientMapper.myList(
                new Page<>(1, 10),
                new ClientListParam().setClientCode(clientCode)
        );
        assertEquals(1, testPage.getRecords().size());
        assertEquals(client.getClientCode(), testPage.getRecords().get(0).getClientCode());
        //code查询测试
        testPage = clientMapper.myList(
                new Page<>(1, 10),
                new ClientListParam().setClientName(clientName)
        );
        assertEquals(1, testPage.getRecords().size());
        assertEquals(client.getClientName(), testPage.getRecords().get(0).getClientName());
        //status测试
        testPage = clientMapper.myList(
                new Page<>(1, 10),
                new ClientListParam().setClientStatus(ClientStatus.NEW.name())
        );
        assertTrue(testPage.getRecords().stream().map(Client::getClientName).collect(Collectors.toSet()).contains(clientName));
        //belongdeptID
        testPage = clientMapper.myList(
                new Page<>(1, 10),
                new ClientListParam().setBelongDeptId(belongDeptId)
        );
        assertEquals(1, testPage.getRecords().size());
        assertEquals(client.getBelongDeptId(), testPage.getRecords().get(0).getBelongDeptId());
        //create from to
        testPage = clientMapper.myList(
                new Page<>(1, 10),
                new ClientListParam().setCreateFrom(createAt)
                        .setCreateTo(createAt)
        );
        assertEquals(1, testPage.getRecords().size());
        assertEquals(client.getCreateTime(), createAt);
        //create by
        testPage = clientMapper.myList(
                new Page<>(1, 10),
                new ClientListParam().setCreateById(createBy)
        );
        assertEquals(1, testPage.getRecords().size());
        assertEquals(client.getCreateBy(), createBy);
    }
}
