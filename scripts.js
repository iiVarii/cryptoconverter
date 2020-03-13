var colorElement;
var colorRule;

var allCardList;
var gameCardList;
var divCardList;

var sizeElement;
var size;
var tableRowSize;
var tableColumnSize;

var allCardNames = ["ace", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "jack", "queen", "king"];
var blackCardGroups = ["clubs", "spades"];
var redCardGroups = ["hearts", "diamonds"];

const blankCardClassName = "card_back";
const cardVisualDelay = 400;

var gameScore;
var gameTime;
var gameRunning = false;
var lastCardDiv;
var canPickAgain;

var scoreList = [];


window.addEventListener('load', function () {
    loadScoreDictionary();
    createGame();
}, false);

class Card {
    constructor(name, group) {
        this.name = name;
        this.group = group;
    }

    getCardStyleName() {
        return this.name + "_" + this.group
    }

    getName() {
        return this.name;
    }

    getGroup() {
        return this.group;
    }
}

function createGame() {
    loadScoreDictionary();

    colorElement = document.getElementById("rule_select");
    var colorValue = colorElement.options[colorElement.selectedIndex].value;
    colorRule = colorValue === "true";
    allCardList = [];
    gameCardList = [];
    divCardList = [];

    sizeElement = document.getElementById("size_select");
    size = parseInt(sizeElement.options[sizeElement.selectedIndex].value);

    switch (size) {
        case 6:
            tableRowSize = 2;
            tableColumnSize = 3;
            break;
        case 16:
            tableRowSize = 4;
            tableColumnSize = 4;
            break;
        case 26:
            tableRowSize = 2;
            tableColumnSize = 13;
            break;
        case 52:
            tableRowSize = 4;
            tableColumnSize = 13;
            break;
        default:
            tableRowSize = 4;
            tableColumnSize = 4;
            break;
    }

    if (size > 26 && !colorRule) {
        window.alert("Can not create game with those rules!");
        document.getElementById("size_select").options.selectedIndex = 0;
        size = 6;
        createGame();
        return;
    }

    gameScore = 0;
    gameTime = 0;
    document.getElementById("scoreCounter").innerHTML = "Score: " + gameScore;
    lastCardDiv = undefined;
    canPickAgain = true;

    // Create the card list with possible cards to choose from.
    for (var nameIndex = 0; nameIndex < allCardNames.length; nameIndex++) {
        for (var blackGroupIndex = 0; blackGroupIndex < blackCardGroups.length; blackGroupIndex++) {
            allCardList.push(new Card(allCardNames[nameIndex], blackCardGroups[blackGroupIndex]))
        }

        for (var redGroupIndex = 0; redGroupIndex < redCardGroups.length; redGroupIndex++) {
            allCardList.push(new Card(allCardNames[nameIndex], redCardGroups[redGroupIndex]))
        }
    }

    // Choose cards required for the game from all cards.
    for (var cardAmount = 0; cardAmount < (tableColumnSize * tableRowSize) / 2; cardAmount++) {
        var randomCard;

        var alreadyHasPairedCard = true;
        if (colorRule) {
            randomCard = allCardList[Math.floor(Math.random() * allCardList.length)];
        } else {
            loop1:
                while (alreadyHasPairedCard) {
                    randomCard = allCardList[Math.floor(Math.random() * allCardList.length)];
                    for (const cardInGame of gameCardList) {
                        if (checkCardPairing(randomCard, cardInGame)) {
                            alreadyHasPairedCard = true;
                            continue loop1;
                        }
                    }
                    alreadyHasPairedCard = false;
                }
        }

        for (var j = 0; j < allCardList.length; j++) {
            var randomPairingCard = allCardList[j];
            if (checkCardPairing(randomCard, randomPairingCard)) {
                gameCardList.push(randomCard);
                gameCardList.push(randomPairingCard);
                removeElementFromArray(allCardList, randomCard);
                removeElementFromArray(allCardList, randomPairingCard);
                break
            }
        }
    }
    gameCardList = shuffle(gameCardList);

    // Create the table for cards.
    document.getElementById("cardTable").innerHTML = "";
    var inlineTableDiv = document.getElementById("cardTable");
    var table = document.createElement("TABLE");
    table.setAttribute("align", "center");
    var tableBody = document.createElement("TBODY");
    table.appendChild(tableBody);

    var cardLoopCounter = 0;

    for (var rowIndex = 0; rowIndex < tableRowSize; rowIndex++) {
        var tr = document.createElement("TR");
        table.appendChild(tr);
        for (var colIndex = 0; colIndex < tableColumnSize; colIndex++) {
            var td = document.createElement("TD");
            var cardDiv = document.createElement("DIV");
            cardDiv.id = gameCardList[cardLoopCounter].getCardStyleName();
            cardDiv.classList.toggle(blankCardClassName);


            cardDiv.setAttribute("type", "image");

            cardDiv.addEventListener('click', function () {
                processCardClick(this);
            }, false);

            divCardList.push(cardDiv);

            td.appendChild(cardDiv);
            tr.appendChild(td);

            cardLoopCounter++;
        }
    }

    initializeTimer();
    gameRunning = true;
    inlineTableDiv.appendChild(table);

    document.getElementById("searchField")
        .addEventListener("keyup", function (event) {
            event.preventDefault();
            if (event.keyCode === 13) {
                search();
            }
        });
}

