import bankFlowCapitalApi from '@/api/budget/flowCenter/bankFlowCapitalApi'
import { Summary } from '@/components/Table'
import { FormAmount } from '@/components/Form'
import { AmountColumn, MatchOptionColumn } from '@/components/Format'
import { amountFormat, formatPercent, hasValue, saveServer } from '@/utils'
import { validateModal } from '@/utils/modal'
import { observer } from '@zswl/admin'
import { Form, Modal, ModalStore, Table, TableStore } from '@zswl/components'
import { Alert } from 'antd'
import { all, create } from 'mathjs'
import { useMemo, useState } from 'react'

const mathjs = create(all)

export { validateModal }
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
  AmountColumn({ title: '应付金额', dataIndex: 'planRepayAmount', editable: false, width: 220 }),
  AmountColumn({
    title: '已付金额',
    dataIndex: 'remainingAmount',
    editable: false,
    width: 220,
    render: (val, { planRepayAmount, remainingAmount }) => <FormAmount.Format value={planRepayAmount - (remainingAmount ?? 0)} initFormat={10000} />,
  }),
  AmountColumn({
    title: '本次核销金额',
    dataIndex: 'writeOffAmount',
    editable: true,
    width: 300,
  }),
]
export const AbsModal = ({ absModal, store, absTable }, ref) => {
  return (
    <Modal title="ABS/ABN产品核销明细" store={absModal} width={1000} destroyOnClose>
      <Form>
        <Table store={absTable} columns={absColumns} editable={true} scroll={{ x: 'auto' }} />
      </Form>
    </Modal>
  )
}
function CashFlowModal({ store }) {
  const [isEdit, setEdit] = useState(false)
  const absTable = useMemo(
    () =>
      new TableStore({
        pagination: false,
      }),
    []
  )
  const absModal = useMemo(
    () =>
      new ModalStore({
        onOpen: async ({ submitList, isAuto }) => {
          const targetCashFlow = submitList.map(({ receiptRepayBaseId, cashFlowCode, cashFlowItem }) => {
            return {
              receiptRepayBaseId,
              cashFlowCode,
              cashFlowItem,
            }
          })
          const res = await bankFlowCapitalApi.postRepaySplitList({ targetCashFlow })
          setTimeout(() => {
            absTable.setList(res)
          }, 50)
          return { submitList, isAuto }
        },
        onFinish: async (values) => {
          const { submitList, isAuto } = absModal.getInitialValues()
          const { list } = await absTable.submit()
          const repaySplitInfoList = list.map(({ cashFlowCode, splitId, splitCashFlowCode, cashFlowItem, writeOffAmount }) => {
            return {
              cashFlowCode,
              repaySplitId: splitId,
              repaySplitCashFlowCode: splitCashFlowCode,
              cashFlowItem,
              writeOffAmount: writeOffAmount ? writeOffAmount * 10000 : 0,
            }
          })
          await store.submitWriteOff(submitList, isAuto, repaySplitInfoList)
          absModal.close()
        },
      }),
    [store]
  )
  const fastWriteOff = async () => {
    Modal.confirm({
      title: `一键核销`,
      content: `一键核销由系统根据剩余本金权重自动分配利息核销金额，您若已手工输入数值，将被覆盖，请确认是否继续。`,
      onOk: async () => {
        const { list } = await store.specialPayFlowListTable.submit()

        const hasIBN = list.some((item) => ['ABS', 'ABN'].includes(item.businessType))
        // const hasIBN = true
        if (hasIBN) {
          absModal.open({ submitList: list, isAuto: 1 })
          return
        }
        await store.submitWriteOff(list, 1)
      },
    })
  }
  const disabled = !store.specialPayFlowListTable.getList()?.length
  const thisWriteOffAmount = store.bankFlowTable.getList()?.reduce((pre, cur) => {
    return pre + (cur.paymentAmount || 0)
  }, 0)
  const { sumData } = store
  // 这个弹窗在付款且是资金端的时候新增现金流会出现
  const validContract = async (totalAmount = 'thisWriteOffAmount') => {
    const { list } = await store.specialPayFlowListTable.submit()
    const submitList = list.map((item) => {
      return {
        ...item,
        thisWriteOffAmount: hasValue(item.thisWriteOffAmount) ? mathjs.multiply(mathjs.bignumber(item.thisWriteOffAmount), 10000).toString() : undefined,
      }
    })
    const principalData = submitList.filter((v) => v.cashFlowItem === 'PRINCIPAL')
    if (!principalData.length) {
      return Promise.resolve()
    }
    const remainingList = store.remainingDetailList
      .map((item) => {
        const principalList = principalData.filter((v) => v.financingId === item.financingId && v.financingType === item.financingType)

        const principalAmount = principalList.reduce((pre, cur) => {
          return pre + (+cur[totalAmount] || 0)
        }, 0)
        return {
          ...item,
          thisWriteOffAmount: principalAmount,
          financingCode: principalList[0]?.financingCode,
        }
      })
      .filter((item) => item.thisWriteOffAmount === item.remainingAmount)
    const remainingText = remainingList.map((item) => item.financingCode).join('、')
    return await validateModal(
      {
        content: `本次核销后，“融资合同编号”（${remainingText}）将结清，请确认本息及费用均已核销完成`,
      },
      !!remainingList.length
    )
  }
  const submit = async () => {
    const { list } = await store.specialPayFlowListTable.submit()

    const submitList = list.map((item) => {
      return {
        ...item,
        thisWriteOffAmount: hasValue(item.thisWriteOffAmount) ? mathjs.multiply(mathjs.bignumber(item.thisWriteOffAmount), 10000).toString() : undefined,
      }
    })
    const unequalList = submitList.filter((item) => item.cashFlowItem === 'PRINCIPAL' && item?.thisWriteOffAmount != item?.noPayAmount)
    const unequalText = unequalList.map((item, index) => {
      return (
        <div>
          {index + 1}. {item.orgName} 期项为 {item.phase} 的本金 未付金额为
          {amountFormat(formatPercent(item?.noPayAmount))}（元）与核销金额
          {amountFormat(formatPercent(item?.thisWriteOffAmount))}（元）不一致
        </div>
      )
    })
    await validateModal(
      {
        title: `部分金额未核销`,
        content: (
          <div>
            {unequalText} <div>请确认是否核销，并同步调整还款计划！</div>
          </div>
        ),
      },
      !!unequalList.length
    )
    await validContract()
    const hasIBN = submitList.some((item) => ['ABS', 'ABN'].includes(item.businessType))
    // const hasIBN = true
    if (hasIBN) {
      absModal.open({ submitList })
      return
    }
    store.submitWriteOff(submitList)
    setEdit(true)
  }

  return (
    <>
      <Modal title={`应付流水清单`} store={store.specialPayFlowListModal} okText={'确定'} width={1200} destroyOnClose footer={null}>
        <Alert style={{ marginBottom: 20 }} message="【自动核销只会核销本息，非本息的费用项会被忽略】" closable type="warning"></Alert>
        <Table
          columnsFilter={'flowCenter_BankFlow_SpecialPayFlowListModal'}
          onFilter={(key, val) => saveServer('flowCenter_BankFlow_SpecialPayFlowListModal', val)}
          columnWidth={180}
          rowKey={'idKey'}
          actions={[
            {
              name: '一键核销',
              type: 'primary',
              onClick: async () => {
                await validContract('noPayAmount')
                await fastWriteOff()
              },
              disabled: disabled,
            },
            <Form initialValues={{ thisWriteOffAmount }}>
              <FormAmount.Item name={'thisWriteOffAmount'} label="流水金额合计（元）" style={{ marginBottom: 0 }} disabled></FormAmount.Item>
            </Form>,
          ]}
          extra={[
            !isEdit && {
              name: '编辑',
              disabled: disabled,
              type: 'primary',
              onClick: () => {
                setEdit(true)
              },
            },
            isEdit && {
              name: '取消',
              disabled: disabled,
              onClick: () => {
                setEdit(false)
              },
            },
            isEdit && {
              name: '确定',
              type: 'primary',
              disabled: disabled,
              onClick: submit,
            },
          ]}
          editable={isEdit}
          scroll={{ x: 1800, y: 400 }}
          store={store.specialPayFlowListTable}
          columns={[
            {
              title: '期项',
              dataIndex: 'phase',
              editable: false,
            },
            {
              title: '融资编号',
              dataIndex: 'cashFlowCode',
              editable: false,
              width: 200,
            },

            MatchOptionColumn({
              title: '现金流类型',
              dataIndex: 'cashFlowItem',
              matchOption: 'financePaymentWriteOffOrderEnum',
              editable: false,
            }),
            {
              title: '应付日期',
              dataIndex: 'shouldPayTime',
              editable: false,
            },
            AmountColumn({
              title: '应付金额(元)',
              dataIndex: 'shouldPayAmount',
              editable: false,
            }),
            AmountColumn({
              title: '未付金额(元)',
              dataIndex: 'noPayAmount',
              editable: false,
            }),
            AmountColumn({
              title: '本次核销金额(元)',
              dataIndex: 'thisWriteOffAmount',
              editable: true,
            }),
          ]}
          summary={() => {
            return <Summary columns={store.specialPayFlowListTable.getOptimizedColumns()} sumData={sumData}></Summary>
          }}
        ></Table>
      </Modal>
      <AbsModal absModal={absModal} absTable={absTable} store={store} />
    </>
  )
}

export default observer(CashFlowModal)
