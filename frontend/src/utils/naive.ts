import {
    // component
    NButton,
    NH2,
    NLayout,
    NLayoutContent,
    NLayoutFooter,
    NLayoutHeader,
    NLayoutSider,
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
    ],
})

export default naive
