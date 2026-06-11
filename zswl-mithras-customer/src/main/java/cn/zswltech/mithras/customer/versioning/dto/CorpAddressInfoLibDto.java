package cn.zswltech.mithras.customer.versioning.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/24 20:35
 */
@Data
@Accessors(chain = true)
public class CorpAddressInfoLibDto {

    private List<String> inProvince;

    private List<String> notInProvince;
}
