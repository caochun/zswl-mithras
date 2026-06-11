package cn.zswltech.mithras.contract.enums.contract;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContractLeaseItemFileTypeEnum  implements PullDown {

    LEASE_ITEM_PROMISE_LETTER("租赁物承诺函"),
    INVOICE_SUPPLEMENT_PROMISE_LETTER("发票补足承诺函"),
    BOTH("租赁物承诺函,发票补足承诺函"),
    EMPTY("不涉及"),
    ;

    private final String display;

    @Override
    public String display() {
        return this.display;
    }

}
