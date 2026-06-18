const nonPublicValid = [
  {
    conform: [{ code: 'NP_C_1_01_01', content: '0' }],
    requiredCode: 'NP_C_1_01_02',
  },
  //"NP_C_3_02"、"NP_C_2_02"、"NP_C_2_03_02"、"NP_C_2_04_02"、"NP_C_2_07"、"NP_C_3_02" 这几个加下必填
  { conform: true, requiredCode: 'NP_C_3_02' },
  { conform: true, requiredCode: 'NP_C_2_02' },
  { conform: true, requiredCode: 'NP_C_2_03_02' },
  { conform: true, requiredCode: 'NP_C_2_04_02' },
  { conform: true, requiredCode: 'NP_C_2_07' },
  { conform: true, requiredCode: 'NP_C_3_02' },

  {
    conform: [
      { code: 'NP_C_2_01_01', content: '1' },
      { code: 'NP_C_2_01_02', content: '1' },
      { code: 'NP_C_2_01_03', content: '1' },
      { code: 'NP_C_2_01_04', content: '0' },
    ],
    requiredCode: 'NP_C_1_01_05',
  },
  {
    conform: [{ code: 'NP_C_2_05_01', content: '1' }],
    requiredCode: 'NP_C_2_05_02',
  },
  {
    conform: [
      { code: 'NP_C_3_01_01', content: '1' },
      { code: 'NP_C_3_01_02', content: '1' },
      { code: 'NP_C_3_01_03', content: '1' },
      { code: 'NP_C_3_01_04', content: '1' },
    ],
    requiredCode: 'NP_C_3_01_05',
  },
  {
    conform: [
      { code: 'NP_C_4_01', content: '0' },
      { code: 'NP_C_4_02', content: '0' },
      { code: 'NP_C_4_03', content: '1' },
      { code: 'NP_C_4_04', content: '1' },
      { code: 'NP_C_4_05', content: '1' },
      { code: 'NP_C_4_06', content: '1' },
      { code: 'NP_C_4_07', content: '1' },
      { code: 'NP_C_4_08', content: '1' },
      { code: 'NP_C_4_09', content: '1' },
      { code: 'NP_C_4_10', content: '0' },
      { code: 'NP_C_4_11', content: '1' },
      { code: 'NP_C_4_12', content: '1' },
    ],
    requiredCode: 'NP_C_4_13',
  },
]
const publicValid = [
  {
    conform: [
      { code: 'P_C_1_01', content: '1' },
      { code: 'P_C_1_02', content: '1' },
    ],
    requiredCode: 'P_C_1_03',
  },
  {
    conform: [
      { code: 'P_C_2_01', content: '1' },
      { code: 'P_C_2_02', content: '1' },
      { code: 'P_C_2_03', content: '1' },
      { code: 'P_C_2_04', content: '0' },
      { code: 'P_C_2_05', content: '1' },
    ],
    requiredCode: 'P_C_2_06',
  },
  {
    conform: [
      { code: 'P_C_3_01', content: '1' },
      { code: 'P_C_3_02', content: '1' },
      { code: 'P_C_3_03', content: '1' },
      { code: 'P_C_3_04', content: '0' },
      { code: 'P_C_3_05', content: '1' },
    ],
    requiredCode: 'P_C_3_06',
  },
  {
    conform: [
      { code: 'P_C_4_01', content: '1' },
      { code: 'P_C_4_02', content: '1' },
      { code: 'P_C_4_03', content: '1' },
      { code: 'P_C_4_04', content: '1' },
      { code: 'P_C_4_05', content: '1' },
      { code: 'P_C_4_06', content: '1' },
      { code: 'P_C_4_07', content: '0' },
      { code: 'P_C_4_08', content: '1' },
      { code: 'P_C_4_09', content: '1' },
      { code: 'P_C_4_10', content: '1' },
    ],
    requiredCode: 'P_C_4_11',
  },
]

export { nonPublicValid, publicValid }
