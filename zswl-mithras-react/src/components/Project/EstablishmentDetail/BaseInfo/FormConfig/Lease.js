import { FormItemContent, StarDom } from '@/components/Form'
import { App, Select } from '@zswl/components'
import { Descriptions, Form, Input } from 'antd'
import FormListItem from '../../../FormListItem/ProjectFormListItem'
import styles from '../index.less'
import { observer } from '@zswl/admin'
import FounderSelect from '../../../FounderSelect'
import MultilineText from '../../../MultilineText'
import RegionCascader from '@/components/RegionCascader'
import { uniqBy } from 'lodash'
import Api from '@/api/project/projectEstablishmentDetail'
import ratItem from './ratItems'
import { getRiskControlIndustryClassifySelectOptions } from '@/utils'

import EvaluationSubject from './EvaluationSubject'
import SupplierInfo from './SupplierInfo'

const Lease = ({ showValue, form, detail, isLog, compareChangeList = [] }) => {
  const options = App.getData().optionsType
  const getKeyOptionsLabelMap = (key) => {
    const obj = {}
    if (options && options[key]) {
      options[key].forEach((item) => {
        const { label, value } = item
        obj[value] = label
      })
    }
    return obj
  }
  const getDetailValue = (key) => {
    if (isLog) {
      return detail[key]?.value
    }
    return detail[key]
  }
  const getDetailChange = (key) => {
    if (isLog) {
      return detail[key]?.isChange
    }
    if (compareChangeList.length > 0) {
      return compareChangeList.indexOf(key) > -1
    }
    return false
  }
  const labelRed = (val) => {
    return { color: val ? 'red' : undefined }
  }
  const isNormal = detail.approvalType === 'NORMAL'

  const onEvaluateMainChange = async (labelValue) => {
    if (!labelValue) return
    const res = await Api.postGetAddressByClientId({ clientId: labelValue.value })
    form.setFieldValue('area', [res.province, res.city, res.district])
  }
  return (
    <Descriptions
      title="基本信息"
      bordered
      column={2}
      labelStyle={{ background: '#F5F6FA' }}
      size={'small'}
      className={styles.des}
    >
      <Descriptions.Item label={'项目名称'} labelStyle={labelRed(getDetailChange('projName'))}>
        <FormItemContent
          isChange={getDetailChange('projName')}
          formContent={
            <Form.Item name="projName">
              <Input disabled />
            </Form.Item>
          }
          value={getDetailValue('projName')}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item label={'项目编号'} labelStyle={labelRed(getDetailChange('projCode'))}>
        <FormItemContent
          isChange={getDetailChange('projCode')}
          formContent={
            <Form.Item name="projCode">
              <Input disabled />
            </Form.Item>
          }
          value={getDetailValue('projCode')}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item label={'业务类型'} labelStyle={labelRed(getDetailChange('bizType'))}>
        <FormItemContent
          isChange={getDetailChange('bizType')}
          formContent={
            <Form.Item name="bizType">
              <Select disabled options={options.projEstablishBizType} />
            </Form.Item>
          }
          value={getKeyOptionsLabelMap('projEstablishBizType')[getDetailValue('bizType')]}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item label={'租赁类型'} labelStyle={labelRed(getDetailChange('leaseTypes'))}>
        <FormItemContent
          isChange={getDetailChange('leaseTypes')}
          formContent={
            <Form.Item name="leaseTypes">
              <Select placeholder="请选择租赁类型！" mode="multiple" options={options.leaseType} />
            </Form.Item>
          }
          value={getDetailValue('leaseTypes')
            ?.map((item) => getKeyOptionsLabelMap('leaseType')[item])
            ?.join(',')}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={<StarDom name={'项目来源'} />}
        labelStyle={labelRed(getDetailChange('projSource'))}
      >
        <FormItemContent
          isChange={getDetailChange('projSource')}
          formContent={
            <Form.Item name="projSource" rules={[{ required: true, message: '请选择项目来源!' }]}>
              <Select placeholder="请选择项目来源！" options={options.projSourceType} />
            </Form.Item>
          }
          value={getKeyOptionsLabelMap('projSourceType')[getDetailValue('projSource')]}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={<StarDom name={'评估主体'} />}
        labelStyle={labelRed(getDetailChange('evaluationSubjectId'))}
      >
        <EvaluationSubject
          showValue={showValue}
          onEvaluateMainChange={onEvaluateMainChange}
          getDetailValue={getDetailValue}
          getDetailChange={getDetailChange}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={<StarDom name={'评估主体区域'} />}
        labelStyle={labelRed(
          getDetailChange('province') || getDetailChange('city') || getDetailChange('district')
        )}
      >
        <FormItemContent
          isChange={
            getDetailChange('province') || getDetailChange('city') || getDetailChange('district')
          }
          formContent={
            <Form.Item name="area" rules={[{ required: true, message: '请选择!' }]}>
              <RegionCascader />
            </Form.Item>
          }
          value={getDetailValue('areaName')}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={<StarDom name={'风控行业分类'} />}
        labelStyle={labelRed(getDetailChange('riskControlIndustryClassify'))}
      >
        <FormItemContent
          isChange={getDetailChange('riskControlIndustryClassify')}
          formContent={
            <Form.Item
              noStyle
              shouldUpdate={(prev, cur) =>
                prev?.riskControlIndustryClassify !== cur?.riskControlIndustryClassify
              }
            >
              {() => (
                <Form.Item name="riskControlIndustryClassify" rules={[{ required: true, message: '请选择!' }]}>
                  <Select
                    options={getRiskControlIndustryClassifySelectOptions(
                      form.getFieldValue('riskControlIndustryClassify')
                    )}
                  ></Select>
                </Form.Item>
              )}
            </Form.Item>
          }
          value={getKeyOptionsLabelMap('riskControlIndustryClassify')[getDetailValue('riskControlIndustryClassify')]}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={<StarDom name={'地区分类'} />}
        labelStyle={labelRed(getDetailChange('regionalProjectClassify'))}
      >
        <FormItemContent
          isChange={getDetailChange('regionalProjectClassify')}
          formContent={
            <Form.Item
              name="regionalProjectClassify"
              rules={[{ required: true, message: '请选择!' }]}
            >
              <Select options={options.projRegionalClassify} />
            </Form.Item>
          }
          value={
            getKeyOptionsLabelMap('projRegionalClassify')[getDetailValue('regionalProjectClassify')]
          }
          showValue={showValue}
        />
      </Descriptions.Item>
      {ratItem({ detail, isLog })}
      <Descriptions.Item
        label={<StarDom name={'资金用途'} />}
        span={2}
        labelStyle={labelRed(getDetailChange('fundsPurpose'))}
      >
        <FormItemContent
          isChange={getDetailChange('fundsPurpose')}
          formContent={
            <Form.Item name="fundsPurpose" rules={[{ required: true, message: '请输入资金用途!' }]}>
              <Input placeholder="请输入资金用途！" />
            </Form.Item>
          }
          value={getDetailValue('fundsPurpose')}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={isNormal ? <StarDom name={'项目背景'} /> : '项目背景'}
        span={2}
        labelStyle={labelRed(getDetailChange('projBackground'))}
      >
        <FormItemContent
          isChange={getDetailChange('projBackground')}
          formContent={
            <Form.Item
              name="projBackground"
              rules={[{ required: isNormal, message: '请输入项目背景!' }]}
            >
              <Input.TextArea
                autoSize={{ minRows: 4, maxRows: 20 }}
                placeholder="请输入项目背景！"
              />
            </Form.Item>
          }
          value={<MultilineText content={getDetailValue('projBackground')} />}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item label={'备注'} span={2} labelStyle={labelRed(getDetailChange('remark'))}>
        <FormItemContent
          isChange={getDetailChange('remark')}
          formContent={
            <Form.Item name="remark">
              <Input.TextArea autoSize={{ minRows: 4, maxRows: 20 }} placeholder="请输入备注!" />
            </Form.Item>
          }
          value={<MultilineText content={getDetailValue('remark')} />}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={<StarDom name={'承租人'} />}
        span={2}
        labelStyle={labelRed(getDetailChange('lesseeInfo'))}
      >
        <FormItemContent
          isChange={getDetailChange('lesseeInfo')}
          formContent={
            <Form.List
              name="lesseeInfo"
              rules={[{ required: true, message: '请至少添加一组承租人!' }]}
            >
              {(fields, { add, remove }) => {
                return (
                  <FormListItem
                    fields={fields}
                    add={add}
                    form={form}
                    required
                    remove={remove}
                    scene="main"
                    addText="添加承租人"
                    fieldKey={'lesseeInfo'}
                    noClientType
                  />
                )
              }}
            </Form.List>
          }
          value={<FormListItem.Detail values={getDetailValue('lesseeInfo')} />}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={'担保人'}
        span={2}
        labelStyle={labelRed(getDetailChange('guaranteeInfo'))}
      >
        <FormItemContent
          isChange={getDetailChange('guaranteeInfo')}
          formContent={
            <Form.List name="guaranteeInfo">
              {(fields, { add, remove }) => {
                return (
                  <FormListItem
                    fields={fields}
                    fieldKey={'guaranteeInfo'}
                    form={form}
                    add={add}
                    remove={remove}
                    addText="添加担保人"
                  />
                )
              }}
            </Form.List>
          }
          value={<FormListItem.Detail values={getDetailValue('guaranteeInfo')} />}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={'抵押人'}
        span={2}
        labelStyle={labelRed(getDetailChange('mortgagorInfo'))}
      >
        <FormItemContent
          isChange={getDetailChange('mortgagorInfo')}
          formContent={
            <Form.List name="mortgagorInfo">
              {(fields, { add, remove }) => {
                return (
                  <FormListItem
                    fieldKey={'mortgagorInfo'}
                    fields={fields}
                    form={form}
                    add={add}
                    remove={remove}
                    addText="添加抵押人"
                  />
                )
              }}
            </Form.List>
          }
          value={<FormListItem.Detail values={getDetailValue('mortgagorInfo')} />}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={'质押人'}
        span={2}
        labelStyle={labelRed(getDetailChange('pledgorInfo'))}
      >
        <FormItemContent
          isChange={getDetailChange('pledgorInfo')}
          formContent={
            <Form.List name="pledgorInfo">
              {(fields, { add, remove }) => {
                return (
                  <FormListItem
                    fieldKey={'pledgorInfo'}
                    fields={fields}
                    add={add}
                    form={form}
                    remove={remove}
                    addText="添加质押人"
                  />
                )
              }}
            </Form.List>
          }
          value={<FormListItem.Detail values={getDetailValue('pledgorInfo')} />}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={<StarDom name={'供应商'} />}
        span={2}
        labelStyle={labelRed(getDetailChange('supplierInfo'))}
      >
        <SupplierInfo
          showValue={showValue}
          getDetailValue={getDetailValue}
          getDetailChange={getDetailChange}
        />
      </Descriptions.Item>

      <Descriptions.Item
        label={'项目主办'}
        labelStyle={labelRed(getDetailChange('projSponsorUserId'))}
      >
        <FormItemContent
          isChange={getDetailChange('projSponsorUserId')}
          formContent={
            <Form.Item name="projSponsorUserId">
              <FounderSelect
                disabled
                placeholder="请选择项目主办！"
                queryParams={{ job: 'projmanager' }}
              />
            </Form.Item>
          }
          value={getDetailValue('projSponsorUserId')?.label}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={'项目协办'}
        labelStyle={labelRed(getDetailChange('projCosponsorUserIds'))}
      >
        <FormItemContent
          isChange={getDetailChange('projCosponsorUserIds')}
          formContent={
            <Form.Item name="projCosponsorUserIds">
              <FounderSelect
                placeholder="请选择项目协办！"
                mode="multiple"
                queryParams={{ job: 'projmanager', sameDept: false }}
              />
            </Form.Item>
          }
          value={getDetailValue('projCosponsorUserIds')
            ?.map((item) => item.label)
            .join(',')}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item label={'业务部门'} labelStyle={labelRed(getDetailChange('bizDeptName'))}>
        <FormItemContent
          isChange={getDetailChange('bizDeptName')}
          formContent={
            <Form.Item name="bizDeptName">
              <Input disabled />
            </Form.Item>
          }
          value={getDetailValue('bizDeptName')}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={'业务部门负责人'}
        labelStyle={labelRed(getDetailChange('bizDeptLeaderName'))}
      >
        <FormItemContent
          isChange={getDetailChange('bizDeptLeaderName')}
          formContent={
            <Form.Item name="bizDeptLeaderName">
              <Input disabled />
            </Form.Item>
          }
          value={getDetailValue('bizDeptLeaderName')}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={'业务分管领导'}
        labelStyle={labelRed(getDetailChange('bizDivisionLeaderName'))}
      >
        <FormItemContent
          isChange={getDetailChange('bizDivisionLeaderName')}
          formContent={
            <Form.Item name="bizDivisionLeaderName">
              <Input disabled />
            </Form.Item>
          }
          value={getDetailValue('bizDivisionLeaderName')}
          showValue={showValue}
        />
      </Descriptions.Item>
      <Descriptions.Item
        label={'风控经理'}
        // label={!isNormal ? <StarDom name="风控经理" /> : '风控经理'}
        labelStyle={labelRed(getDetailChange('riskControlManagerId'))}
      >
        <FormItemContent
          isChange={getDetailChange('riskControlManagerId')}
          formContent={
            <Form.Item
              name="riskControlManagerId"
              // rules={[{ required: !isNormal, message: '请选择风控经理!' }]}
            >
              <FounderSelect
                disabled
                mode="multiple"
                placeholder="请选择风控经理！"
                queryParams={{ job: 'riskmanager', sameDept: false }}
              />
            </Form.Item>
          }
          // value={getDetailValue('riskControlManagerId')?.label}
          value={getDetailValue('riskControlManagerId')
            ?.map((item) => item.label)
            .join(',')}
          showValue={showValue}
        />
      </Descriptions.Item>
    </Descriptions>
  )
}

export default observer(Lease)
