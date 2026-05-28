package cn.zswltech.mithras.others.poc;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

/**
 * @author luyi
 */
public class 审批流定义复制 {

    static String s = "6487500\n" +
            "6430077\n" +
            "6344365\n" +
            "6312500\n" +
            "6312502\n" +
            "6185110\n" +
            "6025018\n" +
            "6025012\n" +
            "6025006\n" +
            "6025000\n" +
            "5990006\n" +
            "5990000\n" +
            "5895164\n" +
            "5887504\n" +
            "5877502\n" +
            "5830014\n" +
            "5830008\n" +
            "5727500\n" +
            "5670000\n" +
            "5620006\n" +
            "5620000\n" +
            "5597622\n" +
            "5587502\n" +
            "5587500\n" +
            "5372514\n" +
            "5372510\n" +
            "5365006\n" +
            "5365000\n" +
            "5187589\n" +
            "5187587\n" +
            "5112500\n" +
            "5097500\n" +
            "5095006\n" +
            "5095000\n" +
            "2490002\n" +
            "2470022\n" +
            "4955006\n" +
            "4955000\n" +
            "4882506\n" +
            "4882500\n" +
            "4880006\n" +
            "4880000\n" +
            "4877506\n" +
            "4877500\n" +
            "4825656\n" +
            "4745000\n" +
            "4655012\n" +
            "4655014\n" +
            "4550405\n" +
            "4550403\n" +
            "4467510\n" +
            "4467500\n" +
            "4297500\n" +
            "4290000\n" +
            "4245375\n" +
            "3880004\n" +
            "3792500\n" +
            "3755002\n" +
            "3755000\n" +
            "3612500\n" +
            "3580006\n" +
            "3562500\n" +
            "3547500\n" +
            "3240000\n" +
            "2965000\n" +
            "2507500";


    @SneakyThrows
    public static void main(String[] args) {
        String token = "{\"_salt_\":\"1B52F06ADD1B56BAF1BAE50747944CB5\",\"_qjt_ac_\":\"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlzcyI6ImZyYXVkbWV0cml4IiwiaWQiOjMsImV4cCI6MTcwMTQyMzAzNiwiaWF0IjoxNzAwODE4MjM2LCJqdGkiOiIyODY5RUIyRENBMjE2NTY5MzBGOTdCMjgzRkU2OEU0OCJ9.QQmpcOkxsEbg0CqaHuGt86DMpckBFQlqsMWkfFv_ZQQ\"}";
        ClassPathResource classPathResource = new ClassPathResource("/poc/bpmn");
        File[] files = classPathResource.getFile().listFiles();
        for (File file : files) {
            String s = FileUtil.readUtf8String(file);
            JSONObject jo = new JSONObject();
            jo.set("bpmnXml", s);
            String str = HttpUtil.createPost("http://localhost:7003/flow/model/save")
                    .header("Token", token)
                    .body(jo.toString()).execute()
                    .body();

            Integer code = JSONUtil.parseObj(str).getInt("code");
            if (200 != code) {
                System.out.println(str);
                break;
            }
            String modelId = JSONUtil.parseObj(str).getStr("data");
            jo = new JSONObject();
            jo.put("modelId", modelId);
            str = HttpUtil.createPost("http://localhost:7003/flow/model/deploy")
                    .header("Token", token)
                    .body(jo.toString()).execute()
                    .body();
            code = JSONUtil.parseObj(str).getInt("code");
            if (200 != code) {
                System.out.println(str);
                break;
            }
        }

    }
}
