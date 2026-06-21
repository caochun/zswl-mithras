import { Page, Table, SearchBar } from '@zswl/components'
import { getQuery, observer, toJS } from '@zswl/admin'
import Store from './store'
import { useEffect, useMemo } from 'react'
import styles from './index.less'
import EditModal from './EditModal'
import IconFont from '@/components/Icon'
import { PageListDownloadAction as PageListDown } from '@/components/Actions'
import { ClientSelect, OrgSelect, FounderSelect } from '@/components/Select'
import { saveServer } from '@/utils'

const { Item } = SearchBar
function ProjectEstablishment() {
  const store = useMemo(() => new Store(), [])
  const openModal = getQuery('openModal')
  useEffect(() => {
    openModal === 'true' && store.createModal.open()
  }, [openModal])
  const { options, getKeyOptionsLabelMap } = store

  const columns = useMemo(() => {
    return [
      {
        title: '项目名称',
        dataIndex: 'projName',
        width: 300,
        fixed: 'left',
        actions({ projName, bizType, id, projEstablishStatus, projSponsorUserId }) {
          return [
            {
              name: projName,
              to: `/project/establishment/detail/${id}?typeId=establishment&bizType=${bizType}`,
              className: 'z-single-line',
              // style: { width: 190 },
            },
          ]
        },
      },
      {
        title: '项目编号',
        width: 130,
        dataIndex: 'projCode',
        tooltip: true,
      },
      {
        title: '业务类型',
        width: 100,
        dataIndex: 'bizType',
        render: (item, index) => {
          return getKeyOptionsLabelMap('projEstablishBizType')[item]
        },
      },
      {
        title: '业务部门',
        dataIndex: 'bizDeptName',
        tooltip: true,
        width: 140,
      },
      {
        title: '项目主办',
        dataIndex: 'projSponsorUserName',
        tooltip: true,
        width: 130,
      },
      {
        title: '项目协办',
        width: 140,
        dataIndex: 'projCosponsorUserNames',
        render: (v, i) => {
          return v?.join(',') || '-'
        },
      },
      {
        title: '客户名称',
        width: 250,
        tooltip: true,
        dataIndex: 'clientName',
      },
      {
        title: '立项状态',
        dataIndex: 'projEstablishStatus',
        width: 140,
        render: (item, index) => {
          return item ? getKeyOptionsLabelMap('projEstablishStatus')[item] : '-'
        },
      },
      {
        title: '审批状态',
        dataIndex: 'projEstablishProcessStatus',
        width: 120,
        render: (item, index) => {
          if (item) {
            return item ? getKeyOptionsLabelMap('projProcessStatus')[item] : '-'
          }
          return '-'
        },
      },
      {
        title: '创建时间',
        width: 180,
        tooltip: true,
        dataIndex: 'createTime',
      },
      {
        title: '更新时间',
        width: 180,
        tooltip: true,
        dataIndex: 'updateTime',
      },
    ]
  }, [])

  return (
    <Page>
      <div className={styles.customerWrap}>
        <Table
          columnsFilter={'project_establishment_1'}
          onFilter={(key, val) => saveServer('project_establishment_1', val)}
          // selectable
          resizable
          store={store.table}
          selectable={{
            getCheckboxProps: (record) => {
              return {
                disabled: record.projEstablishStatus === 'CLOSED',
              }
            },
          }}
          searchbar={{
            labelCol: { span: 6 },
            items: [
              {
                label: '项目名称',
                name: 'projName',
                fixed: 'left',
              },
              <Item label="客户名称" name="clientId" key="clientId">
                <ClientSelect canJump={false} functionCode="clientlist-1"></ClientSelect>
              </Item>,
              {
                label: '业务类型',
                name: 'bizType',
                options: options.projEstablishBizType || [],
                allowClear: true,
              },
              {
                label: '项目编号',
                name: 'projCode',
              },
              <Item label="业务部门" name="bizDeptId" key="bizDeptId">
                <OrgSelect functionCode="selectorgs-3"></OrgSelect>
              </Item>,
              <Item label="项目主办" name="projSponsorUserId" key="projSponsorUserId">
                <FounderSelect functionCode="selectfounder-4"></FounderSelect>
              </Item>,
              <Item label="项目协办" name="projCosponsorUserId" key="projCosponsorUserId">
                <FounderSelect functionCode="selectfounder-4"></FounderSelect>
              </Item>,
              {
                label: '立项状态',
                name: 'projEstablishStatus',
                options: options.projEstablishStatus,
              },
              {
                label: '审批状态',
                name: 'projEstablishProcessStatus',
                options: options.projProcessStatus,
                allowClear: true,
              },
              { label: '创建时间', name: 'createDate', type: 'rangePicker' },
              { label: '更新时间', name: 'updateDate', type: 'rangePicker' },
            ],
          }}
          extra={[<PageListDown key="1" module="establishment" table={store.table} />]}
          actions={[
            {
              name: (
                <span>
                  <IconFont type="icon-icon_add" />
                  创建立项
                </span>
              ),
              onClick: store.createModal.open,
              type: 'primary',
            },
            // { name: '交接', onClick: store.withdraw },
            {
              name: '关闭立项',
              onClick: () => {
                if (store.table.getSelected().keys.length === 0) {
                  return
                }
                store.remove()
              },
              disabled: store.table.getSelected().keys.length === 0,
            },
          ]}
          scroll={{
            x: 1200,
          }}
          columns={columns}
        />
        <EditModal modalStore={store.createModal} />
      </div>
    </Page>
  )
}

export default observer(ProjectEstablishment)
