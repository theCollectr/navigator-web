let stompClient = null;
let gameId = null;

$("#passwordField").keyup(function (event) {
    if (event.keyCode === 13) {
        $("#connectButton").click();
    }
});

$("#registerPasswordField").keyup(function (event) {
    if (event.keyCode === 13) {
        $("#registerButton").click();
    }
});

function connect() {
    const username = $('#usernameField').val();
    const password = $('#passwordField').val();
    if (username.length === 0 || password.length === 0) {
        return;
    }
    const socket = new SockJS('/request');
    stompClient = Stomp.over(socket);
    stompClient.connect({username: username, password: password}, function () {
        stompClient.subscribe('/user/queue/event/player', handleEvent);
        stompClient.subscribe('/user/queue/event/game/list', handleListEvent);
        stompClient.subscribe('/user/queue/event/game/joined', handleJoinEvent);
        stompClient.subscribe('/topic/games/update', handleListEvent);
        goToJoinGameScreen();
    }, function (err) {
        console.log(err)
        const response = $("#loginResponse");
        response.show();
        response.text("Wrong Credentials")
    });
}

function signup() {
    const username = $('#registerUsernameField').val();
    const password = $('#registerPasswordField').val();
    console.log(username)
    if (username.length === 0 || password.length === 0) {
        return;
    }
    const url = '/signup';
    const data = {username: username, password: password};
    $.post(url, data,
        function (data) {
            console.log(data);
            const response = $("#signupResponse");
            response.show();
            response.text(data);
        });
}

function goToJoinGameScreen() {
    hideAllScreens();
    showJoinGame();
    requestGamesList();
}

function hideAllScreens() {
    $("#loginFormScreen").hide();
    $("#joinGameScreen").hide();
    $("#waitingScreen").hide();
    $("#startGameScreen").hide();
    $("#gameScreen").hide();
    $("#gameEndedScreen").hide();
}


function showJoinGame() {
    $('#joinGameScreen').show()
}

function requestGamesList() {
    stompClient.send('/app/request/game/list')
}

function newGame() {
    stompClient.send('/app/request/game/create')
}

function startGame() {
    stompClient.send('/app/request/game/start', {}, gameId);
}

function joinGame(gameId) {
    stompClient.send('/app/request/game/join', {}, gameId);
}

function handleJoinEvent(event) {
    hideAllScreens();
    const joinEvent = JSON.parse(event.body)
    gameId = joinEvent.gameId
    stompClient.unsubscribe('/topic/games/update')
    if (joinEvent.host) {
        $('#startGameScreen').show();
    } else {
        $('#waitingScreen').show();
    }
}

function handleListEvent(event) {
    const gamesList = JSON.parse(event.body);
    $('#gameList').empty()
    gamesList.forEach(addGameNode)
}

function addGameNode(gameId) {
    const gameNode = createGameNode(gameId);
    document.getElementById('gameList').appendChild(gameNode);
}

function createGameNode(gameId) {
    const node = document.createElement('li');
    const text = document.createTextNode(gameId);
    const button = document.createElement('button');
    button.onclick = function () {
        joinGame(gameId)
    };
    button.textContent = 'join';
    node.appendChild(text)
    node.appendChild(button)
    return node;
}

function handleEvent(event) {
    const playerEvent = JSON.parse(event.body);
    console.log(playerEvent);
    if (playerEvent.type === 'event') {
        handlePlayerEvent(playerEvent);
    } else if (playerEvent.type === 'response') {
        handlePlayerResponse(playerEvent);
    }
}

function handlePlayerEvent(playerEvent) {
    const gameEndedScreen = $("#gameEndedScreen");
    const gameEndedText = $("#gameEndedText");
    if (playerEvent.eventType === 'gameStart') {
        hideAllScreens();
        $('#gameScreen').show();
    } else if (playerEvent.eventType === 'gameEnd') {
        hideAllScreens();
        if (playerEvent.state === 'won') {
            gameEndedText.text('you won the game');
        } else {
            gameEndedText.text('you lost the game');
        }
        gameEndedScreen.show();
    }
}
