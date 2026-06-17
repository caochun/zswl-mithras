import { useState } from 'react'
import { Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import Api from '../api'
import { message } from 'antd'
import { amountFormat, formatPercent } from '@/utils'
import { AmountColumn, AmountEditable, DateColumn, MatchOptionColumn } from '@/components/Format'
import { saveServer } from '@/utils'

function Index({ id: financingId, tableStore, disabled }) {
  const [editableKey, setEditableKey] = useState(null)
  const remove = async (record) => {
    if (tableStore.isNewRow(record)) {
      tableStore.deleteRow(record)
    } else {
      await Api.delCost({ id: record.id })
      tableStore.search()
    }
    message.success('删除成功')
  }
  const save = async (record) => {
    const { id } = record
    const { values } = await tableStore.submit()
    const { payDate, ...rest } = values[id]
    if (id && !tableStore.isNewRow(record)) {
      await Api.editCost({
        ...rest,
        payDate: payDate && moment(payDate).format('yyyy-MM-DD'),
        id,
        amount: rest.amount * 10000,
        financingId,
      })
    } else {
      await Api.addCost({
        ...rest,
        payDate: payDate && moment(payDate).format('yyyy-MM-DD'),
        amount: +rest.amount * 10000,
        financingId,
      })
    }
    await tableStore.search()
    setEditableKey(null)
    message.success('保存成功')
  }
  return (
    <div style={{ marginTop: 10 }}>
      <Table
        columnsFilter={'Financing_Cost_1'}
        onFilter={(key,val) => saveServer('Financing_Cost_1',val)}
        store={tableStore}
        actions={<h3>费用明细</h3>}
        editable={(record) => record.id === editableKey}
        extra={[
          {
            name: '新增费用',
            type: 'primary',
            disabled,
            onClick() {
              setEditableKey(tableStore.addRow())
            },
          },
        ]}
        columns={[
          { title: '中介机构', dataIndex: 'intermediaries' },
          { title: '机构名称', dataIndex: 'institutionName' },
          { title: '费用类型', dataIndex: 'expenseType', matchOption: 'directFinancingFeeType' },
          AmountColumn({ title: '金额（元)', dataIndex: 'amount', editable: true }),
          { title: '支付方式', dataIndex: 'paymentMethod' },
          DateColumn({
            title: '支付时间',
            dataIndex: 'payDate',
            editable: true,
            width: 140,
            requiredMark: true,
          }),
          { title: '备注', dataIndex: 'remark' },
          MatchOptionColumn({
            title: '核销状态',
            dataIndex: 'writeOffStatus',
            matchOption: 'fundReceiptRepayCashFlowState',
            editable: false,
          }),
          {
            title: '操作',
            actions({ id }) {
              return [
                {
                  name: '编辑',
                  disabled,
                  hidden: id === editableKey,
                  onClick: () => setEditableKey(id),
                },
                { name: '保存', hidden: id !== editableKey, onClick: save },
                { name: '删除', onClick: remove, confirm: true, disabled },
              ]
            },
          },
        ]}
      />
    </div>
  )
}

export default observer(Index)
