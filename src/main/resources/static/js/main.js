'use strict';

var usernamePage = document.querySelector('#username-page');
var recipientPage = document.querySelector('#recipient-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var recipientForm = document.querySelector('#recipientForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

var stompClient = null;
var username = null;
var recipient = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function initialize(event) {
    username = document.querySelector('#name').value.trim();

    if(username) {
        usernamePage.classList.add('hidden');
        recipientPage.classList.remove('hidden');

        document.querySelector('#name').blur();
        document.querySelector('#rec-name').focus();
    }
    event.preventDefault();
}

function connect(event) {
    recipient = document.querySelector('#rec-name').value.trim();
    if (recipient) {
        recipientPage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        document.querySelector('#rec-name').blur();
        document.querySelector('#message').focus();

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({ username: username, }, onConnected, onError);
    }
    event.preventDefault();
}


function onConnected() {
    stompClient.subscribe('/users/queue/messages',onMessageReceived);

    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({sender: username, receiver: recipient, type: 'JOIN'})
    )

    connectingElement.classList.add('hidden');
}


function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    let messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        let chatMessage = {
            sender: username,
            receiver: recipient,
            content: messageInput.value,
            type: 'CHAT'
        };
        // stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        stompClient.send("/app/hello", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else {
        messageElement.classList.add('chat-message');
        if (message.sender !== username) {
            var avatarElement = document.createElement('i');
            var avatarText = document.createTextNode(message.sender[0]);
            avatarElement.appendChild(avatarText);
            avatarElement.style['background-color'] = getAvatarColor(message.sender);

            messageElement.appendChild(avatarElement);

            var usernameElement = document.createElement('span');
            var usernameText = document.createTextNode(message.sender);
            usernameElement.appendChild(usernameText);
            messageElement.appendChild(usernameElement);
        }
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);
    if (message.type === 'CHAT' && message.sender === username) {
        textElement.style["text-align-last"] = 'right';
    }

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}


function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

document.querySelector('#name').focus();

usernameForm.addEventListener('submit', initialize, true)
recipientForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', sendMessage, true)