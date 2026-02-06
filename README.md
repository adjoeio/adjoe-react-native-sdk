
This project is the property of adjoe GmbH and is published for the sole use of entities with which adjoe has a contractual agreement.
The unauthorized redistribution of any or all parts of this project is strictly prohibited.

# Add React Native module to your project

To integrate the adjoe React Native module into your React Native project, follow these steps:

1. Open your project's `package.json` file.

2. Add the adjoe as a dependency under the `dependencies` section. You can change the version number to the desired version of adjoe SDK you want to integrate.

```yaml
dependencies: {
  "react-native-adjoe-sdk": "https://github.com/adjoeio/adjoe-react-native-sdk#v4.2.0"
}

```
3. Open your `build.gradle` file in android folder and add the following section:

```yaml
allprojects {
    repositories {
        maven {
            url  "https://releases.adjoe.io/maven"
        }
    }
}
```

4. Run the following command:
```
npm install
```