package cn.zswltech.mithras.others.service.service.collection;

import cn.zswltech.mithras.service.job.CollectionJob;
import cn.zswltech.mithras.service.service.collection.CollectionService;
import cn.zswltech.mithras.web.MithrasApplication;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
//@Rollback
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("pre")
class CollectionServiceTest {

    @Resource
    CollectionService collectionService;
    @Resource
    CollectionJob collectionJob;

    @Test
    void listProRemainPrinciAndProOverdueAmount() {
        List<Long> projIds = Arrays.asList(163L, 122L);
        System.out.println(collectionService.listProRemainPrinci(projIds));
        System.out.println(collectionService.listProOverdueAmount(projIds));
    }

    @Test
    void penaltyInterestJobHandler(){
        collectionJob.penaltyInterestJobHandler3();
    }

    @Test
    void updateClientPenaltyInterest() {
        collectionJob.updateClientPenaltyInterest();
    }
    @Test
    public void calcRemainingPrincipalByReceiptIdTest() {
        System.out.println(collectionService.calcRemainingPrincipalByReceiptId(4288L, null));
    }

}