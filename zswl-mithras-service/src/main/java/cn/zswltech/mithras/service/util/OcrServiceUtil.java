package cn.zswltech.mithras.service.util;

import cn.hutool.core.io.IoUtil;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.sleipnir.toolkit.OcrUtil;
import cn.zswltech.sleipnir.toolkit.SleipnirException;
import cn.zswltech.sleipnir.toolkit.request.OcrFileInfo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yupengfei
 * @date 2024/6/26 17:58
 */
@Slf4j
public class OcrServiceUtil {
    /**
     * 如果文件为pdf或word，将文件进行切割
     *
     * @param file 文件
     * @return 文件流集合
     */
    public static List<OcrFileInfo> getOcrFileInfoList(MultipartFile file) {
        //获取文件后缀
        List<OcrFileInfo> ocrFileInfos = new ArrayList<>();
        String fileExtension = getFileExtension(file);
        try {
            if ("pdf".equals(fileExtension)) {
                List<OcrFileInfo> ocrFileInfoList = OcrUtil.extractPDF(file.getInputStream(), null);
                transformOcrFileInfo(file.getOriginalFilename(), ocrFileInfoList);
                ocrFileInfos.addAll(ocrFileInfoList);
            } else if ("docx".equals(fileExtension) || "doc".equals(fileExtension)) {
                List<OcrFileInfo> ocrFileInfoList = OcrUtil.extractWordImage(file.getInputStream());
                transformOcrFileInfo(file.getOriginalFilename(), ocrFileInfoList);
                ocrFileInfos.addAll(ocrFileInfoList);
            } else {
                OcrFileInfo ocrFileInfo = new OcrFileInfo();
                try {
                    byte[] bytes = IOUtils.toByteArray(file.getInputStream());
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    IoUtil.write(os, true, bytes);
                    ocrFileInfo.setInputStream(new ByteArrayInputStream(os.toByteArray()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String fileName = file.getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {
                    int dotIndex = fileName.lastIndexOf(".");
                    String prefix = dotIndex == -1 ? fileName : fileName.substring(0, dotIndex);
                    ocrFileInfo.setOriginalFilename(prefix);
                }
                ocrFileInfos.add(ocrFileInfo);
            }
        } catch (IOException e) {
            log.error("读取文件异常:" + e.getMessage());
            throw new MithrasException("读取文件异常");
        } catch (SleipnirException e) {
            throw new MithrasException(e.getMessage());
        }
        return ocrFileInfos;
    }

    /**
     * 获取文件的类型
     *
     * @param file 文件
     * @return 文件后缀
     */
    private static String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && !originalFilename.isEmpty()) {
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex > 0 && dotIndex < originalFilename.length() - 1) {
                return originalFilename.substring(dotIndex + 1);
            }
        }
        return "";
    }

    /**
     * 改造发票信息
     *
     * @param fileName        原始文件名
     * @param ocrFileInfoList 发票信息集合
     */
    private static void transformOcrFileInfo(String fileName, List<OcrFileInfo> ocrFileInfoList) {
        if (ocrFileInfoList.size() == 1) {
            int dotIndex = fileName.lastIndexOf(".");
            String prefix = dotIndex == -1 ? fileName : fileName.substring(0, dotIndex);
            ocrFileInfoList.get(0).setOriginalFilename(prefix);
            return;
        }
        for (int i = 0; i < ocrFileInfoList.size(); i++) {
            if (StringUtils.isNotBlank(fileName)) {
                int dotIndex = fileName.lastIndexOf(".");
                String prefix = fileName.substring(0, dotIndex);
                ocrFileInfoList.get(i).setOriginalFilename(prefix + "-" + i);
            }
        }
    }
}
