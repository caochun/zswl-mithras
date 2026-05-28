  export const getKeyOptionsLabelMap = (key, options) => {
    const obj = {}
    if (options && options[key]) {
      options[key].forEach((item) => {
        const { label, value } = item
        obj[value] = label
      })
    }
    return obj
  }
  export const projectFinancingRatio = (val) => {
    const data = JSON.parse(JSON.stringify(val))
    data.map((v, i) => {
      if (v.label === '0') {
        v.label = '其他'
      } else {
        v.label = +v.label / 10000 + '%'
      }
    })
    return data
  }