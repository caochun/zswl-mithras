import { getTableColumns } from '@/utils'
import { observer, history } from '@zswl/admin'
import { Button, Table } from '@zswl/components'
import { Checkbox, Space } from 'antd'
import { useEffect, useState, useMemo } from 'react'
import DataUpload from '@/components/DataUpload'
import { DownloadTemplateAction as DownloadTemplate } from '@/components/Actions'
import FilesManageDraw from './FilesManageDraw'
import { PolicyColumns as ALL_COLUMNS } from '@/components/PolicyColumns/PolicyColumnsEntries'
import PolicyModal from './PolicyModal'
import styles from './index.less'
import Store from './store'
import { saveServer } from '@/utils'

const Index = ({
  canEditFlag = true,
  paramsAsPolicy = {},
  businessVersion,
  mainId,
  baseDetailData = {},
  callBack,
}) => {
  const isPolicyPage = paramsAsPolicy.pageSource === 'policy'
  console.log({ paramsAsPolicy })

  const store = useMemo(() => {
    return new Store({ mainId, businessVersion, paramsAsPolicy, baseDetailData })
  }, [mainId, businessVersion, JSON.stringify(paramsAsPolicy), JSON.stringify(baseDetailData)])

  const { policyCheck, setPolicyCheck } = store
  const policyData = store.policyTable.getList()

  useEffect(() => {
    setPolicyCheck(baseDetailData?.policyFlag)
  }, [baseDetailData?.policyFlag])

  const columns = useMemo(() => {
    const nameColumns = [
      {
        title: '保险单号',
        width: 200,
        actions({ policyCode, id }) {
          return [
            {
              name: policyCode,
              disabled: baseDetailData?.paymentStatus === 'CLOSED',
              onClick: () => {
                history.push(
                  `/afterLease/policyManage/detail/${id}?dataSource=${
                    paramsAsPolicy.pageSource ?? 'payment'
                  }`
                )
                callBack?.()
              },
            },
          ]
        },
      },
      '保险机构',
      '险种',
      '保单金额(元)',
      '标识信息',
      { title: '保险起始日-详情', rename: '保险起始日' },
      { title: '保险到期日-详情', rename: '保险到期日' },
      '是否续保',
      '备注',
      '创建人',
      '创建时间',
    ]
    return getTableColumns(ALL_COLUMNS, nameColumns)
  }, [baseDetailData?.paymentStatus])

  return (
    <>
      <div className={styles.nav} style={{ marginBottom: 20 }}>
        <div className={styles.title}>保单信息</div>

        <Space>
          <DownloadTemplate
            params={{
              templateName: 'TEMPLATE_OSS_NAME_PAYMENT_POLICY_ITEM',
              moduleType: 'PAYMENT',
            }}
          />

          {canEditFlag && (
            <DataUpload accept="*" maxCount={1} api={store.onFileChange}>
              <Button.Upload type="primary" disabled={[1, 2].includes(policyCheck)}>
                导入
              </Button.Upload>
            </DataUpload>
          )}

          {canEditFlag && <Button onClick={store.batchRemove}>批量删除</Button>}

          {canEditFlag && (
            <Button
              type="primary"
              onClick={() => store.policyModal.open()}
              disabled={[1, 2].includes(policyCheck)}
            >
              新增
            </Button>
          )}

          <Button
            type="primary"
            onClick={store.policyBatchDown}
            disabled={!store.policyTable.getList().length}
          >
            批量下载
          </Button>
        </Space>
      </div>
      <Table
        columnsFilter="cpm_paymentApplication_policy_1"
        onFilter={(key,val) => saveServer('cpm_paymentApplication_policy_1',val)}
        serial
        rowClassName={(record) => {
          const endDay = moment(record.insuranceEndDate)
          const nowDay = moment()
          const diffDay = endDay.diff(nowDay, 'days')
          return diffDay < 5 ? styles.overtime : undefined
        }}
        selectable={{
          type: 'checkbox',
        }}
        editable={false}
        columnWidth={180}
        store={store.policyTable}
        columns={[
          ...columns,
          {
            title: '操作',
            width: 160,
            fixed: 'right',
            actions: (record) =>
              [
                canEditFlag && {
                  name: '编辑',
                  onClick: () => store.policyItemEdit(record),
                },
                canEditFlag && {
                  name: '删除',
                  onClick: () => store.policyItemDelete(record),
                },
                {
                  name: '查看附件',
                  disabled: !record?.materials?.length > 0,
                  onClick: () => store.filesManageDraw.open(record?.materials),
                },
              ].filter(Boolean),
          },
        ]}
        extra={<Checkbox onChange={store.onAdventChange}>临期保险</Checkbox>}
        // 临时处理 paramsAsPolicy.pageSource !== 'remind'
        actions={[
          !isPolicyPage && paramsAsPolicy.pageSource !== 'remind' && (
            <div>
              <Checkbox
                checked={policyCheck === 1}
                onChange={(e) => {
                  const value = e.target.checked ? 1 : 0
                  store.onChecked({ checked: value })
                  setPolicyCheck(value)
                }}
                disabled={policyData?.length || canEditFlag === false || policyCheck === 2}
              >
                无需购买保险
              </Checkbox>
              <Checkbox
                checked={policyCheck === 2}
                disabled={policyData?.length || canEditFlag === false || policyCheck === 1}
                onChange={(e) => {
                  const value = e.target.checked ? 2 : 0
                  store.onChecked({ checked: value })
                  setPolicyCheck(value)
                }}
              >
                尚未购买保险
              </Checkbox>
            </div>
          ),
        ].filter(Boolean)}
        scroll={{ x: 1000 }}
      />
      <PolicyModal store={store} />
      <FilesManageDraw store={store} />
    </>
  )
}

export default observer(Index)
