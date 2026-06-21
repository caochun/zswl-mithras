import { Collapse, Divider, Skeleton, Badge } from 'antd'
import { getQuery, observer } from '@zswl/admin'
import styles from './index.less'
import { compareDetail, compareTableData } from '@/utils'
import CreditTermTable from '../../CreditTermTable'
import { Form, Page } from '@zswl/components'
import TextAreaEditable from '../../components/TextAreaEditable'
import FtpFormatTable from '../../FtpFormatTable'
import FtpGuideTable from '../../FtpGuideTable'
import { REGIONAL, ASSET_INDUSTRY, RISK_INDUSTRY } from '../../enum'
import _, { uniqueId } from 'lodash'
import store from './store'
import FormatTable from '../../components/FormatTable'
import moment from 'moment'
import { AmountColumn } from '@/components/Format'

const { Panel } = Collapse
const QuarterColumn = [
  {
    title: '资产行业分类',
    dataIndex: 'assetIndustry',
    editable: false,
    width: 140,
    onCell: (record, index) => {
      if ([0, 3, 6, 9].includes(index)) return { rowSpan: 3 }
      if ([1, 2, 4, 5, 7, 8, 10, 11].includes(index)) return { rowSpan: 0 }
      return {
        rowSpan: 1,
      }
    },
  },
  {
    title: '地区分类',
    dataIndex: 'regional',
    width: 160,
    editable: false,
  },
  {
    title: ' 一年内（含）',
    children: [
      AmountColumn({ title: '上市公司', dataIndex: 'col0', editable: true, width: 120 }),
      AmountColumn({ title: '国有企业', dataIndex: 'col1', editable: true, width: 120 }),
      AmountColumn({ title: '其他', dataIndex: 'col2', editable: true, width: 120 }),
    ],
  },
  {
    title: ' 1-3年期(含)',
    children: [
      AmountColumn({ title: '上市公司', dataIndex: 'col3', editable: true, width: 120 }),
      AmountColumn({ title: '国有企业', dataIndex: 'col4', editable: true, width: 120 }),
      AmountColumn({ title: '其他', dataIndex: 'col5', editable: true, width: 120 }),
    ],
  },
  {
    title: ' 3年以上',
    children: [
      AmountColumn({ title: '上市公司', dataIndex: 'col6', editable: true, width: 120 }),
      AmountColumn({ title: '国有企业', dataIndex: 'col7', editable: true, width: 120 }),
      AmountColumn({ title: '其他', dataIndex: 'col8', editable: true, width: 120 }),
    ],
  },
]
function BudgetPricingBusinessDetailLogDiff({ changeList = [], params: { id } }) {
  const { form } = store
  const compareData = store.page.getData()

  const { newData, oldData, moduleChanged } = compareData ?? {}
  const getFormData = (data = []) => {
    const newObj = {}
    data.forEach(({ descContent, descType }) => {
      const type = descType?.value
      newObj[`${type}_NEW`] = descContent
      newObj[`${type}`] = descContent.beforeValue
    })
    return newObj
  }
  const formData = getFormData(newData?.DESCRIPTION_TEXT)
  const getComponentData = (key, componentType = 'table') => {
    const newKeyData = newData?.[key]
    const oldKeyData = oldData?.[key]
    if (componentType === 'desc') {
      const { newDetail, detail, isLog } = compareTableData(newKeyData)
      return { newDetail: newDetail?.[0], detail: detail?.[0], isLog: isLog?.[0] }
    }
    if (componentType === 'table') {
      return { newDetail: newKeyData, detail: oldKeyData }
    }
    if (_.isFunction(componentType)) {
      return componentType(newKeyData, oldKeyData)
    }
  }
  const creditTermData = getComponentData('MONTHLY_DEDUCTION')
  const formatGuidanceData = (res, isNew) => {
    const tableList = []
    // 循环取 row 1- 15 的数据
    for (let i = 1; i <= 15; i++) {
      if ([13].includes(i)) {
        continue
      }
      const row = {}
      const IdList = []
      const data = (isNew ? (res?.[`row${i}`] ?? {})?.diffValueList : res?.[`row${i}`]) ?? []
      data.forEach((item, index) => {
        if (i > 9 && i <= 14) {
          row[`col${index * 3}`] = isNew ? item : item.value
          row[`col${index * 3}Id`] = item.id
        } else {
          row[`col${index}`] = isNew ? item : item.value
          row[`col${index}Id`] = item.id
        }
        IdList.push(item.id)
      })
      row.riskIndustry =
        i < 9 ? RISK_INDUSTRY[0] : i < 12 ? RISK_INDUSTRY[1] : RISK_INDUSTRY[i - 11]
      row.assetIndustry = i < 9 ? ASSET_INDUSTRY[Math.ceil((i - 1) / 3)] : '/'
      row.regional = i < 13 ? REGIONAL[(i - 1) % 3] : '/'
      row.id = uniqueId()
      tableList.push(row)
    }

    return tableList ?? []
  }
  const monthlyGuidanceData = getComponentData('MONTHLY_GUIDANCE', (res, oldKeyData) => {
    if (!res) return { newDetail: [], detail: [] }
    return {
      newDetail: formatGuidanceData(res?.[0], true),
      detail: formatGuidanceData(oldKeyData?.[0], false),
    }
  })

  const formatQuarterData = (res, isNew) => {
    const tableList = []
    // 循环取 row 1- 9 的数据
    for (let i = 1; i <= 9; i++) {
      const row = {}
      const data = (isNew ? (res?.[`row${i}`] ?? {})?.diffValueList : res?.[`row${i}`]) ?? []
      data?.forEach((item, index) => {
        row[`col${index}`] = isNew ? item : item.value
      })
      row.assetIndustry = i < 9 ? ASSET_INDUSTRY[Math.ceil((i - 1) / 3)] : '/'
      row.regional = i < 13 ? REGIONAL[(i - 1) % 3] : '/'
      row.id = uniqueId()
      tableList.push(row)
    }
    return tableList
  }
  const quarterGuidanceData = getComponentData('QUARTERLY_PRICING', (res, oldKeyData) => {
    if (!res) return { newDetail: [], detail: [] }

    return {
      newDetail: formatQuarterData(res?.[0], true),
      detail: formatQuarterData(oldKeyData?.[0], false),
    }
  })
  const monthlyGuidanceExt = getComponentData('MONTHLY_GUIDANCE_EXT')
  const defaultActiveKey = ['MONTHLY_DEDUCTION', 'MONTHLY_GUIDANCE', 'QUARTERLY_PRICING']
  const { month } = getQuery()
  const hasQuarter = [1, 4, 7, 10].includes(moment(month).month() + 1)
  return (
    <Page params={{ id }} store={store.page}>
      {Object.keys(compareData).length > 0 ? (
        <div className={styles.diffLog}>
          <h3>变更日志版本对比</h3>
          <Form store={form} initialValues={formData}>
            <Collapse defaultActiveKey={defaultActiveKey} accordion className={styles.collapse}>
              <Panel
                header={
                  <div>
                    <span>月度FTP 指导推导变更日志</span>
                    {moduleChanged.MONTHLY_DEDUCTION && (
                      <>
                        &nbsp;&nbsp;
                        <Badge color={'red'} />
                      </>
                    )}
                  </div>
                }
              >
                <CreditTermTable
                  detail={creditTermData.detail}
                  canEdit={false}
                  allModuleData={oldData}
                />
                <Form.Item name="MONTHLY_DEDUCTION">
                  <TextAreaEditable rows={12} />
                </Form.Item>
                <Divider orientation="left" plain>
                  变更之后
                </Divider>
                <CreditTermTable
                  detail={creditTermData.newDetail}
                  allModuleData={newData}
                  canEdit={false}
                  isFormApproval
                />
                <Form.Item name="MONTHLY_DEDUCTION_NEW">
                  <TextAreaEditable rows={12} />
                </Form.Item>
              </Panel>
              <Panel
                header={
                  <div>
                    <span>{'FTP 成本定价'}变更日志</span>
                    {moduleChanged.MONTHLY_GUIDANCE && (
                      <>
                        &nbsp;&nbsp;
                        <Badge color={'red'} />
                      </>
                    )}
                  </div>
                }
              >
                <>
                  <FtpGuideTable
                    // table={ftpGuideTable}
                    detail={monthlyGuidanceData.detail}
                  />
                  <FtpFormatTable detail={monthlyGuidanceExt.detail} />
                  <Form.Item name="MONTHLY_GUIDANCE">
                    <TextAreaEditable rows={4} />
                  </Form.Item>
                  <div className="z-sub-title" style={{ marginBottom: 12 }}>
                    补充说明
                  </div>
                  <Form.Item name="MONTHLY_SUPPLEMENT">
                    <TextAreaEditable rows={4} placeholder="请输入补充说明" />
                  </Form.Item>
                </>
                <Divider orientation="left" plain>
                  变更之后
                </Divider>
                <>
                  <FtpGuideTable detail={monthlyGuidanceData.newDetail} />
                  <FtpFormatTable detail={monthlyGuidanceExt.newDetail} canEdit={false} />
                  <Form.Item name="MONTHLY_GUIDANCE_NEW">
                    <TextAreaEditable isEdit={false} rows={4} />
                  </Form.Item>
                  <div className="z-sub-title" style={{ marginBottom: 12 }}>
                    补充说明
                  </div>
                  <Form.Item name="MONTHLY_SUPPLEMENT_NEW">
                    <TextAreaEditable isEdit={false} rows={4} placeholder="请输入补充说明" />
                  </Form.Item>
                </>
              </Panel>

              {hasQuarter && (
                <Panel
                  header={
                    <div>
                      <span>季度项目最低收益率变更日志</span>
                      {moduleChanged.QUARTERLY_PRICING && (
                        <>
                          &nbsp;&nbsp;
                          <Badge color={'red'} />
                        </>
                      )}
                    </div>
                  }
                >
                  <FormatTable
                    columns={QuarterColumn}
                    dataSource={quarterGuidanceData.detail}
                    editable={false}
                    rowKey="id"
                  />
                  <Form.Item name="QUARTERLY_PRICING">
                    <TextAreaEditable rows={6} />
                  </Form.Item>
                  <div className="z-sub-title" style={{ marginBottom: 12 }}>
                    补充说明
                  </div>
                  <Form.Item name="QUARTERLY_SUPPLEMENT">
                    <TextAreaEditable rows={4} />
                  </Form.Item>
                  <Divider orientation="left" plain>
                    变更之后
                  </Divider>
                  <FormatTable
                    columns={QuarterColumn}
                    dataSource={quarterGuidanceData.newDetail}
                    editable={false}
                    rowKey="id"
                  />
                  <Form.Item name="QUARTERLY_PRICING_NEW">
                    <TextAreaEditable rows={6} />
                  </Form.Item>
                  <div className="z-sub-title" style={{ marginBottom: 12 }}>
                    补充说明
                  </div>
                  <Form.Item name="QUARTERLY_SUPPLEMENT_NEW">
                    <TextAreaEditable rows={4} placeholder="请输入补充说明" />
                  </Form.Item>
                </Panel>
              )}
            </Collapse>
          </Form>
        </div>
      ) : (
        <Skeleton></Skeleton>
      )}
    </Page>
  )
}

export default observer(BudgetPricingBusinessDetailLogDiff)
