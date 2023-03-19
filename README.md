# ButterMove Cost Calculator

> Author: Maximiliano Mateos
> 
> GitHub: https://github.com/maxmateos

## About

Audiience Code challenge. 

Price calculator that provides the price for hiring ButterMove moving company services. Supports capability for configuration of different calculation algorithms per state, each with their own rate and discount policies.


## Build
This project uses gradlew dependency manager. You can build the app by running
```console
$ ./gradlew build -x test
```

## Run the App
You can execute the application in two ways. Both will attempt to make use of `localhost:8080` port. 

### Dockerized Execution
This project supports spinning up a docker image to execute through. Run the following command to do so.
```console
$ docker compose up --build
```

### Java Local Environment IDE
The main java method of this project can be found in: 
```src/main/java/com/buttermove/demo/ButterMovePriceCalculatorApplication.java```

The main method can be executed from a Java IDE like IntelliJ IDEA.

## Endpoint Documentation UI
You can find the project's swagger endpoint definition in:
```src/main/resources/static/swagger.yaml```

This can be visualized in a swagger editor like https://editor.swagger.io/ .

The project already hosts a visualizer of the endpoint's live definition, so, alternatively, you can spin up the app in your preferred way and then visit the following url in your browser:
```http://localhost:8080/swagger-ui/```

### Make Requests to the service
In addition to traditional http requesting tools like cURL or Postman, you can issue requests directly from the Documentation UI

- To do so, access the Documentation UI portal in your browser. You should see the swagger UI as well as the API sections.
- This API makes use of Basic Authentication to authenticate requests, so you'll need to provide valid credentials in order to make requests from the UI.
Click on the green `Authorize` button to the right of the `Servers` tooltip. 
- Enter a valid username & password pair, you can get or configure these in  `src/main/resources/application.yaml`
- Submit through the `Authorize` button and close the auth prompt.
- Navigate to the desired endpoint by clicking on the desired section and request. 
    > e.g. Calculator -> GET /v1/price-calculator...
- Click on Try it out button to the right.
- Edit the request parameters.
- Click Execute. You should see a successful response if you included the default example values.

## Configurations File
This project is highly configurable and designed to be scaled as a real application would. This is why all "magic numbers" in requirements document where put in `src/main/resources/application.yaml` and so they can be updated or tested with different values. 

You can also see the active configurations by calling the configuration endpoints once the application is up.

## Run Unit Tests
Uses JUnit to run tests.
```console
$ ./gradlew test
```

## Run Google Java Format to Edit File Format
```console
$ ./gradlew goGJF
```