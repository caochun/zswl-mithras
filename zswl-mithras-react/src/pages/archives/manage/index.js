import { Page, Table, App, Button } from '@zswl/components'
import { history, observer } from '@zswl/admin'
import store from './store'
import styles from './index.less'
import ALL_COLUMNS from '../Column'
import { getTableColumns, getFormColumns } from '@/utils'
import { InputEditable } from '@/components/Format'
import DownloadModal from './DownloadModal'
import FileModal from './FileModal'
import { Space, Tag, Tooltip } from 'antd'
import { saveServer } from '@/utils'

const nameColumns = [
  {
    title: '项目名称',
    dataIndex: 'projName',
    width: 250,
    fixed: 'left',
    render: (val, records) => {
      const canClick = records?.id?.toString().indexOf('folder') > -1
      if (canClick) {
        const isLevel = records?.key?.toString().indexOf('_') > -1
        if (isLevel) {
          return <span>{val}</span>
        }
        return (
          <Tooltip title={val}>
            <a
              onClick={() => history.push(`/archives/manage/detail/${records.key}`)}
              className="z-single-line"
            >
              {val}
            </a>
          </Tooltip>
        )
      } else {
        return <span>{val}</span>
      }
    },
  },
  '归档编号',
  '业务类型',
  {
    title: '业务部门',
    dataIndex: 'bizDept',
  },
  {
    title: '业务主办',
    dataIndex: 'projSponsorUserName',
  },
  {
    title: '客户名称',
    dataIndex: 'clientName',
  },
  {
    title: '归档状态',
    dataIndex: 'status',
    render(val) {
      const { label, color } = App.matchOption('archivingStatus', val)
      return (
        <Space size={0}>
          <Tag style={{ border: 0 }} color={color}>
            {label}
          </Tag>
        </Space>
      )
    },
  },
  '审批状态',
  '必传文档情况',
  '创建时间',
  {
    title: '更新时间',
    dataIndex: 'updateTime',
    width: 180,
    fixed: 'right',
    render: (haveTime, records) => {
      if (haveTime) {
        return <div>{haveTime}</div>
      } else if (records?.id?.toString().indexOf('folder') > -1) {
        return null
      } else {
        return (
          <Space>
            <a onClick={() => window.open(`/preview/reportPreview/${records.id}`)}>预览</a>
            {records?.canDownload ? (
              <Button
                type="link"
                onClick={() =>
                  store.downloadListFile({
                    mainId: records?.parent[0],
                    fileId: records.id,
                    moduleType: 'ARCHIVES',
                  })
                }
              >
                下载
              </Button>
            ) : (
              <span></span>
            )}
          </Space>
        )
      }
    },
  },
]
const formNameColumns = [
  '文件类型',
  '业务类型',
  { title: '项目名称', editable: InputEditable({ disabled: false }) },
  '业务部门',
  '业务主办',
  '客户名称',
  '创建时间',
  '更新时间',
]
const columns = getTableColumns(ALL_COLUMNS, nameColumns)
const formColumns = getFormColumns(ALL_COLUMNS, formNameColumns)
function Index({ query }) {
  const { expandedRowKeys, data } = store
  const { rows } = store.table.getSelected()
  return (
    <Page>
      <div className={styles.customerWrap}>
        <Table
        columnsFilter={'archives_manage_1'}
                onFilter={(key,val) => saveServer('archives_manage_1',val)}
        
          selectable={{
            checkStrictly: false,
            getCheckboxProps(record) {
              return {
                disabled: record?.id?.toString().indexOf('folder') > -1 && !record.children?.length,
              }
            },
          }}
          store={store.table}
          expandable={{
            expandedRowKeys,
            onExpandedRowsChange: store.setExpandedRowKeys,
          }}
          editable={false}
          searchbar={{
            labelCol: { span: 6 },
            items: formColumns,
          }}
          actions={[
            {
              name: '申请下载',
              onClick: store.createModal.open,
              type: 'primary',
              disabled: !rows?.length,
            },
            {
              name: '发起归档',
              onClick: store.fileModal.open,
            },
          ]}
          scroll={{
            x: 1200,
          }}
          columns={columns}
        />
      </div>
      <DownloadModal />
      <FileModal store={store} />
    </Page>
  )
}

export default observer(Index)
