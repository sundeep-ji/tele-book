const btn = document.getElementById("menu-btn")
const nav = document.getElementById("menu")
const c_btn = document.getElementById("c_btn")
const c_menu = document.getElementById("c_menu")
const user_btn = document.getElementById("user_btn")
const user_menu = document.getElementById("user_menu")
const message_box = document.getElementById("message_box");

function message_close() {
    message_box.classList.add("hidden");
}

btn.addEventListener('click', () => {
    btn.classList.toggle('open')
    nav.classList.toggle('flex')
    nav.classList.toggle('hidden')
})

c_btn.addEventListener('mouseenter', () => {
    c_menu.classList.remove('hidden')
})

c_btn.addEventListener('mouseleave', () => {
    c_menu.classList.add('hidden')
})

user_btn.addEventListener('mouseenter', () => {
    user_menu.classList.remove('hidden')
})

user_btn.addEventListener('mouseleave', () => {
    user_menu.classList.add('hidden')
})

close_btn_message.addEventListener('close', () => {
    message_box.classList.add('hidden');
})