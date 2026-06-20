import { App, Page, SearchBar, Table } from '@zswl/components'
import { history, observer } from '@zswl/admin'
import store from './store'
import { useMemo } from 'react'
import { Tooltip } from 'antd'
import styles from './index.less'
import { amountFormat, saveServer } from '@/utils'
import AmountRange from '@/components/AmountRange'
import moment from 'moment'
import { ClientSelect } from '@/components/Select'

const { Item } = SearchBar

function CollectionWriteOff() {
  const columns = useMemo(() => {
    return [
      {
        title: '编号',
        dataIndex: 'code',
        width: 180,
        fixed: 'left',
        render: (v, { code, id }) => {
          return (
            <a onClick={() => history.push(`/cpm/collectionWriteOff/detail/${id}`)}>
              <Tooltip title={code}>
                <div className={styles.tooltipTitle}>{code}</div>
              </Tooltip>
            </a>
          )
        },
      },
      {
        title: '合同编号',
        width: 280,
        dataIndex: 'contractCode',
        actions({ contractCode, contractId }) {
          return [
            {
              name: contractCode,
              to: `/contract/list/detail/${contractId}`,
            },
          ]
        },
      },
      {
        title: '客户名称',
        width: 300,
        dataIndex: 'clientName',
        render: (val) => (
          <Tooltip title={val}>
            <div className={styles.tooltipTitle}>{val}</div>
          </Tooltip>
        ),
      },

      {
        title: '状态',
        dataIndex: 'writeOffStatus',
        tooltip: true,
        width: 140,
      },
      {
        title: '期项',
        dataIndex: 'phase',
        tooltip: true,
        width: 130,
      },

      {
        title: '现金流项目',
        dataIndex: 'cashFlowItem',
        width: 180,
        render: (val) => (
          <Tooltip title={val}>
            <div className={styles.tooltipTitle}>{val}</div>
          </Tooltip>
        ),
      },
      {
        title: '计划收款日期',
        width: 140,
        tooltip: true,
        dateFormat: 'yyyy-MM-DD',
        dataIndex: 'planCollectionDate',
      },
      {
        title: <div style={{ textAlign: 'right' }}>计划收款金额(元)</div>,
        width: 180,
        tooltip: true,
        dataIndex: 'planCollectionAmount',
        render: (val) => <div style={{ textAlign: 'right' }}>{amountFormat(val / 10000)}</div>,
      },
      {
        title: '实收日期',
        width: 180,
        tooltip: true,
        dateFormat: 'yyyy-MM-DD',
        dataIndex: 'collectionDate',
        render: (item) => {
          if (item) {
            return item
          } else {
            return '-'
          }
        },
      },
      {
        title: <div style={{ textAlign: 'right' }}>实收金额(元)</div>,
        width: 180,
        tooltip: true,
        dataIndex: 'collectionAmount',
        render: (val) => <div style={{ textAlign: 'right' }}>{amountFormat(val / 10000)}</div>,
      },
    ]
  }, [])

  return (
    <Page>
      <div className={styles.wrap}>
        <Table
                columnsFilter={'cpm_collectionWriteOff_1'}
                onFilter={(key,val) => saveServer('cpm_collectionWriteOff_1',val)}
          resizable
          store={store.table}
          // selectable={{
          //   getCheckboxProps: (record) => {
          //     return {
          //       disabled: record.projReviewStatus === 'CLOSED',
          //     }
          //   },
          // }}
          searchbar={{
            labelCol: { span: 7 },
            initialValues: {
              planCollectionDate: [moment().startOf('M'), moment().endOf('M')],
            },
            items: [
              {
                label: '计划收款日期',
                name: 'planCollectionDate',
                type: 'rangePicker',
              },
              <Item label="客户名称" name="clientId" key="clientId">
                <ClientSelect canJump={false} functionCode="clientlist-4"></ClientSelect>
              </Item>,
              {
                label: '现金流项目',
                name: 'cashFlowItem',
                options: App.getData().optionsType.cashFlowItemEnum,
              },
              {
                label: '合同编号',
                name: 'contractCode',
              },
              {
                label: '核销状态',
                name: 'status',
                options: App.getData().optionsType.collectionWriteOffStatusEnum,
              },
              <Item
                name="amount"
                label="计划收款金额"
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
              {
                label: '实收日期',
                name: 'collectionDate',
                type: 'rangePicker',
              },
            ],
          }}
          actions={[
            {
              name: '导出',
              onClick: store.export,
              type: 'primary',
            },
          ]}
          scroll={{
            x: 1200,
          }}
          columns={columns}
        />
      </div>
    </Page>
  )
}

export default observer(CollectionWriteOff)
