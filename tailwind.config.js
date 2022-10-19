/** @type {import('tailwindcss').Config} */
module.exports = {
    content: [
        'src/main/resources/templates/*.html',
        'src/main/resources/templates/user/*.html'
    ],
    theme: {
        screens: {
            sm: '480px',
            md: '768px',
            lg: '976px',
            xl: '1044px'
        },
        extend: {
            colors: {
                'landing': '#C5C0BC',
                'skin': '#F0D8B6',
                'aboutlightblue': '#5AB9F3',
                'blue': '#5C6575',
                'darkblue': '#464F60',
                'aboutbluedark': '#1C5C9B',
                'contactred': '#F73A45',
                'error': "#842029",
                'success': "#0f5132",
                'contactreddark': '#DA2C37',
            }
        },
    },
    plugins: [
        require('@tailwindcss/forms')
    ],
}
