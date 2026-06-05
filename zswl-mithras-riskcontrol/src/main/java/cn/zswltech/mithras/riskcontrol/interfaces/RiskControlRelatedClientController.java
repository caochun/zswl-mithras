package cn.zswltech.mithras.riskcontrol.interfaces;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.riskcontrol.RiskControlRelatedClientApi;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlRelatedTransactionPageReq;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlRelatedTransactionRsp;
import cn.zswltech.mithras.riskcontrol.relation.RiskControlRelatedClientApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author zhaozhengkang
 * @description 金控关联方名录
 * @date 2023-03-08
 */
@RestController
@Slf4j
public class RiskControlRelatedClientController implements RiskControlRelatedClientApi {
    @Resource
    private RiskControlRelatedClientApplicationService baseService;

    @Override
    public R<Void> importFile(MultipartFile file) {
        try {
            baseService.importFile(file.getInputStream());
        } catch (Exception e) {
            log.error("导入金控关联方名录失败", e);
            return R.fail("导入金控关联方名录失败");
        }
        return R.ok();
    }

    @Override
    public R<PageR<RiskControlRelatedTransactionRsp>> paymentList(RiskControlRelatedTransactionPageReq req) {
        return R.ok(baseService.paymentList(req));
    }

    @Override
    public R<PageR<RiskControlRelatedTransactionRsp>> collectionList(RiskControlRelatedTransactionPageReq req) {
        return R.ok(baseService.collectionList(req));
    }

    @Override
    public R<Map<String, List<String>>> pullDown() {
        return R.ok(baseService.pullDown());
    }
}
