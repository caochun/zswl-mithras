package cn.zswltech.mithras.credit.application.groupcredit.establish;

public interface GroupCreditEstablishProcessPort {

    void start(GroupCreditEstablishProcessStartCommand command);

    GroupCreditEstablishProcessInfo findRelatedProcess(Long groupCreditEstablishId);

    boolean isProcessPass(Integer endType);

    boolean isStartUserTask(GroupCreditEstablishProcessInfo processInfo);
}
