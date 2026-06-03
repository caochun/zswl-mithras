package cn.zswltech.mithras.others.hand.extract.client;

import cn.zswltech.mithras.customer.domain.enums.MarriageType;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Objects;
import java.util.Optional;

/**
 * 汉得客户导入数据帮助类
 *
 * @author wangchuanhao
 * @date 2022/9/25 11:34 AM
 */
public class ClientImporterHelper {

    /**
     * 提取自然人手机号
     * @return
     */
    public static String extractNormalPhone(JSONObject clientDataObj) {
        String clientName = clientDataObj.getString("bp_name");
        return Optional.ofNullable(clientDataObj.getString("normalContractArray"))
                .map(JSONArray::parseArray)
                .map(arr -> arr.stream().map(d -> (JSONObject)d).filter(d -> clientName.equals(d.getString("contact_person"))).findFirst().map(d -> d.getString("cell_phone")).orElse(null))
                .orElse(null);
    }

    /**
     * 提取自然人邮箱
     * @param clientDataObj
     * @return
     */
    public static String extractNormalMail(JSONObject clientDataObj) {
        String clientName = clientDataObj.getString("bp_name");
        return Optional.ofNullable(clientDataObj.getString("normalContractArray"))
                .map(JSONArray::parseArray)
                .map(arr -> arr.stream().map(d -> (JSONObject)d).filter(d -> clientName.equals(d.getString("contact_person"))).findFirst().map(d -> d.getString("email")).orElse(null))
                .orElse(null);
    }

    /**
     * 提取自然人地址
     * @param clientDataObj
     * @return
     */
    public static String extractNormalAddress(JSONObject clientDataObj) {
        JSONObject addressObj = Optional.ofNullable(clientDataObj.getString("normalAddressArray"))
                .map(JSONArray::parseArray)
                .map(arr -> arr.stream().map(d -> (JSONObject)d).filter(d -> "HOUSE_ADDRESS".equals(d.getString("address_type"))).findFirst().orElse(null))
                .orElse(null);
        if (Objects.isNull(addressObj)) {
            return null;
        }
        return Optional.ofNullable(addressObj.getString("country_id_n")).orElse("")
                + Optional.ofNullable(addressObj.getString("province_id_n")).orElse("")
                + Optional.ofNullable(addressObj.getString("city_id_n")).orElse("")
                + Optional.ofNullable(addressObj.getString("district_id_n")).orElse("")
                + Optional.ofNullable(addressObj.getString("address")).orElse("");
    }

    /**
     * 自然人婚姻状况枚举
     * @param handEnum
     * @return
     */
    public static String extractMarriageType(String handEnum) {
        if (Objects.isNull(handEnum)) {
            return null;
        }
        switch (handEnum) {
            case "DIVORCED": return MarriageType.DIVORCED.name();
            case "MARRIED": return MarriageType.MARRIED.name();
            case "UNMARRIED": return MarriageType.UNMARRIED.name();
            case "WIDOWED": return MarriageType.WIDOWHOOD.name();
        }
        return null;
    }

}