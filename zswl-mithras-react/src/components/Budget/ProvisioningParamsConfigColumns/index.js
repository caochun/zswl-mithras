import { AmountColumn, MatchOptionColumn } from '@/components/Format'
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
            onChange: (val, { dataSource }) => {
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
            onChange: (val, { dataSource }) => {
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
