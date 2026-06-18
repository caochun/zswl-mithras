import { useState } from 'react'
import { message } from 'antd'
import { Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import { amountFormat, downFile, formatPercent } from '@/utils'
import { SubscribeOrgSelect, SubscribeBondSelect } from '../../Select'
import Api from '@/api/financial/directFinancingDetail'
import { AmountEditable } from '@/components/Format'
import { saveServer } from '@/utils'

const { Summary } = Table
function Index({ id: financingId, disabled }) {
  const [editableKey, setEditableKey] = useState(null)
  const tableStore = Table.useStore({
    pagination: false,
    request: async (params) => {
      const data = await Api.getSubscribeList({ ...params, financingId })
      return data?.page
    },
  })
  const remove = async (record) => {
    if (tableStore.isNewRow(record)) {
      tableStore.deleteRow(record)
    } else {
      await Api.delSubscribe({ id: record.id })
      message.success('删除成功')
      tableStore.search()
    }
  }
  const save = async (record) => {
    const { id } = record
    const { values } = await tableStore.submit()
    const data = values[id]
    const { orgnizationName, ...rest } = data
    const orgInfo = {
      orgnizationId: orgnizationName?.value,
      orgnizationName: orgnizationName?.label,
    }
    if (id && !tableStore.isNewRow(record)) {
      await Api.editSubscribe({ ...rest, ...orgInfo, id, financingId })
    } else {
      await Api.addSubscribe({ ...rest, ...orgInfo, financingId })
    }
    await tableStore.search()
    setEditableKey(null)
    message.success('保存成功')
  }
  const exportSubscribe = async () => {
    const res = await Api.exportSubscribe({
      financingId,
    })
    if (res?.code === 200) {
      downFile(res)
      message.success('导出成功')
    } else if (res?.msg) {
      message.error(res.msg)
    }
  }

  return (
    <div>
      <Table
              columnsFilter={'detail_Subscribe_1'}
              onFilter={(key,val) => saveServer('detail_Subscribe_1',val)}
        store={tableStore}
        editable={(record) => record.id === editableKey}
        actions={<h3>认购明细</h3>}
        extra={[
          { name: '导出', type: 'primary', onClick: exportSubscribe },
          {
            name: '新增',
            type: 'primary',
            disabled,
            onClick() {
              setEditableKey(tableStore.addRow())
            },
          },
        ]}
        columns={[
          {
            title: '认购机构',
            dataIndex: 'orgnizationName',
            editable({ orgnizationId, orgnizationName }) {
              return {
                initialValue: { label: orgnizationName, value: orgnizationId },
                element: <SubscribeOrgSelect style={{ width: '100%' }} />,
              }
            },
            width: 200,
          },
          {
            title: '认购证券',
            dataIndex: 'productId',
            editable({ productId }) {
              return {
                initialValue: productId,
                element: <SubscribeBondSelect financingId={financingId} />,
              }
            },
            render: (_, { productName }) => <span>{productName}</span>,
          },
          {
            title: '认购额度（万元)',
            dataIndex: 'subscriptionLimit',
            align: 'right',
            editable: (val) =>
              AmountEditable(val, 'subscriptionLimit', {
                disabled: false,
                transform: (value) => value,
              }),
            render: (val) => amountFormat(formatPercent(val)),
          },
          {
            title: '是否授信机构',
            dataIndex: 'isCreditOrgnization',
            render: (val) => <span>{val === undefined ? '' : val ? '是' : '否'}</span>,
            editable: false,
          },
          { title: '备注', dataIndex: 'remark' },
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
        summary={(data) => {
          const total = data.reduce((previousValue, currentValue) => {
            return previousValue + (currentValue.subscriptionLimit || 0)
          }, 0)
          return (
            <Summary.Row>
              <Summary.Cell>合计</Summary.Cell>
              <Summary.Cell>-</Summary.Cell>
              <Summary.Cell>
                <div style={{ textAlign: 'right' }}>{amountFormat(formatPercent(total))}</div>
              </Summary.Cell>
              {[1, 2, 3].map((_) => (
                <Summary.Cell>-</Summary.Cell>
              ))}
            </Summary.Row>
          )
        }}
      />
    </div>
  )
}

export default observer(Index)
