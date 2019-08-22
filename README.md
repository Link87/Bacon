# Bacon 🥓🥓🥓

*The Reversi AI of Group 6*

## Usage

You will need [Java](https://developers.redhat.com/products/openjdk/download) (11 at least) and [Ant](https://ant.apache.org/) to be installed.

To build the project, run

```bash
ant build
```

This creates the `bacon.jar` file in `bin/jar`. You can execute the Jar file directly by using

```bash
ant run -Dserver=<server> -Dport=<port> -Dargs=<arguments>
```

where `-Dargs` can be used to pass arbitrary arguments to the client. The following arguments are understood:

- `-s, --server <host>` server to connect with (mandatory)
- `-p, --port <port>` port to connect to (mandatory)
- `--no-prune` disable alpha-beta-pruning
- `--no-sort` disable move sorting entirely
- `-b, --beam <width>` set beam width for forward pruning
- `--no-beam` disable beam search, same as `-b 0`
- `-err` write errors and warnings to `stderr`
- `--help` display the help text

`-Dserver` and `-Dport` are just short hand for the same-called client arguments.
You may also use `-s` and `-p` in `-Dargs` instead.

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