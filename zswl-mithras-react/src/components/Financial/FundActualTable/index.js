import { observer, getQuery } from '@zswl/admin'
import { useEffect, forwardRef, useImperativeHandle, useMemo } from 'react'
import { Table, Button, Form } from '@zswl/components'
import { Space, DatePicker } from 'antd'
import DataUpload from '@/components/DataUpload'
import RenderColumn from '@/components/RenderColumn'
import moment from 'moment'
import DownloadTemplate from '@/components/Actions/DownloadTemplate'
import styles from './index.less'
import Store from './store'
import { MatchOptionColumn } from '@/components/Format'
import { saveServer } from '@/utils'

const { Item } = Form

function Index(
  {
    scene,
    sourceData,
    isFormApproval,
    financingId,
    canEdit = true,
    showImportBtn = true,
    businessVersion,
    baseInfoData = {},
    showActualLoanDate = true,
    isOtherChange = true,
    baseStore,
  },
  ref
) {
  const [form] = Form.useForm()

  const store = useMemo(() => {
    return new Store({
      isFormApproval,
      financingId,
      businessVersion,
      scene,
      sourceData,
      baseStore,
    })
  }, [scene, isFormApproval, financingId, businessVersion, sourceData, baseStore])

  const { $table } = store

  useImperativeHandle(ref, () => ({
    refresh: () => {
      $table.search()
    },
  }))

  useEffect(() => {
    form.setFieldsValue({
      actualLoanDate: baseInfoData.actualLoanDate ? moment(baseInfoData.actualLoanDate) : undefined,
    })
  }, [form, baseInfoData])

  return (
    <div className={styles.pages}>
      <div className={styles.page}>
        <div className={styles.titleWrap}>
          <div className={styles.subTitle}>融资编号：{baseInfoData.financingCode}</div>
          <div className={styles.rightWrap}>
            <Form form={form}>
              <Space>
                {showActualLoanDate ? (
                  <Item label={'实际贷款日期'} name="actualLoanDate">
                    <DatePicker
                      disabled={!canEdit || !isOtherChange}
                      allowClear={false}
                      placeholder={'请选择'}
                      style={{ width: 200 }}
                      onChange={store.modifyActualDate}
                    />
                  </Item>
                ) : null}
                <DownloadTemplate
                  params={{
                    templateName: 'TEMPLATE_OSS_NAME_FINANCING_REPAY',
                    moduleType: 'FUND_FINANCING',
                  }}
                />
                {showImportBtn && (
                  <DataUpload maxCount={1} onChange={store.importTable} accept="*">
                    <Button disabled={!canEdit}>导入还款计划</Button>
                  </DataUpload>
                )}
                <Button onClick={store.postExportTable} type="primary">
                  导出
                </Button>
              </Space>
            </Form>
          </div>
        </div>
        <Table
          columnsFilter={'Component_ActualTable_1'}
          onFilter={(key, val) => saveServer('Component_ActualTable_1', val)}
          resizable
          store={$table}
          // scroll={{ x: 1200 }}
          columnWidth={140}
          rowKey={(record) => {
            return record.id?.value ?? record.id
          }}
          columns={[
            {
              title: '现金流编号',
              width: 280,
              dataIndex: 'cashFlowCode',
              render(val, t) {
                return <RenderColumn data={val} isCompare={isFormApproval}></RenderColumn>
              },
            },
            {
              title: '期项',
              dataIndex: 'phase',
              render(val, t) {
                return <RenderColumn data={val} isCompare={isFormApproval}></RenderColumn>
              },
            },
            {
              title: '日期',
              dataIndex: 'repayDate',
              render(val, t) {
                return <RenderColumn data={val} isCompare={isFormApproval}></RenderColumn>
              },
            },

            {
              title: '本金(元)',
              align: 'right',
              dataIndex: 'principleAmount',
              render(val, t) {
                return <RenderColumn data={val} isCompare={isFormApproval} formatNum></RenderColumn>
              },
            },
            {
              title: '利息(元)',
              align: 'right',
              dataIndex: 'interestAmount',
              render(val, t) {
                return <RenderColumn data={val} isCompare={isFormApproval} formatNum></RenderColumn>
              },
            },
            {
              title: '应还总额(元)',
              align: 'right',
              dataIndex: 'repayAmount',
              render(val, t) {
                return <RenderColumn data={val} isCompare={isFormApproval} formatNum></RenderColumn>
              },
            },
            {
              title: '剩余本金(元)',
              align: 'right',
              dataIndex: 'remainingPrincipleAmount',
              width: 180,
              render(val, t) {
                return <RenderColumn data={val} isCompare={isFormApproval} formatNum></RenderColumn>
              },
            },
            MatchOptionColumn({
              title: '核销状态',
              dataIndex: 'writeOffStatus',
              matchOption: 'fundReceiptRepayCashFlowState',
            }),
            MatchOptionColumn({
              title: '确认状态',
              dataIndex: 'isConfirmed',
              matchOption: 'isConfirmedEnum',
            }),
            MatchOptionColumn({
              title: '还款状态',
              dataIndex: 'isPaid',
              matchOption: 'isConfirmedEnum',
            }),
          ]}
        />
      </div>
    </div>
  )
}

export default observer(forwardRef(Index))
