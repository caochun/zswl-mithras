package cn.zswltech.mithras.application.orchestration.capital.write_off.model;

import cn.zswltech.mithras.dto.capital.BankCenterSubTableFinanceListRSP;
import cn.zswltech.mithras.dto.capital.BankCenterSubTableProjectListRSP;
import cn.zswltech.mithras.third.financialshare.model.FinanceFlowRecord;
import lombok.Data;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author yangxiong
 * @date 2024/5/31/15:41
 * @description 核销金额的时候需要自己new一个自己使用
 */
@Data
public class FinanceFlowWriteOffBO {

    //自动核销使用队列
    private Deque<FinanceFlowRecord> autoFlowRecordQueue = new LinkedList<>();
    //手动核销使用队列
    private Deque<FinanceFlowRecord> handFlowRecordQueue = new LinkedList<>();
    //自动核销使用的全局金额
    private AtomicLong autoWriteOffAmount = new AtomicLong();
    //手动核销使用的全局金额
    private AtomicLong handWriteOffAmount = new AtomicLong();
    //需要手动核销的现金流项目-项目端 这里需要使用双端队列，以便核销不完的钱可以再重队列尾部放回去
    private Map<String, List<BankCenterSubTableProjectListRSP>> projectMap = new HashMap<>();
    //需要手动核销的现金流项目-资金端 这里需要使用双端队列，以便核销不完的钱可以再重队列尾部放回去
    private Map<String, List<BankCenterSubTableFinanceListRSP>> financeMap = new HashMap<>();
    // 单次核销需要推送的银行流水集合
    private List<Long> financeFlowRecordIds = new ArrayList<>();
}
