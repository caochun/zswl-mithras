package cn.zswltech.mithras.others.hand.extract.contract;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import liquibase.pro.packaged.J;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 提取器
 *
 * @author wangchuanhao
 * @date 2022/8/15 2:52 PM
 */
@Slf4j
public class ContractExtractor {

    private static final String OUTPUT_FILE_PATH = "/Users/wang/Desktop/合同模块数据整理_线上_20220818_1352.xlsx";
    private static final String OUTPUT_JSON_FILE_PATH = "/Users/wang/Desktop/合同模块数据整理json_线上_20220818_1633.json";

    @SneakyThrows
    public void extractList() {

        // postman curl 自动生成 开始
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(600L, TimeUnit.SECONDS)
                .readTimeout(600L, TimeUnit.SECONDS)
                .writeTimeout(600L, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
        RequestBody body = RequestBody.create(mediaType, "_request_data=%7B%22parameter%22%3A%7B%22randomString%22%3A%220.5991243703953948Thu%20Aug%2018%202022%2011%3A05%3A09%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)%22%7D%7D");
        Request request = new Request.Builder()
                .url("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.cont.mapper.HlsCusConContractMapper.queryConContractDetails&layout_code=CONT301&tab_code=G_CON&bp_seq=&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true")
                .method("POST", body)
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Cookie", "vw=1440; vh=625; SESSIONID_HAP=9bf65e87-8bb2-4314-a600-97145c52ae23; TARGETURL=modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview%3Ffunction_group_id%3D10260%26function_code%3DCONT301; userId=10001; app_theme=hap; app_subject=HAP; EL-ADMIN-TOEKN=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJhbGxvY2F0aW9uSWRcIjoxNDEsXCJ1c2VybmFtZVwiOlwiYWRtaW5cIn0iLCJleHAiOjE2NjA3OTYwODMsImlhdCI6MTY2MDcyNDA4M30.Z9LQ4VKGQ85zFq7ztXH6xifFMA1RcuoNQiFWHCEXVjb--zDfhoB4Itq39WlaoCDxnL5ItNKMTnP07jspZVjdnw; sidebarStatus=0")
                .addHeader("Origin", "http://10.100.222.10")
                .addHeader("Referer", "http://10.100.222.10/core/modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview?function_group_id=10260&function_code=CONT301")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
                .addHeader("X-CSRF-TOKEN", "e22aa9b8-e4ac-4fc2-ae9d-bcfa43bbc06a")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("springFlag", "Y")
                .build();
        Response response = client.newCall(request).execute();
        // postman curl 自动生成 结束

        JSONObject resObj = JSONObject.parseObject(response.body().string());
        log.info("数据:{}", resObj.toJSONString());
        JSONArray dataArray = resObj.getJSONObject("result").getJSONArray("record");
        log.info("数据条数:{}", dataArray.size());
        ExcelWriter excelWriter = new ExcelWriter(OUTPUT_FILE_PATH);
        //excelWriter.writeHeadRow(Arrays.asList("合同编号", "项目名称", "合同金额", "承租人", "担保人", "收款账号", "收款账户名", "收款开户行", "业务经理", "项目协办", "部门"));
        List<InitDataModel> dataModelList = new ArrayList<>();
        for (int i = 0; i < dataArray.size(); i++) {
            JSONObject dataObj = dataArray.getJSONObject(i);
            // 过滤掉作废合同
            if ("作废".equals(dataObj.getString("contract_status_n"))) {
                continue;
            }
            InitDataModel dataModel = InitDataModel.builder()
                    .contractNumber(dataObj.getString("contract_number"))
                    .projName(dataObj.getString("project_name"))
                    .contractAmount(dataObj.getString("contract_amount"))
                    .createDate(dataObj.getString("create_date"))
                    .contractStatusName(dataObj.getString("contract_status_n"))
                    .contractProcessStatusName(dataObj.getString("workflow_status_n"))
                    .sponsorUserName(dataObj.getString("employee_id_n"))
                    .cosponsorUserName(extractCosponsor(dataObj))
                    .dept(dataObj.getString("lease_organization_n"))
                    .build();

            // 银行信息
            JSONArray bankArray = extractBank(dataObj);
            if (bankArray.size() > 0) {
                dataModel.setAccountBanks(bankArray.stream().map(j -> ((JSONObject)j).getString("bank_branch_name")).filter(StringUtils::isNotBlank).collect(Collectors.joining(",")));
                dataModel.setAccountNumbers(bankArray.stream().map(j -> ((JSONObject)j).getString("bank_account_num")).filter(StringUtils::isNotBlank).collect(Collectors.joining(",")));
                dataModel.setAccountNames(bankArray.stream().map(j -> ((JSONObject)j).getString("bank_account_name")).filter(StringUtils::isNotBlank).collect(Collectors.joining(",")));
            }

            JSONArray bpArray = extractBp(dataObj);
            // 担保人信息
            dataModel.setGuaranteeNames(bpArray.stream()
                    .filter(j -> "担保人".equals(((JSONObject)j).getString("bp_category_n")))
                    .map(j -> ((JSONObject)j).getString("bp_id_n"))
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining(",")));

            // 联合承租人
            dataModel.setLesseeNames(bpArray.stream()
                    .filter(j -> "承租人".equals(((JSONObject)j).getString("bp_category_n")) || "联合承租人".equals(((JSONObject)j).getString("bp_category_n")))
                    .map(j -> ((JSONObject)j).getString("bp_id_n"))
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining(",")));

            dataModelList.add(dataModel);
        }
        excelWriter.write(dataModelList);
        excelWriter.flush();
        excelWriter.close();
    }

    private static String extractCosponsor(JSONObject jsonObject) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(jsonObject.getString("assist_employee_id_n"))) {
            sb.append(jsonObject.getString("assist_employee_id_n"));
        }
        if (StringUtils.isNotBlank(jsonObject.getString("assist_employee_id_a_n"))) {
            sb.append("," + jsonObject.getString("assist_employee_id_a_n"));
        }
        return sb.toString();
    }

    @SneakyThrows
    private static JSONArray extractBank(JSONObject data) {
        // 银行账号
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(600L, TimeUnit.SECONDS)
                .readTimeout(600L, TimeUnit.SECONDS)
                .writeTimeout(600L, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
        JSONObject d = new JSONObject();
        d.put("parameter", data);
        String bodyData = "_request_data=" + URLEncoder.encode(d.toJSONString(), "utf-8");
        log.info("请求数据:{}", bodyData);
        RequestBody body = RequestBody.create(mediaType, bodyData);
        Request request = new Request.Builder()
                .url("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.cont.mapper.ContractBankAccountMapper.queryConContractAccountDetails&layout_code=CONT303F1&tab_code=G_LOAN_ACCOUNT_NUMBER&bp_seq=&document_category=CONTRACT&document_type=CONLB&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true")
                .method("POST", body)
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Cookie", "vw=1440; vh=625; SESSIONID_HAP=9bf65e87-8bb2-4314-a600-97145c52ae23; TARGETURL=modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview%3Ffunction_group_id%3D10260%26function_code%3DCONT301; userId=10001; app_theme=hap; app_subject=HAP; EL-ADMIN-TOEKN=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJhbGxvY2F0aW9uSWRcIjoxNDEsXCJ1c2VybmFtZVwiOlwiYWRtaW5cIn0iLCJleHAiOjE2NjA3OTYwODMsImlhdCI6MTY2MDcyNDA4M30.Z9LQ4VKGQ85zFq7ztXH6xifFMA1RcuoNQiFWHCEXVjb--zDfhoB4Itq39WlaoCDxnL5ItNKMTnP07jspZVjdnw; sidebarStatus=0")
                .addHeader("Origin", "http://10.100.222.10")
                .addHeader("Referer", "http://10.100.222.10/core/modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview?function_group_id=10260&function_code=CONT301")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
                .addHeader("X-CSRF-TOKEN", "e22aa9b8-e4ac-4fc2-ae9d-bcfa43bbc06a")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("springFlag", "Y")
                .build();
        Response response = client.newCall(request).execute();
        JSONObject resultObj = JSONObject.parseObject(response.body().string());
        log.info("银行结果:{}", resultObj.toJSONString());
        return resultObj.getJSONObject("result").getJSONArray("record");
    }

    @SneakyThrows
    private static JSONArray extractGuarantee(JSONObject data) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(600L, TimeUnit.SECONDS)
                .readTimeout(600L, TimeUnit.SECONDS)
                .writeTimeout(600L, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
        JSONObject d = new JSONObject();
        d.put("parameter", data);
        String bodyData = "_request_data=" + URLEncoder.encode(d.toJSONString(), "utf-8");
        log.info("请求数据:{}", bodyData);
        RequestBody body = RequestBody.create(mediaType, bodyData);
        Request request = new Request.Builder()
                .url("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.cont.mapper.ContractGuaranteeMapper.queryContractGuaranteeDetail&layout_code=CONT303F1&tab_code=G_GUARANTEE_INFORMATION&bp_seq=&document_category=CONTRACT&document_type=CONLB&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true")
                .method("POST", body)
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Cookie", "SESSIONID_HAP=9bf65e87-8bb2-4314-a600-97145c52ae23; vh=625; vw=1440; TARGETURL=modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview%3Ffunction_group_id%3D10260%26function_code%3DCONT301; userId=10001; app_theme=hap; app_subject=HAP; EL-ADMIN-TOEKN=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJhbGxvY2F0aW9uSWRcIjoxNDEsXCJ1c2VybmFtZVwiOlwiYWRtaW5cIn0iLCJleHAiOjE2NjA3OTYwODMsImlhdCI6MTY2MDcyNDA4M30.Z9LQ4VKGQ85zFq7ztXH6xifFMA1RcuoNQiFWHCEXVjb--zDfhoB4Itq39WlaoCDxnL5ItNKMTnP07jspZVjdnw; sidebarStatus=0")
                .addHeader("Origin", "http://10.100.222.10")
                .addHeader("Referer", "http://10.100.222.10/core/modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview?function_group_id=10260&function_code=CONT301")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
                .addHeader("X-CSRF-TOKEN", "e22aa9b8-e4ac-4fc2-ae9d-bcfa43bbc06a")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("springFlag", "Y")
                .build();
        Response response = client.newCall(request).execute();
        JSONObject resultObj = JSONObject.parseObject(response.body().string());
        log.info("银行结果:{}", resultObj.toJSONString());
        return resultObj.getJSONObject("result").getJSONArray("record");
    }

    @SneakyThrows
    public static JSONArray extractBp(JSONObject data) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(600L, TimeUnit.SECONDS)
                .readTimeout(600L, TimeUnit.SECONDS)
                .writeTimeout(600L, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
        JSONObject d = new JSONObject();
        d.put("parameter", data);
        String bodyData = "_request_data=" + URLEncoder.encode(d.toJSONString(), "utf-8");
        log.info("请求数据:{}", bodyData);
        RequestBody body = RequestBody.create(mediaType, bodyData);
        Request request = new Request.Builder()
                .url("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.cont.mapper.HlsCusConContractBpMapper.queryContractBp&layout_code=CONT303F1&tab_code=G_BP&bp_seq=&document_category=CONTRACT&document_type=CONLB&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true")
                .method("POST", body)
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Cookie", "vw=1440; vh=625; SESSIONID_HAP=9bf65e87-8bb2-4314-a600-97145c52ae23; TARGETURL=modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview%3Ffunction_group_id%3D10260%26function_code%3DCONT301; userId=10001; app_theme=hap; app_subject=HAP; EL-ADMIN-TOEKN=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJhbGxvY2F0aW9uSWRcIjoxNDEsXCJ1c2VybmFtZVwiOlwiYWRtaW5cIn0iLCJleHAiOjE2NjA3OTYwODMsImlhdCI6MTY2MDcyNDA4M30.Z9LQ4VKGQ85zFq7ztXH6xifFMA1RcuoNQiFWHCEXVjb--zDfhoB4Itq39WlaoCDxnL5ItNKMTnP07jspZVjdnw; sidebarStatus=0")
                .addHeader("Origin", "http://10.100.222.10")
                .addHeader("Referer", "http://10.100.222.10/core/modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview?function_group_id=10260&function_code=CONT301")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
                .addHeader("X-CSRF-TOKEN", "e22aa9b8-e4ac-4fc2-ae9d-bcfa43bbc06a")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("springFlag", "Y")
                .build();
        Response response = client.newCall(request).execute();
        JSONObject resultObj = JSONObject.parseObject(response.body().string());
        log.info("客户数据:{}", resultObj.toJSONString());
        return resultObj.getJSONObject("result").getJSONArray("record");
        //return resultObj.getJSONObject("result").getJSONArray("record").stream().map(j -> ((JSONObject)j).getString("bp_id_n")).filter(StringUtils::isNotBlank).collect(Collectors.joining(","));
    }

    @SneakyThrows
    private static JSONArray extractPawnInfo(JSONObject data) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(600L, TimeUnit.SECONDS)
                .readTimeout(600L, TimeUnit.SECONDS)
                .writeTimeout(600L, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
        JSONObject d = new JSONObject();
        d.put("parameter", data);
        String bodyData = "_request_data=" + URLEncoder.encode(d.toJSONString(), "utf-8");
        log.info("请求数据:{}", bodyData);
        RequestBody body = RequestBody.create(mediaType, bodyData);
        Request request = new Request.Builder()
                .url("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.cont.mapper.HlsCusConContractMortgageMapper.queryContractMortgageDetail&layout_code=CONT303F1&tab_code=G_MORTGAGE&bp_seq=&document_category=CONTRACT&document_type=CONLB&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true")
                .method("POST", body)
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Cookie", "SESSIONID_HAP=9bf65e87-8bb2-4314-a600-97145c52ae23; vh=625; vw=1440; TARGETURL=modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview%3Ffunction_group_id%3D10260%26function_code%3DCONT301; userId=10001; app_theme=hap; app_subject=HAP; EL-ADMIN-TOEKN=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJhbGxvY2F0aW9uSWRcIjoxNDEsXCJ1c2VybmFtZVwiOlwiYWRtaW5cIn0iLCJleHAiOjE2NjA3OTYwODMsImlhdCI6MTY2MDcyNDA4M30.Z9LQ4VKGQ85zFq7ztXH6xifFMA1RcuoNQiFWHCEXVjb--zDfhoB4Itq39WlaoCDxnL5ItNKMTnP07jspZVjdnw; sidebarStatus=0")
                .addHeader("Origin", "http://10.100.222.10")
                .addHeader("Referer", "http://10.100.222.10/core/modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview?function_group_id=10260&function_code=CONT301")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
                .addHeader("X-CSRF-TOKEN", "e22aa9b8-e4ac-4fc2-ae9d-bcfa43bbc06a")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("springFlag", "Y")
                .build();
        Response response = client.newCall(request).execute();
        JSONObject resultObj = JSONObject.parseObject(response.body().string());
        log.info("银行结果:{}", resultObj.toJSONString());
        return resultObj.getJSONObject("result").getJSONArray("record");
    }

    @SneakyThrows
    private static JSONArray extractPledgeInfo(JSONObject data) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(600L, TimeUnit.SECONDS)
                .readTimeout(600L, TimeUnit.SECONDS)
                .writeTimeout(600L, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
        JSONObject d = new JSONObject();
        d.put("parameter", data);
        String bodyData = "_request_data=" + URLEncoder.encode(d.toJSONString(), "utf-8");
        log.info("请求数据:{}", bodyData);
        RequestBody body = RequestBody.create(mediaType, bodyData);
        Request request = new Request.Builder()
                .url("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.cont.mapper.HlsCusConContractMortgageMapper.queryContractMortgageDetailOne&layout_code=CONT303F1&tab_code=G_MORTGAGE_PG&bp_seq=&document_category=CONTRACT&document_type=CONLB&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true")
                .method("POST", body)
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Cookie", "SESSIONID_HAP=9bf65e87-8bb2-4314-a600-97145c52ae23; vh=625; vw=1440; TARGETURL=modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview%3Ffunction_group_id%3D10260%26function_code%3DCONT301; userId=10001; app_theme=hap; app_subject=HAP; EL-ADMIN-TOEKN=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJhbGxvY2F0aW9uSWRcIjoxNDEsXCJ1c2VybmFtZVwiOlwiYWRtaW5cIn0iLCJleHAiOjE2NjA3OTYwODMsImlhdCI6MTY2MDcyNDA4M30.Z9LQ4VKGQ85zFq7ztXH6xifFMA1RcuoNQiFWHCEXVjb--zDfhoB4Itq39WlaoCDxnL5ItNKMTnP07jspZVjdnw; sidebarStatus=0")
                .addHeader("Origin", "http://10.100.222.10")
                .addHeader("Referer", "http://10.100.222.10/core/modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview?function_group_id=10260&function_code=CONT301")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
                .addHeader("X-CSRF-TOKEN", "e22aa9b8-e4ac-4fc2-ae9d-bcfa43bbc06a")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("springFlag", "Y")
                .build();
        Response response = client.newCall(request).execute();
        JSONObject resultObj = JSONObject.parseObject(response.body().string());
        log.info("银行结果:{}", resultObj.toJSONString());
        return resultObj.getJSONObject("result").getJSONArray("record");
    }

    /**
     * 合同模块所有数据抓取
     */
    @SneakyThrows
    public void extractAll() {
        // postman curl 自动生成 开始
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(600L, TimeUnit.SECONDS)
                .readTimeout(600L, TimeUnit.SECONDS)
                .writeTimeout(600L, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
        RequestBody body = RequestBody.create(mediaType, "_request_data=%7B%22parameter%22%3A%7B%22randomString%22%3A%220.5991243703953948Thu%20Aug%2018%202022%2011%3A05%3A09%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)%22%7D%7D");
        Request request = new Request.Builder()
                .url("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.cont.mapper.HlsCusConContractMapper.queryConContractDetails&layout_code=CONT301&tab_code=G_CON&bp_seq=&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true")
                .method("POST", body)
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Cookie", "vw=1440; vh=625; SESSIONID_HAP=9bf65e87-8bb2-4314-a600-97145c52ae23; TARGETURL=modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview%3Ffunction_group_id%3D10260%26function_code%3DCONT301; userId=10001; app_theme=hap; app_subject=HAP; EL-ADMIN-TOEKN=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJhbGxvY2F0aW9uSWRcIjoxNDEsXCJ1c2VybmFtZVwiOlwiYWRtaW5cIn0iLCJleHAiOjE2NjA3OTYwODMsImlhdCI6MTY2MDcyNDA4M30.Z9LQ4VKGQ85zFq7ztXH6xifFMA1RcuoNQiFWHCEXVjb--zDfhoB4Itq39WlaoCDxnL5ItNKMTnP07jspZVjdnw; sidebarStatus=0")
                .addHeader("Origin", "http://10.100.222.10")
                .addHeader("Referer", "http://10.100.222.10/core/modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview?function_group_id=10260&function_code=CONT301")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
                .addHeader("X-CSRF-TOKEN", "e22aa9b8-e4ac-4fc2-ae9d-bcfa43bbc06a")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("springFlag", "Y")
                .build();
        Response response = client.newCall(request).execute();
        // postman curl 自动生成 结束

        JSONObject resObj = JSONObject.parseObject(response.body().string());
        log.info("数据:{}", resObj.toJSONString());
        JSONArray dataArray = resObj.getJSONObject("result").getJSONArray("record");
        log.info("数据条数:{}", dataArray.size());

        dataArray.removeIf(j -> "作废".equals(((JSONObject)j).getString("contract_status_n")));
        for (int i = 0; i < dataArray.size(); i++) {
            JSONObject dataObj = dataArray.getJSONObject(i);
            // 客户信息
            JSONArray bpArray = extractBp(dataObj);
            dataObj.put("bpArray", bpArray);
            // 客户收款账号信息
            JSONArray bankArray = extractBank(dataObj);
            dataObj.put("bankArray", bankArray);
            // 担保信息
            JSONArray guaranteeArray = extractGuarantee(dataObj);
            dataObj.put("guaranteeArray", guaranteeArray);
            // 抵押物信息
            JSONArray pawnInfoArray = extractPawnInfo(dataObj);
            dataObj.put("pawnInfoArray", pawnInfoArray);
            // 质押物信息
            JSONArray pledgeInfoArray = extractPledgeInfo(dataObj);
            dataObj.put("pledgeInfoArray", pledgeInfoArray);
        }

        IoUtil.write(new FileOutputStream(OUTPUT_JSON_FILE_PATH), true, dataArray.toJSONString().getBytes(StandardCharsets.UTF_8));
    }

    public static void main(String[] args) {
        extractFileName();
    }

    @SneakyThrows
    public static void extractFileName() {
        // postman curl 自动生成 开始
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(600L, TimeUnit.SECONDS)
                .readTimeout(600L, TimeUnit.SECONDS)
                .writeTimeout(600L, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
        RequestBody body = RequestBody.create(mediaType, "_request_data=%7B%22parameter%22%3A%7B%22randomString%22%3A%220.35105207421744544Fri%20Aug%2019%202022%2017%3A01%3A07%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)%22%7D%7D");
        Request request = new Request.Builder()
                .url("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.cont.mapper.HlsCusConContractMapper.queryConContractDetails&layout_code=CONT301&tab_code=G_CON&bp_seq=&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true")
                .method("POST", body)
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Cookie", "SESSIONID_HAP=ef6d623f-e006-41db-837a-27b38066ddae; vh=572; vw=1440; TARGETURL=modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview%3Ffunction_group_id%3D10226%26function_code%3DPRJ307; userId=10001; app_theme=hap; app_subject=HAP")
                .addHeader("Origin", "http://10.100.222.10")
                .addHeader("Referer", "http://10.100.222.10/core/modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview?function_group_id=10260&function_code=CONT301")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
                .addHeader("X-CSRF-TOKEN", "3b2524ae-aa0d-4287-a065-5572d2e30c1b")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("springFlag", "Y")
                .build();
        Response response = client.newCall(request).execute();
        // postman curl 自动生成 结束

        JSONObject resObj = JSONObject.parseObject(response.body().string());
        log.info("数据:{}", resObj.toJSONString());
        JSONArray dataArray = resObj.getJSONObject("result").getJSONArray("record");
        log.info("数据条数:{}", dataArray.size());

        // 合同编号 -> 资料类别 -> {资料名称,资料id}

        int zuofeiCount = 0;
        Map<String, Map<String, JSONArray>> fileNameMap = new HashMap<>();
        for (int i = 0; i < dataArray.size(); i++) {
            JSONObject dataObj = dataArray.getJSONObject(i);
            // 过滤掉作废合同
            if ("作废".equals(dataObj.getString("contract_status_n"))) {
                log.info("作废跳过");
                zuofeiCount++;
                continue;
            }
            String contractNumber = dataObj.getString("contract_number");
            JSONObject d = new JSONObject();
            d.put("parameter", dataObj);
            dataObj.put("contract_attachment_category", "NEW");
            String bodyData = "_request_data=" + URLEncoder.encode(d.toJSONString(), "utf-8");
            log.info("请求数据:{}", bodyData);
            RequestBody aBody = RequestBody.create(mediaType, bodyData);

            Request aReq = new Request.Builder()
                    .url("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.cont.mapper.HlsCusConContractAttachmentMapper.queryConContractAttachment&layout_code=CONT303F1&tab_code=G_ATTACHMENT&bp_seq=&document_category=CONTRACT&document_type=CONLB&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true")
                    .method("POST", aBody)
                    .addHeader("Accept", "*/*")
                    .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                    .addHeader("Connection", "keep-alive")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .addHeader("Cookie", "SESSIONID_HAP=ef6d623f-e006-41db-837a-27b38066ddae; vh=572; vw=1440; TARGETURL=modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview%3Ffunction_group_id%3D10226%26function_code%3DPRJ307; userId=10001; app_theme=hap; app_subject=HAP")
                    .addHeader("Origin", "http://10.100.222.10")
                    .addHeader("Referer", "http://10.100.222.10/core/modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview?function_group_id=10260&function_code=CONT301")
                    .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
                    .addHeader("X-CSRF-TOKEN", "3b2524ae-aa0d-4287-a065-5572d2e30c1b")
                    .addHeader("X-Requested-With", "XMLHttpRequest")
                    .addHeader("springFlag", "Y")
                    .build();
            Response aRes = client.newCall(aReq).execute();
            JSONArray aResArray = JSONArray.parseObject(aRes.body().string()).getJSONObject("result").getJSONArray("record");
            Map<String, JSONArray> fileMap = fileNameMap.computeIfAbsent(contractNumber, k -> new HashMap<>());
            for (int j = 0; j < aResArray.size(); j++) {
                JSONObject aData = aResArray.getJSONObject(j);
                if (StringUtils.isBlank(aData.getString("file_names"))) {
                    continue;
                }

                JSONArray aFileArray = fileMap.computeIfAbsent(aData.getString("document_name"), k -> new JSONArray());
                RequestBody bBody = RequestBody.create(mediaType, "_request_data=%7B%22parameter%22%3A%7B%22randomString%22%3A%220.48857146753870584Fri%20Aug%2019%202022%2016%3A57%3A03%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)%22%7D%7D");
                Request bReq = new Request.Builder()
                        .url("http://10.100.222.10/core/fnd/attachment/query?headerId=" + aData.getString("contract_attachment_id") + "&tableName=con_contract_attachment&pagesize=10&pagenum=1&pageSize=10&pageNum=1&_fetchall=true&_autocount=false")
                        .method("POST", bBody)
                        .addHeader("Accept", "*/*")
                        .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                        .addHeader("Connection", "keep-alive")
                        .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                        .addHeader("Cookie", "SESSIONID_HAP=ef6d623f-e006-41db-837a-27b38066ddae; vh=572; vw=1440; TARGETURL=modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview%3Ffunction_group_id%3D10226%26function_code%3DPRJ307; userId=10001; app_theme=hap; app_subject=HAP")
                        .addHeader("Origin", "http://10.100.222.10")
                        .addHeader("Referer", "http://10.100.222.10/core/modules/CONT/CON_CONTRACT/CONT301/con_contract_query.lview?function_group_id=10260&function_code=CONT301")
                        .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
                        .addHeader("X-CSRF-TOKEN", "3b2524ae-aa0d-4287-a065-5572d2e30c1b")
                        .addHeader("X-Requested-With", "XMLHttpRequest")
                        .addHeader("springFlag", "Y")
                        .build();
                Response bRes = client.newCall(bReq).execute();
                aFileArray.addAll(JSONObject.parseObject(bRes.body().string()).getJSONObject("result").getJSONArray("record"));
            }
        }
        log.info("作废跳过次数:{}", zuofeiCount);
        IoUtil.write(new FileOutputStream("/Users/wang/Desktop/合同文件整理_线上_20221124_2.json"), true, JSON.toJSONString(fileNameMap).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 初始化数据模型
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class InitDataModel {

        /**
         * 合同编号
         */
        private String contractNumber;

        /**
         * 项目名称
         */
        private String projName;

        /**
         * 合同金额
         */
        private String contractAmount;

        /**
         * 合同状态名称
         */
        private String contractStatusName;

        /**
         * 合同流程状态名称
         */
        private String contractProcessStatusName;

        /**
         * 合同创建日期
         */
        private String createDate;

        /**
         * 承租人
         */
        private String lesseeNames;

        /**
         * 担保人
         */
        private String guaranteeNames;

        /**
         * 收款账号
         */
        private String accountNumbers;

        /**
         * 收款账户名
         */
        private String accountNames;

        /**
         * 收款开户行
         */
        private String accountBanks;

        /**
         * 主办
         */
        private String sponsorUserName;

        /**
         * 协办
         */
        private String cosponsorUserName;

        /**
         * 部门
         */
        private String dept;

    }

}
