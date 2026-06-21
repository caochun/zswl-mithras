import { useMemo, useEffect } from 'react'
import { Table, App } from '@zswl/components'
import IconFont from '@/components/Icon'
import { observer, history, getQuery } from '@zswl/admin'
import { message } from 'antd'
import {
  getKeyOptionsLabelMapPlus,
  isAssetJonAndAdmin,
  saveServer,
} from '@/utils'
import CreateModal from './CreateModal/AfterLeaseCheckPlanCheckListCreateModal'
import store from './store'
import { QuarterMap } from '@/utils/domains/afterLease/AfterLeaseUtils'
import { AmountColumn } from '@/components/Format'
import { PageListDownloadAction as PageListDown } from '@/components/Actions'

function AfterLeaseCheckPlanCheckList() {
  const { optionsType } = App.getData()

  const { rows } = store.$table.getSelected()
  const planStatus = rows[0]?.planStatus || ''
  const approvalStatus = rows[0]?.approvalStatus || ''
  const openModal = getQuery('openModal')
  useEffect(() => {
    openModal === 'true' && store.createModal.open()
  }, [openModal])

  const jumpDetail = ({ id, planStatus: $planStatus, planType, checkPlanClientId }) => {
    if (['CHECKING', 'FINISH'].includes($planStatus)) {
      if (planType === 'COMMONLY') {
        history.push(`/afterLease/checkPlan/commonTemplate/${checkPlanClientId}`)
        return
      }
      history.push(`/afterLease/checkPlan/planDetail/${id}`)
    } else {
      message.warn('只有检查中、检查完毕才能查看详情')
    }
  }
  const columns = useMemo(() => {
    return [
      {
        title: '租后检查计划名称',
        dataIndex: 'planName',
        // fixed: 'left',
        actions: (record) => {
          return [
            {
              name: record.planName,
              onClick: () => {
                jumpDetail(record)
              },
            },
          ]
        },
      },
      {
        title: '计划类型',
        dataIndex: 'planType',
        render: (item) => {
          return getKeyOptionsLabelMapPlus('afterLeaseCheckPlanTypeEnum')[item] || '-'
        },
      },
      // 业务部、主办客户经理、未收本金、风险敞口科目
      AmountColumn({ title: '风险敞口(万元）', dataIndex: 'riskExposure', initFormat: 1 }),
      AmountColumn({ title: '剩余本金(万元）', dataIndex: 'remainingPrincipal', initFormat: 1 }),
      { title: '项目主办', dataIndex: 'projSponsorUserName' },
      { title: '业务部门', dataIndex: 'bizDeptName' },
      { title: '协查风控经理', dataIndex: 'riskControlManagerName' },

      { title: '本次检查形式', dataIndex: 'checkWay', matchOption: 'afterLeaseCheckWayEnum' },

      { title: '本次租后截止时间', dataIndex: 'deadLine' },
      { title: '上次跟进形式', dataIndex: 'lastCheckWay', matchOption: 'afterLeaseCheckWayEnum' },
      {
        title: '计划状态',
        dataIndex: 'planStatus',
        render: (item) => {
          return getKeyOptionsLabelMapPlus('afterLeaseCheckPlanStatusEnum')[item] || '-'
        },
      },
      {
        title: '审批状态',
        dataIndex: 'approvalStatus',
        render: (item) => {
          return getKeyOptionsLabelMapPlus('afterLeaseCheckPlanProcessStatusEnum')[item] || '-'
        },
      },
      {
        title: '当前审批人',
        dataIndex: 'curAssigneeNames',
      },
      {
        title: '检查所属时间',
        dataIndex: 'checkBelongDate',
        render: (_, { year, month, quarter }) => {
          return year ? (quarter ? `${year}年${QuarterMap[quarter]}` : `${year}年${month}月`) : '-'
        },
      },
      { title: '创建时间', dataIndex: 'createTime' },
      { title: '变更时间', dataIndex: 'updateTime' },
    ]
  }, [])

  const closeDisabled = () => {
    const { planType } = rows[0] ?? {}
    // 一般检查支持直接关闭
    if (planType === 'COMMONLY') return false
    return (
      (planStatus && !['NEW'].includes(planStatus)) ||
      (approvalStatus &&
        !['NEW_REJECT', 'NEW_CANCEL', 'MODIFY_REJECT', 'MODIFY_CANCEL'].includes(approvalStatus)) ||
      !isAssetJonAndAdmin()
    ) // 新建,拒绝、取消
  }
  useEffect(() => {
    if (getQuery('reload') === 'true') {
      store.$table.search()
      history.push('/afterLease/checkPlan')
    }
  }, [getQuery('reload')])

  return (
    <div>
      <Table
        scroll={{ x: true }}
        columnWidth={180}
        columns={columns}
        columnsFilter="afterLeaseCheckPlanCheckList"
        onFilter={(key, val) => saveServer('afterLeaseCheckPlanCheckList', val)}
        resizable
        selectable={{
          type: 'radio',
        }}
        extra={[<PageListDown key="1" module="checkList" table={store.$table} />]}
        store={store.$table}
        searchbar={{
          initialValues: { planType: '' },
          items: [
            { label: '计划名称', name: 'planName' },
            {
              label: '检查所属年份',
              name: 'year',
              type: 'datePicker',
              picker: 'year',
            },
            {
              label: '计划类型',
              name: 'planType',
              options:
                [{ value: '', label: '全部' }, ...optionsType.afterLeaseCheckPlanTypeEnum] || [],
              allowClear: false,
            },
            { label: '检查形式', name: 'checkWay', options: 'afterLeaseCheckWayEnum' },
            {
              label: (
                <div style={{ fontSize: 12 }}>
                  <div>本次租后</div>
                  <div>截止时间</div>
                </div>
              ),
              name: 'deadLineForm',
              type: 'rangePicker',
            },
            { label: '上次检查形式', name: 'lastCheckWay', options: 'afterLeaseCheckWayEnum' },
          ],
        }}
        actions={[
          {
            name: (
              <span>
                <IconFont type="icon-icon_add" />
                新增检查计划
              </span>
            ),
            key: 'add',
            onClick: () => store.createModal.open(),
            disabled: !isAssetJonAndAdmin(),
            type: 'primary',
          },
          {
            name: <span>计划编辑</span>,
            key: 'edit',
            // disabled:
            //   (planStatus && !['NEW', 'PUBLISH', 'MODIFY'].includes(planStatus)) ||
            //   !isAssetJonAndAdmin(), // 「已发布」「新建」、变更
            onClick: store.editPlan,
          },
          {
            name: <span>关闭计划</span>,
            key: 'close',
            disabled: closeDisabled(), // 新建,拒绝、取消
            onClick: store.closePlan,
          },
        ]}
      />
      <CreateModal store={store}></CreateModal>
    </div>
  )
}

export default observer(AfterLeaseCheckPlanCheckList)
