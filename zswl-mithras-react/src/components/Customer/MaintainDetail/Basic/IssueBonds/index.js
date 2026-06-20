import { Amount } from '@/components/Format'
import { Table, App } from '@zswl/components'
import { observer, getQuery, toJS } from '@zswl/admin'
import Store from './store'
import { Tooltip } from 'antd'
import IconFont from '@/components/Icon'
import { certTypeList } from '../../general'
import IssueBondsModal from './IssueBondsModal'
import { amountFormat } from '@/utils'
import { useEffect, useMemo } from 'react'
import { saveServer } from '@/utils'

const diffNode = (obj, type) => {
  if (type == 'certType') {
    return (
      <Tooltip title={obj?.value}>
        <span style={{ color: obj?.isChange ? 'red' : '#333' }}>{certTypeList[obj?.value]}</span>
      </Tooltip>
    )
  }
  if (type == 'issueTotal' || type == 'stockScale' || type == 'maturityScale') {
    return (
      <Tooltip title={amountFormat(obj?.value / 10000)}>
        <span style={{ color: obj?.isChange ? 'red' : '#333' }}>
          {<Amount value={obj?.value} />}
        </span>
      </Tooltip>
    )
  }
  return (
    //<div>1111</div>
    <Tooltip title={obj?.value}>
      <span style={{ color: obj?.isChange ? 'red' : '#333' }}>{obj?.value}</span>
    </Tooltip>
  )
}
function Index({ canEditFlag, id, businessVersion, startUserId }) {
  const store = useMemo(
    () => new Store(id, businessVersion, startUserId),
    [id, businessVersion, startUserId]
  )
  return (
    <div>
      <Table
              columnsFilter={'Basic_IssueBonds_1'}
              onFilter={(key,val) => saveServer('Basic_IssueBonds_1',val)}
        resizable
        extra={[
          {
            name: (
              <span>
                <IconFont type="icon-icon_add" />
                新增
              </span>
            ),
            type: 'primary',
            onClick: store.issueBondsModal.open,
            disabled: !canEditFlag,
          },
        ]}
        store={store.bondRating}
        scroll={{
          x: 1400,
        }}
        columns={[
          {
            title: '评级时间',
            dataIndex: 'rateDate',
            width: 140,
            dateFormat: 'yyyy-MM-DD',
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.rateDate, 'rateDate')
              }
              return t.rateDate
            },
          },
          {
            title: '评级公司',
            dataIndex: 'rateCompany',
            width: 120,
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.rateCompany)
              }
              return t.rateCompany
            },
          },
          {
            title: '评级',
            dataIndex: 'rate',
            width: 120,
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.rate)
              }
              return t.rate
            },
          },
          {
            title: '评级展望',
            dataIndex: 'rateFuture',
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.rateFuture)
              }
              return t.rateFuture
            },
          },
          {
            title: '发行总额(亿元)',
            align: 'right',
            dataIndex: 'issueTotal',
            width: 140,
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.issueTotal, 'issueTotal')
              }
              return <Amount value={t.issueTotal} />
            },
          },
          {
            title: '发行数量（只）',
            dataIndex: 'issueAmount',
            width: 130,
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.issueAmount)
              }
              return t.issueAmount
            },
          },
          {
            title: '存量规模(亿元)',
            dataIndex: 'stockScale',
            align: 'right',
            width: 140,
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.stockScale, 'stockScale')
              }
              return <Amount value={t.stockScale} />
            },
          },
          {
            title: '存量只数',
            dataIndex: 'stockAmount',
            width: 110,
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.stockAmount)
              }
              return t.stockAmount
            },
          },
          {
            title: '到期规模(亿元)',
            width: 140,
            align: 'right',
            dataIndex: 'maturityScale',
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.maturityScale, 'maturityScale')
              }
              return <Amount value={t.maturityScale} />
            },
          },
          {
            title: '到期只数',
            dataIndex: 'maturityAmount',
            width: 120,
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.maturityAmount)
              }
              return t.maturityAmount
            },
          },
          {
            title: '操作',
            fixed: 'right',
            width: 100,
            actions() {
              return [
                { name: '编辑', onClick: store.issueBondsModal.open, disabled: !canEditFlag },
                { name: '删除', onClick: store.removeBondInfo, disabled: !canEditFlag },
              ]
            },
          },
        ]}
      />
      <IssueBondsModal store={store} />
    </div>
  )
}

export default observer(Index)
