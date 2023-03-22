document.addEventListener('DOMContentLoaded', function () {
    const noscript = document.querySelector('.noscript');
    noscript.parentNode.removeChild(noscript);
});

const SOKET_ERROR_MESSAGE = 'Connection refused. Reload the page and try again.';

const TOKEN_KEY = 'token';
const HOST = window.location.host;
const CHAT_URI = '/chat';

const errorMessageElement = document.querySelector('.error-message');
const loginButton = document.getElementById('login-button');
const logoutButton = document.getElementById('logout-button');
const messageText = document.getElementById('message-text');
const messageButton = document.getElementById('message-button');
const messages = document.querySelector('.messages');

let token = window.localStorage.getItem(TOKEN_KEY);
let isMessageFirst = true;
let socket;

if (token !== null) {
    startChat(token);
}

loginButton.onclick = () => {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    if (!username || !password) return;
    login(username, password);
};

logoutButton.addEventListener('click', logout);


//==========================================================


function startChat(token) {
    if (!token) return;
    hideLoginForm(true);
    const socketUrl = 'ws://' + HOST + CHAT_URI;
    socket = new WebSocket(socketUrl);
    socket.onopen = socketOnOpen;
    socket.onerror = socketOnError;
    socket.onclose = socketOnClose;
    socket.onmessage = socketOnMessage;
}

function socketOnMessage(message) {
    if (!message?.data) return;
    showNewMessage(JSON.parse(message.data));
}

function showNewMessage(message) {
    if (isMessageFirst) {
        message.map((msg) => {
            messages.appendChild(getMessageElement(msg.name, msg.text));
        });
        isMessageFirst = false;
    } else {
        messages.appendChild(getMessageElement(message.name, message.text));
    }
    messages.scrollTop = messages.scrollHeight;
}

function getMessageElement(name, text) {
    const messageElement = document.createElement('div');
    const nameElement = document.createElement('div');
    const textElement = document.createElement('div');
    messageElement.classList.add('message');
    nameElement.classList.add('name');
    textElement.classList.add('text');
    textElement.innerHTML = text;
    nameElement.innerHTML = name;
    messageElement.appendChild(nameElement);
    messageElement.appendChild(textElement);
    return messageElement;
}

function socketOnOpen() {
    sendMessage(token);
    messageButton.onclick = () => {
        sendMessage(messageText.value);
        messageText.value = '';
    };
    messageText.onkeydown = (event) => {
        if (event.key === 'Enter') {
            sendMessage(messageText.value);
            messageText.value = '';
        }
    };
}

function sendMessage(text) {
    if (!text || !socket) return;
    socket.send(text);
}

function socketOnClose() {
    messageButton.onclick = null;
    messageText.onkeydown = null;
}

function socketOnError() {
    alert(SOKET_ERROR_MESSAGE);
}


//==========================================================


function logout() {
    window.localStorage.clear();
    isMessageFirst = true;
    hideLoginForm(false);
    messages.innerHTML = '';
    if (socket) socket.close();
}

function hideLoginForm(isToHide) {
    errorMessage();
    if (isToHide) {
        document.querySelector('.login-form').classList.add('hide');
        document.querySelector('.chatbox').classList.remove('hide');
        logoutButton.classList.remove('hide');
    } else {
        document.querySelector('.login-form').classList.remove('hide');
        document.querySelector('.chatbox').classList.add('hide');
        logoutButton.classList.add('hide');
    }

}

function errorMessage(message) {
    if (!message) {
        errorMessageElement.classList.add('hide');
        return;
    }
    errorMessageElement.innerHTML = message;
    errorMessageElement.classList.remove('hide');
}

async function login(username, password) {
    const body = {
        username: username,
        password: password
    };
    let response;
    try {
        response = await fetch('/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(body)
        })
        if (response?.ok) {
            let json = await response.json();
            if (json?.token) {
                localStorage.setItem(TOKEN_KEY, json.token);
                token = json.token;
                startChat(token);
            } else {
                errorMessage('Получен не верный токен!')
            }
        } else {
            if (response.status === 400) {
                errorMessage('Не верный формат данных json');
            }
            if (response.status === 403) {
                errorMessage('Имя уже занято или не верный пароль!');
            }
        }
    } catch (error) {
        console.log(error);
        hideLoginForm(false);
    }
}