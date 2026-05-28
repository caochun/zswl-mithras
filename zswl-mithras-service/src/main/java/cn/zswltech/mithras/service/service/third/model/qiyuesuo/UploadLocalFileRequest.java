package cn.zswltech.mithras.service.service.third.model.qiyuesuo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author bigbear
 * @date 2024/11/26 11:33
 * @description
 */
@Data
public class UploadLocalFileRequest {

    @ApiModelProperty(value = "签署文档")
    private MultipartFile file;

    /**
     * {@link FileTypeEnum#getValue()}
     */
    @ApiModelProperty(value = "签署文档类型")
    private FileTypeEnum fileType;

    @ApiModelProperty(value = "签署文档名称")
    private String title;

    /**
     * 签署文档类型 【传参方式】 pdf, doc, docx, wps, rtf, png, gif, jpg, jpeg, tiff, html, htm, xls, xlsx, txt
     */
    @Getter
    @AllArgsConstructor
    public enum FileTypeEnum{
        PDF("pdf"),
        DOC("doc"),
        DOCX("docx"),
        WPS("wps"),
        RTF("rtf"),
        PNG("png"),
        GIF("gif"),
        JPG("jpg"),
        JPEG("jpeg"),
        TIFF("tiff"),
        HTML("html"),
        HTM("htm"),
        XLS("xls"),
        XLSX("xlsx"),
        TXT("txt");

        private final String value;
    }
}
