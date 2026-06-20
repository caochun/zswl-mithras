import { App, Page, SearchBar, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import store from './store'
import { useMemo } from 'react'
import { amountFormat } from '@/utils'
import AmountRange from '@/components/AmountRange'
import CollectionDayModal from './CollectionDayModal'
import { ClientSelect, OrgSelect } from '@/components/Select'
import { saveServer } from '@/utils'

const { Item } = SearchBar

function PaymentWriteOff() {
  const columns = useMemo(() => {
    return [
      {
        title: '编号',
        dataIndex: 'paymentCode',
        width: 150,
        fixed: 'left',
        actions({ paymentCode, paymentId, paymentProcessStatus }) {
          return [
            {
              name: paymentCode,
              disabled: paymentProcessStatus === 'CLOSED',
              to: `/cpm/paymentWriteOff/detail/${paymentId}`,
            },
          ]
        },
      },
      {
        title: '合同编号',
        width: 280,
        dataIndex: 'contractCode',
        tooltip: true,
      },
      {
        title: '客户名称',
        width: 250,
        dataIndex: 'clientName',
        tooltip: true,
      },
      {
        title: '业务部门',
        dataIndex: 'bizDeptName',
        width: 140,
      },
      {
        title: '核销状态',
        dataIndex: 'writeOffStatus',
        width: 140,
        render: (val) => App.matchOption('paymentWriteOffStatus', val).label,
      },
      {
        title: '超期天数',
        dataIndex: 'beyondDays',
        width: 140,
        render: (val) => {
          return <div style={{ color: val > 10 ? 'red' : undefined }}>{val ?? '-'}</div>
        },
      },
      {
        title: '应付日期',
        width: 180,
        tooltip: true,
        dataIndex: 'applyPaymentDate',
        render: (val) => val,
      },

      {
        title: <div style={{ textAlign: 'right' }}>应付金额(元)</div>,
        width: 150,
        tooltip: true,
        dataIndex: 'applyPaymentAmount',
        render: (val) => <div style={{ textAlign: 'right' }}>{amountFormat(val / 10000)}</div>,
      },
      {
        title: '实付日期',
        width: 180,
        tooltip: true,
        dataIndex: 'paidInDate',
        render: (val) => val || '-',
      },
      {
        title: <div style={{ textAlign: 'right' }}>已付金额(元)</div>,
        width: 150,
        tooltip: true,
        dataIndex: 'paidAmount',
        render: (val) => <div style={{ textAlign: 'right' }}>{amountFormat(val / 10000)}</div>,
      },
      {
        title: <div style={{ textAlign: 'right' }}>剩余未付金额(元)</div>,
        width: 180,
        tooltip: true,
        dataIndex: 'remainingAmount',
        render: (val) => <div style={{ textAlign: 'right' }}>{amountFormat(val / 10000)}</div>,
      },
    ]
  }, [])

  return (
    <Page>
      <div>
        <Table
                columnsFilter={'cpm_paymentWriteOff_1'}
                onFilter={(key,val) => saveServer('cpm_paymentWriteOff_1',val)}
          resizable
          store={store.table}
          rowKey="paymentId"
          selectable={{
            type: 'radio',
            getCheckboxProps: (record) => {
              return {
                // 如果合同是直租合同或者付款申请状态是“已投放”就置灰
                // 已确认
                // 如果是非直租合同且付款申请状态是生效就可以点击。
                disabled:
                  record.leaseType === 'zhi_zu' ||
                  record.paymentStatus === 'FINISHED' ||
                  !['WRITTEN_OFF', 'PART_WRITTEN_OFF'].includes(record.writeOffStatus),
                // (record.leaseType !== 'zhi_zu' && record.paymentStatus !== 'TAKE_EFFECT'),
              }
            },
          }}
          searchbar={{
            labelCol: { span: 6 },
            items: [
              <Item label="客户名称" name="clientId" key="clientId">
                <ClientSelect canJump={false} functionCode="clientlist-8"></ClientSelect>
              </Item>,
              {
                label: '核销状态',
                name: 'writeOffStatus',
                options: 'paymentWriteOffStatus',
              },
              {
                label: '应付日期',
                name: 'applyPaymentDate',
                type: 'rangePicker',
              },
              {
                label: '合同编号',
                name: 'contractCode',
              },
              <Item
                name="applyPaymentAmount"
                label="应付金额"
                key="applyPaymentAmount"
                rules={[
                  ({ getFieldValue }) => ({
                    validator(_, value) {
                      if (value) {
                        if (value[0] && value[0] < 0) {
                          return Promise.reject(new Error('最小值不能小于0！'))
                        }
                        if (value[1] && value[1] < 0) {
                          return Promise.reject(new Error('最大值不能小于0！'))
                        }
                        if (value[0] && value[1]) {
                          if (value[1] < value[0]) {
                            return Promise.reject(new Error('金额最大值不能小于最小值！'))
                          }
                        }
                      }

                      return Promise.resolve()
                    },
                  }),
                ]}
              >
                <AmountRange />
              </Item>,
              {
                label: '实付日期',
                name: 'paidInDate',
                type: 'rangePicker',
              },
              <Item label="业务部门" name="bizDeptId" key="bizDeptId">
                <OrgSelect functionCode="paymentWriteOffSelectorgs"></OrgSelect>
              </Item>,
            ],
          }}
          scroll={{
            x: 1200,
          }}
          columns={columns}
        />
      </div>
      <CollectionDayModal store={store}></CollectionDayModal>
    </Page>
  )
}

export default observer(PaymentWriteOff)
