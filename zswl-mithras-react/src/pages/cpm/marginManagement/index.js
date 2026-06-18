import { Table, Select, SearchBar, Page } from '@zswl/components'
import AmountRange from '@/components/AmountRange'
import IconFont from '@/components/Icon'
import { Tooltip } from 'antd'
import styles from './index.less'
import store from './store'
import { observer, history } from '@zswl/admin'
import { ClientSelect } from '@/components/Select'
import { amountFormat } from '@/utils'
import { saveServer } from '@/utils'
const { Item } = SearchBar

const PaymentApplication = () => {
  const { keys } = store.table.getSelected()

  return (
    <Page>
      <div className={styles.marginManagement}>
        <Table
                columnsFilter={'cpm_marginManagement_1'}
                onFilter={(key,val) => saveServer('cpm_marginManagement_1',val)}
          bordered
          actions={[
            {
              name: '导出',
              onClick: store.exportList,
              type: 'primary',
            },
          ]}
          store={store.table}
          searchbar={{
            labelCol: { span: 6 },
            items: [
              <Item label="客户名称" name="clientId" key="clientId">
                <ClientSelect canJump={false} functionCode="clientlist-6"></ClientSelect>
              </Item>,
              { label: '收款日期', name: 'Date', type: 'rangePicker', allowClear: true },
              {
                label: '合同编号',
                name: 'contractCode',
              },
              <Item
                name="amount"
                label="保证金余额"
                key="amount"
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
            ],
          }}
          scroll={{
            x: 1200,
          }}
          columns={[
            {
              title: '编号',
              dataIndex: 'code',
              key: 'code',
              width: 160,
              fixed: 'left',
              resizable: true,
              render: (v, t) => {
                return (
                  <a onClick={() => history.push(`/cpm/marginManagement/detail/${t.id}`)}>
                    <Tooltip title={t.code}>{t.code}</Tooltip>
                  </a>
                )
              },
            },
            {
              title: '基本信息',
              fixed: 'left',
              resizable: true,
              children: [
                {
                  title: '合同编号',
                  dataIndex: 'contractCode',
                  key: 'contractCode',
                  width: 260,
                  render: (v) => (
                    <Tooltip title={v}>
                      <div className={styles.contractCode}>{v}</div>
                    </Tooltip>
                  ),
                },
                {
                  title: '客户名称',
                  dataIndex: 'clientName',
                  key: 'clientName',
                  width: 290,
                  render: (v) => (
                    <Tooltip title={v} placement="topLeft">
                      <div className={styles.clientName}>{v}</div>
                    </Tooltip>
                  ),
                },
              ],
            },
            {
              title: '保证金收款',
              children: [
                {
                  title: '收款日期',
                  dataIndex: 'collectionDate',
                  key: 'collectionDate',
                  width: 200,
                  render: (value) => value || '-',
                },
                {
                  title: '保证金余额(元)',
                  dataIndex: 'marginAmount',
                  key: 'marginAmount',
                  width: 200,
                  align: 'right',
                  render: (val) => amountFormat(val / 10000),
                },
              ],
            },
            {
              title: '保证金付款',
              children: [
                {
                  title: '已退款金额(元)',
                  dataIndex: 'backAmount',
                  key: 'backAmount',
                  width: 200,
                  align: 'right',
                  render: (val) => amountFormat(val / 10000),
                },
                {
                  title: '已抵扣金额(元)',
                  dataIndex: 'deductAmount',
                  key: 'deductAmount',
                  width: 200,
                  align: 'right',
                  render: (val) => amountFormat(val / 10000),
                },
                {
                  title: '可退金额(元)',
                  dataIndex: 'canBackAmount',
                  key: 'canBackAmount',
                  width: 200,
                  align: 'right',
                  render: (val) => amountFormat(val / 10000),
                },
              ],
            },
          ]}
        />
      </div>
    </Page>
  )
}
export default observer(PaymentApplication)
