import { Table, Select, App } from '@zswl/components'
import PaymentRecordsModal from './PaymentRecordsModal'
import { DownOutlined } from '@ant-design/icons'
import { history } from '@zswl/admin'
import styles from './index.less'
import store from './store'
import { useEffect } from 'react'
import { Button, Dropdown, Menu, Tooltip } from 'antd'
import { amountFormat } from '@/utils'
import { saveServer } from '@/utils'

const PaymentRecords = ({ id, callback }) => {
  useEffect(() => {
    return () => {
      App.resetStore(store)
    }
  }, [])
  const menu = (t) => {
    return (
      <Menu
        onClick={(e) => {
          store.updateRecord(e, t.id, 1)
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
        onClick={(e) => {
          store.updateRecord(e, t.id, 2, callback)
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
                columnsFilter={'marginManagement_PaymentRecords_1'}
                onFilter={(key,val) => saveServer('marginManagement_PaymentRecords_1',val)}
          // selectable
          store={store.table}
          // autoRequest={false}
          scroll={{
            x: 1200,
          }}
          extra={
            [
              // {
              //   name: '+ 新增',
              //   type: 'primary',
              //   onClick: store.collectionModal.open,
              // },
            ]
          }
          columns={[
            {
              title: '编号',
              dataIndex: 'sortId',
              key: 'sortId',
              render: (val, record) => {
                return (
                  <a
                    onClick={() => {
                      history.push(`/cpm/collectionWriteOff/detail/${record.id}`)
                    }}
                  >
                    {val}
                  </a>
                )
              },
            },
            {
              title: '信息来源',
              dataIndex: 'dataSource',
              key: 'dataSource',
            },
            {
              title: '收款类型',
              dataIndex: 'collectionType',
              key: 'collectionType',
            },
            {
              title: '应收日期',
              dataIndex: 'planCollectionDate',
              key: 'planCollectionDate',
              dateFormat: 'yyyy-MM-DD',
              render: (v) => {
                return v || '-'
              },
            },
            {
              title: '实收日期',
              dataIndex: 'collectionDate',
              key: 'collectionDate',
              dateFormat: 'yyyy-MM-DD',
              render: (v) => {
                return v || '-'
              },
            },
            {
              title: '应收金额(元)',
              dataIndex: 'planCollectionAmount',
              key: 'planCollectionAmount',
              align: 'right',
              render: (v) => (
                <Tooltip title={amountFormat(v / 10000)}>{amountFormat(v / 10000)}</Tooltip>
              ),
            },
            {
              title: '实收金额(元)',
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
            //           {App.matchOption('MarginWriteOffStatusEnum', v).label} <DownOutlined />
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
              actions() {
                return [{ name: '查看', onClick: store.collectionModal.open }]
              },
            },
          ]}
        />
      </div>
      <PaymentRecordsModal />
    </div>
  )
}
export default PaymentRecords
