import WorkbenchTodoStatistics from './ToDoStatistics/WorkbenchTodoStatistics'
import WorkbenchContractAging from './ContractAging/WorkbenchContractAging'
import DashboardOperationLaunchComplete from '../../OperationView/LaunchComplete/DashboardOperationLaunchComplete'
import DashboardOperationCapacityAnalysis from '../../OperationView/CapacityAnalysis/DashboardOperationCapacityAnalysis'
import DashboardOperationConversionRates from '../../OperationView/ConversionRates/DashboardOperationConversionRates'
import DashboardOperationAgingStatistics from '../../OperationView/AgingStatistics/DashboardOperationAgingStatistics'
import Title from '../../Title'
import styles from './index.less'

const compList = [
  <WorkbenchTodoStatistics />, // 待办统计
  <WorkbenchContractAging />, // 合同审批时效及退回情况
  <DashboardOperationLaunchComplete />, // 投放完成情况
  <DashboardOperationCapacityAnalysis />, // 产能分析
  <DashboardOperationConversionRates />, // 转化率情况
  <DashboardOperationAgingStatistics />, // 时效统计
]

const WorkbenchOperationView = ({ title, iconType }) => {
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

export default WorkbenchOperationView
