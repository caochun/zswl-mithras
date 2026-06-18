import { getTableColumns } from '@/utils'
import { observer } from '@zswl/admin'
import { Button, Table } from '@zswl/components'
import { Checkbox, Space } from 'antd'
import { useEffect, useState, useMemo } from 'react'
import DataUpload from '@/components/DataUpload'
import DownloadTemplate from '@/components/Actions/DownloadTemplate'
import FilesManageDraw from './FilesManageDraw'
import ALL_COLUMNS from '@/components/PolicyColumns'
import PolicyModal from './PolicyModal'
import styles from './index.less'
import Store from './store'
import { saveServer } from '@/utils'

const Index = ({ canEditFlag = true, mainId, baseDetailData = {} }) => {
  const store = useMemo(() => {
    return new Store({ mainId, baseDetailData })
  }, [mainId])

  const { policyCheck, setPolicyCheck } = store

  useEffect(() => {
    setPolicyCheck(baseDetailData?.policyFlag)
  }, [baseDetailData?.policyFlag])

  const columns = useMemo(() => {
    const nameColumns = [
      {
        title: '保险单号',
        dataIndex: 'policyCode',
        width: 200,
        actions: null,
      },
      '保险机构',
      '险种',
      '保单金额(元)',
      { title: '保险起始日-详情', rename: '保险起始日' },
      { title: '保险到期日-详情', rename: '保险到期日' },
      '是否续保',
      '标识信息',
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

          {canEditFlag && (
            <Button
              type="primary"
              onClick={() => store.policyModal.open()}
              disabled={[1, 2].includes(policyCheck)}
            >
              新增
            </Button>
          )}

          <Button type="primary" onClick={store.policyBatchDown}>
            批量下载
          </Button>
          <Button type="primary" onClick={store.batchRemove}>
            批量删除
          </Button>
        </Space>
      </div>
      <Table
        columnsFilter="cpm_paymentApplication_policy"
                onFilter={(key,val) => saveServer('cpm_paymentApplication_policy',val)}
        
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
                {
                  name: '编辑',
                  onClick: () => store.policyModal.open(record),
                },
                {
                  name: '删除',
                  onClick: () => store.policyItemDelete(record),
                },
                {
                  name: '查看附件',
                  onClick: () => store.filesManageDraw.open({ mainId: record.id }),
                },
              ].filter(Boolean),
          },
        ]}
        extra={<Checkbox onChange={store.onAdventChange}>临期保险</Checkbox>}
        scroll={{ x: 1000 }}
      />
      <PolicyModal store={store} />
      <FilesManageDraw store={store} />
    </>
  )
}

export default observer(Index)
