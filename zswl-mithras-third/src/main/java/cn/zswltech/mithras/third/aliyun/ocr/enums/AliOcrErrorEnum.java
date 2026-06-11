package cn.zswltech.mithras.third.aliyun.ocr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 阿里云识别 错误码
 *
 * @author wangchuanhao
 * @date 2022/7/20 3:04 PM
 */
@AllArgsConstructor
@Getter
public enum AliOcrErrorEnum {

    /**
     *
     */
    ERR_DEFAULT(-1, "未知错误"),
    ERR_400(400,"taskId参数错误"),
    ERR_401(401,"url参数错误"),
    ERR_402(402,"params参数错误"),
    ERR_403(403,"图像内容错误或图像格式、编码不支持或图像最短边小于15px、最长边大于4096px"),
    ERR_404(404,"URL长度不能超过1024个字节"),
    ERR_405(405,"图像内容大小不能超过4M"),
    ERR_406(406,"img和url参数不能同时为空"),
    ERR_407(407,"img和url参数不能同时存在"),
    ERR_408(408,"sourcePath参数为空或者对应的文件不存在，请检查"),
    ERR_409(409,"sourcePath对应的不是一个文件，请检查"),
    ERR_410(410,"url不可用或者无法获取图像，请检查"),
    ERR_411(411,"请求速度超过了服务的处理能力，请降低qps或者稍后再试"),
    ERR_412(412,"PDF文件不能超过10M"),
    ERR_413(413,"PDF文件页数不能超过100"),
    ERR_501(501,"无效的服务类型"),
    ERR_502(502,"无效或者未授权的识别能力，如需开通更多识别能力请联系读光购买"),
    ERR_503(503,"当前license额度已经用完"),
    ERR_504(504,"无法获取license信息，请确认加密锁是否安装正确或者联系读光团队技术同学"),
    ERR_505(505,"license信息错误，请确认加密锁是否安装正确或者联系读光团队技术同学"),
    ERR_508(508,"获取license信息持续失败，请确认加密锁安装正确后并重启应用或者联系读光团队"),
    ERR_509(509,"签名未授权，请通过正确的url访问"),
    ERR_513(513,"获取图像内容错误："),
    ERR_515(515,"ocr请求失败，respondCode："),
    ERR_516(516,"ocr服务返回结果为空"),
    ERR_517(517,"ocr服务错误"),
    ERR_519(519,"不允许在一台物理机上启动多个读光服务"),
    ERR_520(520,"混贴切图失败，请稍后重试"),
    ERR_523(523,"测试license时间到期"),
    ERR_525(525,"图像和服务类型不匹配")
    ;

    private Integer code;
    private String msg;
    private static Map<Integer, AliOcrErrorEnum> map;

    static {
        map = Stream.of(AliOcrErrorEnum.values()).collect(Collectors.toMap(AliOcrErrorEnum::getCode, e -> e));
    }

    public static AliOcrErrorEnum getByCode(Integer code) {
        return map.getOrDefault(code, ERR_DEFAULT);
    }

}
