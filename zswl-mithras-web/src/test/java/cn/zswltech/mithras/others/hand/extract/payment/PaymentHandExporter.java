package cn.zswltech.mithras.others.hand.extract.payment;

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
 * 付款提取
 *
 * @author wangchuanhao
 * @date 2022/9/23 4:43 PM
 */
@Slf4j
public class PaymentHandExporter {

    public static void main(String[] args) throws Exception {
        String data = export();
        IoUtil.write(new FileOutputStream("/Users/wang/Desktop/汉得付款模块导出_20220923.json"), true, data.getBytes(StandardCharsets.UTF_8));
    }

    public static String export() {
        JSONArray resArray = new JSONArray();
        String allPaymentListString = PaymentHandRequestUtil.baseList();
        JSONArray allPaymentArray = HandRequestUtil.extractCommonArray(allPaymentListString);
        log.info("付款数量:{}", allPaymentArray.size());
        for (int i = 0; i < allPaymentArray.size(); i++) {
            JSONObject listDataObj = allPaymentArray.getJSONObject(i);
            JSONObject paymentDetailObj = HandRequestUtil.extractCommonArray(PaymentHandRequestUtil.baseInfo(listDataObj.getString("payment_req_id"))).getJSONObject(0);

            JSONArray paymentFlowArray = HandRequestUtil.extractCommonArray(PaymentHandRequestUtil.paymentFlowList(paymentDetailObj));
            JSONArray paymenyAnswer1Array = HandRequestUtil.extractCommonArray(PaymentHandRequestUtil.paymenyAnswer1(paymentDetailObj));
            JSONArray paymenyAnswer2Array = HandRequestUtil.extractCommonArray(PaymentHandRequestUtil.paymenyAnswer2(paymentDetailObj));
            JSONArray paymenyAnswer3Array = HandRequestUtil.extractCommonArray(PaymentHandRequestUtil.paymenyAnswer3(paymentDetailObj));
            JSONArray paymenyAnswer4Array = HandRequestUtil.extractCommonArray(PaymentHandRequestUtil.paymenyAnswer4(paymentDetailObj));
            JSONArray paymenyAnswer5Array = HandRequestUtil.extractCommonArray(PaymentHandRequestUtil.paymenyAnswer5(paymentDetailObj));
            JSONObject fileDataObj = handleFileList(paymentDetailObj);

            paymentDetailObj.put("paymentFlowArray", paymentFlowArray);
            paymentDetailObj.put("paymenyAnswer1Array", paymenyAnswer1Array);
            paymentDetailObj.put("paymenyAnswer2Array", paymenyAnswer2Array);
            paymentDetailObj.put("paymenyAnswer3Array", paymenyAnswer3Array);
            paymentDetailObj.put("paymenyAnswer4Array", paymenyAnswer4Array);
            paymentDetailObj.put("paymenyAnswer5Array", paymenyAnswer5Array);
            paymentDetailObj.put("fileDataObj", fileDataObj);

            resArray.add(paymentDetailObj);
        }
        return resArray.toJSONString();
    }

    public static JSONObject handleFileList(JSONObject detailObj) {
        Map<String, JSONArray> fileMap = new HashMap<>();

        // 获取资料清单
        JSONArray materialsArray = HandRequestUtil.extractCommonArray(PaymentHandRequestUtil.materialsList(detailObj));
        for (int j = 0; j < materialsArray.size(); j++) {
            JSONObject materialsData = materialsArray.getJSONObject(j);
            if (StringUtils.isBlank(materialsData.getString("file_names"))) {
                continue;
            }
            JSONArray fileArray = fileMap.computeIfAbsent(materialsData.getString("document_name"), k -> new JSONArray());
            fileArray.addAll(HandRequestUtil.extractCommonArray(PaymentHandRequestUtil.materialsDetail(materialsData.getString("req_attachment_id"))));
        }

        // 获取附件
        JSONArray attachmentArray = HandRequestUtil.extractCommonArray(PaymentHandRequestUtil.attachmentList(detailObj));
        for (int j = 0; j < attachmentArray.size(); j++) {
            JSONObject attachmentData = attachmentArray.getJSONObject(j);
            if (StringUtils.isBlank(attachmentData.getString("file_names"))) {
                continue;
            }
            JSONArray fileArray = fileMap.computeIfAbsent(attachmentData.getString("archive_category_n"), k -> new JSONArray());
            fileArray.addAll(HandRequestUtil.extractCommonArray(PaymentHandRequestUtil.materialsDetail(attachmentData.getString("req_attachment_id"))));
        }
        return JSONObject.parseObject(JSON.toJSONString(fileMap));
    }

}
