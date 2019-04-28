# Bacon 🥓🥓🥓

*The Reversi AI of Group 6*

## Usage

You will need [Java 11](https://jdk.java.net/12/) and [Ant](https://ant.apache.org/) to be installed.

To build the project, run

```bash
ant build
```

This creates the `bacon.jar` file in `bin/jar`. You can execute the Jar file directly by using

```bash
ant run -Dserver=<server> -Dport=<port>
```

or use `java -jar` and pass the server and port via `-s/--server` and `-p/--port`.

You can also pass arbitrary arguments in Ant with `-Dargs='<your arguments>'`.

## Tests

Run

```bash
ant test
```

to run all unit tests.

## Documentation

```bash
ant doc
```

generates the private-level JavaDoc in the `doc` directory.