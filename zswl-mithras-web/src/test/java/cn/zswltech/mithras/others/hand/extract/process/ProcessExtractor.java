package cn.zswltech.mithras.others.hand.extract.process;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 流程操作数据提取
 *
 * @author wangchuanhao
 * @date 2022/8/18 9:47 AM
 */
@Slf4j
public class ProcessExtractor {

    //private static final String OUTPUT_FILE_PATH = "/Users/wang/Desktop/合同模块数据整理_线上_20220817.xlsx";
    private static final String OUTPUT_JSON_FILE_PATH = "/Users/wang/Desktop/流程模块数据整理json_线上_20220818.json";

    @SneakyThrows
    public void extractList() {
        JSONArray allArray = new JSONArray();
        int p = 1, pageSize = 10;
        while (true) {
            // postman curl 自动生成 开始
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(600L, TimeUnit.SECONDS)
                    .readTimeout(600L, TimeUnit.SECONDS)
                    .writeTimeout(600L, TimeUnit.SECONDS)
                    .build();
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
            RequestBody body = RequestBody.create(mediaType, "_request_data=%7B%22parameter%22%3A%7B%22randomString%22%3A%220.4538557564325978Thu%20Aug%2018%202022%2011%3A36%3A40%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)%22%7D%7D");
            Request request = new Request.Builder()
                    .url("http://10.100.222.10/core/wfl/leaf/query/process-instances/monitor?pagenum=" + p + "&pageSize=" + pageSize + "&_fetchall=false&_autocount=true")
                    .method("POST", body)
                    .addHeader("Accept", "*/*")
                    .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                    .addHeader("Connection", "keep-alive")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .addHeader("Cookie", "vw=1440; vh=625; SESSIONID_HAP=9bf65e87-8bb2-4314-a600-97145c52ae23; TARGETURL=modules/WFL/WFL003/process_monitor.lview%3Ffunction_group_id%3D10117%26function_code%3DWFL003; userId=10001; app_theme=hap; app_subject=HAP; EL-ADMIN-TOEKN=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJhbGxvY2F0aW9uSWRcIjoxNDEsXCJ1c2VybmFtZVwiOlwiYWRtaW5cIn0iLCJleHAiOjE2NjA3OTYwODMsImlhdCI6MTY2MDcyNDA4M30.Z9LQ4VKGQ85zFq7ztXH6xifFMA1RcuoNQiFWHCEXVjb--zDfhoB4Itq39WlaoCDxnL5ItNKMTnP07jspZVjdnw; sidebarStatus=0")
                    .addHeader("Origin", "http://10.100.222.10")
                    .addHeader("Referer", "http://10.100.222.10/core/modules/WFL/WFL003/process_monitor.lview?function_group_id=10117&function_code=WFL003")
                    .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
                    .addHeader("X-CSRF-TOKEN", "e22aa9b8-e4ac-4fc2-ae9d-bcfa43bbc06a")
                    .addHeader("X-Requested-With", "XMLHttpRequest")
                    .addHeader("springFlag", "Y")
                    .build();
            Response response = client.newCall(request).execute();

            JSONObject resObj = JSONObject.parseObject(response.body().string());
            log.info("数据:{}", resObj.toJSONString());
            JSONArray dataArray = resObj.getJSONObject("result").getJSONArray("record");
            allArray.addAll(dataArray);
            p++;
            if (dataArray.size() < pageSize) {
                break;
            }
        }

        log.info("数据条数:{}", allArray.size());

        List<InitDataModel> initDataModelList = new ArrayList<>();
        for (int i = 0; i < allArray.size(); i++) {
            JSONObject dataObj = allArray.getJSONObject(i);
            InitDataModel initDataModel = InitDataModel.builder()
                    .processInstanceId(dataObj.getString("id"))
                    .processInstanceName(dataObj.getString("document_name"))
                    .modelName(dataObj.getString("process_name"))
                    .startUserName(dataObj.getString("start_user_name"))
                    .startTime(dataObj.getString("start_time"))
                    .endTime(dataObj.getString("end_time"))
                    .build();

            // 审批记录
            initDataModel.setOperateModelList(extractOperateRecord(initDataModel.getProcessInstanceId()));

            initDataModelList.add(initDataModel);
        }
        IoUtil.write(new FileOutputStream(OUTPUT_JSON_FILE_PATH), true, JSON.toJSONString(initDataModelList).getBytes(StandardCharsets.UTF_8));
        // ExcelWriter excelWriter = new ExcelWriter(OUTPUT_FILE_PATH);
        //
    }

