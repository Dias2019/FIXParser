# FIXParser 📈

Welcome to the **FIXParser** project! This repository contains a Java application designed as a take-home assignment to parse and process FIX (Financial Information eXchange) protocol messages. The FIX protocol is widely used in the financial industry for electronic communication of trade-related messages.

## Project Overview

- **Language**: Java
- **Main Class**: `Main.java`
- **Functionality**: Currently, the application prints "Hello, World!" to the console. Future updates will include parsing capabilities for FIX messages.

## Getting Started

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/yourusername/FIXParser.git
   ```
2. **Navigate to the Project Directory**:
   ```bash
   cd FIXParser
   ```
3. **Compile and Run**:
   ```bash
   javac src/main/java/org/parser/Main.java
   java -cp src/main/java org.parser.Main
   ```

## Benchmark Testing 🚀

The project includes benchmark tests to measure the performance of the FIX parsing functionality:

- **Benchmark Test Class**: `FIXParserServiceBenchmarkTest.java`
- **Test Data**: 100 copies of the same FIX message

### What's Being Measured

The benchmark tests measure:

- Throughput (messages processed per second)
- Average processing time per message
- Memory usage during parsing operations
- Performance under different load conditions

Results from these tests help optimize the parser for production environments and provide insights into how the parser performs with various message types and volumes.

### Benchmark Results

Below is a visualization of how the parser performance scales with the number of threads:

![Thread Performance Comparison](img/numOfThreads.jpg)

The graph shows the relationship between the number of processing threads and overall throughput. This data helps determine the optimal thread configuration for different environments and message volumes.

## Future Plans 🚀

- Implement FIX message parsing.
- Add support for different FIX versions.
- Enhance error handling and logging.

---

Feel free to reach out if you have any questions or suggestions! 😊
