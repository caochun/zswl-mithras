import { OrgSelect, FounderSelect } from '@/components'
import { Select, SearchBar } from '@zswl/components'
import { DatePicker, Row, Col, Input } from 'antd'
import moment from 'moment'

const { Item } = SearchBar

export const PersonItem = ({ searchForm }) => {
  const onTypeChange = () => {
    searchForm.setFieldValue('divideTargetId', undefined)
  }
  return (
    <Item col={12}>
      <Row>
        <Col span={12}>
          <Item name="divideType" label="人员/部门池">
            <Select
              options={'kpiProjectWeightTypeEnum'}
              allowClear={false}
              onChange={onTypeChange}
            ></Select>
          </Item>
        </Col>
        <Col span={12}>
          <Item noStyle dependencies={['divideType']}>
            {({ getFieldValue }) => {
              const divideType = getFieldValue('divideType')
              if (divideType) {
                return (
                  <Item name="divideTargetId">
                    {divideType === 'BUSINESS_DEPT' ? (
                      <OrgSelect style={{ width: '100%' }} />
                    ) : (
                      <FounderSelect style={{ width: '100%' }} />
                    )}
                  </Item>
                )
              }
              return null
            }}
          </Item>
        </Col>
      </Row>
    </Item>
  )
}

export const DepartMentalItem = () => {
  return (
    <Item name="deptId" label="考核部门">
      <OrgSelect functionCode="kpiEstimationOrgSelect" />
    </Item>
  )
}

export const ProjectClassItem = () => {
  return (
    <Item name="class" label="项目类别">
      <Select options="yesorno" />
    </Item>
  )
}

export const ContractCodeItem = () => {
  return (
    <Item name="contractCode" label="合同编号">
      <Input />
    </Item>
  )
}

export const ProjNameItem = () => {
  return (
    <Item name="projName" label="项目名称">
      <Input />
    </Item>
  )
}

export const CalculateDateItem = () => {
  return (
    <Item
      name="calculateDate"
      label="核算月份"
      transform={(val) => {
        const calculateDate = val && moment(val).format('yyyy-MM')
        const [calculateDateYear, calculateDateMonth] =
          (calculateDate && calculateDate.split('-')) || []
        return {
          calculateDate: undefined,
          calculateDateMonth: calculateDateMonth && parseInt(calculateDateMonth),
          calculateDateYear,
        }
      }}
    >
      <DatePicker picker="month" style={{ width: '100%' }} />
    </Item>
  )
}
