package cn.zswltech.mithras.service.controller.riskcontrol;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.riskcontrol.RiskControlRelatedClientApi;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlRelatedTransactionPageReq;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlRelatedTransactionRsp;
import cn.zswltech.mithras.riskcontrol.relation.RiskControlRelatedClient;
import cn.zswltech.mithras.service.service.riskcontrol.RiskControlRelatedClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 金控关联方名录
 * @date 2023-03-08
 */
@RestController
@Slf4j
public class RiskControlRelatedClientController implements RiskControlRelatedClientApi {
    @Resource
    private RiskControlRelatedClientService baseService;

    private final Map<String, List<String>> pullDownMap = new HashMap<>();

    @Override
    public R<Void> importFile(MultipartFile file) {
        try {
            baseService.importFile(file.getInputStream());
        } catch (Exception e) {
            log.error("导入金控关联方名录失败", e);
            return R.fail("导入金控关联方名录失败");
        }
        pullDownMap.clear();
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
        if(ObjectUtil.isNotEmpty(pullDownMap)){
            return R.ok(pullDownMap);
        }
        List<RiskControlRelatedClient> listAll = baseService.list();
        Set<String> relatedPartyType = listAll.stream().map(RiskControlRelatedClient::getRelatedPartyType)
                .collect(Collectors.toSet());
        Set<String> parentRelationType = listAll.stream().map(RiskControlRelatedClient::getParentRelationType)
                .collect(Collectors.toSet());
        Set<String> subRelationType = listAll.stream().map(RiskControlRelatedClient::getSubRelationType)
                .collect(Collectors.toSet());
        pullDownMap.put("relatedPartyType", new ArrayList<>(relatedPartyType));
        pullDownMap.put("parentRelationType", new ArrayList<>(parentRelationType));
        pullDownMap.put("subRelationType", new ArrayList<>(subRelationType));
        return R.ok(pullDownMap);
    }
}