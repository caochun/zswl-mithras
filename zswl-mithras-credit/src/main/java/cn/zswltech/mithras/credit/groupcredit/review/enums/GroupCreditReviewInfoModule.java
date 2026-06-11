package cn.zswltech.mithras.credit.groupcredit.review.enums;

public enum GroupCreditReviewInfoModule {

    BASE_INFO("集团授信评审基本信息"),
    MATERIALS_LIST("资料清单"),
    ;

    GroupCreditReviewInfoModule(String display) {
        this.display = display;
    }

    public final String display;

    public static GroupCreditReviewInfoModule of(String code) {
        for (GroupCreditReviewInfoModule value : GroupCreditReviewInfoModule.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
