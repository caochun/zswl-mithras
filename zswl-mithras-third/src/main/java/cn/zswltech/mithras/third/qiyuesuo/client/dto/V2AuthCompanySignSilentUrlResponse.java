package cn.zswltech.mithras.third.qiyuesuo.client.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.qiyuesuo.v3sdk.model.v2auth.response.V2AuthCompanysignsilentUrlResponse;

/**
 * @author bigbear
 * @date 2024/12/10 16:05
 * @description
 */
@Data
@NoArgsConstructor
public class V2AuthCompanySignSilentUrlResponse {

    /**
     * result : {"url":"http://10.100.3.173:9180/auth-sign-silent?viewToken=c33834f1-7e5e-4948-adf1-6f490112a865&themeColor=%232489F2&lang=zh_CN"}
     * code : 0
     * message : SUCCESS
     */

    private V2AuthCompanysignsilentUrlResponse result;
    private int code;
    private String message;
}
