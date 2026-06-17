import { SubmitAuditAction, ApprovalRecordAction } from '@/components/RiskActions'
import LoginInfo from '@/pages/blackListManage/components/LoginInfo'
import EnterDesc from '@/pages/blackListManage/components/EnterDesc'
import BreakForm from '@/pages/blackListManage/components/BreakForm'
import { FormStore, PageStore, Page, Button, App, Table } from '@zswl/components'
import { history, makeAutoObservable, observer } from '@zswl/admin'
import { message } from 'antd'
import approvalControlApi from '@/api/blackList/approvalControlApi'
import listLibraryApi from '@/api/blackList/listLibraryApi'
import manualOutboundFormApi from '@/api/blackList/manualOutboundFormApi'
import { getTableColumns, isUnifiedCreditCode } from '@/utils'
import { useMemo } from 'react'
import approvalBreakthroughApi from '@/api/blackList/approvalBreakthroughApi'
import ALl_COLUMNS from '@/components/BlackGray/Columns'
import { saveServer } from '@/utils'

class Store {
  constructor({ type, path } = {}) {
    this.type = type
    this.path = path
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: async (params) => {
      if (!params?.id) return {}
      let res
      if (this.type === 'outbound') {
        res = await manualOutboundFormApi.postOutboundDetail({ id: +params?.id })
      } else {
        res = await approvalBreakthroughApi.postBusinessDetail({ id: +params?.id })
      }
      this.businessList = await listLibraryApi.postBreakBusiness({
        unifiedSocialCreditCode: res.unifiedSocialCreditCode,
      })
      const { applyFileKeys: keys, ...rest } = res
      // const applyFileKeys = await App.queryFileData(keys)
      this.tableData = res.applyReason
      return { ...rest }
    },
  })
  getEnterpriseName = async (searchValue) => {
    if (!searchValue) return []
    const params = { reduceStatus: 1, pageSize: 100 }

    if (!isUnifiedCreditCode(searchValue)) {
      params.enterpriseName = searchValue
    } else {
      params.unifiedSocialCreditCode = searchValue
    }

    return await listLibraryApi.postOrgList(params).then((res) => {
      const list = (res?.list ?? []).map((item) => ({
        label: item.enterpriseName,
        value: item.unifiedSocialCreditCode,
      }))
      return list
    })
  }
  form = new FormStore({})
  blackGrayId
  recordId
  save = async (e, isSubmit = false) => {
    const { id } = this.page.getParams()
    const params = await this.form.submit()
    params.applyReason = this.tableData.map((v) => ({
      ...v,
      status: 1,
    }))
    let func
    if (this.type === 'outbound') {
      func = id ? manualOutboundFormApi.postOutboundModify : manualOutboundFormApi.postOutboundAdd
    } else {
      func = id
        ? approvalBreakthroughApi.postBusinessModify
        : approvalBreakthroughApi.postBusinessAdd
    }
    const recordId = await func({ ...params, blackGrayId: this.blackGrayId, id })
    if (!isSubmit) {
      message.success(`保存成功`)
      history.goBack()
    }
    this.recordId = recordId
    return recordId
  }
  businessList = []

  setBusinessList = async (code) => {
    if (code) {
      this.businessList = await listLibraryApi.postBreakBusiness({ unifiedSocialCreditCode: code })
    } else {
      this.businessList = []
    }
  }
  enterpriseNameChange = async ({ value: unifiedSocialCreditCode } = {}) => {
    await this.setBusinessList(unifiedSocialCreditCode)
    this.form.setFieldsValue({
      unifiedSocialCreditCode,
      businessType: undefined,
      proposedBusinessType: undefined,
    })
    this.tableData = []
  }
  tableData = []
  businessTypeChange = async (value) => {
    const { enterpriseName, unifiedSocialCreditCode } = this.form.getFieldsValue()
    const params = {
      enterpriseName: enterpriseName.label,
      unifiedSocialCreditCode,
      businessType: value,
    }
    const { list } = await listLibraryApi.postOrgList(params)

    this.tableData = list.map((v) => ({
      ...v,
      applyReasonName: v?.applyReasonName?.join('、'),
      applyReasonType: JSON.stringify(v.applyReasonType),
    }))
  }
  submit = async (values) => {
    const saveId = await this.save({}, true)
    const { id } = this.page.getParams()
    const func =
      this.type === 'outbound'
        ? approvalControlApi.postManualOutbound
        : approvalControlApi.postBreakBusiness
    await func({ ...values, id: id ?? saveId })
    message.success(`提交成功`)
    history.goBack()
  }
}

export const EnterTable = ({ dataSource = [] }) => {
  return (
    <Table
      columnsFilter={'blackListManage_components_BreakDetail'}
              onFilter={(key,val) => saveServer('blackListManage_components_BreakDetail',val)}
      
      columns={getTableColumns(ALl_COLUMNS, [
        '黑灰标识',
        {
          title: '申请原因',
          rename: '入库原因',
          dataIndex: 'applyReasonName',
        },
        '入库时间',
        '计划出库时间',
        '是否报送金控',
        { title: '业务规模（万元）', render: (val) => val },
      ])}
      dataSource={dataSource}
    />
  )
}
function Id({ params, path, query, type = 'outbound' }) {
  const { view } = query

  const store = useMemo(() => new Store({ type, path }), [type])
  const detail = store.page.getData()
  const { recordId } = store

  const { auditTaskId } = detail
  const modelKey = type === 'outbound' ? 'BLACK_GRAY_MANUAL_OUTBOUND' : 'BLACK_GRAY_BUSINESS_BREAK'
  const current = params.id ? (view ? '查看详情' : '修改') : '新增'
  return (
    <Page
      current={current}
      params={{ ...params, recordId }}
      store={store}
      header={{
        extra: [
          !view && (
            <Button.Save onClick={store.save} key="save">
              保存
            </Button.Save>
          ),

          !view && (
            <Button.Submit
              type="primary"
              onClick={() => store.submit()}
              disabled={store.tableData.length === 0}
              key="submit"
            />
          ),
        ],
      }}
    >
      {view ? (
        <EnterDesc detail={detail} type={type} />
      ) : (
        <BreakForm initialValues={detail} store={store} type={type} tableData={store.tableData} />
      )}

      <LoginInfo />
    </Page>
  )
}

export default observer(Id)
