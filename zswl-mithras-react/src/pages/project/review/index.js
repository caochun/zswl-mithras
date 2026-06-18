import { Page, Table, SearchBar } from '@zswl/components'
import { getQuery, observer } from '@zswl/admin'
import Store from './store'
import { useEffect, useMemo } from 'react'
import styles from './index.less'
import EditModal from './EditModal'
import IconFont from '@/components/Icon'
import { amountFormat } from '@/utils'
import { PageListDown } from '@/components'
import { ClientSelect, OrgSelect, FounderSelect } from '@/components/Select'
import { saveServer } from '@/utils'

const { Item } = SearchBar
function Index() {
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
        actions({ projName, bizType, id, projReviewStatus }) {
          return [
            {
              name: projName,
              to: `/project/review/detail/${id}?typeId=review&bizType=${bizType}`,
              className: 'z-single-line',
              // style: { width: 200 },
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
        title: <div style={{ textAlign: 'right' }}>申报授信金额(万元)</div>,
        width: 180,
        tooltip: true,
        dataIndex: 'declaredAmount',
        render: (val) => {
          return (
            <div style={{ textAlign: 'right' }}>{val ? amountFormat(val / 100000000) : '-'}</div>
          )
        },
      },
      {
        title: '客户名称',
        width: 250,
        tooltip: true,
        dataIndex: 'clientName',
      },
      {
        title: '审批状态',
        dataIndex: 'projReviewProcessStatus',
        width: 140,
        render: (item, index) => {
          return item ? getKeyOptionsLabelMap('projProcessStatus')[item] : '-'
        },
      },
      {
        title: '项目状态',
        dataIndex: 'projReviewStatus',
        width: 140,
        render: (item, index) => {
          return item ? getKeyOptionsLabelMap('projReviewStatus')[item] : '-'
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
          columnsFilter={'project_review_1'}
          onFilter={(key, val) => saveServer('project_review_1', val)}
          resizable
          store={store.table}
          selectable={{
            getCheckboxProps: (record) => {
              return {
                disabled: record.projReviewStatus === 'CLOSED',
              }
            },
          }}
          extra={[<PageListDown key="1" module="review" table={store.table} />]}
          searchbar={{
            labelCol: { span: 6 },
            items: [
              {
                label: '项目名称',
                name: 'projName',
              },
              <Item label="客户名称" name="clientId" key="clientId">
                <ClientSelect canJump={false} functionCode="clientlist-9"></ClientSelect>
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
                <OrgSelect functionCode="selectorgs-4"></OrgSelect>
              </Item>,
              <Item label="项目主办" name="projSponsorUserId" key="projSponsorUserId">
                <FounderSelect functionCode="selectfounder-5"></FounderSelect>
              </Item>,
              <Item label="项目协办" name="projCosponsorUserId" key="projCosponsorUserId">
                <FounderSelect functionCode="selectfounder-5"></FounderSelect>
              </Item>,

              {
                label: '评审状态',
                name: 'projReviewProcessStatus',
                options: options.projProcessStatus,
              },
              { label: '创建时间', name: 'createDate', type: 'rangePicker' },
              { label: '更新时间', name: 'updateDate', type: 'rangePicker' },
            ],
          }}
          actions={[
            {
              name: (
                <span>
                  <IconFont type="icon-icon_add" />
                  发起评审
                </span>
              ),
              onClick: store.createModal.open,
              type: 'primary',
            },
            {
              name: '关闭评审',
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
        <EditModal store={store} />
      </div>
    </Page>
  )
}

export default observer(Index)
