import { Form, Select } from '@zswl/components'
import { validatorBigThenZero } from '@/utils'
import IconFont from '@/components/Icon'
import { Col, Row, Space, Tag } from 'antd'
import { OrgSelect, FounderSelect } from '@/components/Select'
import { isObject } from 'lodash'
import styles from './index.less'
import { observer, getQuery } from '@zswl/admin'

import { AllocateTypeInfo, TagColor } from '../Context'


const { Item } = Form

function Index(props) {
  const isFormApproval = getQuery('typeId') == 'approval'

  // source: unDeal 未分配| deal 已分配 | adjust 调整
  const { listName, addText = '添加', value, source } = props
  const form = Form.useFormInstance()

  // 只有在 adjust（调整）场景中，可以增减表单行数
  const canEdit = true

  const AddButton = ({ add }) => (
    <div className={styles.add} onClick={() => add()}>
      <div
        className={styles.icon}
        style={{
          marginRight: 8,
        }}
      >
        <IconFont type="icon-icon_add" />
      </div>
      {addText}
    </div>
  )

  const onAllocateTypeChange = (name) => {
    form.setFieldValue([listName, name, 'weightTarget'], undefined)
    form.setFieldValue([listName, name, 'weightValue'], undefined)
  }
  const isDept = ['deptWeightInfoList', 'deptLaunchWeightInfoList'].includes(listName)
  return (
    <Form.List name={listName} initialValue={value}>
      {(fields, { add, remove }) => {
        return (
          <div style={{ width: '100%' }}>
            {fields?.map(({ key, name }, index) => {
              return (
                <Row
                  key={key}
                  gutter={12}
                  style={{ display: 'flex', alignItems: 'center', marginBottom: '8px' }}
                >
                  {!isDept && (
                    <Col span={5}>
                      <Item
                        name={[name, 'weightType']}
                        rules={[{ required: true, message: '请选择!' }]}
                        style={{ margin: 0 }}
                      >
                        <Select
                          disabled={!canEdit}
                          allowClear
                          onChange={() => onAllocateTypeChange(name)}
                          options={'kpiProjectWeightTypeEnum'}
                          placeholder="请选择"
                          style={{ width: '100%' }}
                        ></Select>
                      </Item>
                    </Col>
                  )}
                  <Item noStyle dependencies={[[listName, name, 'weightType']]}>
                    {({ getFieldValue }) => {
                      let weightType = getFieldValue([listName, name, 'weightType'])
                      if (isDept) {
                        weightType = 'BUSINESS_DEPT'
                      }
                      if (weightType) {
                        const typeInfo = AllocateTypeInfo[weightType] ?? {}

                        // const unDeal_otherDeptRecommend =
                        //   weightType === 'OTHER_DEPT_RECOMMEND' &&
                        //   ['unDeal', 'adjust'].includes(source)

                        const TargetSelect =
                          weightType === 'BUSINESS_DEPT' ? (
                            <OrgSelect
                              disabled={isDept ? false : !canEdit || isFormApproval}
                              placeholder={`请选择${typeInfo.allocatePlaceHold || ''}`}
                            />
                          ) : (
                            <FounderSelect
                              // 在 未分配（unDeal）场景中，跨部门推荐人 (OTHER_DEPT_RECOMMEND), 允许修改
                              disabled={!canEdit || isFormApproval}
                              placeholder={`请选择${typeInfo.allocatePlaceHold || ''}`}
                              // params={{
                              //   job: undefined,
                              // }}
                            />
                          )

                        return (
                          <>
                            <Col span={6}>
                              <Item
                                name={[name, 'weightTarget']}
                                rules={
                                  // !unDeal_otherDeptRecommend &&
                                  [{ required: true, message: '请选择!' }].filter(Boolean)
                                }
                                style={{ margin: 0 }}
                              >
                                {TargetSelect}
                              </Item>
                            </Col>
                            <Col span={8}>
                              <Item
                                label={typeInfo.ratioLable || '占比'}
                                name={[name, 'weightValue']}
                                rules={[
                                  {
                                    required: true,
                                    message: '请输入!',
                                  },
                                  validatorBigThenZero,
                                ].filter(Boolean)}
                                style={{ margin: 0 }}
                              >
                                <FormAmount
                                  style={{ width: '100%' }}
                                  min={0}
                                  max={100}
                                  addonAfter="%"
                                  // disabled={!['adjust', 'unDeal'].includes(source)}
                                />
                              </Item>
                            </Col>
                          </>
                        )
                      }
                    }}
                  </Item>
                  <Col>
                    <Row>
                      {canEdit && (
                        <Space>
                          {isDept ? (
                            value &&
                            !value[index]?.isBusinessDept && (
                              <div className={styles.add} onClick={() => remove(name)}>
                                <div className={styles.icon}>
                                  <IconFont type="icon-icon_delete" />
                                </div>
                              </div>
                            )
                          ) : (
                            <div className={styles.add} onClick={() => remove(name)}>
                              <div className={styles.icon}>
                                <IconFont type="icon-icon_delete" />
                              </div>
                            </div>
                          )}

                          {index === fields.length - 1 && <AddButton add={add}></AddButton>}
                        </Space>
                      )}
                    </Row>
                  </Col>
                </Row>
              )
            })}
            {!fields.length && <AddButton add={add}></AddButton>}
          </div>
        )
      }}
    </Form.List>
  )
}

const Content = ({ data, listName }) => {
  const isDept = ['deptWeightInfoList', 'deptLaunchWeightInfoList'].includes(listName)
  const ContentItem = data
    .filter((v) => (isObject(v.weightType) ? v.weightType.beforeValue : v.weightType))
    .map((item, index) => {
      return (
        <div key={index} style={{ display: 'flex' }}>
          {!isDept && (
            <span>
              <Tag
                color={
                  TagColor[
                    isObject(item.weightType) ? item.weightType.beforeValue : item.weightType
                  ]
                }
              >
                <FiledFormat
                  title={
                    isObject(item.weightTypeName)
                      ? item.weightTypeName.beforeValue
                      : item.weightTypeName
                  }
                ></FiledFormat>
              </Tag>
            </span>
          )}
          <span>
            <FiledFormat
              title={
                isObject(item.weightTargetName)
                  ? item.weightTargetName?.beforeValue
                  : item.weightTargetName
              }
              isChange={item.weightTargetNameRed || item.weightTargetName?.isChange}
            ></FiledFormat>
          </span>
          <span>：</span>
          <span>
            <FiledFormat
              unit="%"
              title={PureAmountFormat(
                isObject(item.weightValue) ? item.weightValue?.beforeValue : item.weightValue,
                ''
              )}
              isChange={item.weightValueRed || item.weightValue?.isChange}
            ></FiledFormat>
          </span>
          <span>；</span>
        </div>
      )
    })
  return (
    <Space wrap size={[0, 4]} direction="vertical">
      {ContentItem}
    </Space>
  )
}
Index.Detail = ({ value, listName }) => {
  if (!value || value?.length === 0) {
    return '-'
  }
  return <div className={styles.detail}>{<Content listName={listName} data={value}></Content>}</div>
}
export default observer(Index)
