import { Page, SearchBar, Table } from '@zswl/components'
import { getQuery, history, observer, toJS } from '@zswl/admin'
import store from './store'
import { useCallback, useEffect, useMemo } from 'react'
import styles from './index.less'
import IconFont from '@/components/Icon'
import { amountFormat } from '@/utils'
import AmountRange from '@/components/AmountRange'
import { ClientSelect } from '@/components'
import AddModal from './AddModal'
import { saveServer } from '@/utils'

const { Item } = SearchBar

const clientStatusObj = {
  NEW: '新建',
  PEND_TAKE_EFFECT: '变更中',
  TAKE_EFFECT: '生效',
}
const clientProcessStatus = {
  NO_APPROVAL_REQUIRED: '无需审批',
  UNDER_APPROVAL: '审批中',
  UNSUMMITTED: '未提交',
  APPROVAL_PASS: '审批通过',
  APPROVAL_REJECT: '审批拒绝',
}

function Index() {
  const { options, getKeyOptionsLabelMap } = store
  const { keys } = store.table.getSelected()

  const columns = useMemo(() => {
    return [
      {
        title: '编号',
        dataIndex: 'paymentCode',
        width: 150,
        fixed: 'left',
        actions({ paymentCode, id, paymentStatus, paymentProcessStatus }) {
          return [
            {
              name: <div className={styles.customerTitle}>{paymentCode}</div>,
              // disabled: paymentStatus === 'CLOSED',
              to: `/cpm/paymentApplication/detail/${id}?canEditFlag=${
                paymentStatus !== 'CLOSED' &&
                (paymentProcessStatus === 'UN_SUBMIT' || paymentProcessStatus === 'APPROVAL_REJECT')
              }`,
            },
          ]
        },
      },
      {
        title: '合同编号',
        width: 270,
        dataIndex: 'contractCode',
        tooltip: true,
      },
      {
        title: '客户名称',
        width: 300,
        dataIndex: 'clientName',
        resizable: true,
        tooltip: true,
      },
      {
        title: '申请付款日期',
        dataIndex: 'applyPaymentDate',
        tooltip: true,
        width: 160,
        render: (val) => val,
      },
      {
        title: <div style={{ textAlign: 'right' }}>申请付款金额(元)</div>,
        dataIndex: 'applyPaymentAmount',
        tooltip: true,
        width: 160,
        render: (val) => <div style={{ textAlign: 'right' }}>{amountFormat(val / 10000)}</div>,
      },
      {
        title: '审批状态',
        dataIndex: 'paymentProcessStatus',
        width: 140,
        render: (item, index) => {
          return item ? getKeyOptionsLabelMap('commonProcessStatus')[item] : '-'
        },
      },
      {
        title: '申请状态',
        dataIndex: 'paymentStatus',
        width: 140,
        render: (item, index) => {
          return item ? getKeyOptionsLabelMap('paymentStatusEnum')[item] : '-'
        },
      },
      {
        title: '创建人',
        width: 120,
        tooltip: true,
        dataIndex: 'applicant',
      },
      {
        title: '创建时间',
        width: 180,
        tooltip: true,
        dataIndex: 'createTime',
        render: (val) => val,
      },
    ]
  }, [])
  const openModal = getQuery('openModal')
  useEffect(() => {
    openModal === 'true' && store.createModal.open()
  }, [openModal])
  return (
    <Page>
      <div className={styles.wrap}>
        <Table
          columnsFilter={'cpm_paymentApplication_1'}
          onFilter={(key, val) => saveServer('cpm_paymentApplication_1', val)}
          resizable
          store={store.table}
          selectable={{
            getCheckboxProps: (record) => {
              return {
                disabled: record.paymentStatus === 'CLOSED',
              }
            },
          }}
          searchbar={{
            items: [
              <Item label="客户名称" name="clientId" key="clientId">
                <ClientSelect canJump={false} functionCode="clientlist-7"></ClientSelect>
              </Item>,
              {
                label: '合同编号',
                name: 'contractCode',
              },
              {
                label: '审批状态',
                name: 'paymentProcessStatus',
                options: options.projReviewProcessStatus,
              },
              <Item
                name="applyPaymentAmount"
                label="申请付款金额"
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
                label: '申请付款日期',
                name: 'applyPaymentDate',
                type: 'rangePicker',
              },
            ],
          }}
          actions={[
            {
              name: (
                <span>
                  <IconFont type="icon-icon_add" />
                  创建付款
                </span>
              ),
              onClick: () => store.createModal.open(),
              type: 'primary',
            },
            // { name: '交接', onClick: store.withdraw },
            {
              name: '关闭付款申请',
              onClick: () => {
                console.log(store.table.getSelected())
                if (store.table.getSelected().keys.length === 0) {
                  return
                }
                store.remove()
              },
              disabled: store.table.getSelected().keys.length === 0,
            },
          ]}
          scroll={{
            x: 1200,
          }}
          columns={columns}
        />
        <AddModal />
      </div>
    </Page>
  )
}

export default observer(Index)
