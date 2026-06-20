import { Button, Form, Modal, ModalStore, Page, Table } from '@zswl/components'
import { getLocalStorage, getQuery, observer } from '@zswl/admin'
import { getTableColumns } from '@/utils'
import ALL_COLUMNS from '../Column'
import Store from './store'
import { useMemo, useState } from 'react'
import AddModal from '../AddModal'
import BaseInfo from '../BaseInfo'
import TrackingTask from '../TrackingTask'
import trackingApi from '@/api/trackEvent/trackingApi'
import { saveServer } from '@/utils'

function Index({ path, type, defaultData,projReviewMeetMinuteId='' }) {
  const store = useMemo(() => new Store(), [])
  const { keys, rows } = store.table.getSelected()
  const [detail, setDetail] = useState({})
  const modal = useMemo(
    () =>
      new ModalStore({
        onOpen: async (values) => {
          const res = await trackingApi.getTrackEventDetail(values)
          setDetail(res)
        },
      }),
    []
  )
  const canDelete = keys.length === 1 && rows[0].taskStatus
  const columns = useMemo(() => {
    const nameColumns = [
      {
        title: '任务名称',
        actions: ({ taskName: name, id }) => [
          {
            name,
            to: `${path}/detail/${id}`,
            onClick: type ? () => modal.open({ id }) : undefined,
          },
        ],
        search: true,
      },

      { title: '任务类型', search: true },
      { title: '提出人', search: true },
      '计划日期',
      { title: '处理人', search: true },
      '提醒频率',
      '起租后X自然日',
      '任务内容',
      { title: '任务状态', search: true },
      !type && { title: '合同编号', search: true },
      !type && { title: '客户名称', search: true },
      !type && { title: '项目名称', search: true },
      !type && '项目编号',
      '创建时间',
    ]

    return getTableColumns(ALL_COLUMNS, nameColumns, !type)
  }, [])
  const { bizId, curAssigneeIds, createBy } = defaultData || {}
  // 账本模式：没有业务ID时为主页，否则为详情页
  const isLedger = !bizId
  const userInfo = getLocalStorage('userInfo')
  const userId = userInfo?.id
  const isFormApproval = getQuery('typeId') == 'approval'
  // 审批流时，只有当前处理人可以关闭跟踪任务

  const isAssignee = isFormApproval
    ? curAssigneeIds?.split(',').includes(`${userId}`)
    : createBy == userId

  const disabled = isLedger ? false : !isAssignee
  return (
    <Page params={{ ...defaultData }} store={store.page}>
      <Table
        columnsFilter={'lease_tracking_1'}
        onFilter={(key, val) => saveServer('lease_tracking_1', val)}
        store={store.table}
        editable={false}
        selectable
        columnWidth={120}
        actions={[
          !disabled && <AddModal projReviewMeetMinuteId={projReviewMeetMinuteId} store={store} params={defaultData} />,
          !disabled && (
            <Button.Disable
              confirm={{
                title: '确定关闭吗?',
                content: '关闭后将不再发起待办流程，是否确认关闭该跟踪任务？',
              }}
              onClick={store.close}
              key="close"
              disabled={!canDelete}
              access={'trackEventClose'}
            >
              关闭
            </Button.Disable>
          ),
          !defaultData?.bizId && <Button.Download onClick={store.export}>批量导出</Button.Download>,
        ]}
        scroll={{
          x: 1200,
        }}
        columns={columns}
      />
      <Modal store={modal} title="新增跟踪事项" width={800} destroyOnClose footer={null}>
        <Form>
          <div style={{ overflowY: 'scroll', height: 500 }}>
            <BaseInfo dataSource={detail.trackEventContractInfo} canEdit={false} />
            <TrackingTask dataSource={detail} canEdit={false} />
          </div>
        </Form>
      </Modal>
    </Page>
  )
}

export default observer(Index)
