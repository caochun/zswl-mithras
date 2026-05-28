package cn.zswltech.mithras.dto.third.financial;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName ThirdBaseREQ
 * @Description
 * @Author jackerhe
 * @Date 2022/10/17 3:51 下午
 * @Version 1.0
 **/
@Data
public class ThirdBaseREQ {

    @ApiModelProperty("私钥+时间戳+随机数 的sha1加密结果")
    private String secret;
    @ApiModelProperty("随机数")
    private String random;
    @ApiModelProperty("时间戳")
    private Long timestamp;

}
