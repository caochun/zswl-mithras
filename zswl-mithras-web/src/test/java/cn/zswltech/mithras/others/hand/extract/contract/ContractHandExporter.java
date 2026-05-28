package cn.zswltech.mithras.others.hand.extract.contract;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.zswltech.mithras.others.hand.extract.HandRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 合同数据导出
 *
 * @author wangchuanhao
 * @date 2022/9/23 2:44 PM
 */
@Slf4j
public class ContractHandExporter {

    public static void main(String[] args) throws Exception {
        String data = export();
        IoUtil.write(new FileOutputStream("/Users/wang/Desktop/汉得合同模块导出_20220928.json"), true, data.getBytes(StandardCharsets.UTF_8));
    }

    public static String export() {
        JSONArray resArray = new JSONArray();
        String allContractListString = ContractHandRequestUtil.baseList();
        JSONArray allContractArray = HandRequestUtil.extractCommonArray(allContractListString);
        log.info("合同数量:{}", allContractArray.size());

        for (int i = 0; i < allContractArray.size(); i++) {
            JSONObject listDataObj = allContractArray.getJSONObject(i);
            log.info("合同处理编号:{}", listDataObj.getString("contract_number"));

            JSONArray clientArray = HandRequestUtil.extractCommonArray(ContractHandRequestUtil.clientInfo(listDataObj));
            JSONArray cashflowArray = HandRequestUtil.extractCommonArray(ContractHandRequestUtil.cashflowInfo(listDataObj));
            JSONArray bankArray = HandRequestUtil.extractCommonArray(ContractHandRequestUtil.bankAccountInfo(listDataObj));
            JSONArray guaranteeArray = HandRequestUtil.extractCommonArray(ContractHandRequestUtil.guaranteeInfo(listDataObj));
            JSONArray mortgageArray = HandRequestUtil.extractCommonArray(ContractHandRequestUtil.mortgageInfo(listDataObj));
            JSONArray pledgeArray = HandRequestUtil.extractCommonArray(ContractHandRequestUtil.pledgeInfo(listDataObj));
            JSONArray priceArray = HandRequestUtil.extractCommonArray(ContractHandRequestUtil.prjQuotation(listDataObj));
            JSONObject materialsReqObj = JSONObject.parseObject(listDataObj.toJSONString());
            materialsReqObj.put("contract_attachment_category", "NEW");
            JSONObject fileDataObj = handleFileList(materialsReqObj);

            listDataObj.put("clientArray", clientArray);
            listDataObj.put("cashflowArray", cashflowArray);
            listDataObj.put("bankArray", bankArray);
            listDataObj.put("guaranteeArray", guaranteeArray);
            listDataObj.put("mortgageArray", mortgageArray);
            listDataObj.put("pledgeArray", pledgeArray);
            listDataObj.put("priceArray", priceArray);
            listDataObj.put("fileDataObj", fileDataObj);

            resArray.add(listDataObj);
        }
        return resArray.toJSONString();
    }

    /**
     * 处理文件
     * @param detailObj
     * @return
     */
    public static JSONObject handleFileList(JSONObject detailObj) {
        Map<String, JSONArray> fileMap = new HashMap<>();

        // 获取资料清单
        JSONArray materialsArray = HandRequestUtil.extractCommonArray(ContractHandRequestUtil.materialsList(detailObj));
        for (int j = 0; j < materialsArray.size(); j++) {
            JSONObject materialsData = materialsArray.getJSONObject(j);
            if (StringUtils.isBlank(materialsData.getString("file_names"))) {
                continue;
            }
            JSONArray fileArray = fileMap.computeIfAbsent(materialsData.getString("document_name"), k -> new JSONArray());
            fileArray.addAll(HandRequestUtil.extractCommonArray(ContractHandRequestUtil.materialsDetail(materialsData.getString("contract_attachment_id"))));
        }

        return JSONObject.parseObject(JSON.toJSONString(fileMap));
    }

}
