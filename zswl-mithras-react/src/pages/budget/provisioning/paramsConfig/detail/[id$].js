import { observer, getQuery, history } from '@zswl/admin'
import Store from './store'
import { Page, Table, Button, Form, Select, Access } from '@zswl/components'
import { Space, InputNumber } from 'antd'
import styles from './style.less'
import { AmountColumn, MatchOptionColumn } from '@/components/Format'
import { useMemo } from 'react'
import mathjs from '@/utils/math'

export const getColumnsByType = ({ enums, detail, formStore }) => {
  const percentCommonProps = {
    precision: 99,
    initFormat: 1 / 100,
    suffix: '%',
    needSmallNumber: false,
    wrapItemProps: {
      inputConfig: {
        min: -Infinity,
      },
    },
  }
  console.log('enums: ', enums)
  // 判断类型：根据配置名称关键字生成不同表头
  if (['RATING_MAPPING'].includes(detail?.configCode)) {
    // 内部/国内评级 与 穆迪评级映射
    return [
      MatchOptionColumn({
        title: '国内评级',
        dataIndex: 'innerLevel',
        matchOption: enums?.innerLevelEnum,
        editable: true,
      }),
      MatchOptionColumn({
        title: '穆迪评级',
        dataIndex: 'outerLevel',
        matchOption: enums?.outerLevelEnum,
        editable: true,
      }),
    ]
  }

  if (['BREACH_MAPPING'].includes(detail?.configCode)) {
    // 穆迪评级和违约概率映射关系
    return [
      MatchOptionColumn({
        title: '穆迪评级',
        dataIndex: 'outerLevel',
        editable: true,
        matchOption: enums?.outerLevelEnum,
      }),
      AmountColumn({
        title: '违约概率PD(指数平滑后）',
        dataIndex: 'outerPd',
        ...percentCommonProps,
        editable: true,
      }),
    ]
  }

  if (['FORWARD_Z'].includes(detail?.configCode)) {
    // 前瞻调整因子Z参数
    return [
      {
        title: '项目类别',
        dataIndex: 'group',
        editable: false,
      },
      AmountColumn({
        title: '基准情景',
        dataIndex: 'factorBaseZ',
        precision: 99,
        initFormat: 1,
        editable: true,
        needSmallNumber: false,
        wrapItemProps: {
          inputConfig: {
            min: -Infinity,
          },
        },
      }),
      AmountColumn({
        title: '乐观情景',
        dataIndex: 'factorOptZ',
        precision: 99,
        initFormat: 1,
        editable: true,
        needSmallNumber: false,
        wrapItemProps: {
          inputConfig: {
            min: -Infinity,
          },
        },
      }),
      AmountColumn({
        title: '悲观情景',
        dataIndex: 'factorGloZ',
        initFormat: 1,
        precision: 99,
        editable: true,
        needSmallNumber: false,
        wrapItemProps: {
          inputConfig: {
            min: -Infinity,
          },
        },
      }),
    ]
  }

  if (['LOSS_LGD'].includes(detail?.configCode)) {
    // 违约损失率LGD
    return [
      MatchOptionColumn({
        title: '租赁物类型',
        dataIndex: 'leaseType',
        width: 220,
        matchOption: enums?.leaseTypeEnum,
        editable: false,
      }),
      AmountColumn({
        title: '违约损失率LGD',
        dataIndex: 'lgd',
        width: 180,
        ...percentCommonProps,
        editable: true,
      }),
    ]
  }

  if (['SCENARIO_WEIGHT'].includes(detail?.configCode)) {
    // 情景权重
    return [
      {
        title: '情景',
        dataIndex: 'scene',
        width: 200,
        editable: false,
      },
      AmountColumn({
        title: '权重',
        dataIndex: 'sceneWeight',
        width: 160,
        editable: true,
        ...percentCommonProps,
      }),
    ]
  }
  if (['INNER_BREACH_MAPPING'].includes(detail?.configCode)) {
    // 内部违约损失率LGD
    return [
      MatchOptionColumn({
        title: '国内评级',
        dataIndex: 'innerLevel',
        matchOption: enums?.innerLevelEnum,
        editable: true,
      }),
      AmountColumn({
        title: 'PD上限',
        dataIndex: 'innerPdUpper',
        width: 180,
        ...percentCommonProps,
        editable: true,
        wrapItemProps: {
          inputConfig: {
            min: -Infinity,
            onChange: (val, { dataSource, dataIndex, index }) => {
              const prevVal = +formStore.getFieldValue([dataSource.id, 'innerPdLower'])

              formStore.setFieldValue(
                [dataSource.id, 'innerPd'],
                Math.sqrt(mathjs.multiply(+val, prevVal))
              )
            },
          },
        },
      }),
      AmountColumn({
        title: 'PD下限',
        dataIndex: 'innerPdLower',
        width: 180,
        ...percentCommonProps,
        editable: true,
        wrapItemProps: {
          inputConfig: {
            min: -Infinity,
            onChange: (val, { dataSource, dataIndex, index }) => {
              const prevVal = +formStore.getFieldValue([dataSource.id, 'innerPdUpper'])

              formStore.setFieldValue(
                [dataSource.id, 'innerPd'],
                Math.sqrt(mathjs.multiply(+val, prevVal))
              )
            },
          },
        },
      }),
      AmountColumn({
        title: 'PD均值',
        dataIndex: 'innerPd',
        width: 180,
        ...percentCommonProps,
        editable: true,
      }),
    ]
  }
  return []
}
const Index = ({ params }) => {
  const store = useMemo(() => new Store(), [])
  const { id } = params
  const detail = store.page.getData()
  const { view } = getQuery()
  const readOnly = ['readOnly', 'version'].includes(view)
  const list = store.$table.getList() || []
  const needAddConfig = ['RATING_MAPPING', 'BREACH_MAPPING'].includes(detail?.configCode)
  const formStore = store.$table.getFormStore()
  const enums = detail?.enums || {}

  const baseColumns = getColumnsByType({ enums, detail, formStore })
  const columns = [
    ...baseColumns,
    !readOnly &&
      needAddConfig && {
        title: '操作',
        dataIndex: 'actions',
        width: 150,
        actions: (_, record, index) => {
          return [
            {
              name: '删除',
              onClick: () => {
                store.$table.deleteRow(_.id)
              },
            },
          ]
        },
        fixed: 'right',
        width: 100,
      },
  ].filter(Boolean)
  return (
    <Page
      className={styles.index}
      current={detail?.configName}
      store={store.page}
      params={{ id }}
      extra={
        <Space className={styles.extra}>
          {readOnly && <span>版本时间：{detail?.versionTime || '-'}</span>}
          {!readOnly && needAddConfig && (
            <Button
              type="primary"
              onClick={() => {
                store.$table.addRow()
              }}
            >
              增加
            </Button>
          )}
          {!readOnly && <Button onClick={() => history.goBack()}>取消</Button>}
          {!readOnly && (
            <Button type="primary" onClick={() => store.save()}>
              保存
            </Button>
          )}
        </Space>
      }
    >
      <Table
        columns={columns}
        store={store.$table}
        serial
        resizable
        columnWidth={120}
        scroll={{ x: 'auto' }}
        editable={!readOnly}
      />
    </Page>
  )
}

export default observer(Index)
