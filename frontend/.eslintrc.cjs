module.exports = {
    root: true,
    env: {
        browser: true,
        es2021: true,
        node: true,
    },
    extends: [
        // 'standard-with-typescript',
        'eslint:recommended',
        'plugin:@typescript-eslint/recommended',
        'plugin:vue/vue3-recommended',
        'prettier',
    ],
    overrides: [
        {
            env: {
                node: true,
            },
            files: ['.eslintrc.{js,cjs}'],
            parserOptions: {
                sourceType: 'script',
            },
        },
    ],
    parser: 'vue-eslint-parser',
    parserOptions: {
        parser: '@typescript-eslint/parser',
        ecmaVersion: 'latest',
        sourceType: 'module',
        // project: ['./tsconfig.json', './tsconfig.node.json'],
    },
    plugins: ['@typescript-eslint', 'vue'],
    rules: {
        'vue/multi-word-component-names': [
            'error',
            {
                ignores: ['index'],
            },
        ],
    },
    ignorePatterns: ['.eslintrc.cjs', 'uno.config.ts', './types/global.d.ts'],
}
