package cn.zswltech.mithras.report.handler.impl;

import cn.zswltech.mithras.collection.model.CollectionRecordInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author luyi
 */
@ExtendWith(MockitoExtension.class)
class CrActualRepayHandlerTest {

    @InjectMocks
    CrActualRepayHandler crActualRepayHandler;

    @Test
    void mergePhaseAmount() {
        List<CollectionRecordInfo> list = new ArrayList<>();
        CollectionRecordInfo info = new CollectionRecordInfo();
        info.setCollectionId(1L);
        info.setCollectionAmount(100L);
        info.setCollectionDate(LocalDate.now());
        info.setPrincipal(111L);
        CollectionRecordInfo inf2 = new CollectionRecordInfo();
        inf2.setCollectionId(1L);
        inf2.setCollectionAmount(100L);
        inf2.setCollectionDate(LocalDate.now());
        inf2.setPrincipal(111L);
        list.add(info);
        list.add(inf2);
        Collection<CollectionRecordInfo> infoList = crActualRepayHandler.mergePhaseAmount(list);
        Assertions.assertEquals(infoList.size(), 1);
        Assertions.assertEquals(infoList.stream().findFirst().get().getCollectionAmount(), 200L);
    }
}