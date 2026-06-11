package cn.zswltech.mithras.application.orchestration.third.financial.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.UUIDUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.kpi.KpiProvisionBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.kpi.KpiProvisionBaseInfoDetailRSP;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.third.retry.mapper.model.ExceptionRequestInfo;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiHandleFactory;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiHandler;
import cn.zswltech.mithras.third.retry.application.ExceptionRequestRecordService;
import cn.zswltech.mithras.application.orchestration.kpi.KpiProvisionDetailService;
import cn.zswltech.mithras.third.application.financial.SyncReceiveProvisionService;
import cn.zswltech.mithras.third.financialshare.client.req.ReceiveProvisionREQ;
import cn.zswltech.mithras.third.financialshare.client.resp.FinancialCommonRSP;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName SyncReceiveProvisionServiceImpl
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/8/7 2:17 下午
 * @Version 1.0
 **/
@Service
@Slf4j
public class SyncReceiveProvisionServiceImpl implements SyncReceiveProvisionService {

    @Resource
    private KpiProvisionDetailService kpiProvisionDetailService;

    @Resource
    private PlatformApiHandleFactory platformApiHandleFactory;
    @Resource
    private ExceptionRequestRecordService exceptionRequestInfoService;
    @Resource
    private OrgDOMapper orgDOMapper;

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() + 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));


    @Override
    public void syncReceiveProvision(Long provisionId) {
        log.info("SyncReceiveProvisionService syncReceiveProvision began {}", provisionId);
        KpiProvisionBaseInfoDetailREQ kpiProvisionBaseInfoDetailREQ = new KpiProvisionBaseInfoDetailREQ();
        kpiProvisionBaseInfoDetailREQ.setId(provisionId);
        kpiProvisionBaseInfoDetailREQ.setPage(1);
        kpiProvisionBaseInfoDetailREQ.setPageSize(Integer.MAX_VALUE);
        KpiProvisionBaseInfoDetailRSP details = kpiProvisionDetailService.detail(kpiProvisionBaseInfoDetailREQ);
        if (ObjectUtil.isNotEmpty(details) && ObjectUtil.isNotEmpty(details.getProvisionBaseInfoList()) && ObjectUtil.isNotEmpty(details.getProvisionBaseInfoList().getList())) {
            //借据维度构造
            Map<Long, List<KpiProvisionBaseInfoDetailRSP.KpiProvisionBaseInfoBody>> receiptProvisionMap = details.getProvisionBaseInfoList().getList().stream().collect(Collectors.groupingBy(KpiProvisionBaseInfoDetailRSP.KpiProvisionBaseInfoBody::getReceiptId));
            Map<Long, Long> orgId2Code = orgDOMapper.selectByIds(details.getProvisionBaseInfoList().getList().stream().map(KpiProvisionBaseInfoDetailRSP.KpiProvisionBaseInfoBody::getProfitBelongDeptId).collect(Collectors.toList()), null).stream().collect(Collectors.toMap(OrgDO::getId, OrgDO::getMainOrgId, (a, b) -> a));
            //查询已经传输数据
            Map<Long, BigDecimal> lastReceiptId2BonusCurrent = new HashMap<>();
            List<ExceptionRequestInfo> exceptionRequestInfo = exceptionRequestInfoService.list(Wrappers.<ExceptionRequestInfo>lambdaQuery()
                    .eq(ExceptionRequestInfo::getBusinessId, details.getProvisionDate()));
            if (ObjectUtil.isNotEmpty(exceptionRequestInfo)) {
                exceptionRequestInfo.forEach(info -> {
                    List<ReceiveProvisionREQ> receiveProvisionREQS = JSON.parseArray(info.getReqData(), ReceiveProvisionREQ.class);
                    if (ObjectUtil.isNotEmpty(receiveProvisionREQS)) {
                        receiveProvisionREQS.stream().filter(base -> ObjectUtil.isNotEmpty(base.getEntry())).forEach(receiveProvisionREQ -> {
                            List<ReceiveProvisionREQ.ReceiveProvisionBody> provisionBodies = receiveProvisionREQ.getEntry();
                            provisionBodies.stream().filter(base -> ObjectUtil.isNotEmpty(base.getTallyamount())).forEach(body -> {
                                lastReceiptId2BonusCurrent.put(body.getReceiptId(), lastReceiptId2BonusCurrent.getOrDefault(body.getReceiptId(), BigDecimal.ZERO).add(body.getTallyamount()));
                            });
                        });
                    }
                });
            }
            List<ReceiveProvisionREQ> reqs = new ArrayList();
            receiptProvisionMap.forEach((key, value) -> {
                ReceiveProvisionREQ req = new ReceiveProvisionREQ();
                List<ReceiveProvisionREQ.ReceiveProvisionBody> list = new ArrayList();
                req.setBizdate(LocalDateTimeUtil.format(details.getProvisionDate(), DatePattern.NORM_DATE_PATTERN));
                req.setSourcebillno(UUIDUtil.genUuid());
                req.setDept(String.valueOf(orgId2Code.get(value.get(0).getProfitBelongDeptId())));
                value.forEach(base -> {
                    ReceiveProvisionREQ.ReceiveProvisionBody receiveProvisionBody = req.new ReceiveProvisionBody();
                    receiveProvisionBody.setYwlxtype(getCqBizTypeCode(base.getBizType()));
                    receiveProvisionBody.setCico_contract_num(base.getContractCode());
                    receiveProvisionBody.setReceiptId(base.getReceiptId());
                    receiveProvisionBody.setTallyamount(LongUtil.tenThousand2Dollar(LongUtil.null2zero(base.getBonusCurrent()).toString()).subtract(lastReceiptId2BonusCurrent.getOrDefault(base.getReceiptId(), BigDecimal.ZERO)));
                    if (receiveProvisionBody.getTallyamount().compareTo(BigDecimal.ZERO) != 0) {
                        list.add(receiveProvisionBody);
                    }
                });
                if (ObjectUtil.isNotEmpty(list)) {
                    req.setEntry(list);
                    reqs.add(req);
                }
            });
            PlatformApiHandler<List<ReceiveProvisionREQ>, FinancialCommonRSP> platformApiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.CQ_RECEIVE_PROVISION);
            if (ObjectUtil.isNotEmpty(reqs)) {
                CompletableFuture.runAsync(() -> {
                    platformApiHandler.execute(reqs);
                }, threadPool);
            }
        }
        //todo 同步记账申请单
    }


    /**
     * 016 对外出租业务
     * 048 融资租赁
     * 050 转租业务
     * 051 售后回租
     * 052 保理业务
     **/
    private String getCqBizTypeCode(String code) {
        //租赁
        if (ObjectUtil.isNotEmpty(LeaseType.of(code)) || ProjectBizType.ZL.name().equals(code)) {
            return "048";
        }
        if (ProjectBizType.BL.name().equals(code)) {
            return "052";
        }
        if (ProjectBizType.ZZ.name().equals(code)) {
            return "050";
        }
        if (ProjectBizType.ZR.name().equals(code)) {
            //待定
            return null;
        }
        return null;
    }
}
