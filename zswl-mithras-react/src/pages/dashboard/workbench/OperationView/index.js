import ToDoStatistics from './ToDoStatistics'
import ContractAging from './ContractAging'
import LaunchComplete from './LaunchComplete'
import CapacityAnalysis from './CapacityAnalysis'
import ConversionRates from './ConversionRates'
import AgingStatistics from './AgingStatistics'
import { Title } from '@/pages/dashboard/workbench/components'
import styles from './index.less'

const compList = [
  <ToDoStatistics />, // 待办统计
  <ContractAging />, // 合同审批时效及退回情况
  <LaunchComplete />, // 投放完成情况
  <CapacityAnalysis />, // 产能分析
  <ConversionRates />, // 转化率情况
  <AgingStatistics />, // 时效统计
]

const Index = ({ title, iconType }) => {
  return (
    <>
      <Title title={title} iconType={iconType}></Title>
      <div className={styles.wrap}>
        {compList.map((item, index) => {
          return (
            <div style={{ marginBottom: 20 }} key={index}>
              {item}
            </div>
          )
        })}
      </div>
    </>
  )
}

export default Index
