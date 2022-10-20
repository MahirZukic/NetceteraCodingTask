# Read Me for starting Netcetera TicTacToe game
## Requirements:
* JDK11 or later
* Maven (optional)
* IDE such as IntelliJ or Eclipse (optional)
* Docker and `docker-compose`

## Execution OPTION 1
This project can be either run via:
* `java -jar ./target/NetceteraTask-0.0.1-SNAPSHOT.jar`
* running a NetceteraTaskApplication from IntelliJ or other IDE
* running via maven with the command `mvn spring-boot:run` or in case we don't have Maven installed locally `./mvnw spring-boot:run`

NOTE: running this way it is assumed that Redis server is already running in the background on localhost with 6379 port

## Execution OPTION 2
* open up a command line or bash inside this folder
* run the following commands:
  * `docker-compose -f docker-compose.yml build`
  * `docker-compose -f docker-compose.yml up`

This will firstly build the docker image for this application, and secondly run the docker image, and it's dependencies
which is in our case redis server.

The configuration is already set up in the dockerfile so nothing else is needed.

## Instructions
After choosing any of the above-mentioned options and seeing these messages:
* tic-tac-toe-game       | 2022-10-20 12:49:38.122  INFO 1 --- [           main] d.n.n.NetceteraTaskApplication           : Started NetceteraTaskApplication in 7.889 seconds (JVM running for 8.517)

we can tell that the application has successfully started, and we can start using our application:
Using this application requires calling REST APIs and we will need programs such as:
* Postman
* Insomnia REST Client
* Advanced REST Client
* curl
* RESTer
* RESTClient
* etc.

First thing we should do is create some players. We can do so in the following way: 
* POST request to 
  * `http://localhost:8080/api/player/create`
  * with payload like this:
    * {
      "username": "Mahir",
      "email": "Mahir@gmail.com"
      }

This will give us a response like this:
* {
  "id": 7713720028733256149,
  "username": "Mahir",
  "email": "Mahir@gmail.com"
  }
* {
  "id": 1219165127350567168,
  "username": "Robot",
  "email": "robot@gmail.com"
  }

After that is done, we need to create a game with those two players. This will be done in the following way
* POST request to
    * `http://localhost:8080/api/game/create`
    * with payload like this:
        * {
          "playerOne": 7713720028733256149,
          "playerTwo": 1219165127350567168
          }

Here we will use the ids which were gotten in previous API invocation.
This will give us a response like this:
* {
  "id": 1558241609253111310,
  "playerOne": 7713720028733256149,
  "playerTwo": 1219165127350567168,
  "moves": null,
  "result": "GAME_CREATED",
  "createdAt": "2022-10-20T12:40:38.812978"
  }

After the game is created for those players, what is left to do? Play the game.

We will do it in the following way:
* POST request to
    * `http://localhost:8080/api/game/play`
    * with payload like this:
        * {
          "gameId": 1558241609253111310,
          "move": 3
          }

Here we will use the gameId from the previous response, and the move represents a place where we want to place tic
or tac based on a board which looks like this

    7    8    9
    4    5    6
    1    2    3

pretty much like a numpad on a keyboard.

After calling this API we will get a response like this:
* {
  "id": 1558241609253111310,
  "playerOne": 7713720028733256149,
  "playerTwo": 1219165127350567168,
  "moves": 1,
  "result": "GAME_IN_PROGRESS",
  "createdAt": "2022-10-20T12:40:48.812978"
  }

We will need to call the above-mentioned API several times until the game is over.
* {
  "id": 6790635670120757621,
  "playerOne": 6592145929852306099,
  "playerTwo": 1219165127350567168,
  "moves": 14326578,
  "result": "FINISHED_GAME_PLAYER_TWO_WON",
  "createdAt": "2022-10-20T13:28:37.693319"
  }
* {
  "id": 2669237982284592952,
  "playerOne": 6592145929852306099,
  "playerTwo": 1219165127350567168,
  "moves": 12437,
  "result": "FINISHED_GAME_PLAYER_ONE_WON",
  "createdAt": "2022-10-20T13:31:36.331167"
  }
* {
  "id": 8669243807365591280,
  "playerOne": 6592145929852306099,
  "playerTwo": 1219165127350567168,
  "moves": 523769841,
  "result": "FINISHED_GAME_TIE",
  "createdAt": "2022-10-20T13:32:26.184377"
  }

After the game is over or even before it, we can query the game in the following way:
* GET request to
    * `http://localhost:8080/api/game/GAME_ID`
    * e.g. `http://localhost:8080/api/game/8669243807365591280`
where the `GAME_ID` is the id of the game we want to find, such as `8669243807365591280`.

In that case we would this as a response:
* {
  "id": 1922722474443319971,
  "playerOne": 6592145929852306099,
  "playerTwo": 1219165127350567168,
  "moves": null,
  "result": "GAME_CREATED",
  "createdAt": "2022-10-20T13:45:53.737498"
  }

Players and Games are stored in a Redis server which is used for caching and high throughput.

After some time (5 minutes by default) any stale game will be moved from redis server to our Database, which in my case 
was h2 in-memory DB, but could be replaced for Postgresql or MySql easily.
If a game is older or a game was finished, when queried, it will be gotten from Database instead, since it will have been
cleaned up and removed from the Redis server.

There is no security considerations nor credentials needed to run the application.
However, when making successive calls to `game play` API, the game doesn't check whether a correct player is connected,
nor if a player has exceeded this plays. Game assumes that moves will come in succession and from the right players,
meaning that if the final move string looks like `14326578`, the game will assume that:
* Player one has played: 1    3    6    7 moved
* Player two has played: 4    2    5    8 moved
Where the application would determine that player 2 has won with a 2-5-8 sequence.
