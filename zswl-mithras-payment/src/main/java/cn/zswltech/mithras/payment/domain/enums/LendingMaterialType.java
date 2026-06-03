package cn.zswltech.mithras.payment.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/17 09:44
 */
@Getter
@AllArgsConstructor
public enum LendingMaterialType implements PullDown, IMaterialsTypeConvert {

    CONTRACT_AND_LAW_RELATED("已签署的合同文本及相关法律文件", 10),
    SIGN_PHOTO_VIDEO("合同签署照片和视频", 20),
    LEASE_ITEM("租赁物清单", 30),
    LEASE_RELATED("租赁物相关", 40),
    PLEDGE_PROCEDURES("已办妥质押手续证明材料", 50),
    PUBLICITY_INFORMATION("全国企业信用信息公示系统、全国法院被执行人信息网及租赁物在中登网的查询情况", 60),
    RECEIPT_VOUCHER("我司已收到的相关起租款项到账凭证", 70),
    MEETING_MINUTES("最终有权决议机构会议纪要和记录复印件", 80),
    LOAN_APPROVAL("放款审核岗审核材料", 90),
    OTHER_ACCESSORIES("其他附件",100);

    public String display;
    public Integer sort;

    private static final Map<String, LendingMaterialType> map;
    static {
        map = Stream.of(LendingMaterialType.values()).collect(Collectors.toMap(LendingMaterialType::name, e -> e));
    }

    public static LendingMaterialType getByName(String name) {
        return map.get(name);
    }

    public static List<String> listAll() {
        return new ArrayList<>(map.keySet());
    }

    @Override
    public String businessModule() {
        return "PAYMENT";
    }

    @Override
    public String display() {
        return display;
    }
}
