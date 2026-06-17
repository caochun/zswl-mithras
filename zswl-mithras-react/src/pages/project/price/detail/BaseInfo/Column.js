import {
  FiledFormat,
  getValue,
  TextAreaEditable,
  MatchOptionColumn,
  TextAreaColumn,
  InputColumn,
  FounderColumn,
} from '@/components/Format'
import { Select, App, Form } from '@zswl/components'
import { RegionCascader } from '@/components'
import { rules } from '@/utils'
import { uniqBy } from 'lodash'
import { BlackInfo } from '@/components/BlackGray/BlackInfo'
import { Input, Row, Button, Space } from 'antd'
import { PeopleListColumn } from '@/pages/project/review/detail/BaseInfo/Column'
import IconFont from '@/components/Icon'
import styles from './index.less'

export const SupplierColumn = ({
  dataIndex = 'supplierInfo',
  title = '供应商',
  requiredMark = true,
  ...rest
}) => {
  return {
    title,
    dataIndex,
    span: 2,
    requiredMark,
    editable: {
      element: (
        <Form.Item dependencies={['leaseTypes']} noStyle>
          {({ getFieldValue }) => {
            const leaseTypes = getFieldValue('leaseTypes')
            const hasZhiZhu = leaseTypes?.includes('zhi_zu')
            if (!hasZhiZhu) return
            const rule = [requiredMark ? rules.required() : undefined]
            return (
              <Form.List
                name={dataIndex}
                rules={[{ required: true, message: '请输入!' }]}
                initialValue={[{}]}
              >
                {(fields, { add, remove }) => {
                  return (
                    <>
                      <Row
                        key={0}
                        style={{
                          marginBottom: '8px',
                          display: 'block',
                        }}
                      >
                        <Form.Item
                          rules={[{ required: true, message: '请输入!' }]}
                          className={styles.addInput}
                          name={[0, 'clientName']}
                        >
                          <Input />
                        </Form.Item>
                        <Space className={styles.add}>
                          <IconFont type="icon-icon_add" />
                          <Button type="link" onClick={() => add({})} className={styles.add}>
                            添加供应商
                          </Button>
                        </Space>
                      </Row>
                      {fields?.map(({ key, name, ...restField }, index) => {
                        if (key === 0) {
                          return
                        }
                        return (
                          <Row
                            key={key}
                            style={{
                              display: 'flex',
                              alignItems: 'flex-start',
                              marginBottom: '8px',
                            }}
                          >
                            <Form.Item
                              rules={[{ required: true, message: '请输入!' }]}
                              {...restField}
                              name={[name, 'clientName']}
                            >
                              <Input />
                            </Form.Item>
                            <Button
                              type="link"
                              onClick={() => remove(name)}
                              className={styles.remove}
                            >
                              删除
                            </Button>
                          </Row>
                        )
                      })}
                    </>
                  )
                }}
              </Form.List>
            )
          }}
        </Form.Item>
      ),
    },
    render: (val, record) => {
      return record[dataIndex]?.map((item) => item.clientName)?.join(',') || '-'
    },
    ...rest,
  }
}

