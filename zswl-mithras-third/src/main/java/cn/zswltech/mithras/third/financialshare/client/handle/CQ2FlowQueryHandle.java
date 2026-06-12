package cn.zswltech.mithras.third.financialshare.client.handle;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.third.financialshare.enums.FinancialDevUrlENUM;
import cn.zswltech.mithras.third.financialshare.enums.FinancialRSPENUM;
import cn.zswltech.mithras.third.financialshare.enums.FinancialUrlENUM;
import cn.zswltech.mithras.third.retry.persistence.model.ExceptionRequestInfo;
import cn.zswltech.mithras.third.financialshare.model.FinanceFlowTempRecord;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.third.retry.application.ExceptionRequestRecordService;
import cn.zswltech.mithras.third.financialshare.application.FinanceFlowRecordTempService;
import cn.zswltech.mithras.third.financialshare.client.FinancialApiHandler;
import cn.zswltech.mithras.third.financialshare.application.FinancialConfigService;
import cn.zswltech.mithras.third.financialshare.client.req.CQ2FlowQueryReq;
import cn.zswltech.mithras.third.financialshare.client.resp.CQ2FlowQueryRsp;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.hutool.core.date.DatePattern.NORM_DATETIME_PATTERN;
import static cn.hutool.core.date.DatePattern.PURE_DATETIME_PATTERN;

/**
 * @ClassName PaymentHandle
 * @Description 苍穹应付单
 * @Author jackerhe
 * @Date 2022/10/26 2:43 下午
 * @Version 1.0
 **/
@Component
public class CQ2FlowQueryHandle extends FinancialApiHandler<CQ2FlowQueryReq, CQ2FlowQueryRsp> {

    @Resource
    private FinancialConfigService financialConfigService;

    @Resource
    private ExceptionRequestRecordService exceptionRequestInfoService;

    @Resource
    private FinanceFlowRecordTempService financeFlowRecordTempService;

    private final static LocalDateTime BEGAN_DATE = LocalDateTime.of(2024, 7, 1,0, 0,0);

