import { useEffect, useMemo } from 'react'
import { Table, Button, Form } from '@zswl/components'
import { observer } from '@zswl/admin'
import { Space, DatePicker } from 'antd'
import moment from 'moment'
import { compareTableData } from '@/utils'
import RenderColumn from '@/components/RenderColumn'
import DataUpload from '@/components/DataUpload'
import { DownloadTemplateAction as DownloadTemplate } from '@/components/Actions'
import styles from './index.less'
import Store from './store'
import { saveServer } from '@/utils'

const { Item } = Form

const Index = ({
  financingId,
  isFormApproval,
  businessVersion,
  canEdit,
  baseInfoData = {},
  allModuleData = null, // 版本日志
  detail,
  isOtherChange,
  baseStore,
}) => {
  const [form] = Form.useForm()

  // 获取基础信息模块
  const getBaseInfoData = useMemo(() => {
    if (!allModuleData) return baseInfoData
    const modeleKey = 'BASE_INFO'
    if (isFormApproval) {
      const { newDetail } = compareTableData(allModuleData[modeleKey])
      return newDetail[0]
    }
    return allModuleData[modeleKey][0]
  }, [allModuleData])

  const store = useMemo(() => {
    return new Store({ businessVersion, isFormApproval, financingId, detail, baseStore })
  }, [businessVersion, isFormApproval, financingId, detail, baseStore])

  useEffect(() => {
    if (getBaseInfoData.planLoanDate) {
      form.setFieldsValue({
        planLoanDate: getBaseInfoData.planLoanDate
          ? moment(getBaseInfoData.planLoanDate)
          : undefined,
      })
    }
  }, [getBaseInfoData])

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <div className={styles.title}>还款概算表</div>
        <div className={styles.rightWrap}>
          <Form form={form}>
            <Space>
              <Item label={'计划贷款日期'} name="planLoanDate" required>
                <DatePicker
                  disabled={isOtherChange || !canEdit}
                  allowClear={false}
                  placeholder={'请选择'}
                  style={{ width: 200 }}
                  onChange={store.modifyPlanDate}
                />
              </Item>
              <DownloadTemplate
                params={{
                  templateName: 'TEMPLATE_OSS_NAME_FINANCING_REPAY',
                  moduleType: 'FUND_FINANCING',
                }}
              />
              <DataUpload maxCount={1} onChange={store.importTable} accept="*">
                <Button disabled={isOtherChange || !canEdit}>导入还款计划</Button>
              </DataUpload>
              <Button type="primary" onClick={store.postExportTable}>
                导出
              </Button>
            </Space>
          </Form>
        </div>
      </div>
      <Table
        columnsFilter={'detail_EstimateTable_1'}
        onFilter={(key, val) => saveServer('detail_EstimateTable_1', val)}
        rowKey={(record) => {
          return record.phase?.value ?? record.phase
        }}
        scroll={false}
        store={store.$table}
        columns={[
          {
            title: '日期',
            dataIndex: 'repayDate',
            render(val, t) {
              return <RenderColumn data={val} isCompare={isFormApproval}></RenderColumn>
            },
          },
          {
            title: '期项',
            width: 100,
            dataIndex: 'phase',
            render(val, t) {
              return <RenderColumn data={val} isCompare={isFormApproval}></RenderColumn>
            },
          },
          {
            title: '本金(元)',
            dataIndex: 'principleAmount',
            align: 'right',
            render(val, t) {
              return <RenderColumn data={val} isCompare={isFormApproval} formatNum></RenderColumn>
            },
          },
          {
            title: '利息(元)',
            dataIndex: 'interestAmount',
            align: 'right',
            render(val, t) {
              return <RenderColumn data={val} isCompare={isFormApproval} formatNum></RenderColumn>
            },
          },
          {
            title: '应还金额(元)',
            dataIndex: 'repayAmount',
            align: 'right',
            render(val, t) {
              return <RenderColumn data={val} isCompare={isFormApproval} formatNum></RenderColumn>
            },
          },
          {
            title: '剩余本金(元)',
            dataIndex: 'remainingPrincipleAmount',
            align: 'right',
            render(val, t) {
              return <RenderColumn data={val} isCompare={isFormApproval} formatNum></RenderColumn>
            },
          },
        ]}
      />
    </div>
  )
}

export default observer(Index)
