package cn.zswltech.mithras.others.hand.extract.client;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.zswltech.mithras.others.hand.extract.HandRequestUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 汉得客户模块数据导出
 *
 * @author wangchuanhao
 * @date 2022/9/22 2:50 PM
 */
@Slf4j
public class ClientHandExporter {

    public static void main(String[] args) throws Exception {
        String data = export();
        IoUtil.write(new FileOutputStream("/Users/wang/Desktop/汉得客户模块导出_20220928.json"), true, data.getBytes(StandardCharsets.UTF_8));
    }

    @SneakyThrows
    public static String export() {
        JSONArray resArray = new JSONArray();
        // 列表
        String allClientListString = ClientHandRequestUtil.baseList();
        JSONArray allClientArray = HandRequestUtil.extractCommonArray(allClientListString);
        log.info("客户数量:{}", allClientArray.size());
        ExecutorService fixPool = Executors.newFixedThreadPool(3);
        CountDownLatch countDownLatch = new CountDownLatch(allClientArray.size());
        for (int i = 0; i < allClientArray.size(); i++) {
            JSONObject listDataObj = allClientArray.getJSONObject(i);
            log.info("处理客户:{}", listDataObj.getString("bp_name"));
            CompletableFuture.runAsync(() -> {
                try {
                    if ("ORG".equals(listDataObj.getString("bp_class"))) {
                        JSONObject dataObj = handleCorp(listDataObj);
                        synchronized (ClientHandExporter.class) {
                            resArray.add(dataObj);
                        }
                    } else if ("NP".equals(listDataObj.getString("bp_class"))) {
                        JSONObject dataObj = handleNormal(listDataObj);
                        synchronized (ClientHandExporter.class) {
                            resArray.add(dataObj);
                        }
                    }
                } catch (Exception e) {
                    log.error("处理失败:{}", listDataObj.getString("bp_name"), e);
                } finally {
                    countDownLatch.countDown();
                }
            }, fixPool);
        }
        countDownLatch.await();
        return resArray.toJSONString();
    }

    public static JSONObject handleCorp(JSONObject listDataObj) {
        JSONObject clientDetailObj = HandRequestUtil.extractCommonArray(ClientHandRequestUtil.corpBaseInfo(listDataObj.getString("bp_id"))).getJSONObject(0);
        //log.info("处理客户:{}", clientDetailObj.getString("bp_name"));

        JSONArray corpAddressArray = HandRequestUtil.extractCommonArray(ClientHandRequestUtil.corpAddressInfo(clientDetailObj));
        JSONArray corpContractArray = HandRequestUtil.extractCommonArray(ClientHandRequestUtil.corpContractInfo(clientDetailObj));
        JSONArray corpMasterArray = HandRequestUtil.extractCommonArray(ClientHandRequestUtil.corpMasterInfo(clientDetailObj));
        JSONArray corpShareholderArray = HandRequestUtil.extractCommonArray(ClientHandRequestUtil.corpShareholderInfo(clientDetailObj));
        JSONArray corpBankAccountArray = HandRequestUtil.extractCommonArray(ClientHandRequestUtil.corpBankAccountInfo(clientDetailObj));
        JSONArray corpRelatedArray = HandRequestUtil.extractCommonArray(ClientHandRequestUtil.corpRelatedInfo(clientDetailObj));
        JSONObject fileDataObj = handleFileList(clientDetailObj, "ORG");
        JSONArray subjectArray = handleCorpSubject(listDataObj.getString("bp_id"));

        clientDetailObj.put("create_user_name", listDataObj.getString("owner_user_id_n"));
        clientDetailObj.put("corpAddressArray", corpAddressArray);
        clientDetailObj.put("corpContractArray", corpContractArray);
        clientDetailObj.put("corpMasterArray", corpMasterArray);
        clientDetailObj.put("corpShareholderArray", corpShareholderArray);
        clientDetailObj.put("corpBankAccountArray", corpBankAccountArray);
        clientDetailObj.put("corpRelatedArray", corpRelatedArray);
        clientDetailObj.put("fileDataObj", fileDataObj);
        clientDetailObj.put("subjectArray", subjectArray);

        return clientDetailObj;
    }

