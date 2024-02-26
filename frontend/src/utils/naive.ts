import {
    // component
    NButton,
    NCard,
    NDataTable,
    NForm,
    NFormItem,
    NH2,
    NInput,
    NLayout,
    NLayoutContent,
    NLayoutFooter,
    NLayoutHeader,
    NLayoutSider,
    NModal,
    NRadio,
    NRadioButton,
    NRadioGroup,
    NTooltip,
    // create naive ui
    create,
} from 'naive-ui'

const naive = create({
    components: [
        NButton,
        NLayout,
        NLayoutHeader,
        NLayoutContent,
        NLayoutSider,
        NLayoutFooter,
        NH2,
        NCard,
        NInput,
        NRadioGroup,
        NRadio,
        NRadioButton,
        NDataTable,
        NModal,
        NTooltip,
        NForm,
        NFormItem,
    ],
})

export default naive
