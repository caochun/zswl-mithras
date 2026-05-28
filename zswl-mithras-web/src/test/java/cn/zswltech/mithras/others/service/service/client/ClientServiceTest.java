package cn.zswltech.mithras.others.service.service.client;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.client.client.ClientSyncRSP;
import cn.zswltech.mithras.service.enums.ClientAuthEnum;
import cn.zswltech.mithras.service.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.service.service.bo.ClientAuthBO;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewService;
import cn.zswltech.mithras.service.service.third.TycService;
import cn.zswltech.mithras.web.MithrasApplication;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

/**
 * @author junke
 */
@RunWith(SpringRunner.class)
@Rollback
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class ClientServiceTest {
    @Resource
    private ClientService clientService;
    @Resource
    private TycService tycService;
    @Resource
    private CorpCommerceInfoMapper commerceInfoMapper;
    @Mock
    HttpRequest investRequest;
    @Mock
    HttpResponse investResponse;
    @Mock
    HttpRequest shareholderRequest;
    @Mock
    HttpResponse shareholderResponse;
    @Mock
    HttpRequest baseInfoRequest;
    @Mock
    HttpResponse baseInfoResponse;
    @Resource
    private ProjReviewService projReviewService;


    @SneakyThrows
    @Test
    void sync() {
        ReflectionTestUtils.setField(clientService, "tycService", tycService);
        mockStatic(HttpUtil.class);
        when(HttpUtil.createGet("http://open.api.tianyancha.com/services/open/ic/inverst/2.0?pageSize=500&pageNum=1&keyword=12345")).thenReturn(investRequest);
        when(investRequest.header(anyMap())).thenReturn(investRequest);
        when(investRequest.execute()).thenReturn(investResponse);
        when(investResponse.body()).thenReturn(IoUtil.readUtf8(ClientServiceTest.class.getResourceAsStream("/tyc/对外投资.json")));
        //
        when(HttpUtil.createGet("http://open.api.tianyancha.com/services/open/ic/holder/2.0?pageSize=500&pageNum=1&keyword=12345")).thenReturn(shareholderRequest);
        when(shareholderRequest.header(anyMap())).thenReturn(shareholderRequest);
        when(shareholderRequest.execute()).thenReturn(shareholderResponse);
        when(shareholderResponse.body()).thenReturn(IoUtil.readUtf8(ClientServiceTest.class.getResourceAsStream("/tyc/股东信息.json")));
        //
        when(HttpUtil.createGet("http://open.api.tianyancha.com/services/open/ic/baseinfoV3/2.0?keyword=12345")).thenReturn(baseInfoRequest);
        when(baseInfoRequest.header(anyMap())).thenReturn(baseInfoRequest);
        when(baseInfoRequest.execute()).thenReturn(baseInfoResponse);
        when(baseInfoResponse.body()).thenReturn(IoUtil.readUtf8(ClientServiceTest.class.getResourceAsStream("/tyc/基本信息.json")));

        //
        CorpCommerceInfo info = new CorpCommerceInfo();
        info.setId(8L);
        info.setRegisterCapital(7780032000000L);
        info.setRealCapital(2L);
        commerceInfoMapper.updateById(info);
        ClientSyncRSP sync = clientService.sync(8L);

        System.out.println("====================================================================================");
        System.out.println(JSONUtil.toJsonPrettyStr(sync));
        System.out.println("====================================================================================");

        Assertions.assertNotNull(sync);
        Assertions.assertNotNull(sync.getChangedCommerceInfo().getRealCapital());
        Assertions.assertNull(sync.getChangedCommerceInfo().getRegisterCapital());

        //shareholder
        Assertions.assertTrue(CollUtil.isNotEmpty(sync.getShareholderInfoChangedItemList().getList()));
        Assertions.assertEquals(sync.getShareholderInfoChangedItemList().getList().get(0).getShareholderName(), "李彦宏");

    }

    /**
     * 初始化客户主办及部门
     **/
    @Test
    void initClientAuth(){
        //1.查询出所有未分类客户
        List<Client> list = clientService.list(Wrappers.<Client>lambdaQuery());
        List<Client> updateList = new ArrayList<>();
        list.forEach(client -> {
            ClientAuthBO clientAuthByProj = clientService.getClientAuthByProj(client.getId(), 3L);
            if(ClientAuthEnum.HIGH_SEAS.equals(clientAuthByProj.getClientAuthEnum())) {
                client.setAuthType(ClientAuthEnum.HIGH_SEAS.name());
            } else if (ClientAuthEnum.NO_AFFILIATION.equals(clientAuthByProj.getClientAuthEnum())){
                client.setAuthType(ClientAuthEnum.NO_AFFILIATION.name());
            } else {
                client.setAuthType(ClientAuthEnum.EXCLUSIVE.name());
                client.setBelongSponsorId(clientAuthByProj.getProjSponsorUserId());
                client.setBelongDeptId(clientAuthByProj.getBizDeptId());
            }
            updateList.add(client);
        });
        clientService.updateBatchById(updateList, 500);
    }

    @Test
    void noticeClose(){
        projReviewService.noticeClose();
    }

}