    public static JSONObject handleNormal(JSONObject listDataObj) {
        JSONObject clientDetailObj = HandRequestUtil.extractCommonArray(ClientHandRequestUtil.normalBaseInfo(listDataObj.getString("bp_id"))).getJSONObject(0);
        JSONArray normalBankAccountArray = HandRequestUtil.extractCommonArray(ClientHandRequestUtil.normalBankAccountInfo(clientDetailObj));
        JSONArray normalAddressArray = HandRequestUtil.extractCommonArray(ClientHandRequestUtil.normalAddressInfo(clientDetailObj));
        JSONArray normalContractArray = HandRequestUtil.extractCommonArray(ClientHandRequestUtil.normalContractInfo(clientDetailObj));
        JSONObject fileDataObj = handleFileList(clientDetailObj, "NP");

        clientDetailObj.put("create_user_name", listDataObj.getString("owner_user_id_n"));
        clientDetailObj.put("normalBankAccountArray", normalBankAccountArray);
        clientDetailObj.put("normalAddressArray", normalAddressArray);
        clientDetailObj.put("normalContractArray", normalContractArray);
        clientDetailObj.put("fileDataObj", fileDataObj);
        return clientDetailObj;
    }

    public static JSONObject handleFileList(JSONObject detailObj, String bpClass) {
        Map<String, JSONArray> fileMap = new HashMap<>();

        // 获取资料清单
        JSONArray materialsArray = null;
        if ("ORG".equals(bpClass)) {
            materialsArray = HandRequestUtil.extractCommonArray(ClientHandRequestUtil.corpMaterialsList(detailObj));
        } else {
            materialsArray = HandRequestUtil.extractCommonArray(ClientHandRequestUtil.normalMaterialsList(detailObj));
        }
        for (int j = 0; j < materialsArray.size(); j++) {
            JSONObject materialsData = materialsArray.getJSONObject(j);
            if (StringUtils.isBlank(materialsData.getString("file_names"))) {
                continue;
            }
            JSONArray fileArray = fileMap.computeIfAbsent(materialsData.getString("document_name"), k -> new JSONArray());
            fileArray.addAll(HandRequestUtil.extractCommonArray(ClientHandRequestUtil.materialsDetail(materialsData.getString("bp_attachment_id"))));
        }

        // 获取附件
        JSONArray attachmentArray = HandRequestUtil.extractCommonArray(ClientHandRequestUtil.attachmentList(detailObj, "ORG".equals(bpClass) ? "PRJ306F1" : "PRJ306F2"));
        for (int j = 0; j < attachmentArray.size(); j++) {
            JSONObject attachmentData = attachmentArray.getJSONObject(j);
            if (StringUtils.isBlank(attachmentData.getString("file_names"))) {
                continue;
            }
            JSONArray fileArray = fileMap.computeIfAbsent(attachmentData.getString("archive_category_n"), k -> new JSONArray());
            fileArray.addAll(HandRequestUtil.extractCommonArray(ClientHandRequestUtil.materialsDetail(attachmentData.getString("bp_attachment_id"))));

        }
        return JSONObject.parseObject(JSON.toJSONString(fileMap));
    }

    public static JSONArray handleCorpSubject(String bpId) {
        JSONArray resArray = new JSONArray();
        JSONArray subjectArray = HandRequestUtil.extractCommonArray(ClientHandRequestUtil.corpSubjectList(bpId));
        for (int i = 0; i < subjectArray.size(); i++) {
            JSONObject subjectObj = subjectArray.getJSONObject(i);
            String statementHdId = subjectObj.getString("fin_statement_hd_id");
            JSONArray balanceSheetArray = HandRequestUtil.extractCommonArray(ClientHandRequestUtil.corpSubjectDetail(statementHdId, "BALANCE_SHEET"));
            JSONArray profitSheetArray = HandRequestUtil.extractCommonArray(ClientHandRequestUtil.corpSubjectDetail(statementHdId, "PROFIT_STATEMENT"));
            JSONArray cashFlowArray = HandRequestUtil.extractCommonArray(ClientHandRequestUtil.corpSubjectDetail(statementHdId, "CASH_FLOW_STATEMENT"));
            JSONArray financialArray = HandRequestUtil.extractCommonArray(ClientHandRequestUtil.corpSubjectDetail(statementHdId, "FINANCIAL_INDEX"));
            subjectObj.put("balanceSheetArray", balanceSheetArray);
            subjectObj.put("profitSheetArray", profitSheetArray);
            subjectObj.put("cashFlowArray", cashFlowArray);
            subjectObj.put("financialArray", financialArray);
            resArray.add(subjectObj);
        }
        return resArray;
    }

}
