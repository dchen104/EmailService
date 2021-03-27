# Technologies Used
For this exercise, I chose to use Java 8 as my programming language and gradle as my build tool. I chose
Java because I have extremely good knowledge of creating basic web servers in Java and it would be
the quickest and easiest for me to accomplish this task in the 2 hour timeframe. I used gradle for the same reason.
Gradle as a build tool might be excessive for such a simple task but it made building and running the application
extremely easy and allowed configuration of a simple `config.properties` file very simple, which was
crucial since you need a property to control which email server you want to deploy against and use. Java and
gradle is also a nice combo that can easily get a nice-running web server that works quickly and effectively. I 
could just copy-paste the framework of previous simple web servers I have worked on/seen and could quickly
get started on the code instead of wasting time figuring out how to set up a basic web server in another language.

# Installation
Make sure you have java 8 installed. A Unix tutorial can be found here: https://www.guru99.com/how-to-install-java-on-ubuntu.html.
A similar Windows one can be found here:https://codenotfound.com/java-download-install-jdk-8-windows.html.
Or you can just google how to download/install Java 8.

Once you have Java 8, simply navigate to the root directory and you are ready to go. This project uses gradle
and the gradle wrapper to build the project so the wrapper is all you need which should already be in the
root directory.

# Running the Application
In a Unix/Linux Command Line, simply run `./gradlew run` from the root directory to execute the gradle wrapper and run the program.
It should also build the project. If using Windows, just use the corresponding Windows run command in Command Prompt
to the `gradlew` file to run it. 

Once this is done, that's it! The application should be running and a server listening on `localhost:8000/email` should be running.

To test the application, simply make `POST` requests to `localhost:8000/email`.

# Configuration and Redeployment
In `src/main/resources`, you can find a `config.properties` file. `default.sender` can be set to `snail` or `spend`. This will
change what email server this service deploys against and uses as the default. If you change this setting,
simply press `Ctrl-c` in the gradle process window or kill the process another way and simply re-run `./gradlew run`
to re-run the app with the new setting.

# Basic Code overview
The code has a set-up of a basic Java web server. I have a `EmailSender` abstract class that is implemented
by `SnailgunSender` and `SpendgridSender` that implement the Email sending for each respective email server.

# Trade-offs and Future Improvements
Unfortunately, the time constraint limited what I could do. At the moment, the logic of the code simply sends
the email to the email server and then does pretty much nothing with that response. It returns the JSON response
in the Snailgun implementation, but it only checks if the returned json status is `failed`. Then, it just
returns a RuntimeException if it does. Same with Spendgrid, it just returns back a static string. There is no logic
to determine whether to switch to the other service or not. If I had more time, I'd add some logic that if it
failed sending to one of the servers multiple times (With retries and exponential backoff), it would switch
to the other server automatically (Without needing a re-deploy). Right now, it doesn't even retry if it fails
because I just ran out of time before I could add that. 

The code is pretty basic and lacks complex error handling. Right now, I kind of just throw RuntimeExceptions 
everywhere when there is an error such a JSON parse error or it fails to send the email correctly to the server.
If I had more time, I'd add actual error handling/logic. The code is kind of simple in many of the places where
I don't do the "perfect" coding style thing because of time, such as instantiating Lambda/Anonymous classes
in a constructor. I really shouldn't do that, but I just left it in due to time.

I'd definitely refactor/cleanup a lot of the code with more time.

# API Key Error
Also, for some reason, the API key for Spendgrid was not working. I was just receiving a `"error":"API key doesn't exist"`
when trying to use the Spendgrid API with the provided url and api key. I could never get it to work and I was spending
too much time debugging it and eventually I just assumed it was an invalid API key provided. I'm not sure
if I may have missed something in the instructions or if I messed up. The Snailgun API key worked fine.