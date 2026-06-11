package cn.zswltech.mithras.foundation.util;

import org.springframework.core.NamedThreadLocal;

/**
 * 审批流程生效测试
 *
 * @author wangchuanhao
 * @date 2022/8/9 12:36 PM
 */
public class ApprovalTestUtil {

    private static final ThreadLocal<Boolean> approvalFlagHolder = new NamedThreadLocal("approvalFlagHolder");

    public ApprovalTestUtil() {
    }

    public static Boolean getApprovalFlag() {
        return (Boolean)approvalFlagHolder.get();
    }

    public static void reset() {
        approvalFlagHolder.remove();
    }

    public static void setApprovalFlag(Boolean approvalFlag) {
        approvalFlagHolder.set(approvalFlag);
    }


}
