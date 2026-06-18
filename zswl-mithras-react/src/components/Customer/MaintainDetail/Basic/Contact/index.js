import { Table, App } from '@zswl/components'
import { Tooltip } from 'antd'
import { observer, getQuery } from '@zswl/admin'
import Store from './store'
import IconFont from '@/components/Icon'
import { saveServer } from '@/utils'
import { certTypeList } from '../../general'
import ContractModal from './ContactModal'
import { useEffect, useMemo } from 'react'

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
function Index({ canEditFlag, id, businessVersion, startUserId }) {
  const store = useMemo(
    () => new Store(id, businessVersion, startUserId),
    [id, businessVersion, startUserId]
  )
  return (
    <div>
      <Table
              columnsFilter={'Basic_Contact_1'}
              onFilter={(key,val) => saveServer('Basic_Contact_1',val)}
        resizable
        columnWidth={150}
        scroll={{ x: 1200 }}
        store={store.linkManInfo}
        extra={[
          {
            name: (
              <span>
                <IconFont type="icon-icon_add" />
                新增
              </span>
            ),
            type: 'primary',
            onClick: store.contactModal.open,
            disabled: !canEditFlag,
          },
        ]}
        columns={[
          {
            title: '是否主联系人',
            dataIndex: 'main',
            width: 120,
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.main, 'main')
              }
              return t.main ? '是' : '否'
            },
          },
          {
            title: '职务',
            dataIndex: 'position',
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.position)
              }
              return <Tooltip title={t.position}>{t.position}</Tooltip>
            },
          },
          {
            title: '姓名',
            dataIndex: 'name',
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.name)
              }
              return <Tooltip title={t.name}>{t.name}</Tooltip>
            },
          },
          {
            title: '性别',
            dataIndex: 'gender',
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.gender, 'gender')
              }
              return App.matchOption('genderType', v).label
            },
          },
          {
            title: '电话',
            dataIndex: 'telephone',
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.telephone)
              }
              return <Tooltip title={t.telephone}>{t.telephone}</Tooltip>
            },
          },
          {
            title: '座机',
            dataIndex: 'landlineTelephone',
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.landlineTelephone)
              }
              return <Tooltip title={t.landlineTelephone}>{t.landlineTelephone}</Tooltip>
            },
          },
          {
            title: '邮箱',
            dataIndex: 'mail',
            tooltip: true,
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.mail)
              }
              return <Tooltip title={t.mail}>{t.mail}</Tooltip>
            },
          },
          {
            title: '证件类型',
            dataIndex: 'certType',
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.certType, 'certType')
              }
              return <Tooltip title={certTypeList[t.certType]}>{certTypeList[t.certType]}</Tooltip>
              //return certTypeList[item]
            },
          },
          {
            title: '证件号码',
            dataIndex: 'certNumber',
            tooltip: true,
            render: (v, t) => {
              if (getQuery('typeId') == 'approval') {
                return diffNode(t.certNumber)
              }
              return <Tooltip title={t.certNumber}>{t.certNumber}</Tooltip>
            },
          },
          {
            title: '操作',
            fixed: 'right',
            width: 100,
            actions() {
              return [
                { name: '编辑', onClick: store.contactModal.open, disabled: !canEditFlag },
                { name: '删除', onClick: store.removeContact, disabled: !canEditFlag },
              ]
            },
          },
        ]}
      />
      <ContractModal store={store} />
    </div>
  )
}

export default observer(Index)
