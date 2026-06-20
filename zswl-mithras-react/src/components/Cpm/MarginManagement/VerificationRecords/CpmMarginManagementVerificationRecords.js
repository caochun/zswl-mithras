import { Table, Select } from '@zswl/components'
import store from './store'
import { amountFormat } from '@/utils'
import { saveServer } from '@/utils'
const PaymentRecords = () => {
  return (
    <div>
      <div>
        <Table
                columnsFilter={'marginManagement_VerificationRecords_1'}
                onFilter={(key,val) => saveServer('marginManagement_VerificationRecords_1',val)}
          // selectable
          store={store.table}
          scroll={{
            x: 1200,
          }}
          columns={[
            {
              title: '操作时间',
              dataIndex: 'createTime',
              key: 'createTime',
              dateFormat: 'yyyy-MM-DD HH:mm:ss',
            },
            {
              title: '操作人',
              dataIndex: 'createBy',
              key: 'createBy',
            },
            {
              title: '被操作明细',
              dataIndex: 'operateDetails',
              key: 'operateDetails',
            },
            {
              title: '操作',
              dataIndex: 'operate',
              key: 'operate',
            },
            {
              title: '保证金余额(元)',
              align: 'right',
              dataIndex: 'marginAmount',
              key: 'marginAmount',
              render: (v) => {
                return amountFormat(v / 10000)
              },
            },
          ]}
        />
      </div>
    </div>
  )
}
export default PaymentRecords