function loadScores() {
    document.getElementById("scoreTable").innerHTML = "";
    var inlineTableDiv = document.getElementById("scoreTable");

    var table = document.createElement("TABLE");
    table.setAttribute("align", "center");
    table.className = "customTable";

    var tableBody = document.createElement("TBODY");
    table.appendChild(tableBody);

    var tr = document.createElement("TR");
    tr.className = "tablePadding";
    table.appendChild(tr);

    var th = document.createElement("TH");
    th.className = "tablePadding";
    th.innerText = "Name";
    tr.appendChild(th);

    var th2 = document.createElement("TH");
    th2.className = "tablePadding";
    th2.innerText = "Score";
    tr.appendChild(th2);

    var th3 = document.createElement("TH");
    th3.className = "tablePadding";
    th3.innerText = "Time";
    tr.appendChild(th3);

    for (var index = 0; index < scoreList.length; index++) {
        var trTemporary = document.createElement("TR");
        trTemporary.className = "tablePadding";
        table.appendChild(trTemporary);

        var tdTemporary = document.createElement("TD");
        tdTemporary.innerText = scoreList[index][0];
        tdTemporary.className = "tablePadding";
        trTemporary.appendChild(tdTemporary);

        var tdTemporary2 = document.createElement("TD");
        tdTemporary2.innerText = scoreList[index][1];
        tdTemporary2.className = "tablePadding";
        trTemporary.appendChild(tdTemporary2);

        var tdTemporary3 = document.createElement("TD");
        tdTemporary3.innerText = scoreList[index][2];
        tdTemporary3.className = "tablePadding";
        trTemporary.appendChild(tdTemporary3);
    }
    inlineTableDiv.appendChild(table);
}

function loadScoreDictionary() {
    //var xhttp = new XMLHttpRequest();
    var url = '/~mmerim/cgi-bin/prax3/score_provider.py';
   // xhttp.open("GET", url, true);
   // xhttp.send();

  //  xhttp.onreadystatechange = function () {
  //      if (this.readyState === 4 && this.status === 200 && document.readyState === "complete") {
   //         scoreList = JSON.parse(this.responseText);
   //         loadScores();
   //     }
   // };
	
	fetch(url)
       .then(res => res.json())
       .then(json => {
           scoreList = json;
           loadScores()
       })
       .catch(err => console.error(err));
	

}

function saveScoreDictionary() {
    var http = new XMLHttpRequest();
    var url = '/~mmerim/cgi-bin/prax3/score_dumper.py?data=' + JSON.stringify(scoreList);
    var params = scoreList;
    http.open('POST', url, true);

    http.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');

    http.onreadystatechange = function () {//Call a function when the state changes.
        if (http.readyState === 4 && http.status === 200) {
            alert("Score successfully saved to database!");
            loadScoreDictionary();
        }
    };
    http.send(params);
}

// Modified version from the source https://stackoverflow.com/questions/31405996/find-elapsed-time-in-javascript
function initializeTimer() {
    window.markDate = new Date();
    updateClock();
}

function search() {
    var url = '/~mmerim/cgi-bin/prax3/score_representation.py';
    keyword = document.getElementById("searchField").value;
    if (keyword !== "" || keyword !== " " || keyword !== null) {
        url += "?search=" + keyword
    }
    window.location.href = url;
}


// Modified version from the source https://stackoverflow.com/questions/31405996/find-elapsed-time-in-javascript
function updateClock() {
    var currDate = new Date();
    gameTime = (currDate - markDate) / 1000;
    document.getElementById("timeCounter").innerHTML = timeFormat(gameTime);
    setTimeout(function () {
        if (gameRunning) {
            updateClock()
        }
    }, 1000);
}


