import { Amount } from '@/components/Format'
import { Table, App } from '@zswl/components'
import { observer, getQuery } from '@zswl/admin'
import store from './store'
import { Tooltip } from 'antd'
import IconFont from '@/components/Icon'
import { useEffect, useMemo } from 'react'
import { shareholderTypeList } from '../../general'
import ShareholdersModal from './ShareholdersModal'
import { amountFormat } from '@/utils'
import { saveServer } from '@/utils'

const diffNode = (obj, type) => {
  if (type == 'shareholderType') {
    return (
      <Tooltip title={obj?.value}>
        <span style={{ color: obj?.isChange ? 'red' : '#333' }}>
          {shareholderTypeList[obj?.value]}
        </span>
      </Tooltip>
    )
  }
  if (type == 'realController') {
    return (
      <Tooltip title={obj?.value}>
        <span style={{ color: obj?.isChange ? 'red' : '#333' }}>
          {obj?.value != null ? (obj?.value ? '是' : '否') : '-'}
        </span>
      </Tooltip>
    )
  }
  if (type == 'paidTotal' || type == 'actualPaidTotal') {
    return (
      <Tooltip title={amountFormat(obj?.value / 10000)}>
        <span style={{ color: obj?.isChange ? 'red' : '#333' }}>
          {<Amount value={obj?.value} />}
        </span>
      </Tooltip>
    )
  }
  if (type == 'capitalPercent') {
    return (
      <Tooltip title={obj?.value / 10000}>
        <span style={{ color: obj?.isChange ? 'red' : '#333' }}>
          {<Amount value={obj?.value} />}
        </span>
      </Tooltip>
    )
  }
  return (
    <Tooltip title={obj?.value}>
      <span style={{ color: obj?.isChange ? 'red' : '#333' }}>{obj?.value}</span>
    </Tooltip>
  )
}
function CustomerShareholders({ canEditFlag, id, businessVersion, startUserId }) {
  store.clientId = id
  store.businessVersion = businessVersion
  store.startUserId = startUserId
  // const store = useMemo(
  //   () => new Store(id, businessVersion, startUserId),
  //   [id, businessVersion, startUserId]
  // )

  return (
    <div>
      <Table
                    columnsFilter={'Basic_Shareholders_1'}
                    onFilter={(key,val) => saveServer('Basic_Shareholders_1',val)}
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
            onClick: store.shareholdersModal.open,
            disabled: !canEditFlag,
          },
        ]}
        store={store.shareholder}
        scroll={{
          x: 1200,
        }}
        columns={[
          {
            title: '股东类型',
            dataIndex: 'shareholderType',
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.shareholderType, 'shareholderType')
              }
              return shareholderTypeList[t.shareholderType]
              //return shareholderTypeList[v]
            },
          },
          {
            title: '股东名称',
            dataIndex: 'shareholderName',
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.shareholderName)
              }
              return t.shareholderName
            },
          },
          {
            title: '认缴金额（万元）',
            dataIndex: 'paidTotal',
            width: 150,
            align: 'right',
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t?.paidTotal, 'paidTotal')
              }
              return <Amount value={t?.paidTotal} />
            },
          },
          {
            title: '实缴金额(万)',
            dataIndex: 'actualPaidTotal',
            sorter: true,
            align: 'right',
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t?.actualPaidTotal, 'actualPaidTotal')
              }
              return <Amount value={t?.actualPaidTotal} />
            },
          },
          {
            title: '认缴出资方式',
            dataIndex: 'capitalWay',
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.capitalWay)
              }
              return t.capitalWay
            },
          },
          {
            title: '认缴出资占比(%)',
            align: 'right',
            dataIndex: 'capitalPercent',
            sorter: true,
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t?.capitalPercent, 'capitalPercent')
              }
              return <Amount value={t?.capitalPercent} />
            },
          },
          {
            title: '是否实际控制人',
            dataIndex: 'realController',
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.realController, 'realController')
              }
              return t.realController != null ? (t.realController ? '是' : '否') : '-'
            },
          },
          {
            title: '操作',
            fixed: 'right',
            width: 100,
            actions() {
              return [
                { name: '编辑', onClick: store.shareholdersModal.open, disabled: !canEditFlag },
                { name: '删除', onClick: store.removeShareholder, disabled: !canEditFlag },
              ]
            },
          },
        ]}
      />
      <ShareholdersModal store={store} />
    </div>
  )
}

export default observer(CustomerShareholders)
