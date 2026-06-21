
import { InputNumber } from 'antd'
import classnames from 'classnames'
import { hasValue } from '@/utils'
import { useEffect, useState, useRef } from 'react'
import { financialMarketDeptRatioApi as Api } from '@/api/kpi/baseSet/parameterConfigApi'
import styles from './index.less'

const columns = [
  {
    title: '金融市场部提奖比例',
    dataIndex: 'financialMarketDeptRadio',
    editable: {
      element: <InputNumber addonAfter="%" precision={3} />,
      required: true,
    },
    render: (value) => {
      return hasValue(value) && value + '%'
    },
  },
]

const KpiBaseSetJinRongShiChangTiJiang = ({ typeInfo }) => {
  const [detail, setDatail] = useState({})
  const { isEdit } = typeInfo
  const detailData = useRef({})

  const getData = async () => {
    const res = await Api.getList()
    detailData.current = res
    setDatail({
      ...res.configValue,
    })
    return res.configValue
  }
  const saveData = async (data) => {
    const { id, configCode, configDesc, financialMarketDeptRadio } = data
    await Api.saveList({
      id,
      configCode,
      configDesc,
      configValue: {
        financialMarketDeptRadio,
        configValueType: detailData.current?.configValue.configValueType,
      },
    })
    await getData()
  }

  useEffect(() => {
    getData()
  }, [])

  return (
    <EditDescription
      title=""
      className={classnames(styles.desc, !isEdit && styles.noHeader)}
      canEdit={isEdit}
      detail={detail}
      saveData={saveData}
      columns={columns}
      initEdit={isEdit}
      labelStyle={{ width: 200 }}
    />
  )
}

export default KpiBaseSetJinRongShiChangTiJiang
