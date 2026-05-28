package cn.zswltech.mithras.unittest.service.report;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.report.handler.ReportDataRepository;
import cn.zswltech.mithras.report.handler.impl.CrAccountHandler;
import cn.zswltech.mithras.report.mapper.draft.CrAccountDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.model.CrAccountDraft;
import cn.zswltech.mithras.service.mapper.collection.CollectionBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.lib.contract.ContractBaseInfoLibMapper;
import cn.zswltech.mithras.service.mapper.lib.contract.ContractLeasePriceLibMapper;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.contract.ContractLeasePriceLib;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.payment.PaymentBaseInfoMapper;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static cn.zswltech.mithras.service.enums.common.ProjectBizType.ZZ;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author luyi
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(CrAccountHandler.class)
public class CrAccountHandlerTest {

    @Spy
    @InjectMocks
    private CrAccountHandler crAccountHandler = new CrAccountHandler();
    @Mock
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Mock
    private ContractBaseInfoLibMapper contractBaseInfoLibMapper;
    @Mock
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Mock
    private ReportDataRepository reportDataRepository;
    @Mock
    private ContractLeasePriceLibMapper contractLeasePriceLibMapper;
    @Mock
    private CrAccountDraftMapper crAccountDraftMapper;

    @Test
    @SneakyThrows
    public void testCollectRentCollectionData() {
        //mock data
        CollectionBaseInfo c1 = new CollectionBaseInfo();
        c1.setContractId(1L);
        CollectionBaseInfo c2 = new CollectionBaseInfo();
        c2.setContractId(2L);
        List<CollectionBaseInfo> changedRentList = ListUtil.toList(c1, c2);
        ArrayList<CollectionBaseInfo> notAllWriteOffList = ListUtil.toList(c1);
        //mock data
        ContractBaseInfoLib lib = new ContractBaseInfoLib();
        lib.setOriginId(2L);
        lib.setBizType(ZZ.name());
        PaymentBaseInfo p1 = new PaymentBaseInfo();

        when(collectionBaseInfoMapper.selectList(any())).thenReturn(changedRentList, notAllWriteOffList);
        when(contractBaseInfoLibMapper.listNewestContractByContractIds(any())).thenReturn(ListUtil.toList(lib));
        when(paymentBaseInfoMapper.selectList(any())).thenReturn(ListUtil.toList(p1));
        when(reportDataRepository.receiptPayment(any())).thenReturn(true);
        when(contractLeasePriceLibMapper.selectOne(any())).thenReturn(new ContractLeasePriceLib());
        //
        PowerMockito.doReturn(new CrAccountDraft()).when(crAccountHandler, MemberMatcher.method(CrAccountHandler.class, "buildCrAccount",
                PaymentBaseInfo.class, ContractLeasePriceLib.class, ContractBaseInfoLib.class, List.class
        )).withArguments(any(PaymentBaseInfo.class), any(ContractLeasePriceLib.class), any(ContractBaseInfoLib.class), any(List.class));
        List<CrAccountDraft> result = crAccountHandler.collectRentCollectionData(LocalDateTime.now(), LocalDateTime.now().minusDays(1));
        Assertions.assertEquals(result.size(), 1);

    }

}
