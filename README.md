TheHunt
=======

Game/Live wallpaper for Android trying to become an Interactive graphical platform for AI experiments.

TheHunt’s purpose is to be a convenient visual environment where you can put your own agent and test it’s brain! For instance, the example Prey eats as much as it can while hiding from the user who wields a net and looks forward to catch it.

This leads to the other purpose - to be a fun Android game/an interesting Live Wallpaper.

Feel free to use it for whatever comes to mind, and tell me about it! :-)

Main features
--------------------
  * **Graphics engine**
    * Written from scratch on top of OpenGL ES 2
    * Sprites and Shapes abstractions for more intuitive and easy graphics creation and manipulation
  * **Scrollable world**
    * Ecosystem featuring Currents and Algae
    * Event system which the Agent can use to understand it’s surroundings
    * Tools abstraction as means for the user to modify the environment
      * CatchNet tool implemented which releases a net that can catch the Agent
  * **Agent**
    * Example agent (Prey), the main character in the game and live wallpaper, so far always hungry and cowardly
    * Agent interface for easy implementation of various other agents 


Current issues - many
--------------------------------
  * Documentation is non existent - the code is supposed to be as readable as possible though. Still, a priority.
  * Some game features are missing, like a HUD. As soon as these are implemented I will release the game to the Android Market for all to enjoy
  * Some parts of the code require major refactoring


Project structure
-------------------------
  * *d3kod.graphics* 
    * Graphics engine on OpenGL ES 2.0
    * Supports both Application and Live Wallpaper modes
  * *d3kod.thehunt.world*
    * Interactive graphical environment
    * *events.* * - Agent Senses supported: Algae, At, Current, Food, Light, Noise
  * *d3kod.thehunt.agent*
    * Agent - Agent Template
    * *prey.* * - Agent example and main character in the game/live wallpaper
  * *d3kod.thehunt*
    * Main Application context
    * Android application Activity, GLSurfaceView and Dialogs 
  * *d3kod.thehunt.livewallpaper*
    * Android Live Wallpaper Activity and GLSurfaceView

