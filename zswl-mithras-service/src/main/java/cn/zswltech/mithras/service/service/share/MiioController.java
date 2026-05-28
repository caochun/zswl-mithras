package cn.zswltech.mithras.service.service.share;

import cn.zswltech.mithras.service.config.redis.RedisHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author: ldhu
 * @Data: 2026/3/9
 * @desc:
 */
@Controller
@RequestMapping("/miio")
public class MiioController {
    @Resource
    private DataShareFkService dataShareFkService;
    @Resource
    private RedisHelper redisHelper;

    @RequestMapping("/download")
    public void download(@RequestParam("filename") String filename, HttpServletResponse response) throws IOException {
        // 判断文件名在redis中是否存在  存在则可以下载，不存在则提示文件不存在或者超时
        if (redisHelper.exists(filename)) {
            dataShareFkService.download(filename, response);
        } else {
            responseErrorMsg(response, "文件不存在或已过期：" + filename);
        }
    }

    /**
     * 统一封装：向前端返回JSON格式错误提示
     */
    private void responseErrorMsg(HttpServletResponse response, String msg) throws IOException {
        // 设置响应头
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        // 禁止缓存
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 输出JSON格式提示信息
        try (PrintWriter writer = response.getWriter()) {
            writer.write("{\"code\":500,\"msg\":\"" + msg + "\"}");
            writer.flush();
        }
    }
}