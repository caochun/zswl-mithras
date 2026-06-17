import { useMemo } from 'react'
import { Button, Table } from '@zswl/components'
import { Space, Checkbox } from 'antd'
import { observer } from '@zswl/admin'
import IconFont from '@/components/Icon'
import CreateModal from './CreateModal'
import RenderColumn from '@/components/RenderColumn'
import BankAccount from '@/components/Form/BankAccount'
import { JumpContractDetail, JumpProjDetail } from './Jump'
import Store from './store'
import styles from './index.less'
import { saveServer } from '@/utils'

const Index = ({ financingId, isFormApproval, businessVersion, canEdit = true, detail }) => {
  const store = useMemo(() => {
    return new Store({ businessVersion, isFormApproval, financingId, detail })
  }, [businessVersion, isFormApproval, financingId, detail])

  const columns = useMemo(() => {
    return [
      {
        title: '关联编号',
        width: 200,
        dataIndex: 'pledgeCode',
        render(val, t) {
          return <RenderColumn data={val} isCompare={isFormApproval}></RenderColumn>
        },
      },
      {
        title: '客户名称',
        dataIndex: 'clientName',
        width: 260,
        render(val, t) {
          return <RenderColumn data={val} isCompare={isFormApproval}></RenderColumn>
        },
      },
      {
        title: '项目名称',
        dataIndex: 'projName',
        width: 260,
        render(val, t) {
          return <RenderColumn data={val} isCompare={isFormApproval}></RenderColumn>
        },
      },
      {
        title: '合同编号',
        dataIndex: 'contractCode',
        width: 250,
        render(val, t) {
          if (isFormApproval) {
            return (
              <JumpContractDetail
                title={t.contractCode?.value}
                contractId={t.contractId?.value}
                isChange={t.contractId?.isChange}
              ></JumpContractDetail>
            )
          }
          return (
            <JumpContractDetail
              title={t.contractCode}
              contractId={t.contractId}
            ></JumpContractDetail>
          )
        },
      },
      {
        title: '业务类型',
        dataIndex: 'bizType',
        render(val, t) {
          return (
            <RenderColumn
              data={val}
              isCompare={isFormApproval}
              selectEnum="projEstablishBizType"
            ></RenderColumn>
          )
        },
      },
      {
        title: '是否质押',
        dataIndex: 'isPledge',
        render(val) {
          const newVal = val?.value ?? val
          return (
            <Space>
              <Checkbox checked={!!newVal}></Checkbox>
              {newVal ? '是' : '否'}
            </Space>
          )
        },
      },
      {
        title: '是否监管',
        dataIndex: 'isSupervise',
        render(val) {
          const newVal = val?.value ?? val
          return (
            <Space>
              <Checkbox checked={!!newVal}></Checkbox>
              {newVal ? '是' : '否'}
            </Space>
          )
        },
      },
      {
        title: '银行账号',
        dataIndex: 'accountNumber',
        width: 250,
        render: (val) => {
          return (
            <div style={val?.isChange ? { color: 'red' } : {}}>
              {BankAccount.Format({ value: val })}
            </div>
          )
        },
      },
      {
        title: '开户行',
        width: 300,
        dataIndex: 'accountBank',
        render(val, t) {
          return <RenderColumn data={val} isCompare={isFormApproval}></RenderColumn>
        },
      },

      {
        title: '户名',
        width: 300,
        dataIndex: 'accountName',
        render(val, t) {
          return <RenderColumn data={val} isCompare={isFormApproval}></RenderColumn>
        },
      },
      {
        title: '合同金额(元)',
        dataIndex: 'contractAmount',
        width: 150,
        align: 'right',
        render(val, t) {
          return <RenderColumn data={val} isCompare={isFormApproval} formatNum></RenderColumn>
        },
      },
      {
        title: '合同期限',
        dataIndex: 'contractStartDate',
        width: 300,
        render(val, { contractStartDate, contractEndDate }) {
          if (isFormApproval) {
            return (
              <div
                style={
                  contractStartDate.isChange || contractEndDate.isChange ? { color: 'red' } : {}
                }
              >{`${contractStartDate?.value ?? '-'} ~ ${contractEndDate?.value ?? '-'}`}</div>
            )
          }
          return <div>{`${contractStartDate ?? '-'} ~ ${contractEndDate ?? '-'}`}</div>
        },
      },
      {
        title: '剩余未还本金(元)',
        width: 160,
        align: 'right',
        dataIndex: 'remainingUnpaidPrincipal',
        render(val, t) {
          return <RenderColumn data={val} isCompare={isFormApproval} formatNum></RenderColumn>
        },
      },
      canEdit && {
        // {
        title: '操作',
        dataIndex: 'id',
        width: 180,
        fixed: 'right',
        actions(record) {
          return [
            {
              name: '编辑',
              onClick: () => store.$createModal.open(record),
              disabled: !canEdit,
            },
            { name: '删除', onClick: () => store.remove(record), disabled: !canEdit },
          ]
        },
      },
    ]
  }, [canEdit, store])

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <div className={styles.title}>关联合同明细</div>
        <Space>
          <Button
            onClick={store.$createModal.open}
            type="primary"
            icon={<IconFont type="icon-icon_add" />}
            disabled={!canEdit}
          >
            新增
          </Button>
        </Space>
      </div>
      <Table
        columnsFilter={'detail_Pledge_1'}
        onFilter={(key, val) => saveServer('detail_Pledge_1', val)}
        store={store.$table}
        columns={columns}
        columnWidth={120}
        resizable
      ></Table>
      <CreateModal store={store} financingId={financingId}></CreateModal>
    </div>
  )
}

export default observer(Index)
