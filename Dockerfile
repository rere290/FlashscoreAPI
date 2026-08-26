# --- STAGE 1: Build the Application ---
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Copy Gradle Wrapper and project files
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY src src

# Give execute permissions to Gradle Wrapper
RUN chmod +x gradlew

# Build the Spring Boot application
RUN ./gradlew dependencyInsight --dependency selenium-remote-driver --configuration runtimeClasspath \
    && ./gradlew clean bootJar -x test
# --- STAGE 2: Runtime ---
FROM eclipse-temurin:17-jre

WORKDIR /app

# Install dependencies needed by Selenium/Chrome
RUN apt-get update && apt-get install -y --no-install-recommends \
    wget \
    curl \
    unzip \
    gnupg \
    jq \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Install Google Chrome
RUN wget -q -O /tmp/google-chrome-key.pub https://dl.google.com/linux/linux_signing_key.pub \
    && gpg --dearmor -o /usr/share/keyrings/google-chrome.gpg /tmp/google-chrome-key.pub \
    && echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google-chrome.gpg] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y --no-install-recommends google-chrome-stable \
    && rm -rf /var/lib/apt/lists/* /tmp/google-chrome-key.pub

# Install ChromeDriver
RUN CHROME_VERSION=$(google-chrome --version | grep -oE '[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+') \
    && CHROMEDRIVER_VERSION=$(curl -sS https://googlechromelabs.github.io/chrome-for-testing/known-good-versions-with-downloads.json \
    | jq -r --arg v "$CHROME_VERSION" '.versions[] | select(.version == $v) | .version' | head -n 1) \
    && echo "Chrome version: $CHROME_VERSION" \
    && echo "ChromeDriver version: $CHROMEDRIVER_VERSION" \
    && CHROMEDRIVER_URL=$(curl -sS https://googlechromelabs.github.io/chrome-for-testing/known-good-versions-with-downloads.json \
    | jq -r --arg v "$CHROMEDRIVER_VERSION" '.versions[] | select(.version == $v) | .downloads.chromedriver[] | select(.platform == "linux64") | .url' | head -n 1) \
    && wget -q "$CHROMEDRIVER_URL" -O /tmp/chromedriver.zip \
    && unzip -q /tmp/chromedriver.zip -d /tmp/chromedriver \
    && mv /tmp/chromedriver/chromedriver-linux64/chromedriver /usr/bin/chromedriver \
    && chmod +x /usr/bin/chromedriver \
    && rm -rf /tmp/chromedriver /tmp/chromedriver.zip

ENV CHROME_BIN=/usr/bin/google-chrome
ENV CHROMEDRIVER_BIN=/usr/bin/chromedriver
ENV DRIVER_PATH=/usr/bin/chromedriver

# Copy the executable Spring Boot JAR
COPY --from=builder /app/build/libs/flashscore.api-1.0.0.jar /app/app.jar

# Railway uses the PORT environment variable
EXPOSE 8080

CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
