package cn.zswltech.mithras.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 文件类型枚举
 *
 * @author wangchuanhao
 * @date 2022/7/8 12:40 PM
 */
@AllArgsConstructor
@Getter
public enum FileTypeEnum {

    /**
     * 未知
     */
    UNKNOWN(""),

    /**
     * word
     */
    DOC("doc"),
    DOCM("docm"),
    DOCX("docx"),
    DOCXF("docxf"),
    DOT("dot"),
    DOTM("dotm"),
    DOTX("dotx"),
    EPUB("epub"),
    FODT("fodt"),
    FB2("fb2"),
    HTM("htm"),
    HTML("html"),
    MHT("mht"),
    ODT("odt"),
    OFORM("oform"),
    OTT("ott"),
    OXPS("oxps"),
    PDF("pdf"),
    RTF("rtf"),
    TXT("txt"),
    DJVU("djvu"),
    XML("xml"),
    XPS("xps"),

    /**
     * excel
     */
    CSV("csv"),
    FODS("fods"),
    ODS("ods"),
    OTS("ots"),
    XLS("xls"),
    XLSB("xlsb"),
    XLSM("xlsm"),
    XLSX("xlsx"),
    XLT("xlt"),
    XLTM("xltm"),
    XLTX("xltx"),

    /**
     * ppt
     */
    FODP("fodp"),
    ODP("odp"),
    OTP("otp"),
    POT("pot"),
    POTM("potm"),
    POTX("potx"),
    PPS("pps"),
    PPSM("ppsm"),
    PPSX("ppsx"),
    PPT("ppt"),
    PPTM("pptm"),
    PPTX("pptx"),
    ;

    private String exName;

    private static Map<String, FileTypeEnum> map;

    static {
        map = Stream.of(FileTypeEnum.values()).collect(Collectors.toMap(FileTypeEnum::getExName, e -> e));
    }

    public static FileTypeEnum getByExName(String exName) {
        return map.getOrDefault(exName, UNKNOWN);
    }

    public static String getDocumentType(String exName) {
        FileTypeEnum fileTypeEnum = getByExName(exName);
        switch (fileTypeEnum) {
            case DOC:
            case DOCM:
            case DOCX:
            case DOCXF:
            case DOT:
            case DOTM:
            case DOTX:
            case EPUB:
            case FODT:
            case FB2:
            case HTM:
            case HTML:
            case MHT:
            case ODT:
            case OFORM:
            case OTT:
            case OXPS:
            case PDF:
            case RTF:
            case TXT:
            case DJVU:
            case XML:
            case XPS:
                return "word";
            case CSV:
            case FODS:
            case ODS:
            case OTS:
            case XLS:
            case XLSB:
            case XLSM:
            case XLSX:
            case XLT:
            case XLTM:
            case XLTX:
                return "cell";
            case FODP:
            case ODP:
            case OTP:
            case POT:
            case POTM:
            case POTX:
            case PPS:
            case PPSM:
            case PPSX:
            case PPT:
            case PPTM:
            case PPTX:
                return "slide";
            default:
                return "word";
        }
    }

}
