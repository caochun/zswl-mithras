import { App } from '@zswl/components'

export default function useGetMap() {
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
  return { options, getKeyOptionsLabelMap }
}