    private static final int MAX_ATTEMPTS = 500; // 可以从配置文件或环境变量中读取


    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() + 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.CQ2_FLOW_QUERY;
    }

    @Override
    public String getUrl() {
        //测试环境
        if (!PROD.equals(active)) {
            return financialConfigService.getUrl(FinancialDevUrlENUM.CQ2_FLOW_QUERY.url);
        }
        return financialConfigService.getUrl(FinancialUrlENUM.CQ2_FLOW_QUERY.url);


    }

    @Override
    public CQ2FlowQueryRsp analyResponseResult(String response) {
        return JSONObject.parseObject(response, CQ2FlowQueryRsp.class);
    }

    //这里返回批次号
    @Override
    public CQ2FlowQueryRsp execute(CQ2FlowQueryReq reqData) {
        if (ObjectUtil.isEmpty(reqData)) {
            return null;
        }
        if (ObjectUtil.isEmpty(reqData.getBegintime())) {
            reqData.setBegintime(BEGAN_DATE);
        }
        if (ObjectUtil.isEmpty(reqData.getEndtime())) {
            reqData.setEndtime(LocalDateTime.now());
        }
        reqData.setPageSize(100);
        reqData.setPageNo(1);
        CQ2FlowQueryRsp result = new CQ2FlowQueryRsp();
        result.setBatchId(LocalDateTime.now().format(DateTimeFormatter.ofPattern(PURE_DATETIME_PATTERN)));
        queryWithSplit(reqData, 0, result);
        return result;
    }


    private boolean queryWithSplit(CQ2FlowQueryReq reqData, int depth, CQ2FlowQueryRsp result) {
        CQ2FlowQueryRsp data = fetchData(reqData);
        if(ObjectUtil.isEmpty(data)){
            throw new MithrasException("流水拉取接口失败");
        }
        if(depth > MAX_ATTEMPTS) {
            //最多拉取 50000条数据，防止无限递归
            return true;
        }
        if (ObjectUtil.isEmpty(data.getData()) || ObjectUtil.isEmpty(data.getData().getRows())) {
            return true; // 如果数据为空，认为是成功的，因为没有超出限制
        } else {
            log.info("流水拉取结果{}, {}", result.getBatchId(),data.getData().getRows());
            //入库
            data.setBatchId(result.getBatchId());
            if (depth == 0) {
                result = data;
            }
            //保存流水 自定义保存
            List<FinanceFlowTempRecord> financeFlowTempRecords = new ArrayList<>();
            data.getData().getRows().forEach(row -> {
                FinanceFlowTempRecord record = BeanUtil.copyProperties(row, FinanceFlowTempRecord.class);
                record.setCicoReconciliationcode(row.getCico_reconciliationcode());
                record.setCicoActivepayment(row.getCico_activepayment());
                record.setCicoBruid(row.getCico_bruid());
                record.setCicoBillno(row.getCico_billno());
                record.setCompanyNumber(row.getCompany_number());
                record.setCompanyName(row.getCompany_name());
                record.setAccountbankBankaccountnumber(row.getAccountbank_bankaccountnumber());
                record.setAccountbankAcctname(row.getAccountbank_acctname());
                record.setAccountbankName(row.getAccountbank_name());
                record.setBankNumber(row.getBank_number());
                record.setBankName(row.getBank_name());
                record.setCurrencyName(row.getCurrency_name());
                record.setBatchId(data.getBatchId());
                financeFlowTempRecords.add(record);
            });
            financeFlowRecordTempService.saveBatch(financeFlowTempRecords);
            reqData.setPageNo(reqData.getPageNo() + 1);
            return queryWithSplit(reqData, depth + 1, result);
        }
    }

    // 模拟从API或数据库获取数据的方法
    private CQ2FlowQueryRsp fetchData(CQ2FlowQueryReq reqData) {
        // 这里应该是调用实际的API或数据库查询
        // 模拟返回数据量，实际应用中应替换为实际的查询逻辑
        //预发生产
        return super.execute(reqData);
    }


    @Override
    public Map<String, String> getHttpHeadParam() {
        return financialConfigService.getHttpHeadParam(FinancialUrlENUM.CQ2_FLOW_QUERY);
    }

    @Override
    public void failureRetry(String platform) {
        List<ExceptionRequestInfo> exceptionRequestInfos = exceptionRequestInfoService.listNeedRetry(platform);
        if (ObjectUtil.isEmpty(exceptionRequestInfos)) {
            return;
        }
        exceptionRequestInfos.forEach(base -> CompletableFuture.runAsync(()->execute(JSON.parseObject(base.getReqData(), CQ2FlowQueryReq.class)),
                threadPool));
    }

    @Override
    public boolean isFailureSave() {
        return false;
    }

    @Override
    public String getBusinessId(CQ2FlowQueryReq reqData){
        if(ObjectUtil.isEmpty(reqData)){
            return null;
        }
        return reqData.getRequestId();
    }

    @Override
    public String wrapRequestParam(CQ2FlowQueryReq reqData) {
        if(PlatformApiEnum.CQ_APP_TOKEN.equals(platformApi()) || PlatformApiEnum.CQ_ACCESS_TOKEN.equals(platformApi())){
            return JSONObject.toJSONString(reqData);
        }
        HashMap<String , CQ2FlowQueryReq> hashMap = new HashMap();
        hashMap.put(DATA ,reqData);
        //统一处理为苍穹格式
        return JSONObject.toJSONString(hashMap);
    }

    @Override
    public boolean isExecuteSuccess(CQ2FlowQueryRsp resData) {
        // 权限验证成功
        if(ObjectUtil.isNotEmpty(resData)){
            if(FinancialRSPENUM.SUCCESS.getResult().equals(resData.getState()) || ObjectUtil.equals(true, resData.getStatus()) || ObjectUtil.equals(true, resData.getSuccess())){
                return Boolean.TRUE;
            }else {
                log.info("FinancialApiHandler {} execute error errorCode : {} message : {}", platformApi().apiName, resData.getErrorCode(), resData.getMessage());
            }
        }
        return Boolean.FALSE;
    }

    public String getAddParam(CQ2FlowQueryReq reqData, String url) {
        return url +
                "?pageSize=" + reqData.getPageSize() +
                "&pageNo=" + reqData.getPageNo() +
                "&begintime=" + reqData.getBegintime().format(DateTimeFormatter.ofPattern(NORM_DATETIME_PATTERN)).replace(" ", "%20") +
                "&endtime=" + reqData.getEndtime().format(DateTimeFormatter.ofPattern(NORM_DATETIME_PATTERN)).replace(" ", "%20") +
                "&companynumber=" + reqData.getCompanynumber();
    }
}
