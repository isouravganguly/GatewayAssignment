# Smallcase Android Assignment
A native Android application built with Kotlin and Jetpack Compose that demonstrates deep linking and API integration capabilities.

## Features
### 1. Home Screen
- Two main buttons:
  - "Open In App Browser"
  - "Call API"
### 2. In-App Browser Integration
- Opens https://webcode.tools/generators/html/hyperlink in a Custom Chrome Tab
- Handles deep links in the format: sc-assignment://home/redirect?status={x}&code={y}&data={z}
  - x: String value
  - y: Integer value
  - z: JSON-decodable string
- Automatically closes the Custom Tab when receiving the deep link
- Displays the decoded deep link data in an AlertDialog with proper JSON formatting
### 3. API Integration
- Makes a GET request to a public JSON endpoint (jsonplaceholder.typicode.com)
- Displays the response in an AlertDialog
- Features a Copy button to copy the JSON data
- JSON data is properly formatted for readability
## Technical Implementation
### Architecture
- Built with Kotlin and Jetpack Compose
- Uses MVVM pattern for clean separation of concerns
- Implements coroutines for asynchronous operations
### Key Components
- Custom Chrome Tabs for in-app browser
- Retrofit for API calls
- Gson for JSON parsing and formatting
- Material3 components for UI
### Project Structure
## Setup Instructions
### Prerequisites
- Android Studio Arctic Fox or newer
- Android SDK 21 or higher
- Kotlin 1.5.0 or higher
### Running the Project
1. Clone the repository
2. Open the project in Android Studio
3. Sync project with Gradle files
4. Run the app on an emulator or physical device
### Build Configuration
- minSdk: 21
- targetSdk: Latest stable version
- Kotlin version: 1.5.0+
## Libraries Used
- Jetpack Compose for UI
- Retrofit for networking
- Gson for JSON handling
- Custom Tabs for in-app browser
- Kotlin Coroutines for async operations
## Testing
The app has been tested on:

- Different Android versions (API 21 and above)
- Various screen sizes
- Different deep link scenarios
- Network conditions (success/failure cases)