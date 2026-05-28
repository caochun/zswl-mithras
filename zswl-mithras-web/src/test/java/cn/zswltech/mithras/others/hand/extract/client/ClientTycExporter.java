package cn.zswltech.mithras.others.hand.extract.client;

import cn.hutool.core.io.IoUtil;
import cn.zswltech.mithras.service.service.third.TycService;
import cn.zswltech.mithras.service.service.third.model.MithrasBaseInfo;
import cn.zswltech.mithras.service.service.third.model.MithrasRelatedEnterpriseInfo;
import cn.zswltech.mithras.service.service.third.model.MithrasShareholderInfo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 客户模块天眼查数据
 *
 * @author wangchuanhao
 * @date 2022/9/25 1:20 PM
 */
@Component
@Slf4j
public class ClientTycExporter {

    @Resource
    private TycService tycService;

    public static void main(String[] args) {
        detectErrorData();
    }

    /**
     * 检测重复数据
     */
    @SneakyThrows
    public static void detectErrorData() {
        String handClientDataString = IoUtil.read(new FileSystemResource("/Users/wang/Desktop/数据导入/汉得客户模块导出_20220923.json").getInputStream(), Charset.defaultCharset());
        JSONArray handClientDataArray = JSONArray.parseArray(handClientDataString);
        Map<String, List<String>> uscCodeMap = new HashMap<>();
        Map<String, List<String>> certNumberMap = new HashMap<>();
        Map<String, List<String>> zhongZhengCodeMap = new HashMap<>();
        List<String> lackSocialCreditCodeData = new ArrayList<>();
        List<String> lackCertNumberData = new ArrayList<>();
        Set<String> multiNameData = new HashSet<>(), existNameSet = new HashSet<>();

        for (int i = 0; i < handClientDataArray.size(); i++) {
            JSONObject handClientDataObj = handClientDataArray.getJSONObject(i);
            if (existNameSet.contains(handClientDataObj.getString("bp_name"))) {
                multiNameData.add(handClientDataObj.getString("bp_name"));
            }
            existNameSet.add(handClientDataObj.getString("bp_name"));

            if ("ORG".equals(handClientDataObj.getString("bp_class"))) {
                if (StringUtils.isBlank(handClientDataObj.getString("social_credit_code"))) {
                    lackSocialCreditCodeData.add(handClientDataObj.getString("bp_name"));
                } else {
                    List<String> uscCodeList = uscCodeMap.computeIfAbsent(handClientDataObj.getString("social_credit_code"), k -> new ArrayList<>());
                    uscCodeList.add(handClientDataObj.getString("bp_name"));
                }
                if (StringUtils.isNotBlank(handClientDataObj.getString("identity_code"))) {
                    List<String> zhongZhengCodeList = zhongZhengCodeMap.computeIfAbsent(handClientDataObj.getString("identity_code"), k -> new ArrayList<>());
                    zhongZhengCodeList.add(handClientDataObj.getString("bp_name"));
                }
            } else {
                if (StringUtils.isBlank(handClientDataObj.getString("id_card_no"))) {
                    lackCertNumberData.add(handClientDataObj.getString("bp_name"));
                } else {
                    List<String> certNumberList = certNumberMap.computeIfAbsent(handClientDataObj.getString("id_card_no"), k -> new ArrayList<>());
                    certNumberList.add(handClientDataObj.getString("bp_name"));
                }
            }
        }
        log.info("重名数据:{}", JSON.toJSONString(multiNameData));
        log.info("缺少社会统一信用码数据:{}", JSON.toJSONString(lackSocialCreditCodeData));
        log.info("缺少身份证号数据:{}", JSON.toJSONString(lackCertNumberData));
        log.info("社会统一信用码重复数据:{}", JSON.toJSONString(uscCodeMap.values().stream().filter(l -> l.size() > 1).collect(Collectors.toList())));
        log.info("身份证号重复数据:{}", JSON.toJSONString(certNumberMap.values().stream().filter(l -> l.size() > 1).collect(Collectors.toList())));
        log.info("中征码重复数据:{}", JSON.toJSONString(zhongZhengCodeMap.values().stream().filter(l -> l.size() > 1).collect(Collectors.toList())));
    }

    /**
     * 调用天眼查接口 采集数据
     * @param uscCodeList
     * @return
     */
    @SneakyThrows
    public String tycInfo(Collection<String> uscCodeList) {
        uscCodeList = uscCodeList.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        JSONObject dataObj = new JSONObject();
        ExecutorService fixPool = Executors.newFixedThreadPool(2);
        CountDownLatch countDownLatch = new CountDownLatch(uscCodeList.size());
        for (String uscCode : uscCodeList) {
            CompletableFuture.runAsync(() -> {
                try {
                    log.info("处理数据:{}", uscCode);
                    JSONObject detailObj = new JSONObject();

                    MithrasBaseInfo mithrasBaseInfo = tycService.baseInfo(uscCode);
                    List<MithrasShareholderInfo> shareholderInfoArray = tycService.shareholderInfo(uscCode);
                    List<MithrasRelatedEnterpriseInfo> relatedEnterpriseInfoArray = tycService.relatedEnterpriseInfo(uscCode);
                    detailObj.put("mithrasBaseInfo", mithrasBaseInfo);
                    detailObj.put("shareholderInfoArray", shareholderInfoArray);
                    detailObj.put("relatedEnterpriseInfoArray", relatedEnterpriseInfoArray);
                    synchronized (ClientTycExporter.class) {
                        dataObj.put(uscCode, detailObj);
                    }
                } catch (Exception e) {
                    log.error("处理失败:{}", uscCode, e);
                } finally {
                    countDownLatch.countDown();
                }
            }, fixPool);
        }
        countDownLatch.await();
        log.info("处理完成");
        return dataObj.toJSONString();
    }

}
