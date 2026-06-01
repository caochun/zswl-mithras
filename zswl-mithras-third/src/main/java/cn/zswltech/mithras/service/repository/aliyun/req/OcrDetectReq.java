package cn.zswltech.mithras.service.repository.aliyun.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 阿里云ocr检测请求入参
 *
 * @author wangchuanhao
 * @date 2022/7/20 2:15 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OcrDetectReq {

    /**
     * 固定值 ocrService
     */
    private String method;

    /**
     * 图像二进制数据的base64编码，和url参数只能同时存在一个。
     */
    private String img;

    /**
     * 图片完整URL，URL长度不超过1024字节，和img参数只能同时存在一个。PS：如果您需要通过
     * url进行访问，需要您考虑SSRF攻击的防护
     */
    private String url;

    /**
     * 是否需要表格参数
     */
    private Boolean table;

    /**
     * 是否需要自动旋转角度
     */
    private Boolean rotate;

    /**
     * 是否需要置信度
     */
    private Boolean prob;

    /**
     * 是否需要单字输出
     */
    private Boolean charInfo;

    /**
     * 是否需要分页功能
     */
    private Boolean page;

    /**
     *  是否需要分段功能
     */
    private Boolean paragraph;

    /**
     * 是否需要分行功能
     */
    private Boolean row;

    /**
     * 是否需要去除边界(对于包含多页的图片，去除边界的页内容)
     */
    private Boolean removeBoundary;

    /**
     * 是否去印章
     */
    private Boolean noStamp;

    /**
     * 版面格式相关信息，目前包含标题提取
     */
    private Boolean layout;

    /**
     * :是否需要图案（指纹和印章）坐标输出
     */
    private Boolean figure;

    /**
     * 要识别的图片类型，括号内表示需要传入的参数值，包括文档识别（advanced）、纯英文识别
     * （eng）、电商图片识别（basic）。默认为文档识别（advanced）
     */
    private String type;

}
