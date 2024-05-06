# Attestr Android SDK

![Platform](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)

## Installation

### Maven users

Add this dependency to your project:

```xml
<dependency>
 <groupId>com.attestr</groupId>
 <artifactId>attestr-flowx</artifactId>
 <version>0.1.0</version>
</dependency>
```

### Gradle users

Add this dependency to your project's build file:

```groovy
implementation "com.attestr.attestr-flowx:0.1.0"
```

## Usage

Create new object of AttestrFlowx

```java
AttestrFlowx attestrFlowx = new AttestrFlowx();
```

Initialize AttestrFlowx with client_key, handshake_id & activity

```java
/**
 * Initialises an instance of AttestrFlowx
 * @param cl Mandatory client key
 * @param hs Mandatory handshake key
 * @param activity Activity on which flow is to be rendered
 */
attestrFlowx.init(clientKey, handShakeID, this);
```

Launch AttestrFlowx with locale, retry and queryParameters

```java
/**
 * This function launches the flow with the following specifications
 * @param lc Mandatory language code eg. 'en' for English.
 * @param retry Mandatory parameter to set retry as true if re-running the flow for a previously used handshake.
 * @param qr Optional query parameters.
 */
attestrFlowx.launch(locale, retry, qr);
```

Implement AttestrFlowx eventlistener and define success, error and skip handlers

```java
// Implement AttestrFlowXEventListener 
public class ExampleActivity implements AttestrFlowXEventListener
	
	// Replace following with your own implementations
		@Override
		public void onFlowXComplete(Map<String, Object> data) {
			Toast.makeText(MainActivity.this, "Flow completed successfully", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onFlowXSkip(Map<String, Object> data) {
			Toast.makeText(MainActivity.this, "Flow skipped", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onFlowXError(Map<String, Object> data) {
			String errorMessage = (String) data.get("message");
			Toast.makeText(MainActivity.this, "Error : "+errorMessage, Toast.LENGTH_SHORT).show();
		}

```