// Modified version from the source https://stackoverflow.com/questions/31405996/find-elapsed-time-in-javascript
function timeFormat(seconds) {
    var numhours = parseInt(Math.floor(((seconds % 31536000) % 86400) / 3600), 10);
    var numminutes = parseInt(Math.floor((((seconds % 31536000) % 86400) % 3600) / 60), 10);
    var numseconds = parseInt((((seconds % 31536000) % 86400) % 3600) % 60, 10);
    return ((numhours < 10) ? "0" + numhours : numhours)
        + ":" + ((numminutes < 10) ? "0" + numminutes : numminutes)
        + ":" + ((numseconds < 10) ? "0" + numseconds : numseconds);
}


function addScore(name) {
    if (name !== "") {
        scoreList.push([name, gameScore, timeFormat(gameTime)]);
        saveScoreDictionary();
        window.alert(name.charAt(0).toUpperCase() + name.slice(1) + " completed with score " + gameScore + " and time "
            + timeFormat(gameTime) + ".");
    }
}


function processCardClick(clickedCardDiv) {

    if (!canPickAgain) return;
    if (lastCardDiv === undefined || !lastCardDiv.id === lastCardDiv.className) {
        lastCardDiv = clickedCardDiv;
        toggleCard(clickedCardDiv);

    } else if (lastCardDiv === clickedCardDiv) {
        toggleCard(clickedCardDiv)

    } else {
        if (lastCardDiv.id === lastCardDiv.className) {
            toggleCard(clickedCardDiv);
            canPickAgain = false;
            if (checkCardDivPairing(clickedCardDiv, lastCardDiv)) {
                setTimeout(function () {
                    toggleCard(clickedCardDiv);
                    toggleCard(lastCardDiv);
                    clickedCardDiv.style.visibility = "hidden";
                    lastCardDiv.style.visibility = "hidden";
                    canPickAgain = true;
                    gameScore += 5;
                    document.getElementById("scoreCounter").innerHTML = "Score: " + gameScore;
                    processVictory()
                }, cardVisualDelay);
            } else {
                setTimeout(function () {
                    toggleCard(clickedCardDiv);
                    toggleCard(lastCardDiv);
                    canPickAgain = true;
                    gameScore -= 2;
                    document.getElementById("scoreCounter").innerHTML = "Score: " + gameScore;
                    processVictory()
                }, cardVisualDelay);
            }
        } else {
            toggleCard(clickedCardDiv);
            lastCardDiv = clickedCardDiv;
        }
    }
}


function checkVictory() {
    for (var i = 0; i < divCardList.length; i++) {
        if (divCardList[i].style.visibility !== "hidden") {
            return false;
        }
    }
    return true;
}


function processVictory() {
    if (checkVictory()) {
        gameRunning = false;
        var name = promptName();
        addScore(name);
        loadScoreDictionary();
    }
}


function promptName() {
    return prompt("Enter your name");
}


function checkCardPairing(firstCard, secondCard) {
    if (firstCard.getName() === secondCard.getName()) {
        if ((colorRule && firstCard.getGroup() === getSameColorGroup(secondCard.getGroup())) || !colorRule) {
            return true
        }
    }
    return false;
}


function checkCardDivPairing(firstCardDiv, secondCardDiv) {
    var firstCard;
    var secondCard;
    gameCardList.forEach(function (item) {
        if (item.getCardStyleName() === firstCardDiv.id) {
            firstCard = item;
        }
        if (item.getCardStyleName() === secondCardDiv.id) {
            secondCard = item;
        }
    });

    if (firstCard.getName() === secondCard.getName()) {
        if ((colorRule && firstCard.getGroup() === getSameColorGroup(secondCard.getGroup())) || !colorRule) {
            return true
        }
    }
    return false;
}


function toggleCard(cardDiv) {
    cardDiv.classList.toggle(cardDiv.id);
    cardDiv.classList.toggle(blankCardClassName);
}


function shuffle(array) {
    // Source: https://stackoverflow.com/questions/2450954/how-to-randomize-shuffle-a-javascript-array
    var currentIndex = array.length, temporaryValue, randomIndex;

    while (0 !== currentIndex) {

        randomIndex = Math.floor(Math.random() * currentIndex);
        currentIndex -= 1;

        temporaryValue = array[currentIndex];
        array[currentIndex] = array[randomIndex];
        array[randomIndex] = temporaryValue;
    }

    return array;
}


function getSameColorGroup(groupname) {
    if (groupname === "clubs") return "spades";
    if (groupname === "spades") return "clubs";
    if (groupname === "hearts") return "diamonds";
    if (groupname === "diamonds") return "hearts";
}


function removeElementFromArray(array, elem) {
    // Source: https://stackabuse.com/remove-element-from-an-array-in-javascript/
    var index = array.indexOf(elem);
    if (index > -1) {
        array.splice(index, 1);
    }
}
