import { Table, App } from '@zswl/components'
import { observer, getQuery } from '@zswl/admin'
import Store from './store'
import { Tooltip } from 'antd'
import IconFont from '@/components/Icon'
import { certTypeList } from '../../general'
import SpouseModal from './SpouseModal'
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
  if (type == 'main') {
    return (
      <Tooltip title={obj?.value}>
        <span style={{ color: obj?.isChange ? 'red' : '#333' }}>{obj.value ? '是' : '否'}</span>
      </Tooltip>
    )
  }
  if (type == 'gender') {
    return (
      <Tooltip title={obj?.value}>
        <span style={{ color: obj?.isChange ? 'red' : '#333' }}>
          {obj.value == 'MALE' ? '男' : '女'}
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
function Index({ id, canEditFlag, businessVersion, startUserId }) {
  const store = useMemo(
    () => new Store(id, businessVersion, startUserId),
    [id, businessVersion, startUserId]
  )

  return (
    <div>
      <Table
        columnsFilter={'Basic_Spouse_1'}
        onFilter={(key,val) => saveServer('Basic_Spouse_1',val)}
        store={store.spouseInfo}
        extra={[
          {
            name: (
              <span>
                <IconFont type="icon-icon_add" />
                新增
              </span>
            ),
            type: 'primary',
            onClick: store.spouseModal.open,
            disabled: !canEditFlag,
          },
        ]}
        columns={[
          {
            title: '配偶姓名',
            dataIndex: 'spouseName',
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.spouseName)
              }
              return t.spouseName
            },
          },
          {
            title: '证件类型',
            dataIndex: 'certType',
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.certType)
              }
              return certTypeList[t.certType]
            },
          },
          {
            title: '证件号码',
            dataIndex: 'certNumber',
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.certNumber)
              }
              return t.certNumber
            },
          },
          {
            title: '操作',
            actions() {
              return [
                { name: '编辑', onClick: store.spouseModal.open, disabled: !canEditFlag },
                { name: '删除', onClick: store.deleteSpouse, disabled: !canEditFlag },
              ]
            },
          },
        ]}
      />
      <SpouseModal id={id} store={store} />
    </div>
  )
}

export default observer(Index)
