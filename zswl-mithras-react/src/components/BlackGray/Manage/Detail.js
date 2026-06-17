import { Page, Button, App, FormStore, PageStore, Descriptions } from '@zswl/components'
import { history, makeAutoObservable, observer } from '@zswl/admin'
import { SubmitAuditAction, ApprovalRecordAction } from '@/components/RiskActions'
import LoginInfo from './LoginInfo'
import EnterForm from './EnterForm'
import { useMemo } from 'react'
import recordTableApi from '@/api/blackList/recordTableApi'
import { message } from 'antd'
import queryExternalDataApi from '@/api/blackList/queryExternalDataApi'
import approvalControlApi from '@/api/blackList/approvalControlApi'
import { getDescColumns, isUnifiedCreditCode } from '@/utils'
import moment from 'moment'
import warehouseRuleApi from '@/api/blackList/warehouseRuleApi'
import ALl_COLUMNS from '@/components/BlackGray/Columns'

const columns = getDescColumns(
  ALl_COLUMNS,
  [
    '申请人',
    '申请时间',
    '报告机构',
    '企业名称',
    '统一社会信用代码',
    '业务类型',
    '申请原因描述',
    '黑灰标识',
    '业务规模（万元）',
    '观察期',
    { title: '申请原因', rename: '入库原因' },
    '入库时间',
    '附件',
    '审批状态',
  ].filter(Boolean)
)
class Store {
  constructor({ source }) {
    makeAutoObservable(this)
    this.source = source
  }
  applyReasonOptions = []
  page = new PageStore({
    request: async (params) => {
      const { orgRolesName } = App.getData().user

      const res = await warehouseRuleApi.postConfigList({
        pageSize: 999,
        page: 1,
        source: 'EXTERNAL_APPROVAL',
        suitOrg: orgRolesName[0]?.orgCode,
        status: 1,
      })
      this.applyReasonOptions = res.list
      if (!params?.id) return {}
      const {
        warehouseFileKeys: keys,
        enterpriseName,
        unifiedSocialCreditCode,
        warehouseTime,
        applyReasonType,
        ...rest
      } = await recordTableApi.postRecordDetail({
        blackGrayRecordId: params?.id,
      })

      const warehouseFileKeys = await App.queryFileData(keys)
      this.enterpriseNameChange({ value: unifiedSocialCreditCode, label: enterpriseName })
      return {
        warehouseFileKeys,
        enterpriseName,
        unifiedSocialCreditCode,
        applyReasonType: applyReasonType?.[0],
        warehouseTime: warehouseTime && moment(warehouseTime),
        ...rest,
      }
    },
  })
  getEnterpriseName = async (searchValue) => {
    const params = {}

    if (!isUnifiedCreditCode(searchValue)) {
      params.enterpriseName = searchValue
    } else {
      params.unifiedSocialCreditCode = searchValue
    }

    return await queryExternalDataApi.postVagueEnterprise(params).then((res) =>
      res.map((item) => ({
        label: item.enterpriseName,
        value: item.unifiedSocialCreditCode,
      }))
    )
  }
  form = new FormStore({})
  save = async (e, isSubmit = false) => {
    const { id } = this.page.getParams()
    const params = await this.form.submit()
    const func = id ? recordTableApi.postRecordModify : recordTableApi.postRecordAdd
    const recordId = await func({ ...params, id, source: this.source })
    if (!isSubmit) {
      message.success(`保存成功`)
      history.goBack()
    }
    return recordId
  }
  businessList = []
  getBusiness = async () => {
    const res = await recordTableApi.postTypeList({})
    this.businessList = res
  }
  isAffiliated
  enterpriseName
  enterpriseNameChange = async (params) => {
    if (!params) {
      this.form.setFieldsValue({
        unifiedSocialCreditCode: undefined,
      })
      return
    }
    const { value, label } = params
    this.form.setFieldsValue({
      unifiedSocialCreditCode: value,
    })
    this.enterpriseName = label
    const { groupEnterpriseName, isAffiliated } =
      await queryExternalDataApi.postAffiliatedEnterprise({
        unifiedSocialCreditCode: value,
        enterpriseName: label,
      })
    this.isAffiliated = isAffiliated
    // const values = this.form.getFieldsValue
    this.form.setFieldsValue({
      membershipGroup: groupEnterpriseName,
    })
  }
  businessTypeChange = async (value) => {
    this.form.setFieldValue('applyReasonType', [])
  }
  blackGrayTypeChange = (value) => {
    const periodUnderObservation = value === 'BLACK_LIST' ? '12' : '6'
    this.form.setFieldValue('periodUnderObservation', periodUnderObservation)
    this.businessTypeChange()
  }
  submit = async (values) => {
    const recordId = await this.save({}, true)
    const { id } = this.page.getParams()
    await approvalControlApi.postWarehouseSubmit({ ...values, id: id ?? recordId })
    message.success(`提交成功`)
    history.goBack()
  }
}

function Id({ params, source = 'INTERNAL_APPROVAL', query }) {
  const { view } = query
  const store = useMemo(() => new Store({ source }), [source])
  const detail = store.page.getData()

  const { auditTaskId } = detail

  const current = params.id ? (view ? '查看详情' : '修改') : '新增'
  return (
    <Page
      current={current}
      params={params}
      store={store.page}
      header={{
        extra: [
          !view && (
            <SubmitAuditAction
              form={store.form}
              key="submit"
              params={{ modelKey: 'BLACK_GRAY_WAREHOUSE', taskIds: [] }}
              onSubmit={store.submit}
            />
          ),
          !view && (
            <Button.Save onClick={store.save} key="save">
              保存
            </Button.Save>
          ),

          auditTaskId && (
            <ApprovalRecordAction
              key="history"
              params={{
                taskId: [auditTaskId],
                bizCode: 'blackGrayFlow',
                bizId: params.id,
              }}
            />
          ),
        ],
      }}
    >
      {view ? (
        <Descriptions
          items={columns}
          dataSource={detail}
          editable={false}
          labelStyle={{ width: '160px' }}
          contentStyle={{ width: 230 }}
        />
      ) : (
        <EnterForm detail={detail} store={store} applyReasonOptions={store.applyReasonOptions} />
      )}

      <LoginInfo />
    </Page>
  )
}

export default observer(Id)
