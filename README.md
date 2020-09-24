# DENX
GUI Coap client

# JavaFX install
- sudo apt install openjfx openjdk-8-jdk

# Maven install
- sudo apt install maven

# Dependences
- weka-stable-3.8.4.jar
- slf4j-api-1.7.9.jar
- slf4j-nop-1.7.9.jar

# Maven
- mvn clean compile
- mvn package assembly:single

# Run
- java -cp target/DENX-1.0-SNAPSHOT.jar:Jar-file1.jar:Jar-file2: path.to.Main.class

# Run with dependencies
- java -cp target/DENX-1.0-SNAPSHOT-jar-with-dependencies.jar path.to.Main.class
