import { useMemo } from 'react'
import { Table, App, Page, SearchBar } from '@zswl/components'
import IconFont from '@/components/Icon'
import { ClientSelect, FounderSelect, OrgSelect } from '@/components/Select'
import { observer } from '@zswl/admin'
import { amountFormat, getKeyOptionsLabelMapPlus, formatPercent, hasValue,saveServer } from '@/utils'
import CreateModal from './CreateModal'
import Store from './store'

const { Item } = SearchBar
function Index() {
  const { optionsType } = App.getData()
  const store = useMemo(() => {
    return new Store({})
  }, [])

  const columns = useMemo(() => {
    return [
      {
        title: '项目名称',
        dataIndex: 'projName',
        width: 300,
        fixed: 'left',
        actions({ projName, id }) {
          return [
            {
              name: projName,
              to: `/afterLease/adjust/detail/${id}`,
              className: 'z-single-line',
              // style: { width: 190 },
            },
          ]
        },
      },
      {
        title: '项目编号',
        width: 200,
        dataIndex: 'projCode',
      },
      {
        title: '业务类型',
        width: 100,
        dataIndex: 'bizType',
        render: (item) => {
          return getKeyOptionsLabelMapPlus('projEstablishBizType')[item]
        },
      },
      {
        title: '授信金额(元)',
        dataIndex: 'applyCreditAmount',
        width: 140,
        align: 'right',
        render: (val) => {
          return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
        },
      },
      {
        title: '业务部门',
        dataIndex: 'bizDeptName',
        width: 130,
      },
      {
        title: '项目主办',
        dataIndex: 'projSponsorUserName',
        width: 130,
      },
      {
        title: '项目协办',
        width: 130,
        dataIndex: 'projCosponsorUserNames',
        render: (val) => {
          return val?.join(',') || '-'
        },
      },
      {
        title: '调整日期',
        width: 180,
        dataIndex: 'afterLeaseAdjustData',
      },
      {
        title: '调整类型',
        width: 140,
        dataIndex: 'afterLeaseAdjustType',
        render: (val) => {
          return getKeyOptionsLabelMapPlus('afterLeaseAdjustEnum')[val] || '-'
        },
      },
      {
        title: '审批状态',
        dataIndex: 'adjustProcessStatus',
        width: 100,
        render: (val) => {
          return getKeyOptionsLabelMapPlus('commonProcessStatus')[val] || '-'
        },
      },
    ]
  }, [])

  return (
    <Page store={store}>
      <Table
        resizable
        rowKey={'id'}
        columnsFilter={'afterLease_adjust_1'}
                onFilter={(key,val) => saveServer('afterLease_adjust_1',val)}
        
        store={store.$table}
        searchbar={{
          labelCol: { span: 6 },
          items: [
            {
              label: '项目名称',
              name: 'projName',
            },
            <Item label="客户名称" name="clientId" key="clientId">
              <ClientSelect canJump={false} functionCode="clientlist-adjust"></ClientSelect>
            </Item>,
            {
              label: '业务类型',
              name: 'bizType',
              options: optionsType.projEstablishBizType || [],
              allowClear: true,
            },
            {
              label: '项目编号',
              name: 'projCode',
            },
            <Item label="业务部门" name="bizDeptId" key="bizDeptId">
              <OrgSelect functionCode="selectorgs-adjust"></OrgSelect>
            </Item>,
            <Item label="项目主办" name="projSponsorUserId" key="projSponsorUserId">
              <FounderSelect
                functionCode="selectfounder-adjust"
                params={{ job: 'projmanager' }}
              ></FounderSelect>
            </Item>,
            <Item label="项目协办" name="projCosponsorUserId" key="projCosponsorUserId">
              <FounderSelect
                functionCode="selectfounder-adjust"
                params={{ job: 'projmanager' }}
              ></FounderSelect>
            </Item>,
            { label: '调整日期', name: 'adjustTime', type: 'rangePicker' },
            {
              label: '调整类型',
              name: 'afterLeaseAdjustType',
              options: optionsType.afterLeaseAdjustEnum || [],
              allowClear: true,
            },
            {
              label: '审批状态',
              name: 'adjustProcessStatus',
              options: optionsType.commonProcessStatus,
            },
          ],
        }}
        actions={[
          {
            name: (
              <span>
                <IconFont type="icon-icon_add" />
                项目展期
              </span>
            ),
            onClick: () =>
              store.createModal.open({
                type: 'EXTEND',
                title: '项目展期',
              }),
            type: 'primary',
          },
          {
            name: (
              <span>
                <IconFont type="icon-icon_add" />
                调整还款计划
              </span>
            ),
            onClick: () =>
              store.createModal.open({
                type: 'REPAYMENT',
                title: '调整还款计划',
              }),
            type: 'primary',
          },
        ]}
        scroll={{
          x: 1300,
        }}
        columns={columns}
      />
      <CreateModal store={store}></CreateModal>
    </Page>
  )
}

export default observer(Index)
