package cn.zswltech.mithras.projectprocess.application.job;

public interface ProjectStatusSupportPort {

    void expireProjEstablish(int days);

    void expireProjReview(int days);

    void expireGroupCreditEstablish(int days);

    void expireGroupCreditReview(int days);
}