const ALL_COLUMNS = ({
  detail = {},
  store = {},
  onRegionalClassifyChange = () => {},
  form,
  onEvaluateMainChange = () => {},
} = {}) => {
  const userDisabled = ['NEW_UNDER_APPROVAL', 'CHANGING_UNDER_APPROVAL', 'UNDER_APPROVAL'].includes(
    detail.projReviewProcessStatus
  )
  return [
    InputColumn({
      title: '项目名称',
      dataIndex: 'projName',
      editable: false,
    }),
    InputColumn({
      title: '项目编号',
      dataIndex: 'projCode',
      editable: false,
    }),
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
    {
      title: '风控行业分类',
      dataIndex: 'riskControlIndustryClassify',
      matchOption: 'riskControlIndustryClassify',
      editable: false,
      render: (val) => (
        <FiledFormat title={App.matchOption('riskControlIndustryClassify', getValue(val))?.label} />
      ),
    },
    {
      title: '资金用途',
      dataIndex: 'fundsPurpose',
      requiredMark: true,
      editable: TextAreaEditable({}),
      render: (val) => <FiledFormat title={val} />,
    },
    {
      title: '转让方',
      dataIndex: 'assignor',
      render: (val) => <FiledFormat title={val} />,
    },
    {
      title: '项目来源',
      dataIndex: 'projSource',
      matchOption: 'projSourceType',
      requiredMark: true,
      editable: {
        rules: [rules.required()],
        element: <Select options="projSourceType"></Select>,
      },
      render: (val) => (
        <FiledFormat title={App.matchOption('projSourceType', getValue(val))?.label} />
      ),
    },
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
                  // <Form.Item name={'evaluationSubjectId'} rules={[rules.required('请选择')]}>
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
                  // </Form.Item>
                )
              }}
            </Form.Item>
          ),
        }
      },
      render: (val, { evaluationSubjectName, evaluationSubjectId }) => (
        <div style={{ display: 'flex', alignItems: 'center' }}>
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
          ></RegionCascader>
        ),
      },
      render: (val, { areaName }) => <FiledFormat title={areaName} />,
    },
    {
      title: '行业分类',
      dataIndex: 'projectClassify',
      matchOption: 'projectClassify',
      requiredMark: true,
      editable: {
        rules: [rules.required()],
        element: <Select options="projectClassify"></Select>,
      },
      render: (val) => (
        <FiledFormat title={App.matchOption('projectClassify', getValue(val))?.label} />
      ),
    },

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
    MatchOptionColumn({
      title: 'FTP 行业分类',
      dataIndex: 'ftpIndustryCategory',
      matchOption: 'ftpIndustryCategoryEnum',
      editable: {
        rules: [rules.required()],
        element: (
          <Select
            options="ftpIndustryCategoryEnum"
            onChange={(value) => {
              form.setFieldValue('regionalDivision', null)
              form.setFieldValue('projectManageLevel', null)
              form.setFieldValue('isAAA', null)
            }}
          ></Select>
        ),
      },
      requiredMark: true,
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

              const isDivision = ['FTP_STATE_OWNED_INDUSTRY'].includes(ftpIndustryCategory)
              const matchOption = isDivision
                ? 'projRegionalDivisionEnum'
                : 'projRegionalDivisionPublicAndCivilEnum'
              return (
                <Form.Item
                  name="regionalDivision"
                  rules={[!isOther ? rules.required() : undefined]}
                  hidden={isOther}
                >
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

    MatchOptionColumn({
      title: '项目管理层级',
      dataIndex: 'projectManageLevel',
      matchOption: 'projectManageLevelEnum',
      requiredMark: true,
      editable: () => {
        return (
          <Form.Item dependencies={['ftpIndustryCategory']} noStyle>
            {({ getFieldValue }) => {
              const ftpIndustryCategory = getFieldValue('ftpIndustryCategory')
              const needShow = ['FTP_PUBLIC_UTILITIES', 'FTP_CIVIL_CONSUMPTION'].includes(
                ftpIndustryCategory
              )
              if (!needShow) return
              return (
                <Form.Item name="projectManageLevel" rules={[rules.required()]}>
                  <Select options={'projectManageLevelEnum'} />
                </Form.Item>
              )
            }}
          </Form.Item>
        )
      },
    }),
    MatchOptionColumn({
      title: '是否AAA评级',
      rename: (
        <div>
          <div>是否AAA评级</div>
          <div style={{ color: 'red', fontSize: 12 }}>评级项目主体或担保主体</div>
        </div>
      ),
      dataIndex: 'isAAA',
      matchOption: 'yesOrNo',
      requiredMark: true,
      editable: () => {
        return (
          <Form.Item dependencies={['ftpIndustryCategory']} noStyle>
            {({ getFieldValue }) => {
              const ftpIndustryCategory = getFieldValue('ftpIndustryCategory')
              const needShow = ['FTP_PUBLIC_UTILITIES', 'FTP_CIVIL_CONSUMPTION'].includes(
                ftpIndustryCategory
              )
              if (!needShow) return null
              return (
                <Form.Item name="isAAA" rules={[rules.required()]}>
                  <Select options={'yesOrNo'} />
                </Form.Item>
              )
            }}
          </Form.Item>
        )
      },
    }),
    TextAreaColumn({
      title: '项目背景',
      dataIndex: 'projBackground',
      editable: true,
      requiredMark: true,
      required: true,
    }),
    {
      title: '备注',
      dataIndex: 'remark',
      span: 2,
      editable: TextAreaEditable({}),
      render: (val) => <FiledFormat title={val} hasToolTip={false} />,
    },
    PeopleListColumn({
      title: '承租人',
      dataIndex: 'lesseeInfo',
      requiredMark: true,
      noClientType: true,
    }),
    PeopleListColumn({
      title: '债权人',
      dataIndex: 'creditorInfo',
      noClientType: true,
      requiredMark: true,
    }),
    PeopleListColumn({ title: '债务人', dataIndex: 'debtorInfo', isDebtor: true }),
    PeopleListColumn({ title: '担保人', dataIndex: 'guaranteeInfo' }),
    PeopleListColumn({ title: '抵押人', dataIndex: 'mortgagorInfo' }),
    PeopleListColumn({ title: '质押人', dataIndex: 'pledgorInfo' }),
    SupplierColumn({ title: '供应商', dataIndex: 'supplierInfo' }),

    InputColumn({ title: '项目主办', dataIndex: 'projSponsorUserName' }),
    FounderColumn({
      title: '项目协办',
      width: 140,
      dataIndex: 'projCosponsorUserIds',
      mode: 'multiple',
      render: (val, { projCosponsorUserNames }) =>
        getValue(projCosponsorUserNames)?.join(',') || '-',
    }),
    InputColumn({ title: '业务部门', dataIndex: 'bizDeptName', editable: false }),
    InputColumn({ title: '业务部门负责人', dataIndex: 'bizDeptLeaderName', editable: false }),
    InputColumn({ title: '业务分管领导', dataIndex: 'bizDivisionLeaderName', editable: false }),
    FounderColumn({
      title: '风控经理',
      dataIndex: 'riskControlManagerId',
      requiredMark: true,
      renderField: 'riskControlManagerName',
      functionCode: 'selectfounder-5',
      disabled: userDisabled,
      params: { job: 'riskmanager' },
    }),
    FounderColumn({
      title: '法务经理',
      dataIndex: 'legalManagerUserId',
      requiredMark: true,
      renderField: 'legalManagerName',
      functionCode: 'selectfounder-5',
      params: { job: 'legalmanager' },
    }),
  ].filter(Boolean)
}

export default ALL_COLUMNS
