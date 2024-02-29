// uno.config.ts
import { FileSystemIconLoader } from '@iconify/utils/lib/loader/node-loaders'
import {
    defineConfig,
    presetAttributify,
    presetIcons,
    presetTypography,
    presetUno,
    presetWebFonts,
    transformerDirectives,
    transformerVariantGroup,
} from 'unocss'

export default defineConfig({
    shortcuts: [
        // ...
    ],
    theme: {
        colors: {
            // ...
        },
    },
    presets: [
        presetUno(),
        presetAttributify(),
        presetIcons({
            extraProperties: {
                display: 'inline-block',
                'vertical-align': 'middle',
                // ...
            },
            collections: {
                tabler: () => import('@iconify-json/tabler/icons.json').then((i) => i.default),
                icon: FileSystemIconLoader('./src/assets/icons', (svg) => {
                    return svg.replace(/#fff/, 'currentColor')
                }),
            },
        }),
        presetTypography(),
        presetWebFonts({
            fonts: {
                // ...
            },
        }),
    ],
    transformers: [transformerDirectives(), transformerVariantGroup()],
})
