# Poker Wheel Game
CS2063 - App project - Group 13 : Curtis Lough, Raven-Lee Mills, Rojan Omidvar

# What is it
The poker wheel is a game in which players must assemble a winning hand by turning the wheel to get the cards they need, without knowing for sure how the other players decisions will affect the wheel. In order to win, you’ll need to get inside your opponents heads and figure out how they plan to move, so that you can make the right move to get the card that completes your hand. 
The game is playable primarily as single-player versus a simple AI or with basic multiplayer functionality implemented using Firebase Realtime Database. Adjustable Settings include the ability to choose between 3 or 5 rounds and the ability to choose how many moves each player can have each round. After completing the chosen number of rounds, the player with the best 3 or 5 card hand will be declared the winner.
Hand Ranks are as follows:
  5-Card Hands:
    Royal Flush
    Straight Flush
    Four of a Kind
    Full House
    Flush
    Straight
    Three of a Kind
    Two Pair
    Pair
    High Card

  3-Card Hands:
    Straight Flush
    Three of a Kind
    Straight
    Flush
    
# Exisiting Issues
- The multiplayer feature of the game has not been fully implemented or tested.

# Supported API Levels
- The Poker Wheel Mobile Game only supports android devices with Android 11.0 - R, API Level 30.

# How To Run
- Install the latest version of Android Studio (prerequisite): Find the latest version here: (https://developer.android.com/studio)
- Clone this directory to a folder n your local device (prerequisite)
- Provided the Android Studio application and repository have been set up correctly on your local device, download the application onto your device/emulator. Here is how: (https://developer.android.com/training/basics/firstapp/running-app)
- Have fun!

# Scenarios
Here are some helpful screaios to run through to experience the entire functionality of the current Poker Wheel Game experience.
1. Changing Game Default Settings
  Open the application on your device or emulator.
  Click the “Settings” button
  Note the default values of the various game features.
  Change the value of the number of rounds in the game from “3” to “5”.
  Go back to the game’s main page by pressing the back button.
  Note that the number of rounds in the game has changed. The values are automatically saved once changed.

2. Learning How To Play
  Open the application on your device or emulator.
  Click the “Instructions” button
  Note the instructions on how to play the Poker Wheel game is now being displayed.
  Go back to the game’s main page by pressing the back button.

3. Playing By Yourself
  Open the application on your device or emulator.
  Click the “Play with AI” button
  Note that the game starts and the screen will now display a rendition of the game table in landscape mode.
  Using the current game default settings the player should see four cards on the wheel in the center of the screen and a timer that shows how much time is left in each round.
  Swipe left to move the wheel and the cards on the wheel in the clockwise direction. A toast notification will appear indicating that the player has moved in the clockwise direction.
  Swipe right to move the wheel and the cards on the wheel in the anti- clockwise direction. A toast notification will appear indicating that the player has moved in the anti-clockwise direction.
  Note the text at the bottom of the screen there is text indicating the sum of the players decisions. Moving the wheel clockwise once will subtract one(-1), moving the wheel anti-clockwise once will add one (+1) and not moving the wheel will be represented by zero(0)
  Note the “END TURN” button and the top-left of the screen. Press the button to end your turn.
  Note the poker table will be updated to show the cards each player got from the wheel.
  Repeat the previous steps for the number of rounds chosen in the game.
  Once the game has ended, note the text and the bottom of the screen that says what the best hand was and which player won.


