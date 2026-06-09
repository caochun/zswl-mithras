package cn.zswltech.mithras.system.storage;

import cn.zswl.oss.core.OssClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2025/12/19
 * @description 支持公网访问的包装方法
 */
@Slf4j
@Component
public class MithrasMinioClient {
    private static final String APP_SOURCE = "app";

    @Resource
    private OssClient ossClient;
    @Value("${oss.minio.endpoint}")
    private String endpoint;
    @Value("${oss.minio.publicEndpoint}")
    private String publicEndpoint;

    public String getPreviewUrl(String ossFilePath, int expire) throws Exception {
        return this.getPreviewUrl(ossFilePath, expire, Collections.emptyMap());
    }

    public String getPreviewUrl(String ossFilePath, int expire, Map<String, String> responseHeader) throws Exception {
        if (Objects.equals(APP_SOURCE, requestSource())) {
            String url = ossClient.getPreviewUrl(ossFilePath, expire, responseHeader);
            // 替换公网域名，用替换的方式是为了解决内网上传公网访问的问题（可以通过minio配置解决，但需要重启minio服务，应用服务做代理的方式降低中间件重启的风险）
            return url.replace(endpoint, publicEndpoint);
        } else {
            return ossClient.getPreviewUrl(ossFilePath, expire, responseHeader);
        }
    }

    private String requestSource() {
        try {
            RequestAttributes reqAttributes = RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = ((ServletRequestAttributes) reqAttributes).getRequest();
            return request.getHeader("source");
        } catch (Throwable e) {
            log.error("获取请求终端来源异常", e);
        }
        return null;
    }
}
