import { Amount } from '@/components/Format'
import { Table, App } from '@zswl/components'
import { observer, getQuery } from '@zswl/admin'
import store from './store'
import IconFont from '@/components/Icon'
import { saveServer } from '@/utils'
import { Tooltip } from 'antd'
import EnterprisesModal from './EnterprisesModal'
import { relationshipTypeList, continuousStatusList } from '../../general'
import { amountFormat } from '@/utils'
import { useEffect, useMemo } from 'react'
const diffNode = (obj, type) => {
  if (type == 'relationship') {
    return (
      <Tooltip title={obj?.value}>
        <span style={{ color: obj?.isChange ? 'red' : '#333' }}>
          {relationshipTypeList[obj?.value]}
        </span>
      </Tooltip>
    )
  }
  if (type == 'continuousStatus') {
    return (
      <Tooltip title={obj?.value}>
        <span style={{ color: obj?.isChange ? 'red' : '#333' }}>
          {continuousStatusList[obj?.value]}
        </span>
      </Tooltip>
    )
  }
  if (type == 'registerCapital' || type == 'investAmount') {
    return (
      <Tooltip title={amountFormat(obj?.value / 10000)}>
        <span style={{ color: obj?.isChange ? 'red' : '#333' }}>
          {<Amount value={obj?.value} />}
        </span>
      </Tooltip>
    )
  }
  if (type == 'shareholdingRatio') {
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
function CustomerMaintainEnterprises({ canEditFlag, id, businessVersion, startUserId }) {
  store.startUserId = startUserId
  store.businessVersion = businessVersion
  store.clientId = id
  // const store = useMemo(
  //   () => new Store(id, businessVersion, startUserId),
  //   [id, businessVersion, startUserId]
  // )
  return (
    <div>
      <Table
        columnsFilter={'Basic_Enterprises_1'}
        onFilter={(key,val) => saveServer('Basic_Enterprises_1',val)}
        resizable
        store={store.affiliated}
        scroll={{
          x: 1500,
        }}
        extra={[
          {
            name: (
              <span>
                <IconFont type="icon-icon_add" />
                新增
              </span>
            ),
            type: 'primary',
            onClick: store.enterprisesModal.open,
            disabled: !canEditFlag,
          },
        ]}
        columns={[
          {
            title: '关联企业名称',
            dataIndex: 'enterpriseName',
            width: 300,
            tooltip: true,
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.enterpriseName)
              }
              return <Tooltip title={t.enterpriseName}>{t.enterpriseName}</Tooltip>
            },
          },
          {
            title: '成立年份',
            dataIndex: 'establishDate',
            dateFormat: 'yyyy-MM-DD',
            width: 120,
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.establishDate)
              }
              return <Tooltip title={t.establishDate}>{t.establishDate}</Tooltip>
            },
          },
          {
            title: '关联关系',
            dataIndex: 'relationship',
            width: 100,
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.relationship, 'relationship')
              }
              return (
                <Tooltip title={relationshipTypeList[t.relationship]}>
                  {relationshipTypeList[t.relationship]}
                </Tooltip>
              )
            },
          },
          {
            title: '注册资本(万元)',
            dataIndex: 'registerCapital',
            align: 'right',
            sorter: true,
            width: 150,
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t?.registerCapital, 'registerCapital')
              }
              return <Amount value={t?.registerCapital} />
            },
          },
          {
            title: '持股比例(%)',
            dataIndex: 'shareholdingRatio',
            sorter: true,
            width: 150,
            align: 'right',
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t?.shareholdingRatio, 'shareholdingRatio')
              }
              return <Amount value={t?.shareholdingRatio} />
            },
          },
          {
            title: '存续状态',
            dataIndex: 'continuousStatus',
            width: 100,
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t?.continuousStatus, 'continuousStatus')
              }
              return (
                <Tooltip title={continuousStatusList[t?.continuousStatus]}>
                  {continuousStatusList[t?.continuousStatus]}
                </Tooltip>
              )
            },
          },
          {
            title: '投资金额(万元)',
            dataIndex: 'investAmount',
            align: 'right',
            sorter: true,
            width: 150,
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t?.investAmount, 'investAmount')
              }
              return <Amount value={t?.investAmount} />
            },
          },
          {
            title: '行业',
            dataIndex: 'industryTypeName',
            width: 240,
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t?.industryTypeName)
              }
              return <Tooltip title={t?.industryTypeName}>{t?.industryTypeName}</Tooltip>
            },
          },
          {
            title: '操作',
            fixed: 'right',
            width: 120,
            actions() {
              return [
                { name: '编辑', onClick: store.enterprisesModal.open, disabled: !canEditFlag },
                { name: '删除', onClick: store.removeEnterprise, disabled: !canEditFlag },
              ]
            },
          },
        ]}
      />
      <EnterprisesModal store={store} />
    </div>
  )
}

export default observer(CustomerMaintainEnterprises)
