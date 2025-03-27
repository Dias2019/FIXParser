# FIX Parser 📈

Welcome to the **FIX Parser** project! This repository contains a Java application designed as a take-home assignment to parse and process FIX (Financial Information eXchange) protocol messages. The FIX protocol is widely used in the financial industry for electronic communication of trade-related messages.
To simplify the system, we assume that FIX messages will be received as byte array `byte[] msg`.

## API Usage

```java
FIXParserService service = FIXParserServiceFactory.create();
service.handleFixMessage(fixMessage);
```
- `factory` class will create FIXParserService with default configurations


## Architecture

Here is an overall architecture of FIX Parser which utilized both reusability of objects and concurrency. More details are presented below:   
<img src="img/parser_architecture.png" alt="FIX Parser Architecture" width="700"/>

### Key architecture decisions:
  - Reusibility of Objects and Pre-initialization:
    - All FIX Parsers with queues and a single consumer are created during initialization phase and used throughout service lifecycle. Therefore, minimizing object creation overhead and garbage collectors cleanup overhead.
    - After each parsing, all states are reset and ready for the next process.
  - Reusability vs Concurrency Trade-off:
    - As mentioned above, reusability removes object creation/removal overhead, therefore, improving the performance. However, this also means that we can't apply concurrent parsing as Reusable Objects would be shared across threads and lead to conflicts.
    - Therefore, we used Object Pooling to achieve concurrent parsing of multiple FIX messages. FIXParserService will have pre-initialized pool of FIXParser objects, where each will be handling one FIX msg concurrently.
    - _Remark_: each FIXParser is run on single thread.
  - Minimize heap usage:
    - In PatternMatcher, we used first 32-bit of `long` primitive to store _offset_ and last 32-bit to store _length_. Instead of wrapping two `int` values into Object, which will be allocated to heap.
  - Prioritize logical operations over functional:
    - Using `sum & 0xFF` to prevent value overflow instead of `sum % 256`.
  - In-memory loading for faster lookup:
    - FIXTagMapper was loaded in-memory as `HashMap` and shared between multiple worker threads. Read-only usage of mapping lookup makes it thread-safe and decreases memory footprint.
  - Parallelism over Concurrency:
    - As confirmed via Benchmark Tests, limiting number of FIX Parser workers to `CPU cores` resulted in the optimal performance. This number of threads eliminates `context switching` overhead between threads and confirms that excessive creation of threads degrades performance. 

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

<img src="img/numOfThreads.png" alt="Thread Performance Comparison" width="700"/>

The graph shows the relationship between the number of processing threads and overall throughput. This data helps determine the optimal thread configuration for different environments and message volumes.

## Future Plans 🚀

- Implement FIX message parsing.
- Add support for different FIX versions.
- Enhance error handling and logging.

---

Feel free to reach out if you have any questions or suggestions! 😊