    @SneakyThrows
    private static List<OperateModel> extractOperateRecord(String processInstanceId) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(600L, TimeUnit.SECONDS)
                .readTimeout(600L, TimeUnit.SECONDS)
                .writeTimeout(600L, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
        RequestBody body = RequestBody.create(mediaType, "_request_data=%7B%22parameter%22%3A%7B%22randomString%22%3A%220.13969359243394774Thu%20Aug%2018%202022%2011%3A29%3A27%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)%22%7D%7D");
        Request request = new Request.Builder()
                .url("http://10.100.222.10/core/wfl/leaf/instance/" + processInstanceId + "?pagesize=10&pagenum=1&pageSize=10&pageNum=1&_fetchall=true&_autocount=true")
                .method("POST", body)
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Cookie", "vw=1440; vh=625; SESSIONID_HAP=9bf65e87-8bb2-4314-a600-97145c52ae23; TARGETURL=modules/WFL/WFL003/process_monitor.lview%3Ffunction_group_id%3D10117%26function_code%3DWFL003; userId=10001; app_theme=hap; app_subject=HAP; EL-ADMIN-TOEKN=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJhbGxvY2F0aW9uSWRcIjoxNDEsXCJ1c2VybmFtZVwiOlwiYWRtaW5cIn0iLCJleHAiOjE2NjA3OTYwODMsImlhdCI6MTY2MDcyNDA4M30.Z9LQ4VKGQ85zFq7ztXH6xifFMA1RcuoNQiFWHCEXVjb--zDfhoB4Itq39WlaoCDxnL5ItNKMTnP07jspZVjdnw; sidebarStatus=0")
                .addHeader("Origin", "http://10.100.222.10")
                .addHeader("Referer", "http://10.100.222.10/core/modules/WFL/WFL003/process_monitor.lview?function_group_id=10117&function_code=WFL003")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
                .addHeader("X-CSRF-TOKEN", "e22aa9b8-e4ac-4fc2-ae9d-bcfa43bbc06a")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("springFlag", "Y")
                .build();
        Response response = client.newCall(request).execute();

        JSONArray dataArray = JSONObject.parseObject(response.body().string())
                .getJSONObject("result")
                .getJSONArray("record")
                .getJSONObject(0)
                .getJSONArray("historic_task_list");
        log.info("操作记录:{}", JSON.toJSONString(dataArray));
        return dataArray.stream().map(j -> (JSONObject)j)
                .map(j -> OperateModel.builder()
                        .operatorName(j.getString("assignee_name"))
                        .operateTime(j.getString("end_time"))
                        .comment(j.getString("comment"))
                        .taskNodeName(j.getString("name"))
                        .action(j.getString("action"))
                        .actionName(convertAction(j.getString("action")))
                        .build())
                .collect(Collectors.toList());
    }

    private static String convertAction(String value) {

        if (StringUtils.isNotBlank(value)) {
            if (value.equals("APPROVED")) {
                //业务申请流程 同意 动作改为 已审批
                return "同意";

            } else if (value.equals("REJECTED")) {
                return "拒绝";
            } else if (value.equals("REJECT")) {
                return "拒绝";
            } else if (value.equals("ADD_SIGN")) {
                return "加签";
            } else if (value.equals("DELEGATE")) {
                return "转交";
            } else if (value.equals("AUTO_DELEGATE")) {
                return "自动转交";
            } else if (value.equals("JUMP")) {
                return "跳转";
            } else if (value.equals("RECALL")) {
                return "撤回";
            } else if (value.equals("SAVE")) {
                return "保留";
            } else if (value.equals("CHANGE")) {
                return "变更";
            } else if (value.equals("RETURN")) {
                return "退回提交人";
            } else if (value.equals("CONDITIONAL")) {
                return "有条件同意/复议";
            } else if (value.equals("RETURN-SUBMIT")) {
                return "退回提交人";
            } else if (value.equals("REVIEW")) {
                return "复议";
            } else if (value.equals("CARBON_COPY")) {
                return "抄送";
            }else if(value.equals("RETURN-RE-SUBMIT")) {
                return "退回逐级提交";
            } else {
                return "";
            }
        }
        return "";
    }

    /**
     * 初始化数据模型
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class InitDataModel {

        private String processInstanceId;

        private String processInstanceName;

        private String modelName;

        private String startUserName;

        private String startTime;

        private String endTime;

        private List<OperateModel> operateModelList;

    }

    /**
     * 操作记录
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OperateModel {

        private String operateTime;

        private String comment;

        private String taskNodeName;

        private String operatorName;

        private String action;
        
        private String actionName;

    }

}
