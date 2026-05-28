import { AmountColumn, MatchOptionColumn } from '@/components/Format'

export const formatModalData = (data, isFund = false) => {
  if (isFund) {
    const {
      orgId,
      receiptRepayBaseId,
      cashFlowItem,
      modelId: paymentActualDetailId,
      ...rest
    } = data
    return {
      orgName: orgId.label,
      orgId: orgId.value,
      receiptRepayBaseId: receiptRepayBaseId.value,
      receiptRepayBaseCode: receiptRepayBaseId.label,
      cashFlowItemName: cashFlowItem.label,
      cashFlowItem: cashFlowItem.value,
      paymentActualDetailId,
      ...rest,
    }
  }
  const { client, contract, receipt, cashFlowItem, modelId: paymentActualDetailId, ...rest } = data

  return {
    clientId: client.value,
    clientName: client.label,
    contractId: contract.value,
    contractCode: contract.label,
    receiptId: receipt?.value,
    receiptCode: receipt?.label,
    cashFlowItemName: cashFlowItem.label,
    cashFlowItem: cashFlowItem.value,
    paymentActualDetailId,
    ...rest,
  }
}

export const formatListJson = (list, needFilter = true) => {
  const newList = []
  list.forEach((item) => {
    const { children = [], ...rest } = item
    newList.push(rest)
    children.forEach((element) => {
      newList.push(element)
    })
  })

  return needFilter ? newList.filter((item) => !item.isRemote) : newList
}
export const formatOffModal = (record, isFund) => {
  let newRecd
  if (isFund) {
    const {
      orgName,
      orgId,
      contractCode,
      receiptRepayBaseId,
      receiptRepayBaseCode,
      receiptId,
      cashFlowItemName,
      cashFlowItemId,
      ...rest
    } = record
    newRecd = {
      receiptRepayBaseId: { label: receiptRepayBaseCode, value: receiptRepayBaseId },
      cashFlowItem: { label: cashFlowItemName, value: cashFlowItemId },
      orgId: { label: orgName, value: orgId },
      editType: 'EDIT',
      ...rest,
    }
  } else {
    const {
      clientName,
      clientId,
      contractCode,
      contractId,
      receiptCode,
      receiptId,
      cashFlowItemName,
      cashFlowItemId,
      writeOffedAmount = [],
      ...rest
    } = record

    newRecd = {
      client: { label: clientName, value: clientId },
      contract: { label: contractCode, value: contractId },
      receipt: { label: receiptCode, value: receiptId },
      cashFlowItem: { label: cashFlowItemName, value: cashFlowItemId },
      editType: 'EDIT',
      ...rest,
    }
  }
  return newRecd
}

export const absColumns = [
  { title: '融资编号', dataIndex: 'financingCode', editable: false, width: 200 },
  { title: '证券代码', dataIndex: 'securitiesCode', editable: false, width: 200 },
  { title: '证券简称', dataIndex: 'abbreviation', editable: false, width: 200 },
  { title: '期数', dataIndex: 'phase', editable: false, width: 200 },
  MatchOptionColumn({
    title: '现金流类型',
    dataIndex: 'cashFlowItem',
    matchOption: 'financePaymentWriteOffOrderEnum',
    editable: false,
    width: 200,
  }),
  { title: '应付日期', dataIndex: 'planRepayDate', editable: false, width: 200 },
  AmountColumn({ title: '已付金额', dataIndex: 'writeOffAmount', editable: false, width: 220 }),
  AmountColumn({
    title: '本次核销金额',
    dataIndex: 'amount',
    editable: true,
    width: 300,
  }),
]
