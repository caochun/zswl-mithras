package cn.zswltech.mithras.fund.application.financial;

import cn.zswltech.mithras.fund.application.financial.FundFinancialSystemCallRecordService;
import cn.zswltech.mithras.fund.mapper.model.FundFinancialSystemCallRecord;
import cn.zswltech.mithras.fund.application.financial.dto.FinancialSystemSubmitQuery;
import cn.zswltech.mithras.fund.enums.financial.FundFinancialSystemEnum;
import cn.zswltech.mithras.fund.application.financial.dto.FinancialSystemSubmitResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author zswl
 * 对接保融接口服务类
 */
@Slf4j
@Component
public abstract class FinancialSystemService {

    @Resource
    private RestTemplate restTemplate;

    @Value("${data.share.submitUrl}")
    private String submitUrl;

    @Value("${data.share.baseUrl}")
    private String baseUrl;

    @Value("${data.share.clientId}")
    private String clientId;

    @Resource
    private FundFinancialSystemCallRecordService financialSystemCallRecordService;

    private static final String FINANCIAL_SUBMIT_OPERATION_CODE = "com.cncico.esb.opr.cwgscommon.cwgsApiInfo.cwgsapi";


    /**
     * 推送类型
     * @return
     */
    protected abstract FundFinancialSystemEnum getModule();


    /**
     * 构建请求体
     * @return
     */
    protected abstract FinancialSystemSubmitQuery buildRequest();


    /**
     * 系统校验的错误信息
     * @return
     */
    protected abstract String errorInfo();


    /**
     * 推送数据
     * @return
     */
    public void push(String batchNum){
        FinancialSystemSubmitQuery query = this.buildRequest();
        String res = null;
        String batchNumber = String.format("%s@%s",this.getModule().name(), batchNum);
        log.info("正在给财资系统推送数据,type:{},批次号:{}", this.getModule().getDisplay(),batchNumber);
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            httpHeaders.add("OperationCode", FINANCIAL_SUBMIT_OPERATION_CODE);
            httpHeaders.add("ClientId", clientId);
            String reqJson = JSONObject.toJSONString(query);
            httpHeaders.setContentLength(reqJson.getBytes().length);
            int indexOf = baseUrl.lastIndexOf(":");
            String hostStr = baseUrl.substring(0, indexOf);
            int port = Integer.parseInt(baseUrl.substring(indexOf + 1));
            httpHeaders.setHost(new InetSocketAddress(hostStr, port));
            HttpEntity<String> request = new HttpEntity<>(reqJson, httpHeaders);
            ResponseEntity<String> result = restTemplate.postForEntity(submitUrl, request, String.class);
            res = result.getBody();
        } catch (Exception e) {
            log.error("给财资系统推送数据出现异常",e);
        }finally {
            this.record(query, res, batchNumber);
        }
    }


    /**
     * 调用记录留痕
     * @param query 请求参数
     * @param resultStr 返回结果
     * @param flowId 批次号
     */
    public void record(FinancialSystemSubmitQuery query, String resultStr, String flowId){
        log.info("财资系统请求结束，调用记录入库，批次号:{}", flowId);
        FinancialSystemSubmitResult result = resultStr != null ? JSON.parseObject(resultStr, FinancialSystemSubmitResult.class) : new FinancialSystemSubmitResult();

        FundFinancialSystemCallRecord record = new FundFinancialSystemCallRecord();
        record.setQuery(JSON.toJSONString(query));
        record.setResult(resultStr);
        record.setBatchNumber(flowId);
        record.setDate(LocalDateTime.now());
        FinancialSystemSubmitQuery.Body body = query.getBody();
        List<Object> objectList = JSON.parseArray(JSON.toJSONString(body.getList()), Object.class);
        record.setCount(objectList.size());
        record.setType(this.getModule().getDisplay());
        record.setStatus(Optional.ofNullable(result.getCwgsHead()).map(FinancialSystemSubmitResult.CwgsHead::getReturnMsg).orElse("失败"));
        record.setSystemErrorInfo(this.errorInfo());
        financialSystemCallRecordService.save(record);
    }



    /**
     * 构建系统报文头
     * @return
     */
    public FinancialSystemSubmitQuery.MessageHead getMessageHead(){
        // 融租易系统推送财资系统通用的报文头格式，在此处统一维护
        FundFinancialSystemEnum financialSystemEnum = this.getModule();
        LocalDateTime now = LocalDateTime.now();
        String tranDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String tranTime = now.format(DateTimeFormatter.ofPattern("HHmmss"));

        FinancialSystemSubmitQuery.MessageHead messageHead = new FinancialSystemSubmitQuery.MessageHead();
        messageHead.setServiceCode("ZSZL001");
        messageHead.setServiceNo(financialSystemEnum.getServiceNo());
        messageHead.setConsumerCode(financialSystemEnum.getConsumerCode());
        messageHead.setChannelType("ESB");
        messageHead.setConsumerId("001");
        messageHead.setReqSequence(financialSystemEnum.getServiceNo() + financialSystemEnum.getConsumerCode() + tranDate + tranTime);
        messageHead.setTrandate(Long.valueOf(tranDate));
        messageHead.setTrantime(Long.valueOf(tranTime));
        return messageHead;
    }

    /**
     * 构建应用报文头
     * @return
     */
    public FinancialSystemSubmitQuery.AppHead getAppHead(){
        FinancialSystemSubmitQuery.AppHead appHead = new FinancialSystemSubmitQuery.AppHead();
        appHead.setOperator("ZSZLGGHYXGS");
        appHead.setOrgan("ZSZLGGH");
        return appHead;
    }

}
