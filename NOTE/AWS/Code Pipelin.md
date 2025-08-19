version: 0.2

phases:
  build:
    commands:
      - echo "Starting gradle build..."
      - cd shop
      - chmod +x gradlew
      - ./gradlew build
      - echo "Gradle build finished successfully!"