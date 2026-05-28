package cn.zswltech.mithras.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 版本查询 基类
 *
 * @author wangchuanhao
 * @date 2022/12/14 7:29 PM
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VersionBaseREQ extends AuthBaseReq {

    @ApiModelProperty("版本号")
    @JsonAlias("businessVersion")
    private String version;

    public String getBusinessVersion() {
        return this.version;
    }

    public void setBusinessVersion(String version) {
        this.version = version;
    }

}
