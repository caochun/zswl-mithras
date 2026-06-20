import { Table, Select, App } from '@zswl/components'
import RefundRecordsModal from './RefundRecordsModal'
import DeductionModal from './DeductionModal'
import { useEffect, useState } from 'react'
import { DownOutlined } from '@ant-design/icons'
import { saveServer } from '@/utils'
import styles from './index.less'
import store from './store'
import { amountFormat } from '@/utils'
import { Button, Dropdown, Menu, Tooltip } from 'antd'

const PaymentRecords = ({ id, contractId, callback }) => {
  useEffect(() => {
    return () => {
      App.resetStore(store)
    }
  }, [])
  useEffect(() => {}, [])
  const menu = (t) => {
    return (
      <Menu
        onClick={(r) => {
          store.updateRecord(r, t.id, 1)
        }}
        items={[
          {
            key: 'TO_BE_WRITE_OFF',
            label: '待核销',
          },
          {
            key: 'WRITTEN_OFF',
            label: '已核销',
          },
          {
            key: 'IGNORE',
            label: '忽略',
          },
        ]}
      />
    )
  }
  const menus = (t) => {
    return (
      <Menu
        onClick={(m) => {
          store.updateRecord(m, t.id, 2, callback)
        }}
        items={[
          {
            key: 'TO_BE_REVIEW',
            label: '待复核',
          },
          {
            key: 'REVIEWED',
            label: '已复核',
          },
          {
            key: 'IGNORE',
            label: '忽略',
          },
        ]}
      />
    )
  }
  return (
    <div>
      <div className={styles.customerWrap}>
        <Table
                columnsFilter={'marginManagement_RefundRecords_1'}
                onFilter={(key,val) => saveServer('marginManagement_RefundRecords_1',val)}
          // selectable
          store={store.table}
          scroll={{
            x: 1200,
          }}
          extra={
            [
              // {
              //   name: '+ 新增退款',
              //   onClick: store.refundModal.open,
              // },
              // {
              //   name: '+ 新增抵扣',
              //   onClick: store.deductionModal.open,
              // },
            ]
          }
          columns={[
            {
              title: '编号',
              dataIndex: 'sortId',
              key: 'sortId',
            },
            {
              title: '信息来源',
              dataIndex: 'dataSource',
              key: 'dataSource',
            },
            {
              title: '付款方式',
              dataIndex: 'collectionType',
              key: 'collectionType',
            },
            {
              title: '实付日期',
              dataIndex: 'collectionDate',
              key: 'collectionDate',
              dateFormat: 'yyyy-MM-DD',
            },
            {
              title: '实付金额(元)',
              dataIndex: 'collectionAmount',
              key: 'collectionAmount',
              align: 'right',
              render: (v) => (
                <Tooltip title={amountFormat(v / 10000)}>{amountFormat(v / 10000)}</Tooltip>
              ),
            },
            {
              title: '合同编号',
              dataIndex: 'contractCode',
              key: 'contractCode',
              tooltip: true,
            },
            {
              title: '状态',
              dataIndex: 'writeOffStatus',
              key: 'writeOffStatus',
            },
            // {
            //   title: '核销',
            //   dataIndex: 'writeOff',
            //   key: 'writeOff',
            //   render: (v, t) => {
            //     return (
            //       <Dropdown overlay={menu(t)} placement="bottomLeft" arrow>
            //         <Button>
            //           {App.matchOption('MarginWriteOffStatusEnum', v).label}
            //           <DownOutlined />
            //         </Button>
            //       </Dropdown>
            //     )
            //   },
            // },
            // {
            //   title: '复核',
            //   dataIndex: 'review',
            //   key: 'review',
            //   render: (v, t) => {
            //     return (
            //       <Dropdown overlay={menus(t)} placement="bottomLeft" arrow>
            //         <Button>
            //           {App.matchOption('MarginWriteOffStatusEnum', v).label}
            //           <DownOutlined />
            //         </Button>
            //       </Dropdown>
            //     )
            //   },
            // },
            {
              title: '操作',
              fixed: 'right',
              width: 100,
              actions(value) {
                return [
                  {
                    name: '查看',
                    onClick: () => {
                      if (value.collectionType == '保证金退款') {
                        store.refundModal.open(value)
                      } else {
                        store.deductionModal.open(value)
                      }
                    },
                  },
                ]
              },
            },
          ]}
        />
      </div>
      <RefundRecordsModal />
      <DeductionModal contractId={contractId} />
    </div>
  )
}
export default PaymentRecords
