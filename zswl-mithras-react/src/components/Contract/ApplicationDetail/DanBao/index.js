import { useMemo, useEffect } from 'react'
import { Button, Table, App } from '@zswl/components'
import { observer } from '@zswl/admin'
import { Space, Tooltip } from 'antd'
import IconFont from '@/components/Icon'
import { JumpClient } from '@/components/JumpClient/JumpClientEntries'
import RenderColumn from '@/components/RenderColumn'
import CreateModal from './CreateModal'
import { hasValue } from '@/utils'
import Store from './store'
import mathjs from '@/utils/math'
import styles from './index.less'
import { MatchOptionColumn } from '@/components/Format'
import { saveServer } from '@/utils'

const Index = ({ canEditFlag, baseStore, yuYingJingBanCanEdit }) => {
  const canEdit = canEditFlag || yuYingJingBanCanEdit
  const { bizType } = baseStore
  const { isFormApproval, contractId, businessVersion } = baseStore.page.getParams()
  const store = useMemo(() => {
    return new Store({ baseStore, isFormApproval, contractId, businessVersion })
  }, [isFormApproval, contractId, businessVersion])
  baseStore.danBaoStore = store

  const columns = useMemo(() => {
    return [
      {
        title: '保证合同编号',
        width: 300,
        dataIndex: 'guarantorContractCode',
        render(val, t) {
          return <RenderColumn data={val} isCompare={isFormApproval}></RenderColumn>
        },
      },
      {
        title: '担保人类型',
        width: 120,
        dataIndex: 'guarantorType',
        render(val, t) {
          return (
            <RenderColumn
              data={val}
              isCompare={isFormApproval}
              selectEnum="clientType"
            ></RenderColumn>
          )
        },
      },
      {
        title: '指定联系人',
        width: 120,
        dataIndex: 'contactName',
        render(val, t) {
          return <RenderColumn data={val} isCompare={isFormApproval}></RenderColumn>
        },
      },
      {
        title: '担保人名称',
        dataIndex: 'guarantorInfo',
        width: 250,
        render(val, t) {
          if (isFormApproval) {
            return (
              <JumpClient
                value={t.guarantorInfo?.value}
                isChange={t.guarantorInfo?.isChange}
              ></JumpClient>
            )
          }
          return <JumpClient value={val}> </JumpClient>
        },
      },
      bizType === 'ZL' &&
        MatchOptionColumn({
          title: '决议类型',
          dataIndex: 'resolutionType',
          matchOption: 'resolutionTypeEnum',
          width: 120,
        }),
      {
        title: '关联合同编号',
        width: 280,
        dataIndex: 'relatContracts',
        render(val, t) {
          if (isFormApproval) {
            let name =
              t.relatContracts?.value && t.relatContracts?.value.length > 0
                ? t.relatContracts?.value.join(',')
                : '-'

            return (
              <Tooltip title={name} placement="topLeft">
                <span style={{ color: t.relatContracts?.isChange ? 'red' : '#333' }}>{name}</span>
              </Tooltip>
            )
          }
          let name = val?.length > 0 ? val.join(',') : '-'
          return (
            <Tooltip title={name} placement="topLeft">
              {name}
            </Tooltip>
          )
        },
      },
      {
        title: '担保方式',
        dataIndex: 'guaranteeMethod',
        width: 120,
        render(val, t) {
          return (
            <RenderColumn
              data={val}
              isCompare={isFormApproval}
              selectEnum="guaranteeMethodEnum"
            ></RenderColumn>
          )
        },
      },
      {
        title: '联保标志',
        dataIndex: 'jointGuaranteeMark',
        width: 120,
        render(val, t) {
          return (
            <RenderColumn
              data={val}
              isCompare={isFormApproval}
              selectEnum="jointGuaranteeMarkEnum"
            ></RenderColumn>
          )
        },
      },
      {
        title: '担保本金(元)',
        dataIndex: 'amountSingle',
        width: 300,
        align: 'right',
        render(val, t) {
          if (isFormApproval) {
            if (['SINGLE', 'JOINT'].includes(t.jointGuaranteeMark?.value)) {
              return (
                <span>
                  {hasValue(t.amountSingle?.value)
                    ? mathjs.toNonExponential(
                        mathjs.format(mathjs.divide(t.amountSingle?.value, 10000))
                      )
                    : '-'}
                </span>
              )
            } else if (['MULTIPLE_SEPARATE'].includes(t.jointGuaranteeMark?.value)) {
              const StrNode = t.amountMultiple?.value.map((item, index) => {
                return hasValue(item.amount) ? (
                  <tr key={index}>
                    <td>
                      <Tooltip
                        title={mathjs.toNonExponential(
                          mathjs.format(mathjs.divide(item.amount, 10000))
                        )}
                      >
                        {mathjs.toNonExponential(mathjs.format(mathjs.divide(item.amount, 10000)))}
                      </Tooltip>
                    </td>
                    <td></td>
                    <td>
                      <Tooltip title={item.clientName}>
                        <div className={styles.ellipsis}>({item.clientName})</div>
                      </Tooltip>
                    </td>
                  </tr>
                ) : (
                  '-'
                )
              })
              return StrNode
            }

            return '-'
          }
          if (['SINGLE', 'JOINT'].includes(t.jointGuaranteeMark)) {
            return (
              <span>
                {hasValue(t.amountSingle)
                  ? mathjs.toNonExponential(mathjs.format(mathjs.divide(t.amountSingle, 10000)))
                  : '-'}
              </span>
            )
          } else if (['MULTIPLE_SEPARATE'].includes(t.jointGuaranteeMark)) {
            const StrNode = t.amountMultiple?.map((item, index) => {
              return hasValue(item.amount) ? (
                <tr key={index}>
                  <td align="right">
                    <Tooltip
                      title={mathjs.toNonExponential(
                        mathjs.format(mathjs.divide(item.amount, 10000))
                      )}
                    >
                      {mathjs.toNonExponential(mathjs.format(mathjs.divide(item.amount, 10000)))}
                    </Tooltip>
                  </td>
                  <td></td>
                  <td>
                    <Tooltip title={item.clientName}>
                      <div className={styles.ellipsis}>({item.clientName})</div>
                    </Tooltip>
                  </td>
                </tr>
              ) : (
                '-'
              )
            })

            return StrNode
          }

          return '-'
        },
      },
      {
        title: '是否上报征信',
        dataIndex: 'isReport',
        width: 120,
        render(val, t) {
          return (
            <RenderColumn
              data={val}
              isCompare={isFormApproval}
              selectEnum="isConfirm"
            ></RenderColumn>
          )
        },
      },
      {
        title: '操作',
        dataIndex: 'id',
        fixed: 'right',
        width: 150,
        actions(record) {
          return [
            {
              name: '查看',
              onClick: () => store.$createModal.open({ ...record, isDetail: true }),
            },
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
  }, [canEdit])

  useEffect(() => {
    if (contractId) {
      store.$table.search({ contractId })
    }
  }, [contractId])

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <div className={styles.title}>担保措施</div>
        <Space>
          <Button onClick={store.generateContractNo} disabled={!canEditFlag}>
            生成合同编号
          </Button>
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
        scroll={{ x: 1500 }}
        store={store.$table}
        columns={columns}
        autoRequest={false}
        resizable
        columnsFilter="contract_danbao"
                onFilter={(key,val) => saveServer('contract_danbao',val)}

      ></Table>
      <CreateModal store={store} bizType={bizType}></CreateModal>
    </div>
  )
}

export default observer(Index)
