import ConversionRates from '@/pages/dashboard/workbench/OperationView/ConversionRates'
import { initYearQueryDate } from '@/pages/dashboard/workbench/OperationView/utils'

const Index = ({ title }) => {
  return (
    <>
      <ConversionRates
        initialQuery={{ queryDate: initYearQueryDate }}
        title={title}
      ></ConversionRates>
    </>
  )
}
export default Index
