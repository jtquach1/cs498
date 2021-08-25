# Implementation of Algorithms from _Introduction to Compiler Construction in a Java World_

## Table of Contents

* [Motivation](#motivation)
    * [Why do we study compilers?](#why-do-we-study-compilers)
    * [Purpose of this project](#purpose-of-this-project)
* [Implemented algorithms](#implemented-algorithms)
    * [Lexical analysis](#lexical-analysis)
    * [Parsing](#parsing)
* [Before getting started](#before-getting-started)
    * [Download and install Java](#download-and-install-java)
* [Development](#development)
    * [Download and install Maven](#download-and-install-maven)
    * [Download and build the project](#download-and-build-the-project)
* [Usage](#usage)
    * [How to add the jar to your classpath](#how-to-add-the-jar-to-your-classpath)
    * [Lexical analysis program](#lexical-analysis-program)
    * [LL(1) parsing program](#ll1-parsing-program)
    * [LR(1) parsing program](#lr1-parsing-program)
    * [Converting produced DOT files to PDF](#converting-produced-dot-files-to-pdf)

## Motivation

### Why do we study compilers?

Compilers have real-world applications. New architectures are being developed by the second, such as ARM. Not every
compiler is useful on every machine; for instance, a compiler for C to the Intel architecture isn’t useful for a machine
using the ARM architecture. Second, compilers find their usage in high-performance computing. C and Fortran are popular
languages in HPC because they have good and stable compilers to produce optimized machine code. So, we can say that we
study compilers because they are the backbone to all software development.

### Purpose of this project

This project serves as a pedagogical aid to _Introduction to Compiler Construction in a Java World_ by Bill Campbell,
Swami Iyer, and Bahar Akbal-Delibaş, and presents Java-based implementations of algorithms from the Lexical Analysis and
Parsing chapters. A student benefits from these programs by using them and thus understanding the algorithms much
better, as they have something concrete that supplements what is in the text.

## Implemented algorithms

Where NFA stands for non-deterministic finite automaton, DFA stands for deterministic finite automaton, and CFG stands
for context-free grammar.

### Lexical analysis

1. Convert a regular expression to an NFA (Dijkstra’s shunting yard algorithm, Thompson’s construction)
2. Compute ɛ-closure for a set of states in an NFA
3. Compute ɛ-closure for a single state in an NFA
4. Convert an NFA to a DFA (Powerset construction)
5. Minimize (i.e., prune) a DFA

### Parsing

1. Implement LL(1) parsing
2. Compute first sets of symbols in a CFG
3. Compute first set for a sequence of symbols
4. Compute follow sets of symbols in a CFG
5. Build the LL(1) parse table for a CFG
6. Remove left recursion in a CFG
7. Implement LR(1) parsing
8. Compute the closure of an itemset
9. Compute goto of an itemset
10. Build the LR(1) canonical collection of itemsets
11. Build the LR(1) parse (Action and Goto) tables

## Before getting started

### Download and install Java

This project was developed
with [Java SE 14](https://www.oracle.com/java/technologies/javase/jdk14-archive-downloads.html)
, which contains JRE 14 to run its programs. Check your JRE version like so:

```shell
java -version
```

#### On Windows:

```shell
PS C:\Users\jtqua> java -version
java version "14.0.2" 2020-07-14
Java(TM) SE Runtime Environment (build 14.0.2+12-46)
Java HotSpot(TM) 64-Bit Server VM (build 14.0.2+12-46, mixed mode, sharing)
```

In your Environment Variables, check that your `JAVA_HOME` is set to `C:\Program Files\Java\jdk-14.0.2` and your `PATH`
includes `C:\Program Files\Java\jdk-14.0.2\bin` for both the User and System variables.

#### On Linux:

```shell
jtquach@DESKTOP-4LLQMM3:~$ java -version
openjdk version "14.0.2" 2020-07-14
OpenJDK Runtime Environment (build 14.0.2+12-Ubuntu-120.04)
OpenJDK 64-Bit Server VM (build 14.0.2+12-Ubuntu-120.04, mixed mode, sharing)
```

## Development

### Download and install Maven

This step is required if you want to develop and build the project. At the time of this writing, the current version
of [Maven](https://maven.apache.org/download.cgi) is 3.8.2.

### Download and build the project

#### Clone the repository:

```shell
git clone https://github.com/jtquach1/cs498.git
cd cs498
```

Then, you can proceed to run any of the Maven commands.

#### Compile classes:

```shell
mvn compile
```

#### Run unit tests:

```shell
mvn test
```

#### Build a runnable jar (also compiles classes and runs unit tests):

```shell
mvn package
```

The package should be found in `./target/algorithms.jar`.

#### Cleanup produced files:

```shell
mvn clean
```

## Usage

If you're not developing and just want to run the programs, be sure to get the `algorithms.jar`
from [Releases](https://github.com/jtquach1/cs498/releases) and add it to your `CLASSPATH`.

### How to add the jar to your classpath

#### On Windows

In your Environment Variables, set System variable `CLASSPATH` to where `algorithms.jar` is located;
e.g. `C:\Users\jtqua\Downloads\algorithms.jar`.

#### On Linux

Set the `CLASSPATH` to where `algorithms.jar` is located; e.g. `/home/jtquach/algorithms.jar`.

```shell
export CLASSPATH=.:/home/jtquach/algorithms.jar
```

### Lexical analysis program

#### Command:

```shell
java algorithms.FSA -i inputRegex -o outputPrefix
```

Where `inputRegex` is a regular expression using `.`, `|`, `(`, `)`, `*` operators and `outputPrefix` is a prefix added
to the generated DOT files. `inputRegex` must be surrounded by `""` because `(`, `)`, `|` are special characters
in [Bash](https://www.gnu.org/software/bash/manual/html_node/index.html).

See [this flowchart](pdf/fsa_flowchart.pdf) for more details on running the program.

#### On Windows

```shell
PS C:\Users\jtqua> java algorithms.FSA -i "(a|b)a*b" -o example
Printing out NFA, DFA, and minimal DFA
```

#### On Linux

```shell
jtquach@DESKTOP-4LLQMM3:~$ java algorithms.FSA -i "(a|b)a*b" -o example
Printing out NFA, DFA, and minimal DFA
```

### LL(1) parsing program

#### Commands:

```shell
java algorithms.LL1 -i inputFile -o outputPrefix
java algorithms.LL1 -i inputFile -s sentence -o outputPrefix
```

Where `inputFile` is a file containing a BNF-grammar, `sentence` is a file containing the space-delimited program to
parse, and `outputPrefix` is a prefix added to the generated DOT files.

Each line of `inputFile` is a production defined by the following grammar:

```
<production> ::= <symbol> <space> <delimiter> <space> <sequence>
<sequence> ::= <symbol> <sequence> | <space> <symbol> <sequence> | ε
<symbol> ::= <letter> <symbol> | ε
<letter> ::= A...Z | a...z | 0...9 | ’ | + | - | * | ( | ) | · | #
<space> ::= ‘ ’
<delimiter> ::= ::=
```

See [this flowchart](pdf/ll1_flowchart.pdf) for more details on running the program. Additionally,
see [grammar.txt](./grammar.txt) for an example of an `inputFile` and [sentence.txt](./sentence.txt) as an example of
a `sentence`.

#### On Windows

```shell
PS C:\Users\jtqua> java algorithms.LL1 -i grammar.txt -s sentence.txt -o arithmeticExpression
Printing out grammar, first sets, follow sets, and LL(1) parse table
Grammar is not LL(1), attempting to remove left recursion
Printing sentence parse with LL(1) grammar
```

#### On Linux

```shell
jtquach@DESKTOP-4LLQMM3:~$ java algorithms.LL1 -i grammar.txt -s sentence.txt -o arithmeticExpression
Printing out grammar, first sets, follow sets, and LL(1) parse table
Grammar is not LL(1), attempting to remove left recursion
Printing sentence parse with LL(1) grammar
```

### LR(1) parsing program

#### Commands

```shell
java algorithms.LR1 -i inputFile -o outputPrefix
java algorithms.LR1 -i inputFile -s sentence -o outputPrefix
```

The parameters `inputFile`, `sentence`, and `outputPrefix` are defined the same as those in
the [LL(1) parsing program commands](#commands). See [this flowchart](pdf/lr1_flowchart.pdf) for more details on running
the program. Additionally, see [grammar.txt](./grammar.txt) for an example of an `inputFile`
and [sentence.txt](./sentence.txt) as an example of a `sentence`.

#### On Windows

```shell
PS C:\Users\jtqua> java algorithms.LR1 -i grammar.txt -s sentence.txt -o arithmeticExpression
Printing out grammar, augmented grammar, LR(1) canonical collection, Action table, and Goto table
Printing sentence parse with LR(1) grammar
```

#### On Linux

```shell
jtquach@DESKTOP-4LLQMM3:~$ java algorithms.LR1 -i grammar.txt -s sentence.txt -o arithmeticExpression
Printing out grammar, augmented grammar, LR(1) canonical collection, Action table, and Goto table
Printing sentence parse with LR(1) grammar
```

### Converting produced DOT files to PDF

Download [Graphviz](https://www.graphviz.org/download/) to use the `dot` utility.

#### To convert one DOT file at a time:

```shell
dot -Tpdf example.grammar.dot -o example.grammar.pdf
```

#### Batch convert DOT files

Copy [dot.sh](./dot.sh) from the repository root into the same directory as the DOT files you want to convert. This
script also lists each DOT file with its corresponding PDF.

```shell
bash dot.sh
```

#### On Windows

```shell
PS C:\Users\jtqua> bash dot.sh
Converting DOT to PDF
arithmeticExpression.augmented.dot
arithmeticExpression.augmented.pdf
arithmeticExpression.collection.dot
arithmeticExpression.first.dot
arithmeticExpression.first.pdf
arithmeticExpression.follow.dot
arithmeticExpression.follow.pdf
arithmeticExpression.grammar.dot
arithmeticExpression.grammar.pdf
arithmeticExpression.leftRecursionRemoved.first.dot
arithmeticExpression.leftRecursionRemoved.first.pdf
arithmeticExpression.leftRecursionRemoved.follow.dot
arithmeticExpression.leftRecursionRemoved.follow.pdf
arithmeticExpression.leftRecursionRemoved.grammar.dot
arithmeticExpression.leftRecursionRemoved.grammar.pdf
arithmeticExpression.leftRecursionRemoved.ll1ParseOutput.dot
arithmeticExpression.leftRecursionRemoved.ll1ParseOutput.pdf
arithmeticExpression.leftRecursionRemoved.ll1ParseTable.dot
arithmeticExpression.leftRecursionRemoved.ll1ParseTable.pdf
arithmeticExpression.ll1ParseTable.dot
arithmeticExpression.ll1ParseTable.pdf
arithmeticExpression.lr1ParseOutput.dot
arithmeticExpression.lr1ParseOutput.pdf
arithmeticExpression.lr1ParseTable.dot
arithmeticExpression.lr1ParseTable.pdf
example.dfa.dot
example.dfa.pdf
example.minDfa.dot
example.minDfa.pdf
example.nfa.dot
example.nfa.pdf
```

#### On Linux

```shell
jtquach@DESKTOP-4LLQMM3:~$ bash dot.sh
Converting DOT to PDF
arithmeticExpression.augmented.dot
arithmeticExpression.augmented.pdf
arithmeticExpression.collection.dot
arithmeticExpression.collection.pdf
arithmeticExpression.first.dot
arithmeticExpression.first.pdf
arithmeticExpression.follow.dot
arithmeticExpression.follow.pdf
arithmeticExpression.grammar.dot
arithmeticExpression.grammar.pdf
arithmeticExpression.leftRecursionRemoved.first.dot
arithmeticExpression.leftRecursionRemoved.first.pdf
arithmeticExpression.leftRecursionRemoved.follow.dot
arithmeticExpression.leftRecursionRemoved.follow.pdf
arithmeticExpression.leftRecursionRemoved.grammar.dot
arithmeticExpression.leftRecursionRemoved.grammar.pdf
arithmeticExpression.leftRecursionRemoved.ll1ParseOutput.dot
arithmeticExpression.leftRecursionRemoved.ll1ParseOutput.pdf
arithmeticExpression.leftRecursionRemoved.ll1ParseTable.dot
arithmeticExpression.leftRecursionRemoved.ll1ParseTable.pdf
arithmeticExpression.ll1ParseTable.dot
arithmeticExpression.ll1ParseTable.pdf
arithmeticExpression.lr1ParseOutput.dot
arithmeticExpression.lr1ParseOutput.pdf
arithmeticExpression.lr1ParseTable.dot
arithmeticExpression.lr1ParseTable.pdf
example.dfa.dot
example.dfa.pdf
example.minDfa.dot
example.minDfa.pdf
example.nfa.dot
example.nfa.pdf
```
