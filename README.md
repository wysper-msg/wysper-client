# wysper-client
Client application for the Wysper messaging service

## Initial setup!

First clone the repo into intellij.
1. New project -> from existing source -> from version control

When you get your project running:
1. Right click on the top level directory (wysper-client for me)
2. At the bottom, go to mark as -> sources root

### Basic setup
Go to File -> Project Structure -> and make sure your sdk level is set to 1.8, and your java level is set to 8.
Also in project structure make sure your terminal output is set to some location.



If at any point you have trouble, google the exact error message and you may be able to quickly find a solution.

Next remove the current sdk used in the project structure and re-add it.
File -> Project Structure -> SDK

### Dependencies
Navigate to https://code.google.com/archive/p/json-simple/downloads or click:
https://storage.googleapis.com/google-code-archive-downloads/v2/code.google.com/json-simple/json-simple-1.1.1.jar
to download json-simple. HINT: Remember where you download the .jar file, you need it in the next step.

Once downloaded, go to File -> Project Structure -> Libraries and click the plus at the top
Find where you installed json-simple and add it to the project.

Now you should be able to run the Client and start sending messages!

##Getting JavaFX for Ubuntu
sudo apt-get install openjfx
sudo apt-get install libcanberra-gtk-module
Next, re-add your sdk through File->Project Structure -> SDKs