package cn.zswltech.mithras.others.service;

import cn.zswltech.mithras.message.persistence.mapper.ZhfkNoticeRelationMapper;
import cn.zswltech.mithras.web.MithrasApplication;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@Rollback
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public class MonthlyManageServiceTest {

    @Resource
    ZhfkNoticeRelationMapper zhfkNoticeRelationMapper;

    @Test
    public void readAndDown(){
        String sql = "select a.ID_ as processInstanceId,b.id as noticeId, a.name_ as processInstanceName,\n" +
                "c.name_ as modelName, c.key_ as modelKey\n" +
                "from act_hi_procinst a \n" +
                "inner join zhfk_notice b on a.ID_ = b.content\n" +
                "inner join act_re_procdef c on a.proc_def_id_ = c.id_\n" +
                "where c.key_ not in ('ProjReviewCreateFlow','ProjReviewModifyFlow','ProjReviewPricingApprovalFlow','ContractCreateFlow','ContractModifyFlow','ContractStartRentFlow','ContractAddNewReceiptFlow','ContractEarlySettleFlow','ContractNormalSettleFlow','ContractLPRChangeFlow','ContractExtensionFlow','ContractEarlyRepayFlow','ContractChangeRepayPlanFlow','PaymentCreateFlow')\n" +
                ";";
    }
}
