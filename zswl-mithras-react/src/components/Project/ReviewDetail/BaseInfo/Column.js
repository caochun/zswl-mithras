import {
  FiledFormat,
  getValue,
  InputColumn,
  MatchOptionColumn,
  TextAreaColumn,
} from '@/components/Format'
import { Select, App, Form, Button } from '@zswl/components'
import { FounderSelect } from '@/components'
import { RegionCascader } from '@/components'
import { rules } from '@/utils'
import { uniqBy } from 'lodash'
import { history } from '@zswl/admin'
import { BlackInfo } from '@/components/BlackGray/BlackInfo'

import { PeopleListColumn, SupplierColumn } from '@/components/Project/BaseInfoColumns'
const ALL_COLUMNS = ({
  detail = {},
  store = {},
  form,
  onRegionalClassifyChange = () => {},
  onEvaluateMainChange = () => {},
} = {}) => {
  const userDisabled = ['NEW_UNDER_APPROVAL', 'CHANGING_UNDER_APPROVAL', 'UNDER_APPROVAL'].includes(
    detail.projReviewProcessStatus
  )
  return [
    InputColumn({ title: '项目名称', dataIndex: 'projName' }),
    InputColumn({ title: '项目编号', dataIndex: 'projCode', editable: false }),
    MatchOptionColumn({
      title: '业务类型',
      dataIndex: 'bizType',
      editable: false,
      matchOption: 'projEstablishBizType',
    }),
    {
      title: '租赁类型',
      dataIndex: 'leaseTypes',
      requiredMark: true,
      editable: {
        rules: [rules.required()],
        element: <Select options="leaseType" mode="multiple"></Select>,
      },
      render: (val) => {
        const leaseTypes = getValue(val)?.filter(Boolean) ?? []
        return leaseTypes.map((v) => App.matchOption('leaseType', v)?.label)?.join('、')
      },
    },
    // {
    //   title: '风控行业分类',
    //   dataIndex: 'riskControlIndustryClassify',
    //   matchOption: 'riskControlIndustryClassify',
    //   requiredMark: true,
    //   editable: {
    //     rules: [rules.required()],
    //     element: <Select options="riskControlIndustryClassify" mode="multiple"></Select>,
    //   },
    //   render: (val) => {
    //     <FiledFormat title={App.matchOption('riskControlIndustryClassify', val).label} />
    //   },
    // },
    {
      title: '保理类型',
      dataIndex: 'factoringTypes',
      requiredMark: true,
      editable: {
        rules: [rules.required()],
        element: <Select options="factoringType" mode="multiple"></Select>,
      },
      render: (val) => {
        return getValue(val)
          ?.map((v) => App.matchOption('factoringType', v)?.label)
          ?.join('、')
      },
    },
    {
      title: '转让类型',
      dataIndex: 'zrTypes',
      requiredMark: true,
      editable: {
        rules: [rules.required()],
        element: <Select options="zrType" mode="multiple"></Select>,
      },
      render: (val) => {
        return getValue(val)
          ?.map((v) => App.matchOption('zrType', v)?.label)
          ?.join('、')
      },
    },
    MatchOptionColumn({
      title: '风控行业分类',
      dataIndex: 'riskControlIndustryClassify',
      matchOption: 'riskControlIndustryClassify',
      editable: false,
    }),
    TextAreaColumn({
      title: '资金用途',
      dataIndex: 'fundsPurpose',
      requiredMark: true,
    }),
    InputColumn({ title: '转让方', dataIndex: 'assignor' }),
    MatchOptionColumn({
      title: '项目来源',
      dataIndex: 'projSource',
      matchOption: 'projSourceType',
      requiredMark: true,
    }),
    {
      title: '评估主体',
      dataIndex: 'evaluationSubjectId',
      requiredMark: true,
      editable: ({ bizType }) => {
        return {
          element: (
            <Form.Item noStyle dependencies={['lesseeInfo', 'guaranteeInfo', 'creditorInfo']}>
              {({ getFieldValue, setFieldValue, getFieldsValue }) => {
                const lesseeInfoValue = getFieldValue('lesseeInfo') || getFieldValue('creditorInfo')
                const guaranteeInfoValue = getFieldValue('guaranteeInfo')
                const result = [lesseeInfoValue, guaranteeInfoValue].flat().map((item) => {
                  if (item?.clientId) {
                    return { ...item.clientId }
                  }
                })
                const selectOpt = uniqBy(result.filter(Boolean), 'value')
                const evaluateMainValue = getFieldValue('evaluationSubjectId')
                if (
                  evaluateMainValue &&
                  (selectOpt?.length === 0 ||
                    !selectOpt?.find((item) => item.value == evaluateMainValue.value))
                ) {
                  setFieldValue('evaluationSubjectId', undefined)
                }

                return (
                  <Select
                    allowClear
                    value={store.evaluationSubjectIdValue}
                    onChange={(value) => {
                      store.setEvaluationSubjectIdValue(value)
                      onEvaluateMainChange(value)
                    }}
                    options={selectOpt}
                    placeholder="请选择"
                    notFoundContent={`请选择${
                      ['ZZ', 'ZL'].includes(bizType) ? '承租人' : '债权人'
                    }或担保人`}
                  />
                )
              }}
            </Form.Item>
          ),
        }
      },
      render: (val, { evaluationSubjectName, evaluationSubjectId }) => (
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <FiledFormat title={evaluationSubjectName} />
          <BlackInfo
            params={{ clientId: evaluationSubjectId?.value ?? evaluationSubjectId }}
            style={{ marginLeft: 4 }}
          />
        </div>
      ),
    },
    {
      title: '评估主体区域',
      dataIndex: 'area',
      requiredMark: true,
      editable: {
        rules: [rules.required('请选择')],
        element: (
          <RegionCascader
            value={[detail.province, detail.city, detail.district].filter((item) => item)}
          />
        ),
      },
      render: (val, { areaName }) => <FiledFormat title={areaName} />,
    },
    MatchOptionColumn({
      title: '行业分类',
      dataIndex: 'projectClassify',
      matchOption: 'projectClassify',
      requiredMark: true,
    }),

    {
      title: '地区分类',
      dataIndex: 'regionalProjectClassify',
      matchOption: 'projRegionalClassify',
      requiredMark: true,
      editable: {
        rules: [rules.required()],
        element: (
          <Select options="projRegionalClassify" onChange={onRegionalClassifyChange}></Select>
        ),
      },
      render: (val) => (
        <FiledFormat title={App.matchOption('projRegionalClassify', getValue(val))?.label} />
      ),
    },

    {
      title: '评估主体评级',
      dataIndex: 'ratingFinalScore',
      render: (val, { ratingFinalScore, ratingClientId }) => {
        const goRat = () => {
          history.push(`/customer/customerRat/detail/${ratingClientId}?canEditFlags=false`)
        }
        return (
          <div
            style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              width: '100%',
            }}
          >
            <span>{ratingFinalScore}</span>
            {ratingClientId && (
              <Button type="link" onClick={goRat}>
                评级报告
              </Button>
            )}
          </div>
        )
      },
      editable: false,
    },
    {
      title: '债项评级参考额度（万元）',
      dataIndex: 'ratingQuota',
      render: (val, { ratingQuota, ratingAmountId }) => {
        const goRat = () => {
          history.push(`/customer/debtRat/detail/${ratingAmountId}?canEditFlags=false`)
        }
        return (
          <div
            style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              width: '100%',
            }}
          >
            <span>{ratingQuota}</span>
            {ratingAmountId && (
              <Button type="link" onClick={goRat}>
                评级报告
              </Button>
            )}
          </div>
        )
      },
      editable: false,
    },
    MatchOptionColumn({
      title: 'FTP 行业分类',
      dataIndex: 'ftpIndustryCategory',
      matchOption: 'ftpIndustryCategoryEnum',
      requiredMark: true,
      editable: {
        rules: [rules.required()],
        element: (
          <Select
            options="ftpIndustryCategoryEnum"
            onChange={(value) => {
              form.setFieldValue('regionalDivision', null)
            }}
          ></Select>
        ),
      },
    }),
    {
      title: '区域划分',
      dataIndex: 'regionalDivision',
      requiredMark: true,
      editable: () => {
        return (
          <Form.Item dependencies={['ftpIndustryCategory']} noStyle>
            {({ getFieldValue }) => {
              const ftpIndustryCategory = getFieldValue('ftpIndustryCategory')
              const isOther = ['FTP_OTHER_INDUSTRY'].includes(ftpIndustryCategory)
              if (isOther) return
              const isDivision = ['FTP_STATE_OWNED_INDUSTRY'].includes(ftpIndustryCategory)
              const matchOption = isDivision
                ? 'projRegionalDivisionEnum'
                : 'projRegionalDivisionPublicAndCivilEnum'
              return (
                <Form.Item name="regionalDivision" rules={[rules.required()]}>
                  <Select options={matchOption}></Select>
                </Form.Item>
              )
            }}
          </Form.Item>
        )
      },

      render: (val, { ftpIndustryCategory }) => {
        const isDivision = ['FTP_STATE_OWNED_INDUSTRY'].includes(ftpIndustryCategory)
        const matchOption = isDivision
          ? 'projRegionalDivisionEnum'
          : 'projRegionalDivisionPublicAndCivilEnum'
        return <FiledFormat title={App.matchOption(matchOption, getValue(val))?.label} />
      },
    },
    TextAreaColumn({
      title: '项目背景',
      dataIndex: 'projBackground',
      requiredMark: true,
    }),
    TextAreaColumn({ title: '备注', dataIndex: 'remark' }),

    PeopleListColumn({
      title: '承租人',
      dataIndex: 'lesseeInfo',
      noClientType: true,
      requiredMark: true,
      scene: 'main',
    }),

    PeopleListColumn({
      title: '债权人',
      dataIndex: 'creditorInfo',

      noClientType: true,
      requiredMark: true,
    }),
    PeopleListColumn({
      title: '债务人',
      dataIndex: 'debtorInfo',
      isDebtor: true,
      requiredMark: true,
    }),
    PeopleListColumn({ title: '担保人', dataIndex: 'guaranteeInfo' }),
    PeopleListColumn({ title: '抵押人', dataIndex: 'mortgagorInfo' }),
    PeopleListColumn({ title: '质押人', dataIndex: 'pledgorInfo' }),
    SupplierColumn({}),

    {
      title: '项目主办',
      dataIndex: 'projSponsorUserId',
      editable: false,
      render: (val, { projSponsorUserName }) => <FiledFormat title={projSponsorUserName} />,
    },
    {
      title: '项目协办',
      width: 140,
      dataIndex: 'projCosponsorUserIds',
      mode: 'multiple',
      editable: {
        element: <FounderSelect mode="multiple" functionCode="selectfounder-5" />,
      },
      render: (val, { projCosponsorUserNames }) =>
        getValue(projCosponsorUserNames)?.join(',') || '-',
    },
    InputColumn({ dataIndex: 'bizDeptName', title: '业务部门' }),
    InputColumn({ dataIndex: 'bizDeptLeaderName', title: '业务部门负责人' }),
    InputColumn({ dataIndex: 'bizDivisionLeaderName', title: '业务分管领导' }),
    {
      title: '风控经理',
      dataIndex: 'riskControlManagerId',
      requiredMark: true,
      editable: {
        rules: [rules.required()],
        element: (
          <FounderSelect
            params={{ job: 'riskmanager' }}
            functionCode="selectfounder-5"
            disabled={userDisabled}
          />
        ),
      },
      render: (val, { riskControlManagerName }) => <FiledFormat title={riskControlManagerName} />,
    },
    {
      title: '法务经理',
      dataIndex: 'legalManagerUserId',
      requiredMark: true,
      editable: {
        rules: [rules.required()],
        element: (
          <FounderSelect
            params={{ job: 'legalmanager' }}
            functionCode="selectfounder-5"
            disabled={userDisabled}
          />
        ),
      },
      render: (val, { legalManagerName }) => <FiledFormat title={legalManagerName} />,
    },
  ].filter(Boolean)
}
export default ALL_COLUMNS
