import { message } from 'antd'
import { Table, Modal, Form } from '@zswl/components'
import { observer } from '@zswl/admin'
import { amountFormat, downFile, formatPercent } from '@/utils'
import Api from '@/api/financial/directFinancingDetail'
import moment from 'moment'
import { FormAmount } from '@/components/Form'
import { saveServer } from '@/utils'
import { AmountColumn } from '@/components/Format'

const { Summary } = Table
function Index({ id: financingId, disabled, store }) {
  const tableStore = Table.useStore({
    request: async (params) => {
      const data = await Api.getProductList({ ...params, financingId })
      return data?.page
    },
  })
  const modalStore = Modal.useStore({
    async onOpen(id) {
      if (id) {
        const data = await Api.getProductDetail({ id })
        const { expectedExpirationDate, valueDate, ...rest } = data
        const time = {
          expectedExpirationDate: moment(expectedExpirationDate, 'YYYY-MM-DD'),
          valueDate: moment(valueDate, 'YYYY-MM-DD'),
        }
        return { ...rest, ...time }
      }
      return {}
    },
    async onFinish(values, { id }) {
      const { expectedExpirationDate, valueDate, ...rest } = values
      const time = {
        valueDate: valueDate.format('YYYY-MM-DD'),
        expectedExpirationDate: expectedExpirationDate.format('YYYY-MM-DD'),
      }
      if (id) {
        await Api.editProduct({
          ...time,
          ...rest,
          id,
        })
      } else {
        await Api.addProduct({
          ...time,
          ...rest,
          financingId,
        })
      }

      modalStore.close()
      message.success(`${id ? '编辑' : '新增'}成功`)
      tableStore.search()
      store.financeTable.search()
    },
  })
  const remove = async ({ id }) => {
    await Api.delProduct({ id })
    message.success('删除成功')
    tableStore.search()
  }
  const exportRepay = async () => {
    const res = await Api.exportProduct({
      financingId,
    })
    if (res?.code === 200) {
      downFile(res)
      message.success('导出成功')
    } else if (res?.msg) {
      message.error(res.msg)
    }
  }
  const columns = [
    {
      title: '证券代码',
      dataIndex: 'securitiesCode',
      rules: [{ required: true }],
    },
    {
      title: '证券简称',
      dataIndex: 'abbreviation',
      rules: [{ required: true }],
      render: (val, { id }) => <a onClick={() => modalStore.open(id)}>{val}</a>,
    },
    {
      title: '发行金额(万元)',
      dataIndex: 'issuanceAmount',
      align: 'right',
      rules: [{ required: true }],
      render: (val) => amountFormat(formatPercent(val)),
      element: <FormAmount />,
    },
    {
      title: '分层占比（%)',
      dataIndex: 'layeredProportion',
      rules: [{ required: true }],
      element: <FormAmount />,
      render: (val) => amountFormat(formatPercent(val)),
    },
    { title: '还本方式', dataIndex: 'repaymentMethod', rules: [{ required: true }] },
    {
      title: '发行利率（%)',
      dataIndex: 'issuanceRate',
      rules: [{ required: true }],
      element: <FormAmount />,
      render: (val) => amountFormat(formatPercent(val)),
    },
    {
      title: '年付息次数',
      dataIndex: 'annualPayCount',
      rules: [{ required: true }],
      element: <FormAmount />,
      render: (val) => amountFormat(formatPercent(val)),
    },
    {
      title: '起息日',
      dataIndex: 'valueDate',
      rules: [{ required: true }],
      element: 'datePicker',
    },
    {
      title: '预计到期日',
      dataIndex: 'expectedExpirationDate',
      rules: [{ required: true }],
      element: 'datePicker',
    },
    AmountColumn({
      title: 'FTP 收益率(%)',
      dataIndex: 'ftpYieldRate',
      // suffix: '%',
      wrapItemProps: {
        inputConfig: {
          disabled: true,
        },
      },
    }),
    {
      title: '剩余本金余额(万元)',
      dataIndex: 'remainingPrincipal',
      width: 180,
      align: 'right',
      rules: [{ required: true }],
      render: (val) => amountFormat(formatPercent(val)),
      element: <FormAmount />,
    },
    { title: '评级', dataIndex: 'rating', rules: [{ required: true }] },
    { title: '备注', dataIndex: 'remark' },
  ]

  return (
    <div>
      <Table
        columnsFilter={'detail_Product_1'}
        onFilter={(key, val) => saveServer('detail_Product_1', val)}
        scroll={{
          x: 2000,
        }}
        store={tableStore}
        actions={<h3>产品明细</h3>}
        extra={[
          { name: '导出', type: 'primary', onClick: exportRepay },
          { name: '新增', disabled, type: 'primary', onClick: modalStore.open },
        ]}
        columns={[
          ...columns,
          {
            title: '操作',
            actions() {
              return [{ name: '删除', disabled, onClick: remove, confirm: true }]
            },
          },
        ]}
        summary={(data) => {
          const total = (dataIndex) =>
            data.reduce((previousValue, currentValue) => {
              return previousValue + (currentValue[dataIndex] || 0)
            }, 0)
          return (
            <Summary.Row>
              <Summary.Cell>合计</Summary.Cell>
              <Summary.Cell>-</Summary.Cell>
              <Summary.Cell>
                <div style={{ textAlign: 'right' }}>
                  {amountFormat(formatPercent(total('issuanceAmount')))}
                </div>
              </Summary.Cell>
              <Summary.Cell>{amountFormat(formatPercent(total('layeredProportion')))}</Summary.Cell>
              {[1, 2, 3, 4, 5, 6].map((_) => (
                <Summary.Cell>-</Summary.Cell>
              ))}
              <Summary.Cell>
                <div style={{ textAlign: 'right' }}>
                  {amountFormat(formatPercent(total('remainingPrincipal')))}
                </div>
              </Summary.Cell>
              {[1, 2, 3].map((_) => (
                <Summary.Cell>-</Summary.Cell>
              ))}
            </Summary.Row>
          )
        }}
      />
      <Modal title="新增产品" store={modalStore} width={800} destroyOnClose>
        <Form labelCol={{ span: 6 }} items={columns} />
      </Modal>
    </div>
  )
}

export default observer(Index)
